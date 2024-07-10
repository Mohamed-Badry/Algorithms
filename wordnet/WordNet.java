import java.util.ArrayList;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.DirectedCycle;

public class WordNet {
    private final ST<String, ArrayList<Integer>> synsetST;
    private final SAP wordnetSap;

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

        Digraph wordnetDG = new Digraph(synSize);
        while (!inHyp.isEmpty()) {
            String[] line = inHyp.readLine().split(",", 2);
            int hypId = Integer.parseInt(line[0]);

            if (line.length > 1) {
                for (String hypnym : line[1].split(",")) {
                    wordnetDG.addEdge(hypId, Integer.parseInt(hypnym));
                }
            }
        }

        // if graph is not a rooted DAG throw exception
        if (!isDAG(wordnetDG)) {
            throw new IllegalArgumentException("Digraph isn't a rooted DAG");
        }

        wordnetSap = new SAP(wordnetDG);
    }

    // private boolean isRootedDAG(Digraph graph) {
    // if (!isDAG(graph)) {
    // return false;
    // }

    // // check for one rootedness
    // int rootCount = 0;
    // int lastRoot = -1;
    // for (int i = 0; i < graph.V(); i++) {
    // // System.out.printf("%d: %d\n", i, graph.indegree(i));
    // if (graph.indegree(i) == 0) {
    // rootCount++;
    // lastRoot = i;
    // System.out.println(lastRoot);
    // if (rootCount > 1) {
    // return false;
    // }
    // }
    // }

    // // check for reachability of every node from root
    // DepthFirstDirectedPaths dfs = new DepthFirstDirectedPaths(graph, lastRoot);
    // for (int i = 0; i < graph.V(); i++) {
    // if (!dfs.hasPathTo(i)) {
    // return false;
    // }
    // }

    // return true;
    // }

    private boolean isDAG(Digraph graph) {
        DirectedCycle cycle = new DirectedCycle(graph);
        return !cycle.hasCycle();
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
        StringBuilder synsetBuilder = new StringBuilder();

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

        System.out.println("Enter pairs of nouns (one pair per line).");

        while (!StdIn.isEmpty()) {
            String nounA = StdIn.readString();
            String nounB = StdIn.readString();

            if (!wordNet.isNoun(nounA) || !wordNet.isNoun(nounB)) {
                if (!wordNet.isNoun(nounA)) {
                    System.out.printf("%s is not a wordnet noun.\n", nounA);
                }
                if (!wordNet.isNoun(nounB)) {
                    System.out.printf("%s is not a wordnet noun.\n", nounB);
                }
            } else {
                System.out.printf("length: %d\t\tancestor: %s \n", wordNet.distance(nounA, nounB),
                        wordNet.sap(nounA, nounB));
            }

            System.out.println("\nEnter the next pairs of nouns (one pair per line).");
        }

    }
}