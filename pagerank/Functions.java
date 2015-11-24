import java.io.*;
import java.util.*;

public class Functions {

    public static List<String> tokenizeDoc(String cur_doc) {
        String[] words = cur_doc.split("\t");
        List<String> tokens = new ArrayList<String>();
        for (int i = 0; i < words.length; i++ ) {
            tokens.add(words[i]);
        }
        return tokens;
    }

}

class ValueComparator implements Comparator<String> {
    Map<String, Double> map;

    public ValueComparator(Map<String, Double> map) {
        this.map = map;
    }

    public int compare(String a, String b) {
        if (map.get(a) - map.get(b) >= 0) {
            return -1;
        } else {
            return 1;
        }
    }

}

