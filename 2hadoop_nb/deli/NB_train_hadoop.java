import java.util.*;
import java.io.*;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class NB_train_hadoop {

    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, IntWritable>{

        private final static IntWritable one = new IntWritable(1);
        private final static IntWritable count = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context
                        ) throws IOException, InterruptedException {
            List<String> labels = Functions.tokenizeLabel(value.toString());
            Vector<String> words = Functions.tokenizeDoc(value.toString());

            for (String label : labels) {
                word.set("Y=*");
                context.write(word, one);
                word.set("Y="+label);
                context.write(word, one);
                for (int i = 2; i < words.size(); i++) {
                    word.set("Y="+label+",W="+words.get(i));
                    context.write(word, one);
                }
                word.set("Y="+label+",W=*");
                count.set(words.size()-2);
                context.write(word, count);
            }
        }
      }

    public static class IntSumReducer
            extends Reducer<Text,IntWritable,Text,IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
                           ) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

}


