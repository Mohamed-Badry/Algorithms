import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private int[][] picArray;
    private double[][] energies;
    private boolean isTransposed;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("SeamCarver constructor argument can't be null.");
        }

        int height = picture.height();
        int width = picture.width();

        picArray = new int[width][height];
        energies = new double[width][height];
        isTransposed = false;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                picArray[x][y] = picture.getRGB(x, y);
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                energies[x][y] = energy(x, y);
            }
        }
    }

    private Picture drawPicture(int[][] pictureArray) {

        Picture pic = new Picture(width(), height());

        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                pic.setRGB(i, j, pictureArray[i][j]);
            }
        }
        return pic;

    };

    // current picture
    public Picture picture() {
        if (isTransposed) {
            transpose();
        }

        return drawPicture(picArray);
    }

    // width of current picture
    public int width() {
        if (isTransposed) {
            return arrayHeight();
        }
        return arrayWidth();
    }

    // height of current picture
    public int height() {
        if (isTransposed) {
            return arrayWidth();
        }
        return arrayHeight();
    }

    private int arrayWidth() {

        return picArray.length;
    }

    private int arrayHeight() {

        return picArray[0].length;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validatePicColumn(x);
        validatePicRow(y);

        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
            return 1000;
        }

        return Math.sqrt(gradientXsquared(x, y) + gradientYsquared(x, y));
    }

    private int[] getColor(int x, int y) {
        int rgb;

        if (!isTransposed) {
            rgb = picArray[x][y];
        } else {
            rgb = picArray[y][x];
        }

        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = (rgb >> 0) & 0xFF;

        return new int[] { red, green, blue };
    }

    private double gradientXsquared(int x, int y) {
        int[] rgbLeft = getColor(x - 1, y);
        int[] rgbRight = getColor(x + 1, y);

        int sumOfSquaredDifferences = 0;
        for (int i = 0; i < rgbRight.length; i++) {
            sumOfSquaredDifferences += (int) Math.pow(rgbRight[i] - rgbLeft[i], 2);
        }

        return sumOfSquaredDifferences;
    }

    private double gradientYsquared(int x, int y) {
        int[] rgbTop = getColor(x, y - 1);
        int[] rgbBottom = getColor(x, y + 1);

        int sumOfSquaredDifferences = 0;
        for (int i = 0; i < rgbBottom.length; i++) {
            sumOfSquaredDifferences += (int) Math.pow(rgbBottom[i] - rgbTop[i], 2);
        }

        return sumOfSquaredDifferences;
    }

    private int[][] adj(int[] w) {
        int x = w[0];
        int y = w[1];

        validateArrayColumn(x);
        validateArrayRow(y);

        if (x == 0) {
            return new int[][] { { x, y + 1 }, { x + 1, y + 1 } };
        } else if (x == picArray.length - 1) {
            return new int[][] { { x - 1, y + 1 }, { x, y + 1 } };
        } else {
            return new int[][] { { x - 1, y + 1 }, { x, y + 1 }, { x + 1, y + 1 } };
        }
    }

    private void transpose() {

        int width = arrayWidth();
        int height = arrayHeight();

        int[][] picArrayT = new int[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                picArrayT[j][i] = picArray[i][j];
            }
        }
        picArray = picArrayT;

        double[][] energiesT = new double[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                energiesT[j][i] = energies[i][j];
            }
        }
        energies = energiesT;

        isTransposed = !isTransposed;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {

        if (!isTransposed) {
            transpose();
        }

        return findVertical();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {

        if (isTransposed) {
            transpose();
        }

        return findVertical();
    }

    private int[] findVertical() {

        int width = arrayWidth();
        int height = arrayHeight();

        double[][] distTo = new double[width][height];
        int[][][] edgeTo = new int[width][height][2];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                distTo[x][y] = Double.POSITIVE_INFINITY;
            }
            distTo[x][0] = 0;
        }

        for (int j = 0; j < height - 1; j++) {
            for (int i = 0; i < width - 1; i++) {
                int[] v = { i, j };
                for (int[] w : adj(v)) {
                    if (distTo[w[0]][w[1]] > distTo[v[0]][v[1]] + energies[v[0]][v[1]]) {
                        distTo[w[0]][w[1]] = distTo[v[0]][v[1]] + energies[v[0]][v[1]];
                        edgeTo[w[0]][w[1]] = v;
                    }
                }
            }
        }

        double minDist = Double.POSITIVE_INFINITY;
        int[] minIndex = { 0, 0 };
        for (int i = 0; i < width - 1; i++) {
            if (distTo[i][height - 1] < minDist) {
                minDist = distTo[i][height - 1];
                minIndex = new int[] { i, height - 1 };
            }
        }

        int[] current = minIndex;
        int[] verticalSeam = new int[height];

        for (int j = height - 1; j >= 0; j--) {
            // System.out.printf("(%d, %d) \n", current[0], current[1]);

            verticalSeam[j] = current[0];
            current = edgeTo[current[0]][current[1]];
        }

        return verticalSeam;

    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {

        if (height() <= 1) {
            throw new IllegalArgumentException("Height of picture doesn't allow for horizontal seam removal");
        }

        if (!isTransposed) {
            transpose();
        }
        removeVertical(seam);

    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {

        if (width() <= 1) {
            throw new IllegalArgumentException("Width of picture doesn't allow for vertical seam removal");
        }

        if (isTransposed) {
            transpose();
        }
        removeVertical(seam);

    }

    private void removeVertical(int[] seam) {

        validateSeam(seam);

        int arrayWidth = arrayWidth();
        int arrayHeight = arrayHeight();

        // new energies are gonna have the same number of rows but one less column
        // Picture newPic = new Picture(arrayWidth - 1, arrayHeight);
        int[][] newPicArray = new int[arrayWidth - 1][arrayHeight];
        double[][] newEnergies = new double[arrayWidth - 1][arrayHeight];

        // assign picArray to newPicArray while removing the pixel at the cell
        // (pixelCol, row) from each row
        for (int i = 0; i < seam.length; i++) {

            int k = 0;
            for (int col = 0; col < arrayWidth; col++) {
                if (col == seam[i]) {
                    k = 1;
                    continue;
                }
                newPicArray[col - k][i] = picArray[col][i];
                newEnergies[col - k][i] = energies[col][i];
            }
        }

        picArray = newPicArray;
        energies = newEnergies;

        // recalculates energy values around seam
        for (int row = 0; row < height(); row++) {
            int col = seam[row];

            if (col > 0) {
                newEnergies[col - 1][row] = energy(col - 1, row);
            }

            if (col < arrayWidth - 1) {
                newEnergies[col][row] = energy(col, row);
            }
        }
    }

    private void validateSeam(int[] seam) {

        if (seam == null) {
            throw new IllegalArgumentException("seam can't be null.");
        }

        if (seam.length != arrayHeight()) {
            throw new IllegalArgumentException("seam of invalid length.");
        }

        for (int i = 0; i < seam.length - 1; i++) {
            if (seam[i] < 0 || seam[i] >= arrayWidth()) {
                throw new IllegalArgumentException("seam value out of bounds.");
            }

            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException("invalid seam.");
            }
        }

    }

    private void validatePicColumn(int x) {
        if (x < 0 || x > width() - 1) {
            throw new IllegalArgumentException("pixel x value out of range.");
        }
    }

    private void validatePicRow(int y) {
        if (y < 0 || y > height() - 1) {
            throw new IllegalArgumentException("pixel y value out of range.");
        }
    }

    private void validateArrayColumn(int x) {
        if (x < 0 || x > arrayWidth() - 1) {
            throw new IllegalArgumentException("pixel x value out of range.");
        }
    }

    private void validateArrayRow(int y) {
        if (y < 0 || y > arrayHeight() - 1) {
            throw new IllegalArgumentException("pixel y value out of range.");
        }
    }

    // unit testing (optional)
    public static void main(String[] args) {
        SeamCarver carver = new SeamCarver(new Picture(args[0]));

        // carver.picture().show();
        System.out.println(carver.width());
        System.out.println(carver.height());

        // for (int j = 0; j < carver.height(); j++) {
        // for (int i = 0; i < carver.width(); i++) {
        // System.out.printf("(%d, %d): %.2f ", i, j, carver.energy(i, j));
        // }
        // System.out.println();
        // }
        System.out.println();

        // int[] vSeam = carver.findVerticalSeam();
        // for (int v : vSeam) {
        // System.out.printf("%d ", v);
        // }
        // System.out.println("\n");

        // int[] hSeam = carver.findHorizontalSeam();
        // for (int h : hSeam) {
        // System.out.printf("%d ", h);
        // }
        // System.out.println();

        carver.picture().show();
        for (int k = 0; k < 3; k++) {

            for (int i = 0; i < 50 * k; i++) {
                carver.removeVerticalSeam(carver.findVerticalSeam());
            }
            carver.picture().show();

            for (int i = 0; i < 30 * k; i++) {
                carver.removeHorizontalSeam(carver.findHorizontalSeam());
            }
            carver.picture().show();
        }

        // int[] testSeam = { -1, 4, 3, 2, 1, 2, 3, 4, 5, 6 };
        // carver.removeVerticalSeam(testSeam);
        // System.out.println("vertical removal done");
        // testSeam = new int[] { 3, 4, 3, 2, 1, 2, 3, 4, 5 };
        // carver.removeHorizontalSeam(testSeam);
        // System.out.println("horizontal removal done");

    }

}