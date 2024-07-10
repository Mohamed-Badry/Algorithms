import java.util.ArrayList;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.DirectedCycle;

public class WordNet {
    private Digraph wordnetDG;
    private ST<String, ArrayList<Integer>> synsetST;
    private SAP wordnetSap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("Constructor argument can't be null");
        }
        In inSyn = new In(synsets);
        In inHyp = new In(hypernyms);
        int synSize = 0;

        synsetST = new ST<>();
        while (!inSyn.isEmpty()) {
            synSize++;
            String[] line = inSyn.readLine().split(",");

            int synId = Integer.parseInt(line[0]);
            String[] synset = line[1].split(" ");

            for (String word : synset) {
                if (!synsetST.contains(word)) {
                    synsetST.put(word, new ArrayList<Integer>());
                }
                synsetST.get(word).add(synId);
            }
        }

        wordnetDG = new Digraph(synSize);
        while (!inHyp.isEmpty()) {
            String[] line = inHyp.readLine().split(",", 2);
            int hypId = Integer.parseInt(line[0]);

            if (line.length > 1) {
                for (String hypnym : line[1].split(",")) {
                    wordnetDG.addEdge(hypId, Integer.parseInt(hypnym));
                }
            }
        }

        wordnetSap = new SAP(wordnetDG);

        // add exception for non rootedness
        // if graph is not a rooted DAG throw exception
        DirectedCycle cycle = new DirectedCycle(wordnetDG);
        if (cycle.hasCycle()) {
            throw new IllegalArgumentException("Digraph can't have a cycle");
        }

    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return synsetST.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return synsetST.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Input is not a WordNet noun");
        }
        return wordnetSap.length(synsetST.get(nounA), synsetST.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA
    // and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Input is not a WordNet noun");
        }
        int ancestorID = wordnetSap.ancestor(synsetST.get(nounA), synsetST.get(nounB));
        StringBuilder synsetBuilder = new StringBuilder("");

        for (String word : synsetST.keys()) {
            ArrayList<Integer> synsetIDs = synsetST.get(word);
            for (int synsetID : synsetIDs) {
                if (synsetID == ancestorID) {
                    synsetBuilder.append(String.format("%s ", word));
                }
            }
        }
        return synsetBuilder.toString();
    }

    // do unit testing of this class
    public static void main(String[] args) {

        WordNet wordNet = new WordNet(args[0], args[1]);

        do {
            System.out
                    .println("1: is word a noun?\n2: distance between nounA and nounB\n3: sap between nounA and nounB");

            int choice = StdIn.readInt();
            if (choice == 1) {
                System.out.println(wordNet.isNoun(StdIn.readString()));
            } else if (choice == 2) {
                System.out.println(wordNet.distance(StdIn.readString(), StdIn.readString()));
            } else if (choice == 3) {
                System.out.println(wordNet.sap(StdIn.readString(), StdIn.readString()));
            }

        } while (!StdIn.isEmpty());
    }
}