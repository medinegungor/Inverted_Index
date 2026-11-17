package homework;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class FileProcessor {
    
    // Stop-words 
    public static Set<String> loadStopWords(String filePath) {
        Set<String> stopWords = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (!word.isEmpty()) {
                    stopWords.add(word);
                }
            }
        } catch (IOException e) {
            System.err.println("Stop words dosyası okunamadı: " + e.getMessage());
        }
        return stopWords;
    }

    // delimiters.txt 
    public static String getDelimiterRegex() {
        return "[-+\\s\\r\\n1234567890’'\"()<>[\\]:,–—―…!.—‐?‘’“”;/⁄ ·&@*\\\\•^¤¢$€£¥₩₪†‡°¡¿¬#№%‰‱¶′§~¨_\\|¦⁂☞∴‽※]";
    }

    // A function that splits text into words, converts to lowercase and removes stop-words
    public static String[] processText(String text, Set<String> stopWords) {
        if (text == null || text.isEmpty()) 
        	return new String[0];
        
        String regex = getDelimiterRegex();
        String[] rawTokens = text.split(regex);
        
        List<String> validWords = new java.util.ArrayList<>();
        for (String token : rawTokens) {
            String word = token.trim().toLowerCase();
            
            if (!word.isEmpty() && !stopWords.contains(word)) {
                validWords.add(word);
            }
        }
        return validWords.toArray(new String[0]);
    }
}
