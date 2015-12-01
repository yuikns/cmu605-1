import java.io.*;

public class MergeCounts {

    public static void main(String[] args) {
        try {
            BufferedReader br =
                new BufferedReader(new InputStreamReader(System.in));
            String readline;

            // stream count, use previous key to record
            String previousKey = "null";
            int previousCnt = 0;
            while (null != (readline = br.readLine())) {
                String[] pair = readline.split("\t");
                if (pair[0].equals(previousKey)) {
                    previousCnt += Integer.parseInt(pair[1]);
                } else {
                    if (!previousKey.equals("null")) {
                        System.out.println(previousKey + "\t" + previousCnt);
                    }
                    previousKey = pair[0];
                    previousCnt = Integer.parseInt(pair[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
