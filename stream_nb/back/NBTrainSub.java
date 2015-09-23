import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


public class NBTrain {


    public static void main(String[] args) {
        /* load data from stdin*/
        try {
            BufferedReader br =
                new BufferedReader(new InputStreamReader(System.in));
            String readline;

            while (null != (readline = br.readLine())) {

                String labelStr = readline.substring(0, readline.indexOf("\t"));
                String wordStr = readline.substring(readline.indexOf("\t") + 1);

                String labelTemp;
                String wordTemp;
                int wordCnt = 0;

                while (null != labelStr) {
                    if (labelStr.indexOf(",") == -1) {
                        labelTemp = labelStr;
                        labelStr = null;
                    } else {
                        labelTemp = labelStr.substring(0, labelStr.indexOf(","));
                        labelStr = labelStr.substring(labelStr.indexOf(",") + 1);
                    }

                    System.out.println("1" + labelTemp + "\t1");
                    System.out.println("2" + "\t1");

                    while (null != wordStr) {
                        wordCnt++;
                        if (wordStr.indexOf(" ") == -1) {
                            wordTemp = wordStr;
                            wordStr = null;
                        } else {
                            wordTemp = wordStr.substring(0, wordStr.indexOf(" "));
                            wordStr = wordStr.substring(wordStr.indexOf(" ") + 1);
                        }
                        System.out.println("4" + wordTemp + "&Y=" + labelTemp + "\t1");
                    }

                    System.out.println("3" + labelTemp + "\t" + (wordCnt - 1));

                    wordStr = readline.substring(readline.indexOf("\t") + 1);
                    wordCnt = 0;

                }

            }

        } catch(IOException e) {
                e.printStackTrace();
        }


    }


}
