import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Test {

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();

		File inputFile = new File(args[0]);
		String seed = args[1];
		double alpha = Double.parseDouble(args[2]);
		double epsilon = Double.parseDouble(args[3]);

		// approximate PageRank
		// initialize p and r
		HashMap<String, Double> P = new HashMap<String, Double>();
		HashMap<String, Double> R = new HashMap<String, Double>();
		R.put(seed, 1.0);

		boolean running = true;
        //int iter = 0;
		// int counter = 0;
		while (running) {
            //System.out.println("iter" + (iter++));
			running = false;
			// counter++;
			BufferedReader reader = new BufferedReader(
					new FileReader(inputFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				int tabPos = line.indexOf('\t');
				String curVertex = line.substring(0, tabPos);
				if (!R.containsKey(curVertex)) {
					continue;
				}
				double Ru = R.get(curVertex);


				// old
				String[] vertices = line.substring(tabPos + 1, line.length())
						.split("\t");
				int Du = vertices.length;
				if (Ru / Du > epsilon) {
                    //System.out.println(Ru/Du);
					//if (iter >= 10) break;
                    running = true;
					// update P
					double originPu = 0;
					if (P.containsKey(curVertex)) {
						originPu = P.get(curVertex);
					}
					P.put(curVertex, originPu + alpha * Ru);
					// update R
					double coeff = (1 - alpha) * Ru * 0.5;
					R.put(curVertex, coeff);
					coeff /= Du;
					for (int i = 0; i < vertices.length; i++) {
						originPu = 0;
						if (R.containsKey(vertices[i])) {
							originPu = R.get(vertices[i]);
						}
						R.put(vertices[i], originPu + coeff);
					}
				}
			}
			reader.close();
		}

        //System.out.println("The P size is " + P.size());

		//long stopTime = System.currentTimeMillis();
		//long elapsedTime = stopTime - startTime;
		//System.out.println("time: " + elapsedTime);
		//startTime = System.currentTimeMillis();

		// generate subgraph
		ArrayList<Vertex> subgraph = new ArrayList<Vertex>();
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		String line = null;
		int seedDegree = 0;
		Vertex seedVertex = null;
		while ((line = reader.readLine()) != null) {
			int tabPos = line.indexOf('\t');
			String curVertexID = line.substring(0, tabPos);
			if (!P.containsKey(curVertexID)) {
				continue;
			}
			if (curVertexID.equals(seed)) {
				seedVertex = new Vertex(curVertexID, P.get(curVertexID));
				String[] vertices = line.substring(tabPos + 1, line.length())
						.split("\t");
				seedDegree = vertices.length;
				for (int i = 0; i < vertices.length; i++) {
					seedVertex.add(vertices[i]);
				}
				continue;
			}

			Vertex curVertex = new Vertex(curVertexID, P.get(curVertexID));
			String[] vertices = line.substring(tabPos + 1, line.length())
					.split("\t");
			for (int i = 0; i < vertices.length; i++) {
				curVertex.add(vertices[i]);
			}
			subgraph.add(curVertex);
		}
		reader.close();
		Collections.sort(subgraph);
		// apr finished


        //System.out.println(seed);
        //System.out.println(P.get(seed));
        //System.out.println(seedDegree);
        //System.out.println("----");
        //for (int i = 0; i < subgraph.size(); i++) {
        //    System.out.println(subgraph.get(i).id);
        //    System.out.println(subgraph.get(i).score);
        //    System.out.println(subgraph.get(i).neighbors.size());
        //    System.out.println("----");
        //}

		// sweep
		// finding the best S
		HashSet<String> S = new HashSet<String>();
		S.add(seed);
		int boundary = seedDegree;
		int volume = seedDegree;
		int bestSSize = 0;
		double bestPhi = 1.0;
		for (int i = 0; i < subgraph.size(); i++) {
			Vertex curVetex = subgraph.get(i);
			S.add(curVetex.id);
			ArrayList<String> neighbors = curVetex.neighbors;
			for (int j = 0; j < neighbors.size(); j++) {
				volume++;
				if (S.contains(neighbors.get(j))) {
					boundary--;
				} else {
					boundary++;
				}
			}
			double curPhi = (double) boundary / volume;
			if (curPhi < bestPhi) {
				bestSSize = i + 1;
				bestPhi = curPhi;
			}
		}

		// generate S*
		System.out.println(seed + "\t" + P.get(seed));
        for (int i = 0; i < bestSSize; i++) {
            String id = subgraph.get(i).id;
            System.out.println(id + "\t" + P.get(id));
        }
		 
        //System.out.println(bestSSize);
		//
		// stopTime = System.currentTimeMillis();
		// elapsedTime = stopTime - startTime;
		// System.out.println("time: " + elapsedTime);

		// output GDF format file
		//System.out
		//		.println("nodedef>name VARCHAR,label VARCHAR,width DOUBLE,height DOUBLE");
		//double seedScore = Math.max(1, Math.log(P.get(seed) / epsilon));
		//System.out.println(seed + "," + seed + "," + seedScore + ","
		//		+ seedScore);
		//for (int i = 0; i < bestSSize; i++) {
		//	String id = subgraph.get(i).id;
		//	double score = Math.max(1, Math.log(P.get(id) / epsilon));
		//	System.out.println(id.replace(',', '.') + ","
		//			+ id.replace(',', '.') + "," + score + "," + score);
		//}

		//System.out.println("edgedef>node1 VARCHAR,node2 VARCHAR");
		//S.clear();
		//S.add(seed);
		//for (int i = 0; i < bestSSize; i++) {
		//	String id = subgraph.get(i).id;
		//	for (int j = 0; j < subgraph.get(i).neighbors.size(); j++) {
		//		String neighbor = subgraph.get(i).neighbors.get(j);
		//		if (S.contains(neighbor)) {
		//			System.out.println(id.replace(',', '.') + "," + neighbor);
		//		}
		//	}
		//	S.add(id);
		//}
	}
}

class Vertex implements Comparable<Vertex> {
	public String id;
	public double score;
	public ArrayList<String> neighbors = new ArrayList<String>();

	Vertex(String id, double score) {
		this.id = id;
		this.score = score;
	}

	void add(String vertex) {
		this.neighbors.add(vertex);
	}

	@Override
	public int compareTo(Vertex o) {
		// TODO Auto-generated method stub
		return this.score > o.score ? -1 : 1;
	}
}
