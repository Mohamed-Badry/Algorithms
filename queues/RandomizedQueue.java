import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] a;
    private int size = 0;
    
    private class RandomizedQueueIterator implements Iterator<Item> {
        private int i = size;
        
        public boolean hasNext() { return i > 0; }
        public Item next() { 
            int randindex = StdRandom.uniformInt(0, i--);
            Item next = a[randindex];
            if (next == null) throw new NoSuchElementException("No next element found");
            a[randindex] = a[i];
            a[i] = next;
            return next;
        }
    }
    
    // construct an empty randomized queue
    public RandomizedQueue() {
        a = (Item[]) new Object[1];
    }

    private void validate(Item item) {
        if (item == null) throw new IllegalArgumentException("null argument");
    }

    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            copy[i] = a[i];
        }
        a = copy;
    }


    // is the randomized queue empty?
    public boolean isEmpty() { return size == 0; }

    // return the number of items on the randomized queue
    public int size() { return size; }

    // add the item
    public void enqueue(Item item) {
        validate(item);
        if (size == a.length) resize(2 * a.length);
        a[size++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (size() == 0) throw new NoSuchElementException("The queue is empty"); // fix
        if (size > 0 && size == a.length/4) resize(a.length/2);
        int randindex = StdRandom.uniformInt(0, size);
        Item item = a[randindex];
        a[randindex] = a[--size];
        a[size] = item;
        a[size] = null;
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (size() == 0) throw new NoSuchElementException("The queue is empty"); // fix
        int randindex = StdRandom.uniformInt(0, size);
        return a[randindex];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> q = new RandomizedQueue<Integer>();

        StdOut.println(q.isEmpty());

        for (int i = 0; i < 50; i++) {
            q.enqueue(i);
        }

        for (Integer integer : q) {
            StdOut.printf("%d ", integer);
        }
        
        StdOut.printf("\nsize: %d \n", q.size());
        StdOut.println(q.sample());
        StdOut.println(q.sample());
        
        for (int i = 0; i < 10; i++) {
            q.dequeue();
        }
        
        StdOut.println(q.dequeue());
        StdOut.println(q.dequeue());
        
        for (Integer integer : q) {
            StdOut.printf("%d ", integer);
        }
        StdOut.println();
    }

}