import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.MinPQ;

public class Solver {
    private boolean twinSolved;
    private Node finalNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }

        twinSolved = false;

        MinPQ<Node> mainPQ = new MinPQ<>(new NodeComparator());
        MinPQ<Node> twinPQ = new MinPQ<>(new NodeComparator());

        Node start = new Node(initial, 0, null);
        Node startTwin = new Node(initial.twin(), 0, null);

        mainPQ.insert(start);
        twinPQ.insert(startTwin);

        boolean ongoing = true;

        while (ongoing) {

            Node search = mainPQ.delMin();
            Node searchTwin = twinPQ.delMin();

            if (search.board.isGoal()) {
                ongoing = false;
                finalNode = search;
            }

            if (searchTwin.board.isGoal()) {
                twinSolved = true;
            }

            if (!ongoing || twinSolved) {
                break;
            }

            Node previousNode = search.previous;
            for (Board board : search.board.neighbors()) {
                if (previousNode == null) {
                    mainPQ.insert(new Node(board, 1, search));
                } else {
                    if (!board.equals(previousNode.board)) {
                        mainPQ.insert(new Node(board, search.moves + 1, search));
                    }
                }
            }

            Node previousNodeTwin = searchTwin.previous;
            for (Board boardTwin : searchTwin.board.neighbors()) {
                if (previousNodeTwin == null) {
                    twinPQ.insert(new Node(boardTwin, 1, searchTwin));
                } else {
                    if (!boardTwin.equals(previousNodeTwin.board)) {
                        twinPQ.insert(new Node(boardTwin, search.moves + 1, searchTwin));
                    }
                }
            }
        }
    }

    private class Node {
        Board board;
        int moves;
        Node previous;
        int manhattan;
        int priority;

        // Constructor for Node
        private Node(Board board, int moves, Node previous) {
            this.board = board;
            this.moves = moves;
            this.previous = previous;
            this.manhattan = board.manhattan();
            this.priority = this.manhattan + this.moves;
        }
    }

    private class NodeComparator implements Comparator<Node> {
        public int compare(Node n1, Node n2) {
            if (n1.priority > n2.priority)
                return 1;
            if (n1.priority < n2.priority)
                return -1;
            if (n1.manhattan < n2.manhattan)
                return 1;
            if (n1.manhattan > n2.manhattan)
                return -1;
            else
                return 0;
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        if (twinSolved) {
            return false;
        }
        return true;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!this.isSolvable()) {
            return -1;
        }
        int solveMoves = ((List<Board>) this.solution()).size();
        return solveMoves - 1;

    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!this.isSolvable()) {
            return null;
        }

        List<Board> path = new ArrayList<>();
        Node currentNode = finalNode;

        while (currentNode != null) {
            path.add(currentNode.board);
            currentNode = currentNode.previous;
        }

        Collections.reverse(path);

        return path;
    }

    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}