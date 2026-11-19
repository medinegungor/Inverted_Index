package homework;
import java.util.List;
import java.util.ArrayList;

public class HashTable<V> {
    private String[] keys;
    private V[] values;

    private int capacity;
    private int size;

    private double maxLoadFactor;

    private boolean usePAF;
    private boolean useDoubleHashing;

    private static final String DELETED = "DELETED";

    private int collisionCount = 0;

    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity,double maxLoadFactor, boolean usePAF,boolean useDoubleHashing) {   
        this.capacity = initialCapacity;
        this.maxLoadFactor = maxLoadFactor;
        this.usePAF = usePAF;
        this.useDoubleHashing = useDoubleHashing;
        this.size = 0;
        this.keys = new String[capacity];
        this.values = (V[]) new Object[capacity];
    }

    public int getCollisionCount() {
        return collisionCount;
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    // PRIMARY HASH FUNCTION
    private int hash(String key) {
        long hashCode = 0;
        if (usePAF) {
            int z = 33;
            for (int i = 0; i < key.length(); i++) {
                int charVal = getCharacterValue(key.charAt(i));
                hashCode = (hashCode * z + charVal) % capacity;
            }
        } 
        else {
            for (int i = 0; i < key.length(); i++) {
                hashCode = (hashCode + key.charAt(i)) % capacity;
            }
        }
        return (int) hashCode;
    }

    // SECONDARY HASH FUNCTION
    private int getStepSize(String key, int q) {
        int hashCode = 0;
        if (usePAF) {
            int z = 33;
            for (int i = 0; i < key.length(); i++) {
                int charVal = getCharacterValue(key.charAt(i));
                hashCode = (hashCode * z + charVal) % q;
            }
        } 
        else {
            for (int i = 0; i < key.length(); i++) {
                hashCode = (hashCode + key.charAt(i)) % q;
            }
        }
        return q - hashCode;
    }

    // FIND LARGEST PRIME BELOW CAPACITY
    private int getLargestPrimeBelow(int n) {
        for (int i = n - 1; i >= 2; i--) {
            if (isPrime(i)) {
                return i;
            }
        }
        return -1;
    }

    // PROBE INDEX
    private int getProbeIndex(int originalIndex, int j, String key, int q) {
        if (useDoubleHashing) {
            int stepSize = getStepSize(key, q);
            return (int) ((originalIndex +
                    (long) j * stepSize) % capacity);
        }
        return (originalIndex + j) % capacity;
    }

    // PUT OPERATION
    public void put(String key, V value) {
        if (key == null || key.equals(DELETED)) {
            return;
        }
        int originalIndex = hash(key);
        int firstDeleted = -1;
        int insertionIndex = -1;
        int q = 0;
        if (useDoubleHashing) {
            q = getLargestPrimeBelow(capacity);
        }
        for (int j = 0; j < capacity; j++) {
            int index = getProbeIndex(originalIndex, j, key, q);
            if (keys[index] == null) {
                insertionIndex = (firstDeleted != -1)
                        ? firstDeleted : index;
                break;
            }
            if (keys[index].equals(DELETED)) {
                if (firstDeleted == -1) {
                    firstDeleted = index;
                }
            } 
            else if (keys[index].equals(key)) {
                values[index] = value;
                return;
            }
            else {
                collisionCount++;
            }
        }
        if (insertionIndex == -1 && firstDeleted != -1) {
            insertionIndex = firstDeleted;
        }
        if (insertionIndex == -1) {
            resize(capacity * 2);
            put(key, value);
            return;
        }
        double newLoadFactor = (double) (size + 1) / capacity;
        if (newLoadFactor >= maxLoadFactor) {
            resize(capacity * 2);
            put(key, value);
            return;
        }
        keys[insertionIndex] = key;
        values[insertionIndex] = value;

        size++;
    }

    // RESIZE OPERATION

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        newCapacity = getNextPrime(newCapacity);
        String[] oldKeys = keys;
        V[] oldValues = values;

        capacity = newCapacity;
        keys = new String[capacity];
        values = (V[]) new Object[capacity];
        size = 0;
        for (int i = 0; i < oldKeys.length; i++) {
            if (oldKeys[i] != null &&
                !oldKeys[i].equals(DELETED)) {

                putForResize(oldKeys[i], oldValues[i]);
            }
        }
    }

    // INSERT WITHOUT COUNTING COLLISIONS
    private void putForResize(String key, V value) {
        int originalIndex = hash(key);
        int q = 0;
        if (useDoubleHashing) {
            q = getLargestPrimeBelow(capacity);
        }
        for (int j = 0; j < capacity; j++) {
            int index = getProbeIndex(originalIndex, j, key, q);
            if (keys[index] == null) {
                keys[index] = key;
                values[index] = value;
                size++;
                return;
            }
        }
    }

    // GET OPERATION
    public V get(String key) {
        if (key == null) {
            return null;
        }
        int originalIndex = hash(key);
        int q = 0;
        if (useDoubleHashing) {
            q = getLargestPrimeBelow(capacity);
        }
        for (int j = 0; j < capacity; j++) {
            int index = getProbeIndex(originalIndex, j, key, q);
            if (keys[index] == null) {
                return null;
            }
            if (!keys[index].equals(DELETED) &&
                keys[index].equals(key)) {
                return values[index];
            }
        }
        return null;
    }

    // REMOVE OPERATION
    public V remove(String key) {

        if (key == null) {
            return null;
        }

        int originalIndex = hash(key);

        int q = 0;

        if (useDoubleHashing) {
            q = getLargestPrimeBelow(capacity);
        }
        for (int j = 0; j < capacity; j++) {
            int index = getProbeIndex(originalIndex, j, key, q);
            if (keys[index] == null) {
                return null;
            }
            if (!keys[index].equals(DELETED) &&
                keys[index].equals(key)) {
                V removedValue = values[index];
                keys[index] = DELETED;
                values[index] = null;
                size--;
                return removedValue;
            }
        }
        return null;
    }

    // GET ALL VALUES
    public List<V> getAllValues() {
        List<V> result = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            if (keys[i] != null &&
                !keys[i].equals(DELETED)) {

                result.add(values[i]);
            }
        }
        return result;
    }

    // CHARACTER VALUE FOR PAF
    private int getCharacterValue(char character) {
        char lowerCharacter = Character.toLowerCase(character);
        if (lowerCharacter >= 'a' && lowerCharacter <= 'z') {
            return lowerCharacter - 'a' + 1;
        }
        return (int) lowerCharacter;
    }

    // PRIME NUMBER CHECK
    private boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    // GET NEXT PRIME
    private int getNextPrime(int n) {
        int prime = n;
        while (!isPrime(prime)) {
            prime++;
        }
        return prime;
    }
}
