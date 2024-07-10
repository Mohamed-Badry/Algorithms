import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;

public class Board {

    private int size;
    private int[][] board;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        size = tiles.length;
        board = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = tiles[i][j];
            }
        }
    }

    // helper function that returns the goal of a cell
    private int cellGoal(int row, int col) {
        int val;

        val = row * size + (col + 1);
        if (val == (int) Math.pow(size, 2))
            return 0;
        return val;
    }

    // x position of goal cell
    private int goalx(int goal) {
        if (goal == 0)
            return size - 1;
        int x = (goal - 1) / size;
        return x;
    }

    // y position of goal cell
    private int goaly(int goal) {
        if (goal == 0)
            return size - 1;
        int y = (goal - 1) % (size);
        return y;
    }

    // checks if array has 0
    private boolean containsZero(int[] array) {
        for (int element : array) {
            if (element == 0) {
                return true;
            }
        }
        return false;
    }

    // string representation of this board
    public String toString() {

        StringBuilder s = new StringBuilder();
        s.append(size);
        for (int i = 0; i < size; i++) {
            s.append("\n");
            for (int j = 0; j < size; j++) {
                s.append(String.format("%2s ", board[i][j]));
            }
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return size;
    }

    // number of tiles out of place
    public int hamming() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != cellGoal(i, j) && board[i][j] != 0)
                    count++;
            }
        }
        return count;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int distance = 0;
        for (int i = 0; i < size; i++) {
            // System.out.println();
            for (int j = 0; j < size; j++) {
                if (board[i][j] != 0) {
                    distance += Math.abs(i - goalx(board[i][j])); // add horizontal distance
                    distance += Math.abs(j - goaly(board[i][j])); // add vertical distance
                }
                // System.out.printf(String.format("pos (%d,%d) tile %d: %d \t", i, j,
                // board[i][j], distance));
            }
        }
        // System.out.println();
        return distance;

    }

    // is this board the goal board?
    public boolean isGoal() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != cellGoal(i, j)) {
                    // System.out.printf("cell: %d != goal %d ", board[i][j], cellGoal(j, i));
                    return false;
                }
            }
        }
        return true;
    }

    // does this board equal y?
    @Override
    public boolean equals(Object y) {
        // Check if the object is compared with itself
        if (this == y) {
            return true;
        }

        // Check if the object is null or if it's not an instance of the Board class
        if (y == null || this.getClass() != y.getClass()) {
            return false;
        }

        // Cast the object to Board and compare the fields
        Board other = (Board) y;

        // Check if the size of the boards is the same
        if (size != other.size) {
            return false;
        }

        // Compare the board arrays
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != other.board[i][j]) {
                    return false;
                }
            }
        }

        // If all fields are equal, return true
        return true;
    }

    // returns a neighbor board with the 0 switching places with the
    // cell at (row, col)
    private Board neighbor(int[][] grid, int row, int col) {
        int[][] boardCopy = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                boardCopy[i][j] = grid[i][j];
                if (grid[i][j] == 0) {
                    boardCopy[i][j] = grid[row][col];
                }
            }
        }
        boardCopy[row][col] = 0;

        Board neighBoard = new Board(boardCopy);
        return neighBoard;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {

        Stack<Board> boards = new Stack<>();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == 0) {
                    // top neighbour
                    if (row > 0) {
                        boards.push(neighbor(board, row - 1, col));
                    }

                    // bottom neighbour
                    if (row < size - 1) {
                        boards.push(neighbor(board, row + 1, col));
                    }

                    // right neighbour
                    if (col < size - 1) {
                        boards.push(neighbor(board, row, col + 1));
                    }

                    // left neighbour
                    if (col > 0) {
                        boards.push(neighbor(board, row, col - 1));
                    }
                    break;
                }
            }
        }
        return boards;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {

        int[][] twin = new int[size][];
        boolean twinned = false;

        for (int i = 0; i < size; i++) {
            twin[i] = board[i].clone();

            // if the array doesn't contain a 0 and no twins were created
            // switch the first and last elements of the array
            if (!containsZero(twin[i]) && !twinned) {
                twin[i][size - 1] = board[i][0];
                twin[i][0] = board[i][size - 1];
                twinned = true;
            }
        }

        Board twinBoard = new Board(twin);
        return twinBoard;

    }

    // unit testing (not graded)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        System.out.println(initial.toString());

        System.out.println(initial.isGoal());

        System.out.println(initial.dimension());

        System.out.println(initial.hamming());

        System.out.println(initial.manhattan());

        System.out.println("Twin: ");
        System.out.println(initial.twin().toString());

        System.out.println("Neighbours: ");
        for (Board board : initial.neighbors()) {
            System.out.println(board.toString());
            System.out.println(board.isGoal());
        }

        System.out.println(initial.equals(initial.twin()));
        System.out.println(initial.equals(initial.twin().twin()));

    }

}