import java.io.*;
import java.util.*;

public class ApproxPageRank {

    public static void main(String[] args) {
        /* init */
        // parse the args
        String inputFile = args[0];
        String seed = args[1];
        double alpha = 1.2*Double.parseDouble(args[2]);
        double epsilon = Double.parseDouble(args[3]);
        // init params
        Map<String, Double> p = new HashMap<String, Double>();
        Map<String, Double> r = new HashMap<String, Double>();
        Map<String, List<String>> neighbors =
            new HashMap<String, List<String>>();
        int breakFlag = 0;
        r.put(seed, 1.0);

        /* iteration to update p, r */
        while (breakFlag == 0) {
            // fist set up breakFlag as 1.
            breakFlag = 1;
            try {
                BufferedReader br =
                    new BufferedReader(new FileReader(inputFile));
                String readline = null;
                while (null != (readline = br.readLine()))  {
                    int tabPos = readline.indexOf('\t');
                    String u = readline.substring(0, tabPos);
                    if (!r.containsKey(u)) continue;
                    List<String> temp = Functions.tokenizeDoc(readline);
                    int d_u = temp.size() - 1;
                    double r_u = r.get(u);

                    // exec pushes if not satisfy
                    if ((r_u/d_u) > epsilon) {
                        //System.out.println(r_u/d_u);
                        //if (iter >= 10) break;
                        List<String> nei = new ArrayList<String>();
                        breakFlag = 0;
                        /* do the push */
                        // first set p_u
                        double p_u = 0;
                        if (p.containsKey(u)) p_u = p.get(u);
                        // update p
                        p_u += alpha * r_u;
                        p.put(u, p_u);
                        // update r
                        double r_u_new = (1 - alpha) * r_u * 0.5;
                        r.put(u, r_u_new);
                        r_u_new /= d_u;
                        // update other points
                        for (int i = 1; i < temp.size(); i++) {
                            String v = temp.get(i);
                            nei.add(v);
                            // set up v
                            double r_v = 0.0;
                            if (r.containsKey(v)) r_v = r.get(v);
                            r.put(v, r_u_new + r_v);
                        }
                        neighbors.put(u, nei);
                    }
                }
                br.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }


        /* find the low-conductance subgraph */
        // first sort the decreasing order
        ValueComparator bvc = new ValueComparator(p);
        TreeMap<String, Double> sort_p = new TreeMap<String, Double>(bvc);
        sort_p.putAll(p);

        // init subGraph
        Set<String> subGraph = new HashSet<String>();
        subGraph.add(seed);
        int volum = neighbors.get(seed).size();
        int bonds = volum;
        double phi = (double)bonds/(double)volum;
        int best_index = 0;
        // loop new and get the subGraph
        int index = 0;
        for (String key : sort_p.keySet()) {
            index++;
            if (!key.equals(seed)) {
                subGraph.add(key);
                for (String nei : neighbors.get(key)) {
                    volum++;
                    if (subGraph.contains(nei)) bonds--;
                    else bonds++;
                }
                double new_phi = (double)bonds/volum;
                if (new_phi < phi) {
                    phi = new_phi;
                    best_index = index;
                }
            }
        }

        int loop_index = 0;
        for (String page : sort_p.keySet()) {
            if (loop_index <= best_index) {
                System.out.println(page + "\t" + p.get(page));
            }
            loop_index++;
        }

    }

}

