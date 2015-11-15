import collections
import os
import sys
import numpy as np

from numpy.random import rand
import time
import pyspark

MIN_EPS = 0.005
TINY_EPS = 0.00001

def map_line(line):
    # map the line to a triple (word id, doc id, tfidf)
    triple = line.split(',')
    return int(triple[0]), int(triple[1]), float(triple[2])

def calculate_loss(pred_matrix, true_matrix):

    # calculate the loss and RMSE here, and print to stdout
    # Note: pred_matrix is a matrix of num_words x num_docs
    # Note: true_matrix is the result of ratings.collect()
    error = 0.0
    num_nonzero_entries = 0
    for temp, score_list in true_matrix:
        for word_id, doc_id, tfidf_score in score_list:
            num_nonzero_entries += 1
            error += (pred_matrix[word_id][doc_id] - tfidf_score) ** 2

    # calculate RMSE from error and num_nonzero_entries
    rmse = (error / num_nonzero_entries) ** 0.5
    print('loss: %f, RMSE: %f' % (error, rmse))


def get_worker_id_for_position(word_id, doc_id):
    scs = strata_col_size_bc.value
    return doc_id / scs
    # code this. You should be able to determine the worker id from the word id or doc id,
    # or a combination of both. You might also need strata_row_size and/or strata_col_size
    # Suggestion is to return either a column block index or a row block index
    # Assign columns or rows to specific workers


def blockify_matrix(worker_id, partition):
    blocks = collections.defaultdict(list)
    srs = strata_row_size_bc.value
    for temp_worker_id, triple in partition:
        col_block_index = temp_worker_id
        row_block_index = triple[0] / srs
        blocks[(row_block_index, col_block_index)].append(triple)
    # Note: partition is a list of (worker_id, (word_id, doc_id, tf_idf))
    # This function should aggregate all elements which belong to the same block
    # Basically, it should return a single tuple for each block in the partition.
    # Each of these tuples should have the format:
    # ( (col_block_index, row_block_index), list of (word_id, doc_id, tf_df))

    # You can use the col_block_index and row_block_index of each of these tuples
    # to determine which of the blocks should be processed on each iteration.

    # Output the blocks. Output should be several of
    # ( (row block index, col block index), list of (word_id, doc_id, tf_idf) )
    # note that worker id is probably one of row block index or col block index
    for item in blocks.items():
        yield item


def filter_block_for_iteration(num_workers, num_iteration, row_block_index, col_block_index):
    return (col_block_index + num_iteration) % num_workers == 0
    # implement me! You might also need the number of workers here


def perform_sgd(block):
    (row_block, col_block), tuples = block

    srs = strata_row_size_bc.value
    scs = strata_col_size_bc.value

    # determine row_start and col_start based on row_block, col_block and
    # strata_row_size and strata_col_size.
    row_start = row_block * srs # use col_block and row_block to transform to real row and col indexes
    col_start = col_block * scs # use col_block and row_block to transform to real row and col indexes
    w_mat_block = w_mat_bc.value[row_start:row_start + srs, :]
    h_mat_block = h_mat_bc.value[col_start:col_start + scs, :]
    # Note: you need to use block indexes for w_mat_block and h_mat_block to access w_mat_block and h_mat_block

    beta_value = beta_value_bc.value
    num_old_updates = num_old_updates_bc.value

    num_updated = 0
    for word_id, doc_id, tfidf_score in tuples:
        learning_rate = max(MIN_EPS, \
            (100 + num_old_updates + num_updated) ** (-beta_value))

        # update num_updated
        num_updated += 1

        # update w_mat_block, h_mat_block
        w_mat_block_temp = np.copy(w_mat_block[word_id - row_start])
        h_mat_block_temp = np.copy(h_mat_block[doc_id - col_start])
        inner_prod = -2 * (tfidf_score - np.inner(w_mat_block_temp, h_mat_block_temp))
        w_mat_block[word_id - row_start] = w_mat_block_temp - \
            learning_rate * h_mat_block_temp * inner_prod
        h_mat_block[doc_id - col_start] = h_mat_block_temp - \
            learning_rate * w_mat_block_temp * inner_prod
        # Note: you might need strata_row_size and strata_col_size to
        # map real word/doc ids to block matrix indexes so you can update
        # w_mat_block and h_mat_block

        # Note: Use MIN_EPS as the min value for the learning rate, in case
        # your learning rate is too small

        # Note: You can use num_old_updates here

    return row_block, col_block, w_mat_block, h_mat_block, num_updated


