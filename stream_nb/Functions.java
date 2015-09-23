import java.util.ArrayList;
import java.util.List;

public class Functions {

    public static List<String> tokenizeDoc(String cur_doc, int getLabel) {
        String[] words = cur_doc.split("\\s+");
        List<String> tokens = new ArrayList<String>();
        for (int i = 0; i < words.length; i++) {
            // the first line is labels
            if (i == 0) {
                if (getLabel == 1) {
                    tokens.add(words[i]);
                }
            }
            else {
                words[i] = words[i].replaceAll("\\W", "");
                if (words[i].length() > 0) tokens.add(words[i]);
            }
        }
        return tokens;
    }

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }


}

