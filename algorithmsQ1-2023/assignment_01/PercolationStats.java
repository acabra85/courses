import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

  private static final boolean DEBUG = false;

  private double med;
  private double std;
  private double conHi;
  private double conLow;

  // perform independent trials on an n-by-n grid
  public PercolationStats(int n, int trials) {
    PercolationStats.validateInput(n, trials);
    runExperiment(n, trials);
  }

  private void runExperiment(int n, int trials) {
    double[] fracs = new double[trials];
    int nSqr = n * n;
    for (int i = 0; i < trials; ++i) {
      Percolation percolation = new Percolation(n);
      int id = -1;
      while (!percolation.percolates()) {
        id = StdRandom.uniformInt(nSqr);
        percolation.open((id / n) + 1, (id % n) + 1);
      }
      fracs[i] = (percolation.numberOfOpenSites() * 1.0) / (n * n * 1.0);
      if (DEBUG) {
        StdOut.println(percolation);
      }
    }
    this.med = StdStats.mean(fracs);
    this.std = StdStats.stddev(fracs);
    double tsq = Math.sqrt(trials);
    double value = (1.96d * std) / tsq;
    this.conLow = med - value;
    this.conHi = med + value;
  }

  private static void validateInput(int n, int trials) {
    if (n <= 0 || trials <= 0) {
      throw new IllegalArgumentException(
          String.format("N and Trials must be greater than zero received %d, %d", n, trials));
    }
  }

  // sample mean of percolation threshold
  public double mean() {
    return this.med;
  }

  // sample standard deviation of percolation threshold
  public double stddev() {
    return this.std;
  }

  // low endpoint of 95% confidence interval
  public double confidenceLo() {
    return this.conLow;
  }

  // high endpoint of 95% confidence interval
  public double confidenceHi() {
    return this.conHi;
  }

  // test client (see below) {

  public static void main(String[] args) {
    int n;
    int trials;
    if (args.length == 2) {
      n = Integer.parseInt(args[0]);
      trials = Integer.parseInt(args[1]);
    } else {
      n = StdIn.readInt();
      trials = StdIn.readInt();
    }
    PercolationStats percolationStats = new PercolationStats(n, trials);
    StdOut.printf("mean                    = %s\n", percolationStats.mean());
    StdOut.printf("stddev                  = %s\n", percolationStats.stddev());
    StdOut.printf("95%% confidence interval = [%s, %s]\n", percolationStats.confidenceLo(),
        percolationStats.confidenceHi());
  }

}