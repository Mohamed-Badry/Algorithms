
/******************************************************************************
*  Compilation:  javac TrieSET26.java
*  Execution:    java TrieSET26 < words.txt
*  Dependencies: StdIn.java
*  Data files:   https://algs4.cs.princeton.edu/52trie/shellsST.txt
*
*  A set for A-Z strings, implemented  using a 26-way trie.
*
*  Sample client reads in a list of words from standard input and
*  prints out each word, removing any duplicates.
*
******************************************************************************/
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
// import java.util.LinkedList;

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * The {@code TrieSET26} class represents an ordered set of strings over
 * the A-Z alphabet.
 * It supports the usual <em>add</em>, <em>contains</em>, and <em>delete</em>
 * methods. It also provides character-based methods for finding the string
 * in the set that is the <em>longest prefix</em> of a given prefix,
 * finding all strings in the set that <em>start with</em> a given prefix,
 * and finding all strings in the set that <em>match</em> a given pattern.
 * <p>
 * This implementation uses a 26-way trie.
 * The <em>add</em>, <em>contains</em>, <em>delete</em>, and
 * <em>longest prefix</em> methods take time proportional to the length
 * of the key (in the worst case). Construction takes constant time.
 * <p>
 * For additional documentation, see
 * <a href="https://algs4.cs.princeton.edu/52trie">Section 5.2</a> of
 * <i>Algorithms in Java, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class TrieSET26 implements Iterable<String> {
    private static final int R = 26; // A-Z capital letters

    private Node root; // root of trie
    private int n; // number of keys in trie

    // R-way trie node
    private static class Node {
        boolean isString;
        Node[] next = new Node[R];
    }

    /**
     * Initializes an empty set of strings.
     */
    public TrieSET26() {
    }

    /**
     * Does the set contain the given key?
     * 
     * @param key the key
     * @return {@code true} if the set contains {@code key} and
     *         {@code false} otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public boolean contains(String key) {
        if (key == null)
            throw new IllegalArgumentException("argument to contains() is null");
        Node x = get(root, key, 0);
        if (x == null)
            return false;
        return x.isString;
    }

    private Node get(Node x, String prefix, int d) {
        if (x == null)
            return null;
        if (d == prefix.length())
            return x;
        int c = prefix.charAt(d) - 'A';
        return get(x.next[c], prefix, d + 1);
    }

    /**
     * Adds the key to the set if it is not already present.
     * 
     * @param key the key to add
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void add(String key) {
        if (key == null)
            throw new IllegalArgumentException("argument to add() is null");
        root = add(root, key, 0);
    }

    private Node add(Node x, String key, int d) {
        if (x == null)
            x = new Node();
        if (d == key.length()) {
            if (!x.isString)
                n++;
            x.isString = true;
        } else {
            char c = key.charAt(d);
            int pos = (int) c - 'A';
            x.next[pos] = add(x.next[pos], key, d + 1);
        }
        return x;
    }

    /**
     * Returns the number of strings in the set.
     * 
     * @return the number of strings in the set
     */
    public int size() {
        return n;
    }

    /**
     * Is the set empty?
     * 
     * @return {@code true} if the set is empty, and {@code false} otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns all of the keys in the set that start with {@code prefix}.
     * 
     * @param prefix the prefix
     * @return all of the keys in the set that start with {@code prefix},
     *         as an iterable
     */
    public Iterable<String> keysWithPrefix(String prefix) {
        Queue<String> results = new Queue<>();
        Node x = get(root, prefix, 0);
        collectNonRecursive(x, new StringBuilder(prefix), results);
        return results;
    }

    /**
     * Returns an iterator over the strings in this set.
     *
     * @return an iterator over the strings in this set
     */
    @Override
    public Iterator<String> iterator() {
        return keysWithPrefix("").iterator();
    }

    private void collectNonRecursive(Node x, StringBuilder prefix, Queue<String> results) {
        if (x == null)
            return;

        Deque<Object[]> stack = new ArrayDeque<>();
        stack.push(new Object[] { x, prefix, 0 });

        while (!stack.isEmpty()) {
            Object[] current = stack.pop();
            Node node = (Node) current[0];
            StringBuilder sb = (StringBuilder) current[1];
            int pos = (int) current[2];

            if (node.isString) {
                results.enqueue(sb.toString());
            }

            while (pos < R) {
                if (node.next[pos] != null) {
                    char c = (char) (pos + 'A');
                    StringBuilder newSb = new StringBuilder(sb).append(c);
                    stack.push(new Object[] { node, sb, pos + 1 });
                    stack.push(new Object[] { node.next[pos], newSb, 0 });
                    break;
                }
                pos++;
            }
        }
    }

    /**
     * Returns the string in the set that is the longest prefix of {@code query},
     * or {@code null}, if no such string.
     * 
     * @param query the query string
     * @return the string in the set that is the longest prefix of {@code query},
     *         or {@code null} if no such string
     * @throws IllegalArgumentException if {@code query} is {@code null}
     */
    public String longestPrefixOf(String query) {
        if (query == null)
            throw new IllegalArgumentException("argument to longestPrefixOf() is null");
        int length = longestPrefixOf(root, query, 0, -1);
        if (length == -1)
            return null;
        return query.substring(0, length);
    }

    // returns the length of the longest string key in the subtrie
    // rooted at x that is a prefix of the query string,
    // assuming the first d character match and we have already
    // found a prefix match of the specified length
    private int longestPrefixOf(Node x, String query, int d, int length) {
        if (x == null)
            return length;
        if (x.isString)
            length = d;
        if (d == query.length())
            return length;
        char c = query.charAt(d);
        int pos = (int) c - 'A';
        return longestPrefixOf(x.next[pos], query, d + 1, length);
    }

    /**
     * Removes the key from the set if the key is present.
     * 
     * @param key the key
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void delete(String key) {
        if (key == null)
            throw new IllegalArgumentException("argument to delete() is null");
        root = delete(root, key, 0);
    }

    private Node delete(Node x, String key, int d) {
        if (x == null)
            return null;
        if (d == key.length()) {
            if (x.isString)
                n--;
            x.isString = false;
        } else {
            char c = key.charAt(d);
            int pos = (int) c - 'A';
            x.next[pos] = delete(x.next[pos], key, d + 1);
        }

        // remove subtrie rooted at x if it is completely empty
        if (x.isString)
            return x;
        for (int pos = 0; pos < R; pos++)
            if (x.next[pos] != null)
                return x;
        return null;
    }

    /**
     * Unit tests the {@code TrieSET26} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        TrieSET26 set = new TrieSET26();
        while (!StdIn.isEmpty()) {
            String key = StdIn.readString();
            set.add(key);
        }

        // print results
        if (set.size() < 100) {
            StdOut.println("keys(\"\"):");
            for (String key : set) {
                StdOut.println(key);
            }
            StdOut.println();
        }

        StdOut.println("longestPrefixOf(\"shellsort\"):");
        StdOut.println(set.longestPrefixOf("shellsort"));
        StdOut.println();

        StdOut.println("longestPrefixOf(\"xshellsort\"):");
        StdOut.println(set.longestPrefixOf("xshellsort"));
        StdOut.println();

        StdOut.println("keysWithPrefix(\"shor\"):");
        for (String s : set.keysWithPrefix("shor"))
            StdOut.println(s);
        StdOut.println();

        StdOut.println("keysWithPrefix(\"shortening\"):");
        for (String s : set.keysWithPrefix("shortening"))
            StdOut.println(s);
        StdOut.println();

    }
}

/******************************************************************************
 * Copyright 2002-2022, Robert Sedgewick and Kevin Wayne.
 *
 * This file is part of algs4.jar, which accompanies the textbook
 *
 * Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 * Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 * http://algs4.cs.princeton.edu
 *
 *
 * algs4.jar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * algs4.jar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with algs4.jar. If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
