package homework;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.List;
import java.util.ArrayList;

public class FileProcessor {

    // Reads and loads English stop words into a Set
    public static Set<String> loadStopWords(String filePath) {
        Set<String> stopWords = new HashSet<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                String word = line.trim().toLowerCase(Locale.ROOT);
                if (!word.isEmpty()) {
                    stopWords.add(word);
                }
            }
        } 
        catch (IOException e) {
            System.err.println("Stop words file could not be read: " + e.getMessage());
        }
        return stopWords;
    }

    // Parses and constructs delimiter regex pattern from delimiters.txt
    public static String loadDelimiterRegex(String filePath) throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        StringBuilder delimiters = new StringBuilder();
        Pattern stringPattern = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher matcher = stringPattern.matcher(source);

        while (matcher.find()) {
            String part = matcher.group(1);
            part = part.replace("\\r", "\r").replace("\\n", "\n") .replace("\\t", "\t").replace("\\\"", "\"").replace("\\\\", "\\");
            delimiters.append(part);
        }
        return delimiters.toString();
    }

    // Splits text using delimiters and filters out empty words and stop words
    public static String[] processText(String text, Set<String> stopWords, String delimiterRegex) {
        if (text == null || text.isEmpty()) {
            return new String[0];
        }
        String[] rawTokens = text.split(delimiterRegex);
        List<String> validWords = new ArrayList<>();

        for (String token : rawTokens) {
            String word = token.trim().toLowerCase(Locale.ROOT);
            if (!word.isEmpty() && !stopWords.contains(word)) {
                validWords.add(word);
            }
        }
        return validWords.toArray(new String[0]);
    }

    // Recursively reads all .txt documents in the given directory and indexes processed words
    public static Map<String, String[]> loadDocuments(String folderPath, Set<String> stopWords, String delimiterRegex) throws IOException {
        Map<String, String[]> documents = new LinkedHashMap<>();
        Path root = Paths.get(folderPath);

        if (!Files.isDirectory(root)) {
            throw new IOException("BBC directory not found: " + root.toAbsolutePath());
        }

        try (Stream<Path> paths = Files.walk(root)) {
            Iterator<Path> iterator = paths.iterator();
            while (iterator.hasNext()) {
                Path file = iterator.next();
                if (Files.isRegularFile(file) && file.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".txt")) {
                    byte[] fileBytes = Files.readAllBytes(file);
                    String content = new String(fileBytes, StandardCharsets.UTF_8);
                    String[] words = processText(content, stopWords, delimiterRegex);
                    String documentName = root.relativize(file).toString().replace('\\', '/');                           
                    documents.put(documentName, words);
                }
            }
        }

        return documents;
    }
}