public class CircularSuffixArray {

    private final int length;
    private final int[] index;

    // Helper class to represent a circular suffix
    private class CircularSuffix implements Comparable<CircularSuffix> {
        private final String s;
        private final int start;

        public CircularSuffix(String s, int start) {
            this.s = s;
            this.start = start;
        }

        // Compare two circular suffixes lexicographically
        @Override
        public int compareTo(CircularSuffix that) {
            if (this == that)
                return 0; // Same object
            int n = s.length();
            for (int i = 0; i < n; i++) {
                char thisChar = s.charAt((this.start + i) % n);
                char thatChar = that.s.charAt((that.start + i) % n);
                if (thisChar < thatChar)
                    return -1;
                if (thisChar > thatChar)
                    return 1;
            }
            return 0;
        }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Constructor argument can't be null.");
        }

        length = s.length();
        index = new int[length];

        // Create an array of circular suffixes
        CircularSuffix[] suffixes = new CircularSuffix[length];
        for (int i = 0; i < length; i++) {
            suffixes[i] = new CircularSuffix(s, i);
        }

        // Sort the circular suffixes
        java.util.Arrays.sort(suffixes);

        // Populate the index array
        for (int i = 0; i < length; i++) {
            index[i] = suffixes[i].start;
        }
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length) {
            throw new IllegalArgumentException("Argument is outside its prescribed range.");
        }
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = edu.princeton.cs.algs4.BinaryStdIn.readString();

        CircularSuffixArray suffixArray = new CircularSuffixArray(s);

        edu.princeton.cs.algs4.BinaryStdOut.write(suffixArray.length());
        for (int i = 0; i < s.length(); i++) {
            edu.princeton.cs.algs4.BinaryStdOut.write(String.format("index[%d] = %d\n", i, suffixArray.index(i)));
        }
        edu.princeton.cs.algs4.BinaryStdOut.close();
    }
}
