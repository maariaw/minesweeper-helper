
package minesweeper.structures;

public class MyList<Type> {
    private Type[] elements;
    private int next;
    
    public MyList() {
        this.elements = (Type[]) new Object[10];
        this.next = 0;
    }
    
    public void add(Type item) {
        if (next == elements.length) {
            grow();
        }
        elements[next] = item;
        next++;
    }

    public Type[] getElements() {
        return elements;
    }

    public int size() {
        return next;
    }
    
    public Type get(int index) {
        return elements[index];
    }

    private void grow() {
        Type[] bigger = (Type[]) new Object[elements.length * 2];
        for (int i = 0; i < next; i++) {
            bigger[i] = elements[i];
        }
        this.elements = bigger;
    }

    public void remove(int index) {
        next--;
        for (int i = index; i < next; i++) {
            elements[i] = elements[i + 1];
        }
    }

    public boolean contains(Type element) {
        for (int i = 0; i < next; i++) {
            if (elements[i].equals(element)) {
                return true;
            }
        }
        return false;
    }
    
    public Type[] getContent() {
        Type[] content = (Type[]) new Object[next];
        for (int i = 0; i < next; i++) {
            content[i] = elements[i];
        }
        return content;
    }
}
