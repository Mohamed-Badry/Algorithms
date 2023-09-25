import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private static final double CONFIDENCE_95 = 1.96;
    private int size;
    private int trialCount;
    private double[] pThresholds;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("The grid size must be a positive integer");
        }
        size = n;
        trialCount = trials;
        pThresholds = new double[trials];
        
        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(size);
            
            while (!perc.percolates()) {
                
                int row = StdRandom.uniformInt(1, size+1);
                int col = StdRandom.uniformInt(1, size+1);
                perc.open(row, col);
            }
            // StdOut.printf("%d: percolates\n", i+1);
            double pThreshold = perc.numberOfOpenSites() / (double) (size * size);
            pThresholds[i] = pThreshold;
        } 
    }

    // sample mean of percolation threshold
    public double mean() {
        double mean = StdStats.mean(pThresholds);
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        double stddev = StdStats.stddev(pThresholds);
        return stddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        double mean = StdStats.mean(pThresholds);
        double stddev = StdStats.stddev(pThresholds);
        double lo = mean - (CONFIDENCE_95*stddev/Math.sqrt(trialCount));
        return lo;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        double mean = StdStats.mean(pThresholds);
        double stddev = StdStats.stddev(pThresholds);
        double hi = mean + (CONFIDENCE_95*stddev/Math.sqrt(trialCount));
        return hi;
    }

    // test client (see below)
    public static void main(String[] args) {
        int n = 1;
        int trialCount = 1;
        if (args.length == 2) {
            n = Integer.parseInt(args[0]);
            trialCount = Integer.parseInt(args[1]); 
        } 
        
        PercolationStats perc = new PercolationStats(n, trialCount);
        System.out.printf("mean                    = %f \n", perc.mean());
        System.out.printf("stddev                  = %f \n", perc.stddev());
        System.out.printf("95%% confidence interval = [%f, %f] \n", perc.confidenceLo(), perc.confidenceHi());
    }
}
