
package minesweeper.structures;

public class MyCouple<K, V> {
    private K key;
    private V value;

    public MyCouple(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
