import java.io.*
import java.util.*


public class LR {
    
    public static void main(String[] args) {
        /*
         * 0 is the N size, 1 is the initial learning rate,lambda
         * 2 is the mu, 3 is the max iteration
         * 4 is the training size, 5 is the filename
         */
        if (args.length != 6) {
            System.out.println("Input 6 arguments!")
        }
        int N = Integer.parseInt(args[0])
        float lambda = Float.parseFloat(args[1]);
        float mu = Float.parseFloat(args[2]);
        int iter_num = Integer.parseInt(args[3])
        int training_size = Integer.parseInt(args[4])

        /* A is the clk map, B is the weight map*/
        Map<String, Integer> A = new HashMap<String, Integer>(); 
        Map<String, Float> B = new HashMap<String, Double>(); 
        List<String> all_labels = Functions.getAllLabels();

        try {
            BufferedReader br = 
                    new BufferedReader(new InputSstreamReader(System.in));
            String readline;
            int id = 0;
            int k = 0;
            float temp_lambda = lambda;

            /* Training */
            for (int t = 1; t <= iter_num; t++) {
                // k is the clk, reset clk for every new iteration
                k = 0;
                while (null != (readline = br.readLine()) && k < training_size) {
                    k++;
                    List<String> tokens = Functions.tokenizeDoc(readline);
                    Set<String> file_labels = Functions.transLabel(tokens.get(0));
                    // parameters
                    temp_lambda = lambda / ((float)(t*t))
                    float fenzi = 1 - 2*mu*temp_lambda;
                    float fenmu = k - A.get(key);
                    float fensu = Math.pow(fenzi, fenmu);
                    
                    for (String label : all_labels) {
                       // first get y and p 
                        float y = 0.0;
                        if (file_labels.contains(label)) y = 1.0;
                        float p = 0.0;
                        for (int i = 1, i < tokens.size(); i++) {
                            id = tokens.get(i).hashCode() % N;
                            if (id < 0) id += N;
                            String key = label + String.valueOf(id);
                            if (B.containsKey(key)) p += B.get(key);
                        }
                        p = Math.exp(p);
                        p = p/(1+p);
                        // then update parameters
                        for (int i = 1, i < tokens.size(); i++) {
                            id = tokens.get(i).hashCode() % N;
                            if (id < 0) id += N;
                            String key = label + String.valueOf(id);
                            // test the key 
                            if (!A.containsKey(key)) A.put(key, 0);
                            if (!B.containsKey(key)) B.put(key, 0.0);
                            // first update
                            float temp_fensu = B.get(key) * fensu;
                            B.put(key, temp_fensu);
                            // second update
                            temp_fensu = temp_fensu + temp_lambda*(y-p);
                            B.put(key, temp_fensu);
                            // third update
                            A.put(key,k);
                        }

                    } // finish inner for all labels   
                } // finish while, for batch training
                for (String key : A.keySet()) {
                    float final_fensu = B.get(key) * fensu;
                    B.put(key, final_fensu);
                } 
            } //finish outer for, for all the iter



            BufferedReader test_file = 
                    new BufferedReader(new FileReader(args[5]));
            while (null != (readline = test_file.readLine())) {
                 
                
            } 

            test_file.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        
    }    
    
    
}
