import java.util.*;

public class Functions {

    // tokenize doc into words
    public static List<String> tokenizeDoc(String cur_doc) {
        List<String> result = new ArrayList<String>(); 
        String[] words = cur_doc.split("\\s+");
        
        for (int i = 0; i < words.length; i++) {
            if (i == 0) {
                result.add(words[i])   
            } else {
                words[i] = words[i].replaceAll("\\W", "");
                if (words[i].length() > 0) result.add(words[i]);
            }
        } 
       return result; 
    }

    // get all labels
    public static List<String> getAllLabels() {
        List<String> result = new ArrayList<String>();

        result.add("nl");
        result.add("el");
        result.add("ru");
        result.add("sl");
        result.add("pl");
        result.add("ca");
        result.add("fr");
        result.add("tr");
        result.add("hu");
        result.add("de");
        result.add("hr");
        result.add("es");
        result.add("ga");
        result.add("pt");

        return result;
    }

    // transfer label word to label set
    public static Set<String> transLabel(String input) {
        Set<String> result = new HashSet<String>(); 
        String[] labels = input.split(",");
        
        for (String label : labels) {
            result.add(label);    
        }
        
        return result;
    }

    
}
