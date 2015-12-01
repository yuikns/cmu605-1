import java.util.*;
import java.io.*;

public class Functions {

    public static Vector<String> tokenizeDoc(String cur_doc) {
        String[] words = cur_doc.split("\\s+");
        Vector<String> tokens = new Vector<String>();
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("\\W", "");
            if (words[i].length() > 0) {
                tokens.add(words[i]);
            }
        }
        return tokens;
    }

    public static List<String> tokenizeLabel(String cur_doc) {
        String[] words = cur_doc.split("\t");
        List<String> result = new ArrayList<String>();
        for (String label : words[1].split(",")) {
            result.add(label);
        }
        return result;
    }

}
