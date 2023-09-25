import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int size;
    private int top;
    private int bottom;
    private int openCount;
    private boolean[] openSites;
    private WeightedQuickUnionUF fullUF;
    private WeightedQuickUnionUF grid;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("The grid size must be a positive integer");
        size = n;
        top = 0;
        bottom = size * size + 1;
        openCount = 0;
        openSites = new boolean[bottom - 1]; // create 1D array of length n^2 initialised to false
        grid = new WeightedQuickUnionUF(bottom + 1);
        fullUF = new WeightedQuickUnionUF(bottom + 1);

        for (int i = 0; i < size; i++) {
            grid.union(top, i + 1);
            grid.union(bottom, bottom - 1 - i);
            fullUF.union(top, i + 1);
        }
    }

    // checks if index is valid
    private void validateIndices(int row, int col) {
        if (row < 1 || row > size || col < 1 || col > size)
            throw new IllegalArgumentException("Index out of bounds");
    }

    // maps xy coordinates to 1D coordinates
    private int xyto1D(int x, int y) {
        int r = size * (x - 1) + (y - 1) + 1;
        return r;
    }

    // returns the x coordinate given the 1D coordinate
    private int xfrom1D(int r) {
        int x = ((r - 1) / size) + 1;
        return x;
    }

    // returns the y coordinate given the 1D coordinate
    private int yfrom1D(int r) {
        int y = ((r - 1) % size) + 1;
        return y;
    }

    // returns the 1D coordinate of the 4 adjacent neighbors of a cell at (x, y)
    private int[] adjacent(int x, int y) {
        int r = xyto1D(x, y);

        int leftNeighbor = -1;
        int rightNeighbor = -1;
        int bottomNeighbor = -1;
        int topNeighbor = -1;

        if (xfrom1D(r - 1) == x)
            leftNeighbor = r - 1;
        if (xfrom1D(r + 1) == x)
            rightNeighbor = r + 1;
        if (yfrom1D(r - size) == y)
            topNeighbor = r - size;
        if (yfrom1D(r + size) == y)
            bottomNeighbor = r + size;

        int[] neighbors = new int[] { leftNeighbor, rightNeighbor, topNeighbor, bottomNeighbor };
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] < 1 || neighbors[i] > size * size)
                neighbors[i] = -1;
        }
        return neighbors;

    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        validateIndices(row, col);

        if (isOpen(row, col))
            return;
        int r = xyto1D(row, col);
        openSites[r - 1] = true;
        openCount += 1;
        int[] neighbors = adjacent(row, col);
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] == -1)
                continue;
            int neighborCoord = neighbors[i];
            if (isOpen(xfrom1D(neighborCoord), yfrom1D(neighborCoord))) {
                grid.union(r, neighborCoord);
                fullUF.union(r, neighborCoord);
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validateIndices(row, col);
        int r = xyto1D(row, col);
        return openSites[r - 1];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        validateIndices(row, col);
        int r = xyto1D(row, col);
        boolean full = false;
        if (fullUF.find(r) == fullUF.find(top) && isOpen(row, col)) {
            full = true;
        }
        return full;

    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openCount;
    }

    // does the system percolate?
    public boolean percolates() {
        if (size == 1) {
            return openSites[0];
        }
        if (grid.find(bottom) == grid.find(top))
            return true;
        return false;

    }

    // test client (optional)
    public static void main(String[] args) {

    }
}