
package homework;

public class InvertedIndex {

    private HashTable<HashTable<DocumentFrequency>> index;

    private int initialCapacity;
    private double maxLoadFactor;
    private boolean usePAF;
    private boolean useDoubleHashing;

    public InvertedIndex(int initialCapacity,double maxLoadFactor,boolean usePAF,boolean useDoubleHashing) {
        this.initialCapacity = initialCapacity;
        this.maxLoadFactor = maxLoadFactor;
        this.usePAF = usePAF;
        this.useDoubleHashing = useDoubleHashing;
        index = new HashTable<>( initialCapacity, maxLoadFactor, usePAF,useDoubleHashing );     
    }

    // PUT OPERATION
    public void put(String word, String fileName) {
        if (word == null || fileName == null) {
            return;
        }
        word = word.toLowerCase().trim();
        if (word.isEmpty()) {
            return;
        }
        HashTable<DocumentFrequency> documents = index.get(word);
        
        // If the word does not exist, create a new inner HashTable
        if (documents == null) {
            documents = new HashTable<>(  initialCapacity,maxLoadFactor,usePAF, useDoubleHashing );
            documents.put(fileName,new DocumentFrequency(fileName, 1));            
            index.put(word, documents);
        }
        else {
            DocumentFrequency existingDocument = documents.get(fileName);              
            // If the document already exists, increase frequency
            if (existingDocument != null) {
                existingDocument.incrementFrequency();
                documents.put(fileName, existingDocument);
            }

            // If the document is new, add it to the inner HashTable
            else {
                documents.put(fileName,new DocumentFrequency(fileName, 1));                   
            }
        }
    }

    // GET OPERATION
    public HashTable<DocumentFrequency> get(String word) {
        if (word == null) {
            return null;
        }
        return index.get(word.toLowerCase().trim());
    }

    // REMOVE OPERATION
    public HashTable<DocumentFrequency> remove(String word) {
        if (word == null) {
            return null;
        }
        return index.remove(word.toLowerCase().trim());
    }

    // GET COLLISION COUNT OF MAIN HASH TABLE
    public int getCollisionCount() {
        int totalCollisions = index.getCollisionCount();
        for (HashTable<DocumentFrequency> documents : index.getAllValues()) {              
            totalCollisions += documents.getCollisionCount();
        }
        return totalCollisions;
    }
}
