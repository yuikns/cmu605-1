import java.io.*;
import java.util.*;


public class LR {

    public static void main(String[] args) {
        /*
         * 0 is the N size, 1 is the initial learning rate,lambda
         * 2 is the mu, 3 is the max iteration
         * 4 is the training size, 5 is the filename
         */
        if (args.length != 6) {
            System.out.println("Input 6 arguments!");
        }
        int N = Integer.parseInt(args[0]);
        double lambda = Double.parseDouble(args[1]);
        double mu = Double.parseDouble(args[2]);
        int iter_num = Integer.parseInt(args[3]);
        int training_size = Integer.parseInt(args[4]);

        /* A is the clk map, B is the weight map*/
        Map<String, Integer> A = new HashMap<String, Integer>();
        Map<String, Double> B = new HashMap<String, Double>();
        List<String> all_labels = Functions.getAllLabels();

        try {
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(System.in));
            String readline;
            int k = 0;

            /* Training */
            for (int t = 1; t <= iter_num; t++) {
                // k is the clk, reset clk for every new iteration
                k = 0;
                double temp_lambda = lambda / ((double)(t*t));
                double fenzi = 1 - 2*mu*temp_lambda;

                while (null != (readline = br.readLine()) && k < training_size) {
                    k++;
                    List<String> tokens = Functions.tokenizeDoc(readline);
                    Set<String> file_labels = Functions.transLabel(tokens.get(0));

                    for (String label : all_labels) {
                       // first get y and p
                        double y = 0.0;
                        if (file_labels.contains(label)) y = 1.0;
                        double p = 0.0;
                        for (int i = 1; i < tokens.size(); i++) {
                            int id = tokens.get(i).hashCode() % N;
                            if (id < 0) id += N;
                            String key = label + String.valueOf(id);
                            if (B.containsKey(key)) p += B.get(key);
                        }
                        p = Functions.sigmoid(p);

                        // then update parameters
                        for (int i = 1; i < tokens.size(); i++) {
                            int id = tokens.get(i).hashCode() % N;
                            if (id < 0) id += N;
                            String key = label + String.valueOf(id);
                            // test the key 
                            if (!A.containsKey(key)) A.put(key, 0);
                            if (!B.containsKey(key)) B.put(key, 0.0);
                            // first update
                            double fenmu = k - A.get(key);
                            double fensu = Math.pow(fenzi, fenmu);
                            fensu = B.get(key) * fensu;
                            B.put(key, fensu);
                            // second update
                            fensu = fensu + temp_lambda*(y-p);
                            B.put(key, fensu);
                            // third update
                            A.put(key,k);
                        }

                    } // finish inner for all labels
                } // finish while, for batch training

                for (String key : A.keySet()) {
                    double fenmu = k - A.get(key);
                    double fensu = Math.pow(fenzi, fenmu);
                    fensu = B.get(key) * fensu;
                    B.put(key, fensu);
                }
                A.clear();
            } //finish outer for, for all the iter


            /* Test*/
            BufferedReader test_file =
                    new BufferedReader(new FileReader(args[5]));
            while (null != (readline = test_file.readLine())) {
                List<String> tokens = Functions.tokenizeDoc(readline);
                Set<String> file_labels = Functions.transLabel(tokens.get(0));
                StringBuilder sb = new StringBuilder();
                for (String label : all_labels) {
                    sb.append(label);
                    sb.append("\t");
                    double p = 0.0;
                    for (int i = 1; i < tokens.size(); i++) {
                        int id = tokens.get(i).hashCode() % N;
                        if (id < 0) id += N;
                        String key = label + String.valueOf(id);
                        p += B.get(key);
                    }
                    p = Functions.sigmoid(p);
                    if (label.equals("pt")) {
                        //sb.append(p);
                        sb.append(String.format("%8.8f", p));
                    } else {
                        sb.append(String.format("%8.8f", p));
                        sb.append(",");
                    }
                }
                System.out.println(sb.toString());
            }

            test_file.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

}