if __name__ == '__main__':
    # read command line arguments
    num_factors = int(sys.argv[1])
    num_workers = int(sys.argv[2])
    num_iterations = int(sys.argv[3])
    beta_value = float(sys.argv[4])
    inputV_filepath, outputW_filepath, outputH_filepath = sys.argv[5:]

    # create spark context
    conf = pyspark.SparkConf().setAppName("SGD").setMaster("local[{0}]".format(num_workers))
    sc = pyspark.SparkContext(conf=conf)

    # measure time starting here
    start_time = time.time()

    # get tfidf_scores RDD from data
    # Note: you need to implement the function 'map_line' above.
    if os.path.isfile(inputV_filepath):
        # local file
        tfidf_scores = sc.textFile(inputV_filepath).map(map_line)
    else:
        # directory, or on HDFS
        rating_files = sc.wholeTextFiles(inputV_filepath)
        tfidf_scores = rating_files.flatMap(
            lambda pair: map_line(pair[1]))

    # get the max_word_id and max_doc_id.
    # this can be coded in 1-2 lines, or 10-20 lines, depending on your approach...
    max_word_id = tfidf_scores.max(lambda x: x[0])[0]
    max_doc_id = tfidf_scores.max(lambda x: x[1])[1]
    
    # build W and H as numpy matrices, initialized randomly with ]0,1] values
    w_mat = rand(max_word_id + 1, num_factors) + TINY_EPS
    h_mat = rand(max_doc_id + 1, num_factors) + TINY_EPS
    w_mat = w_mat.astype(np.float32, copy=False)
    h_mat = h_mat.astype(np.float32, copy=False)

    # determine strata block size.
    strata_col_size = (max_doc_id + 1) / num_workers + 1
    strata_row_size = (max_word_id + 1) / num_workers + 1
    strata_col_size_bc = sc.broadcast(strata_col_size)
    strata_row_size_bc = sc.broadcast(strata_row_size)

    # map scores to (worker id, (word id, doc id, tf idf score) (implement get_worker_id_for_position)
    # Here we are assigning each cell of the matrix to a worker
    tfidf_scores = tfidf_scores.map(lambda score: (
        get_worker_id_for_position(score[0], score[1]),
        (
            score[0], # word id
            score[1], # doc id
            score[2]  # tf idf score
        )
        # partitionBy num_workers, by doing this we are distributing the
        # partitions of the RDD to all of the workers. Each worker gets one partition.
        # Lastly, we do a mapPartitionsWithIndex so each worker can group together
        # all cells that belong to the same block.
        # Make sure we preserve partitioning for correctness and parallelism efficiency
    )).partitionBy(num_workers) \
      .mapPartitionsWithIndex(blockify_matrix, preservesPartitioning=True) \
      .cache()

    beta_value_bc = sc.broadcast(beta_value)

    # finally, run sgd. Each iteration should update one strata.
    num_old_updates = 0
    for current_iteration in range(num_iterations):
        num_old_updates_bc = sc.broadcast(num_old_updates)
        # perform updates for one strata in parallel

        # broadcast factor matrices to workers
        w_mat_bc = sc.broadcast(w_mat)
        h_mat_bc = sc.broadcast(h_mat)

        # perform_sgd should return a tuple consisting of:
        # (row block index, col block index, updated w block, updated h block, number of updates done)
        # s[0][0] is the col or row block index, s[0][1] is the col or row block index
        updated = tfidf_scores \
            .filter(lambda s: \
            filter_block_for_iteration(num_workers, current_iteration, s[0][0], s[0][1])) \
            .map(perform_sgd, preservesPartitioning=True) \
            .collect()

        # unpersist outdated old factor matrices
        w_mat_bc.unpersist()
        h_mat_bc.unpersist()
        # aggregate the updates, update the local w_mat and h_mat
        for row_block, col_block, updated_w, updated_h, num_updates in updated:
            # update w_mat and h_mat matrices

            # map block_row block_col to real indexes (words and doc ids)
            w_update_start = row_block * strata_row_size
            w_update_end = (row_block + 1) * strata_row_size
            w_mat[w_update_start:w_update_end, :] = updated_w

            h_update_start = col_block * strata_col_size
            h_update_end = (col_block + 1) * strata_col_size
            h_mat[h_update_start:h_update_end, :] = updated_h

            num_old_updates += num_updates
        # TODO: you may want to call calculate_loss here for your experiments

    # print running time
    end_time = time.time()
    print 'running time: ' + str(end_time - start_time) + 's'

    calculate_loss(np.dot(w_mat, h_mat.transpose()), tfidf_scores.collect())

    # Stop spark
    sc.stop()

    # add for debug
    print len(w_mat)
    print len(w_mat[0])
    print len(h_mat)
    print len(h_mat[0])
    
    # print w_mat and h_mat to outputW_filepath and outputH_filepath
    with open(outputW_filepath, 'w') as f:
        for i in xrange(w_mat.shape[0]):
            f.write(str(w_mat[i][0]))
            for j in range(1, w_mat.shape[1]):
                f.write(',' + str(w_mat[i][j]))
            f.write('\n')
    with open(outputH_filepath, 'w') as f:
        for i in range(h_mat.shape[1]):
            f.write(str(h_mat[0][i]))
            for j in xrange(1, h_mat.shape[0]):
                f.write(',' + str(h_mat[j][i]))
            f.write('\n')




