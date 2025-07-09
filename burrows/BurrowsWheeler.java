import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    private static final int R = 256;
    private static final int LG_R = 8;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {

        int first = 0;
        String s = BinaryStdIn.readString();
        CircularSuffixArray suffixArray = new CircularSuffixArray(s);
        int n = s.length();

        // t[] is the last column in the sorted suffixes array
        char[] t = new char[n];
        for (int i = 0; i < n; i++) {
            int index = suffixArray.index(i) - 1;
            if (index == -1) {
                // first = i where index[i] == 0
                first = i;
                index = n - 1;
            }
            t[i] = s.charAt(index);
        }

        BinaryStdOut.write(first, 32);
        for (int i = 0; i < t.length; i++) {
            BinaryStdOut.write(t[i], LG_R);
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt(32);
        char[] t = BinaryStdIn.readString().toCharArray();

        // construct next[] array and populate it
        int[] next = new int[t.length];
        populateNextArray(next, t);

        // write the inverseTransform to BinaryStdOut
        int index = first;
        for (int i = 0; i < next.length; i++) {
            index = next[index];
            BinaryStdOut.write(t[index], LG_R);
        }
        BinaryStdOut.close();
    }

    private static void populateNextArray(int[] next, char[] t) {
        // Key-indexed counting
        int[] tCounts = new int[R + 1];

        // compute frequency counts
        for (int i = 0; i < t.length; i++) {
            tCounts[t[i] + 1]++;
        }

        // transform counts to indices
        for (int i = 0; i < R; i++) {
            tCounts[i + 1] += tCounts[i];
        }

        for (int i = 0; i < t.length; i++) {
            next[tCounts[t[i]]++] = i;
        }
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            transform();
        } else if (args[0].equals("+")) {
            inverseTransform();
        } else {
            throw new IllegalArgumentException("Illegal command line argument");
        }
    }

}