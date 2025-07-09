import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;

import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Paths;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BoggleSolverTime {

    private static final int[][] NEIGHBOR_DIRECTIONS = {
            { -1, -1 }, { -1, 0 }, { -1, 1 },
            { 0, -1 }, { 0, 1 },
            { 1, -1 }, { 1, 0 }, { 1, 1 }
    };
    private final OptimizedTrie dict;
    private List<List<List<int[]>>> neighbors;

    // Initializes the data structure using the given array of strings as the
    // dictionary.
    // (You can assume each word in the dictionary contains only the uppercase
    // letters A through Z.)
    public BoggleSolverTime(String[] dictionary) {

        // possible memory optimisation by using a 26-way trie.
        dict = new OptimizedTrie();
        for (String word : dictionary) {
            dict.add(word);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        int height = board.rows();
        int width = board.cols();
        StringBuilder prefix = new StringBuilder();
        Set<String> wordsFound = new HashSet<String>();

        // precompute the neighboring cells for each cell
        neighbors = new ArrayList<>(height);

        for (int i = 0; i < height; i++) {
            List<List<int[]>> row = new ArrayList<>(width);
            for (int j = 0; j < width; j++) {
                List<int[]> cellNeighbors = new ArrayList<>();
                for (int[] dir : NEIGHBOR_DIRECTIONS) {

                    int newI = i + dir[0];
                    int newJ = j + dir[1];

                    // Check if the new indices are within the grid bounds
                    if (newI >= 0 && newI < height && newJ >= 0 && newJ < width) {
                        cellNeighbors.add(new int[] { newI, newJ });
                    }
                }
                row.add(cellNeighbors);
            }
            neighbors.add(row);
        }

        // apply dfs to each cell in the grid
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                for (String word : dfs(new int[] { i, j }, height, width, prefix, board)) {
                    wordsFound.add(word);
                }
            }
        }
        return wordsFound;

    }

    // Returns the score of the given word if it is in the dictionary, zero
    // otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {

        if (!dict.contains(word))
            return 0;

        int wordLength = word.length();

        if (wordLength >= 8)
            return 11;

        switch (wordLength) {
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default:
                return 0;
        }
    }

    private Iterable<String> dfs(int[] startCell, int height, int width, StringBuilder prefix, BoggleBoard board) {
        Set<String> wordsFound = new HashSet<String>();
        boolean[][] marked = new boolean[height][width];

        Stack<StackFrame> stack = new Stack<>();
        stack.push(new StackFrame(startCell, null));

        while (!stack.isEmpty()) {
            StackFrame frame = stack.peek();
            int[] cell = frame.cell;

            if (frame.neighbors == null) {
                // First time visiting this cell
                char letter = board.getLetter(cell[0], cell[1]);
                if (letter == 'Q') {
                    prefix.append("QU");
                } else {
                    prefix.append(letter);
                }
                marked[cell[0]][cell[1]] = true;

                // String word = prefix.toString();
                // Check if current prefix is a valid word
                if (prefix.length() >= 3 && dict.contains(prefix)) {
                    wordsFound.add(prefix.toString());
                }

                // Check if we should continue exploring
                if (!dict.hasPrefix(prefix)) {
                    backtrack(stack, prefix, marked);
                    continue;
                }

                // Initialize neighbors
                frame.neighbors = neighbors.get(cell[0]).get(cell[1]).iterator();
            }

            // Find next unvisited neighbor
            boolean foundUnvisited = false;
            while (frame.neighbors.hasNext()) {
                int[] neighbor = frame.neighbors.next();
                if (!marked[neighbor[0]][neighbor[1]]) {
                    stack.push(new StackFrame(neighbor, null));
                    foundUnvisited = true;
                    break;
                }
            }

            // If no unvisited neighbors, backtrack
            if (!foundUnvisited) {
                backtrack(stack, prefix, marked);
            }
        }

        return wordsFound;
    }

    private void backtrack(Stack<StackFrame> stack, StringBuilder prefix, boolean[][] marked) {
        StackFrame frame = stack.pop();
        int[] cell = frame.cell;
        marked[cell[0]][cell[1]] = false;

        int prefixLength = prefix.length();

        if (prefixLength >= 2) {
            // handle Qu special case
            char secondToLastLetter = prefix.charAt(prefixLength - 2);
            char lastLetter = prefix.charAt(prefixLength - 1);
            if (lastLetter == 'U' && secondToLastLetter == 'Q') {
                prefix.setLength(prefixLength - 2);
            } else {
                prefix.setLength(prefixLength - 1);
            }
        } else {
            prefix.setLength(prefixLength - 1);
        }
    }

    private static class StackFrame {
        int[] cell;
        Iterator<int[]> neighbors;

        StackFrame(int[] cell, Iterator<int[]> neighbors) {
            this.cell = Arrays.copyOf(cell, 2);
            this.neighbors = neighbors;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();

        BoggleSolverTime solver = new BoggleSolverTime(dictionary);

        Set<Integer> specialScores = new HashSet<>(
                Arrays.asList(1, 5, 10, 2000, 2500, 3000, 3500, 3999, 4000, 5000, 10000));

        long startTimeTotal = System.nanoTime();
        long boardCount = 0;

        while (true) {
            long startTimeBoard = System.nanoTime();

            BoggleBoard board = new BoggleBoard();
            int score = 0;
            for (String word : solver.getAllValidWords(board)) {
                score += solver.scoreOf(word);
            }

            long endTimeBoard = System.nanoTime();
            long durationBoard = TimeUnit.NANOSECONDS.toMicros(endTimeBoard -
                    startTimeBoard);
            boardCount++;

            System.out.printf("Board %d solved in %d microsecs. Score: %d%n", boardCount,
                    durationBoard, score);

            if (specialScores.contains(score)) {
                specialScores.remove(score);
                System.out.println(String.format("Found the board for score %d", score));

                try {
                    Files.writeString(Paths.get(String.format("boards/board-points%d.txt",
                            score)), board.toString());
                    System.out.println("Saved board successfully.");
                } catch (IOException e) {
                    System.err.println("An error occurred: " + e.getMessage());
                }
            }

            long currentTotalDuration = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()
                    - startTimeTotal);
            System.out.printf("Total time elapsed: %d seconds%n", currentTotalDuration);
            System.out.println("--------------------");

            // If all special scores have been found, you might want to break the loop
            if (specialScores.isEmpty()) {
                System.out.println("All special scores found. Exiting.");
                break;
            }
        }
    }

    // implement a 26-way trie for the dictionary
    // precompute the boggle graph
    // use depth-first search to enumerate all simple paths in the boggle graph
    // implement the backtracking optimisation that uses prefix search to eliminate
    // paths

}
