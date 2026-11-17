package homework;
import java.util.List;
import java.util.ArrayList;

public class HashTable<V> {
	private String[] keys;
	private V[] values;
    private  int capacity;
    private  int size;
    private double maxLoadFactor;
    private boolean usePAF;        
    private boolean useDoubleHashing;
    private static final String DELETED = "DELETED";
    private int collisionCount=0;
    
    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity, double maxLoadFactor, boolean usePAF, boolean useDoubleHashing) {
    	this.capacity=initialCapacity;
    	this.maxLoadFactor= maxLoadFactor;
    	this.usePAF = usePAF;
        this.useDoubleHashing = useDoubleHashing;
        this.size = 0;
        this.keys = new String[capacity];
        this.values = (V[]) new Object[capacity];
    }
    public int getCollisionCount() {
        return collisionCount;
    }
    
    private int hash(String key) {
    	int hashCode = 0;
    	if(usePAF) {
    		int z = 33;
    		for (int i = 0; i < key.length(); i++) {
    			int charVal = key.charAt(i) - 'a' + 1;
                if (charVal < 1) charVal = 1;
                hashCode = (hashCode * z + charVal) % capacity;
            }
    	}
    	else {
    	   for (int i = 0; i < key.length(); i++) {
    	        hashCode += key.charAt(i);
    	   }
        }
    	int index = hashCode % capacity;
    	if (index < 0) {
            index += capacity;
        }
        
        return index;
    }
    
    private int getStepSize(String key, int q) {

        int hashCode = 0;

        if (usePAF) {
            int z = 33;

            for (int i = 0; i < key.length(); i++) {
                int charVal = key.charAt(i) - 'a' + 1;

                if (charVal < 1) {
                    charVal = 1;
                }

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
    
    //PUT OPERATİON
    @SuppressWarnings("unchecked")
    public void put(String key, V document) {
        if (key == null) {
            return;
        }

        int index = hash(key);
        int originalIndex = index;
        int j = 0;
        int firstDeleted = -1;

        int q = 7;
        if (q >= capacity) {
            q = 3;
        }

        while (j < capacity) {

            if (keys[index] == null) {
                break;
            }

            if (keys[index].equals(DELETED)) {
                if (firstDeleted == -1) {
                    firstDeleted = index;
                }
            }
            else if (keys[index].equals(key)) {
                values[index] = document;
                return;
            }
            else {
                collisionCount++;
            }

            j++;

            if (useDoubleHashing) {
                int d_k = getStepSize(key, q);
                index = (originalIndex + j * d_k) % capacity;
            }
            else {
                index = (originalIndex + j) % capacity;
            }
        }

        if (firstDeleted != -1) {
            index = firstDeleted;
        }

        else if (j >= capacity || keys[index] != null) {
            resize(capacity * 2);
            put(key, document);
            return;
        }

        double newLoadFactor = (double) (size + 1) / capacity;

        if (newLoadFactor >= maxLoadFactor) {
            resize(capacity * 2);
            put(key, document);
            return;
        }

        keys[index] = key;
        values[index] = document;
        size++;
    }
    
    //RESİZE OPERATİON
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

            if (oldKeys[i] != null && !oldKeys[i].equals(DELETED)) {
                putForResize(oldKeys[i], oldValues[i]);
            }
        }
    }    		
    private void putForResize(String key, V value) {

        int index = hash(key);
        int originalIndex = index;
        int j = 0;

        int q = 7;
        if (q >= capacity) {
            q = 3;
        }

        while (j < capacity && keys[index] != null) {

            j++;

            if (useDoubleHashing) {
                int d_k = getStepSize(key, q);
                index = (originalIndex + j * d_k) % capacity;
            }
            else {
                index = (originalIndex + j) % capacity;
            }
        }

        if (j >= capacity) {
            return;
        }

        keys[index] = key;
        values[index] = value;
        size++;
    }
    
    public V get(String key) {

        if (key == null) {
            return null;
        }

        int index = hash(key);
        int originalIndex = index;
        int j = 0;

        int q = 7;
        if (q >= capacity) {
            q = 3;
        }

        while (j < capacity && keys[index] != null) {

            if (!keys[index].equals(DELETED) && keys[index].equals(key)) {
                return values[index];
            }

            j++;

            if (useDoubleHashing) {
                int d_k = getStepSize(key, q);
                index = (originalIndex + j * d_k) % capacity;
            }
            else {
                index = (originalIndex + j) % capacity;
            }
        }

        return null;
    }
    
    //REMOVE OPERATİON
    public V remove(String key) {
        if (key == null) return null;

        int index = hash(key);
        int originalIndex = index;
        int j = 0;
        //Doble Hasing
        int q = 7;
        if (q >= capacity) q = 3;

        while (keys[index] != null) {
            if (!keys[index].equals(DELETED) && keys[index].equals(key)) {
            	V removedValue = values[index];
                
                keys[index] = DELETED; 
                values[index] = null; 
                size--;                
                return removedValue;
            }

            j++;
            if (j >= capacity) {
                break;
            }

            if (useDoubleHashing) {
            	int d_k = getStepSize(key, q);
            	index = (originalIndex + (j * d_k)) % capacity;
            } 
            else {
                index = (originalIndex + j) % capacity;
            }
        }

        return null; 
    }
    
    private boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    private int getNextPrime(int n) {
        int prime = n;
        while (!isPrime(prime)) {
            prime++;
        }
        return prime;
    }
    
}