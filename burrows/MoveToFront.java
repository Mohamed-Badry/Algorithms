import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static final int R = 256; // alphabet size of extended ASCII
    private static final int LG_R = 8;

    // apply move-to-front encoding, reading from standard input and writing to
    // standard output
    public static void encode() {
        char[] alphabet = new char[R];
        populateAlphabet(alphabet);

        // read the input
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar(LG_R);

            for (int i = 0; i < R; i++) {
                if (c == alphabet[i]) {
                    BinaryStdOut.write(i, LG_R);

                    // move char at i to front and shift whole alphabet
                    moveToFront(i, alphabet);
                    break;
                }
            }
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to
    // standard output
    public static void decode() {

        char[] alphabet = new char[R];
        populateAlphabet(alphabet);

        // read the input
        while (!BinaryStdIn.isEmpty()) {
            int i = BinaryStdIn.readChar(LG_R);
            BinaryStdOut.write(alphabet[i], LG_R);

            // move char at i to front and shift whole alphabet
            moveToFront(i, alphabet);
        }
        BinaryStdOut.close();
    }

    private static void populateAlphabet(char[] alphabet) {
        for (int i = 0; i < R; i++) {
            alphabet[i] = (char) i;
        }
    }

    // move char at i to front and shift whole alphabet to the right
    private static void moveToFront(int i, char[] alphabet) {
        for (int j = i; j > 0; j--) {
            char temp = alphabet[j];
            alphabet[j] = alphabet[j - 1];
            alphabet[j - 1] = temp;
        }
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {

        if (args[0].equals("-")) {
            encode();
        } else if (args[0].equals("+")) {
            decode();
        } else {
            throw new IllegalArgumentException("Illegal command line argument");
        }
    }

}