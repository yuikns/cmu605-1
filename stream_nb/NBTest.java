import java.io.*;
import java.util.*;


public class NBTest {

    public static void main(String[] args) {
        /* Adjust Parameters */
        double ALPHA = 0.4;
        double BETA = 0.05;


        /* Load file */
        List<List<String>> testList = new ArrayList<List<String>>();
        Set<String> allTestString = new HashSet<String>();
        try {
            BufferedReader br = new BufferedReader(
                                    new FileReader(args[0]));
            String readline;
            while (null != (readline = br.readLine())) {
                List<String> tokens = Functions.tokenizeDoc(readline, 0);
                List<String> tempDocList = new ArrayList<String>();
                for (String word : tokens) {
                    if (!Functions.isNumeric(word)) {
                        allTestString.add(word);
                        tempDocList.add(word);
                    }
                }
                testList.add(tempDocList);
            }
            br.close();
        } catch(IOException e) {
            e.printStackTrace();
        }


        /* Start parsing and record merge count info*/
        int totalLabelCnt = 0;
        int testWordCnt = allTestString.size();
        Map<String, Integer> wordLabel2Cnt = new HashMap<String, Integer>();
        Map<String, Integer> label2Cnt = new HashMap<String, Integer>();
        Map<String, Integer> label2WordCnt = new HashMap<String, Integer>();
        try {
            BufferedReader br2 =
                new BufferedReader(new InputStreamReader(System.in));
            String readline2;
            while (null != (readline2 = br2.readLine())) {
                Integer flag = Integer.parseInt(readline2.substring(0, 1));
                if (flag == 1) {
                    String[] pair = readline2.substring(1).split("\t");
                    label2Cnt.put(pair[0], Integer.parseInt(pair[1]));
                } else if (flag == 3){
                    String[] pair = readline2.substring(1).split("\t");
                    label2WordCnt.put(pair[0], Integer.parseInt(pair[1]));
                } else if (flag == 2) {
                    totalLabelCnt = Integer.parseInt(readline2.split("\t")[1]);
                } else if (flag == 4) {
                    String[] streamWords = readline2.substring(1).split("\t");
                    Integer tmpCnt = Integer.parseInt(streamWords[1]);
                    String[] wordLabelPair = streamWords[0].split("&Y=");
                    if (allTestString.contains(wordLabelPair[0])) {
                        wordLabel2Cnt.put(streamWords[0], tmpCnt);
                    }
                }

            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        /* calculate label score */
        Map<String, Double> label2LabelScore = new HashMap<String, Double>();
        for (String label : label2Cnt.keySet()) {
            double labelScore = Math.log((double)(label2Cnt.get(label) + ALPHA) / ((double)(totalLabelCnt + ALPHA * label2Cnt.size())));
            label2LabelScore.put(label, labelScore);
        }
        label2Cnt.clear();

        /* for each document, find the max label */
        for (List<String> wordList : testList) {
            String maxLabel = "null";
            Double maxScore = -10000000000.0;
            for (String labelKey : label2LabelScore.keySet()) {
                double YScore = label2LabelScore.get(labelKey);
                double XScore = 0.0;
                for (String testWord: wordList) {
                    String XScoreKey = testWord + "&Y=" + labelKey;
                    if (wordLabel2Cnt.containsKey(XScoreKey)) {
                        int wordLabelCnt = wordLabel2Cnt.get(XScoreKey);
                        XScore += Math.log( ((double)wordLabelCnt + ALPHA) / ((double)(label2WordCnt.get(labelKey) + ALPHA * testWordCnt)) );
                    } else {
                        XScore += Math.log(((double)ALPHA) / ((double)(label2WordCnt.get(labelKey) + ALPHA * testWordCnt)));
                    }
                }
                double totalScore = BETA * XScore + (1 - BETA) * YScore;
                if (totalScore > maxScore) {
                    maxScore = totalScore;
                    maxLabel = labelKey;
                }
            }
            System.out.println(maxLabel + "\t" + maxScore);
        }
    }
}
