package homework;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        // File paths for resources
        String stopWordsPath = "stop_words_en.txt";
        String delimitersPath = "delimiters.txt";
        String bbcFolderPath = "bbc";
        String searchWordsPath = "1000.txt";

        // Initial prime capacity requirement for Hash Table
        int INITIAL_CAPACITY = 101;

        System.out.println("=== LOADING BBC DOCUMENTS AND RESOURCES ===");

        // Load stop words and delimiter regex from files
        Set<String> stopWords = FileProcessor.loadStopWords(stopWordsPath);
        String delimiterRegex = "";
        try {
            delimiterRegex = FileProcessor.loadDelimiterRegex(delimitersPath);
        } catch (IOException e) {
            System.err.println("Error reading delimiters file: " + e.getMessage());
            return;
        }

        // Load and process all text documents under BBC folder
        Map<String, String[]> documents = null;
        try {
            documents = FileProcessor.loadDocuments(bbcFolderPath, stopWords, delimiterRegex);
            System.out.println("Total Loaded Documents: " + documents.size());
        } catch (IOException e) {
            System.err.println("Error loading documents: " + e.getMessage());
            return;
        }

        // Load target search words from 1000.txt
        List<String> searchWords = loadSearchWords(searchWordsPath);
        System.out.println("Total Search Words Loaded: " + searchWords.size());
        System.out.println();

        // Configurations for Performance Matrix (Table 1)
        double[] loadFactors = {0.50, 0.80};
        boolean[] pafOptions = {false, true}; // false: SSF, true: PAF
        boolean[] dhOptions = {false, true};  // false: Linear Probing (LP), true: Double Hashing (DH)

        // Print Performance Matrix Header
        System.out.println("=======================================================================================================================================================");
        System.out.printf("| %-12s | %-13s | %-22s | %-15s | %-16s | %-16s | %-16s | %-16s |\n", 
                "Load Factor", "Hash Function", "Collision Handling", "Collision Count", "Indexing Time", "Avg Search (ns)", "Min Search (ns)", "Max Search (ns)");
        System.out.println("=======================================================================================================================================================");

        // Iterate through all 8 test configurations
        for (double lf : loadFactors) {
            for (boolean usePAF : pafOptions) {
                for (boolean useDH : dhOptions) {
                    
                    // Initialize Inverted Index with current configuration
                    InvertedIndex invertedIndex = new InvertedIndex(INITIAL_CAPACITY, lf, usePAF, useDH);

                    // --- INDEXING PHASE ---
                    long startIndexing = System.currentTimeMillis();
                    
                    for (Map.Entry<String, String[]> entry : documents.entrySet()) {
                        String docName = entry.getKey();
                        String[] words = entry.getValue();

                        for (String word : words) {
                            invertedIndex.put(word, docName);
                        }
                    }
                    
                    long endIndexing = System.currentTimeMillis();
                    long indexingTimeMs = endIndexing - startIndexing;
                    
                    // Total collisions across main and nested Hash Tables
                    int totalCollisions = invertedIndex.getCollisionCount();

                    // --- SEARCH PHASE ---
                    long totalSearchTimeNano = 0;
                    long minSearchTimeNano = Long.MAX_VALUE;
                    long maxSearchTimeNano = Long.MIN_VALUE;

                    for (String searchWord : searchWords) {
                        long startSearch = System.nanoTime();
                        HashTable<DocumentFrequency> searchResult = invertedIndex.get(searchWord);
                        long endSearch = System.nanoTime();

                        long duration = endSearch - startSearch;
                        totalSearchTimeNano += duration;

                        if (duration < minSearchTimeNano) {
                            minSearchTimeNano = duration;
                        }
                        if (duration > maxSearchTimeNano) {
                            maxSearchTimeNano = duration;
                        }
                    }

                    // Calculate average, minimum, and maximum search durations
                    double avgSearchTimeNano = searchWords.isEmpty() ? 0 : (double) totalSearchTimeNano / searchWords.size();
                    if (minSearchTimeNano == Long.MAX_VALUE) minSearchTimeNano = 0;
                    if (maxSearchTimeNano == Long.MIN_VALUE) maxSearchTimeNano = 0;

                    // Format and print matrix row
                    String strLF = String.format("a=%.0f%%", lf * 100);
                    String strHash = usePAF ? "PAF" : "SSF";
                    String strCollision = useDH ? "DH" : "LP";

                    System.out.printf("| %-12s | %-13s | %-22s | %-15d | %-13d ms | %-16.2f | %-16d | %-16d |\n",
                            strLF, strHash, strCollision, totalCollisions, indexingTimeMs, avgSearchTimeNano, minSearchTimeNano, maxSearchTimeNano);
                }
            }
        }
        System.out.println("=======================================================================================================================================================");
    }

    // Helper method to read words from 1000.txt
    private static List<String> loadSearchWords(String path) {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty()) {
                    list.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Search words file could not be read: " + e.getMessage());
        }
        return list;
    }
}