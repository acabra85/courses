import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

  private static final boolean DEBUG = false;
  private static final String FULL_LABEL = "0 ";
  private static final String BLOCKED_LABEL = "X ";
  private static final String EMPTY_LABEL = "_ ";
  private final int n;
  private int openSites;
  private final WeightedQuickUnionUF qf;
  private final boolean[] fullSites;
  private boolean percolating;
  private final boolean[] emptySites;
  private final int nSqr;
  private final int startBotts;

  // creates n-by-n grid, with all sites initially blocked
  public Percolation(int n) {
    Percolation.validateN(n);
    this.n = n;
    this.nSqr = n * n;
    this.qf = new WeightedQuickUnionUF(nSqr);
    this.fullSites = new boolean[nSqr];
    this.emptySites = new boolean[nSqr];
    this.percolating = false;
    this.startBotts = nSqr - n;
  }

  private static void validateN(int n) {
    if (n <= 0) {
      throw new IllegalArgumentException("n must be between [1,1000], received: " + n);
    }
  }

  // opens the site (row, col) if it is not open already
  public void open(int row, int col) {
    int r = row - 1;
    int c = col - 1;
    Percolation.validateArguments(this.n, r, c);
    int siteId = r * n + c;
    if (isBlocked(siteId)) {
      processOpening(siteId, r, c);
      ++this.openSites;
    }
    reviewPercolation();
  }

  private boolean isBlocked(int siteId) {
    return !emptySites[siteId];
  }

  private void reviewPercolation() {
    if (!percolating) {
      for (int id = this.startBotts; id < nSqr; ++id) {
        int parent = qf.find(id);
        if (fullSites[parent]) {
          this.percolating = true;
          if (DEBUG) {
            StdOut.printf("Percolates: %s connected to %s %n",
                String.format("[%d, %d]", id / n, id % n),
                String.format("[%d, %d]", parent / n, parent % n));
          }
          return;
        }
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < this.nSqr; ++i) {
      if (i % n == 0) {
        sb.append("\n");
      }
      sb.append(calculateSiteLabel(isBlocked(i), this.fullSites[qf.find(i)]));
    }
    sb.append("\n");
    return sb.toString();
  }

  private String calculateSiteLabel(boolean isBlocked, boolean isFull) {
    if (isBlocked) {
      return BLOCKED_LABEL;
    }
    return isFull ? FULL_LABEL : EMPTY_LABEL;
  }

  private void processOpening(int siteId, int r, int c) {
    if (DEBUG) {
      StdOut.printf("Opening [%d, %d]%n", r, c);
    }
    int[][] neighbors = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    this.emptySites[siteId] = true;
    // any open site on the top will be filled
    if (r == 0) {
      int parentId = -1;
      fullSites[siteId] = true;
      for (int[] nCoord : neighbors) {
        int rN = r + nCoord[0];
        int cN = c + nCoord[1];
        int neighborId = (r + nCoord[0]) * n + c + nCoord[1];
        if (!invalidInput(this.n, rN) && !invalidInput(this.n, cN) && this.emptySites[neighborId]) {
          qf.union(siteId, neighborId);
          parentId = qf.find(neighborId);
          fullSites[parentId] = true;
          fullSites[neighborId] = true;
        }
      }
      if (parentId > -1) {
        fillAll(parentId);
      }
    } else {
      int parentId = -1;
      for (int[] nCoord : neighbors) {
        int rN = r + nCoord[0];
        int cN = c + nCoord[1];
        int neighborId = rN * n + cN;
        if (!invalidInput(this.n, rN) && !invalidInput(this.n, cN) && this.emptySites[neighborId]) {
          qf.union(siteId, neighborId);
          if (fullSites[siteId] || fullSites[neighborId]) {
            parentId = qf.find(neighborId);
            fullSites[parentId] = true;
            fullSites[siteId] = true;
            fullSites[neighborId] = true;
            // fill = true;
          }
        }
      }
      if (parentId > -1) {
        fillAll(parentId);
      }
    }
    if (DEBUG) {
      StdOut.println(this);
    }
  }

  private void fillAll(int parentId) {
    for (int i = 0; i < nSqr; i++) {
      if (qf.find(i) == parentId) {
        this.fullSites[i] = true;
      }
    }
  }


  private static boolean invalidInput(int n, int idx) {
    return idx < 0 || idx >= n;
  }

  private static void validateArguments(int n, int row, int col) {
    if (invalidInput(n, row)) {
      throw new IllegalArgumentException(
          String.format("row must be between [1,%d], received: %d", n, row));
    }
    if (invalidInput(n, col)) {
      throw new IllegalArgumentException(
          String.format("col must be between [1,%d], received: %d", n, col));
    }
  }

  // is the site (row, col) open?
  public boolean isOpen(int row, int col) {
    int r = row - 1;
    int c = col - 1;
    validateArguments(n, r, c);
    return this.emptySites[r * n + c];
  }

  // is the site (row, col) full?
  public boolean isFull(int row, int col) {
    int r = row - 1;
    int c = col - 1;
    validateArguments(n, r, c);
    return this.fullSites[qf.find(r * n + c)];
  }

  // returns the number of open sites
  public int numberOfOpenSites() {
    return this.openSites;
  }

  // does the system percolate?
  public boolean percolates() {
    return this.percolating;
  }

  // test client (optional)
  public static void main(String[] args) {
    StdOut.println("hello percolator");
    Percolation percolation = new Percolation(5);
    StdOut.printf("Open sites: %d%n", percolation.numberOfOpenSites());

    StdOut.println("Percolates :" + percolation.percolates());

    percolation.open(5, 2);
    percolation.open(5, 1);
    percolation.open(4, 4);
    percolation.open(3, 3);
    percolation.open(1, 5);
    percolation.open(5, 3);
    percolation.open(2, 1);
    percolation.open(4, 3);
    percolation.open(5, 2);
    percolation.open(3, 5);
    percolation.open(1, 2);
    percolation.open(1, 5);
    percolation.open(1, 4);
    percolation.open(3, 5);
    percolation.open(1, 3);
    percolation.open(3, 2);
    percolation.open(3, 1);
    percolation.open(5, 1);

    StdOut.printf("Open sites: %d%n", percolation.numberOfOpenSites());
    StdOut.println("Percolates :" + percolation.percolates());
  }
}
