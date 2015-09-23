import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;


public class NBTrain {


    public static void main(String[] args) {
        /* load data from stdin*/
        try {
            BufferedReader br =
                new BufferedReader(new InputStreamReader(System.in));
            String readline;

            // for debug use
            //int cnt = 0;

            while (null != (readline = br.readLine())) {
                // for debug use
                //if (cnt > 0) break;

                List<String> tokens = Functions.tokenizeDoc(readline, 1);
                // the first line is labels
                String[] labels = tokens.get(0).split(",");
                if (tokens.size() > 1) {
                    for (String label : labels) {
                        // add a head, easier to parse
                        System.out.println("1" + label + "\t1");
                        System.out.println("2" + "\t1");
                        for (int i = 1; i< tokens.size(); i++) {
                            System.out.println("4" +  tokens.get(i) + "&Y=" + label + "\t1");
                        }
                        System.out.println("3" + label + "\t" + (tokens.size() - 1));
                    }
                }
                // for debug use
                //cnt++;
            }

        } catch(IOException e) {
                e.printStackTrace();
        }


    }


}
