import java.security.Key;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinearProbing<K, V> {
    private static final int INIT_CAPACITY = 16;
    private double loadFactor = 0.5;
    private int threshold = (int) (INIT_CAPACITY * loadFactor);
    private int countOfItems = 0;
    private int capacity = INIT_CAPACITY;
    private K[] key = (K[]) new Object[capacity];
    private V[] value = (V[]) new Object[capacity];

    public LinearProbing() { }

    private int hash(K key) {
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    public void put(K key, V value) {
        if (countOfItems >= threshold) resize(2 * capacity);
        int i = hash(key);
        while (this.key[i] != null) {
            if (this.key[i].equals(key)) {
                this.value[i] = value;
                return;
            }
            i = (i + 1) % capacity;
        }
        this.key[i] = key;
        this.value[i] = value;
        countOfItems++;
    }

    public V get(K key) {
        int i = hash(key);
        while (this.key[i] != null) {
            if (this.key[i].equals(key)) return value[i];
            i = (i + 1) % capacity;
        }
        return null;
    }

    public void delete(K key) {
        int i = hash(key);
        while (!key.equals(this.key[i])) i = (i + 1) % capacity;
        this.key[i] = null;
        this.value[i] = null;
        i = (i + 1) % capacity;
        while (this.key[i] != null) {
            K rehashKey = this.key[i];
            V rehashValue = this.value[i];
            this.key[i] = null;
            this.value[i] = null;
            countOfItems--;
            put(rehashKey, rehashValue);
            i = (i + 1) % capacity;
        }
        countOfItems--;
        if (countOfItems > 0 && countOfItems <= capacity / 8) resize(capacity / 2);
    }

    private void resize(int newCapacity) {
        LinearProbing<K, V> temp = new LinearProbing<>();
        temp.capacity = newCapacity;
        temp.key = (K[]) new Object[newCapacity];
        temp.value = (V[]) new Object[newCapacity];
        temp.threshold = (int) (newCapacity * loadFactor);
        for (int i = 0; i < capacity; i++) {
            if (key[i] != null) temp.put(key[i], value[i]);
        }
        this.key = temp.key;
        this.value = temp.value;
        this.capacity = temp.capacity;
        this.threshold = temp.threshold;
    }

    public Iterable<K> keys() {
        return new Iterable<K>() {
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    private int i = 0;

                    public boolean hasNext() {
                        while (i < capacity && key[i] == null) i++;
                        return i < capacity;
                    }

                    public K next() {
                        return key[i++];
                    }
                };
            }
        };
    }
}
