import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdOut;

public class Deque<Item> implements Iterable<Item> {
    private Item[] a;
    private int size = 0;
    private int head = 0;
    private int tail = 0;
    
    private class ReversedArrayIterator implements Iterator<Item> {
        private int i = tail;
        
        public boolean hasNext() { return head < i; }
        public void remove() { throw new UnsupportedOperationException("remove isn\'t supported"); }
        public Item next() { 
            Item next = a[--i];
            if (next == null) throw new NoSuchElementException("No next element found");
            return next; 
        }
    }
    
    // construct an empty deque
    public Deque() 
    { a = (Item[]) new Object[1]; }
    
    private void validate(Item item) {
        if (item == null) throw new IllegalArgumentException("null argument");
    }

    private void resize() {
        int capacity = 2 * a.length;
        Item[] copy = (Item[]) new Object[capacity];
        int shift = capacity/4;
        
        for (int i = head; i < tail+1; i++) {
            copy[i + shift] = a[i];  
        }
        head += shift;
        tail = head + size - 1;
        a = copy;
    }

    // is the deque empty?
    public boolean isEmpty() { return size == 0; }
    
    // return the number of items on the deque
    public int size() { return size; }
    
    // add the item to the front
    public void addLast(Item item) {
        validate(item);
        size++;
        if (head == 0 || tail == a.length - 1) resize();
        if (head == 0 && size == 1) head++;
        a[--head] = item;
    }
    // add the item to the back
    public void addFirst(Item item) {
        validate(item);
        size++;
        if (head == 0 || tail == a.length - 1) {
            resize();
        }
        a[tail++] = item;
    }
    
    // remove and return the item from the front
    public Item removeLast() {
        Item last = a[head];
        a[head++] = null;
        size--;
        return last;
    }

    // remove and return the item from the back
    public Item removeFirst() {
        Item first = a[--tail];
        a[tail] = null;
        size--;
        return first;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new ReversedArrayIterator();
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> d = new Deque<Integer>();
        
        StdOut.println(d.isEmpty());

        for (int i = 0; i < 6; i++) {
            d.addFirst(i);
            d.addLast(i*2);
        }

        StdOut.println(d.isEmpty());
        for (Integer integer : d) {
            StdOut.printf("%d ", integer);            
        }
        StdOut.println();
        StdOut.printf("\nremoved %d\n", d.removeFirst());
        for (Integer integer : d) {
            StdOut.printf("%d ", integer);
        }

        StdOut.printf("\nremoved %d\n", d.removeFirst());
        for (Integer integer : d) {
            StdOut.printf("%d ", integer);
        }
        System.out.println();

        StdOut.println(d.size());

        StdOut.printf("\nremoved %d\n", d.removeLast());

        for (Integer integer : d) {
            StdOut.printf("%d ", integer);
        }

        for (int i = 0; i <= 8; i++) {
            d.addLast(i);
        }
        StdOut.println();

        // d.Print();

        for (Integer integer : d) {
            StdOut.printf("%d ", integer);
        }

        System.out.println();

        d.removeFirst();
        d.removeFirst();
        d.removeLast();
        d.removeLast();
        d.removeFirst();

        for (Integer integer : d) {
            System.out.printf("%d ", integer);
        }

        System.out.println();
    }

}
