import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class NBTrain {


    public static void main(String[] args) {
        /* load data from stdin*/
        try {
            BufferedReader br =
                new BufferedReader(new InputStreamReader(System.in));
            String readline;

            while (null != (readline = br.readLine())) {

                List<String> tokens = Functions.tokenizeDoc(readline, 1);
                String[] labels = tokens.get(0).split(",");
                if (tokens.size() > 1) {
                    for (String label : labels) {
                        System.out.println("1" + label + "\t1");
                        System.out.println("2" + "\t1");
                        for (int i = 1; i< tokens.size(); i++) {
                            System.out.println("4" +  tokens.get(i) + "&Y=" + label + "\t1");
                        }
                        System.out.println("3" + label + "\t" + (tokens.size() - 1));
                    }
                }
            }

        } catch(IOException e) {
                e.printStackTrace();
        }

    }

}
