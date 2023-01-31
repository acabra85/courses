import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private static final boolean DEBUG = false;
    private final int n;
    private int openSites;
    private final Site[][] grid;
    private final WeightedQuickUnionUF qf;
    private final Site[] botts;
    private final boolean[] fullSites;

    private static class Site {

        public static final String FULL_LABEL = "0 ";
        public static final String BLOCKED_LABEL = "X ";
        public static final String EMPTY_LABEL = "_ ";
        private boolean empty;
        boolean open;
        int id;

        Site(int id) {
            this.open = false;
            this.id = id;
            this.empty = true;
        }

        public void doOpen() {
            this.open = true;
        }

        public boolean isOpen() {
            return this.open;
        }

        public boolean isFull() {
            return this.open && !this.isEmpty();
        }

        public boolean isEmpty() {
            return this.empty;
        }

        public boolean isBlocked() {
            return !this.open;
        }

        public void doFill() {
            this.empty = false;
        }
    }

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        Percolation.validateN(n);
        this.grid = Percolation.buildGrid(n);
        this.n = n;
        int nSqr = n * n;
        this.qf = new WeightedQuickUnionUF(nSqr);
        this.botts = getRow(n-1);
        this.fullSites = new boolean[nSqr];
    }

    private Site[] getRow(int row) {
        return this.grid[row];
    }

    private static Site[][] buildGrid(int n) {
        Site[][] sites = new Site[n][n];
        int id = -1;
        for (int i = 0; i < n; ++i) {
            Site[] row = new Site[n];
            for (int j = 0; j < n; ++j) {
                row[j] = new Site(++id);
            }
            sites[i] = row;
        }
        return sites;
    }

    private static void validateN(int n) {
        if(n <= 0 || n > 1000) {
            throw new IllegalArgumentException("n must be between [1,1000], received: " + n);
        }
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        int r = row - 1;
        int c = col - 1;
        Percolation.validateArguments(this.n, r, c);
        Site site = grid[r][c];
        if(site.isBlocked()) {
            processOpening(site, r, c);
            ++this.openSites;
            if (Percolation.DEBUG) {
                System.out.println(this);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Site[] sites : grid) {
            for (Site site : sites) {
                sb.append(calculateSiteLabel(site));
            }
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    private String calculateSiteLabel(Site site) {
        if(site.isBlocked()) {
            return Site.BLOCKED_LABEL;
        }
        return fullSites[qf.find(site.id)]
            ? Site.FULL_LABEL
            : Site.EMPTY_LABEL;
    }

    final static int[][] NEIGHBORS = {
            {0, -1},
            {0, 1},
            {-1, 0},
            {1, 0},
    };

    private void processOpening(Site site, int r, int c) {
        site.doOpen();
        // any open site on the top will be filled
        if (r == 0) {
            site.doFill();
            fullSites[site.id] = true;
            for (int[] nCoord : NEIGHBORS) {
                Site neighbor = getSite(r + nCoord[0], c + nCoord[1]);
                if (neighbor != null && neighbor.isOpen()) {
                    qf.union(site.id, neighbor.id);
                    fullSites[qf.find(neighbor.id)] = true;
                    fullSites[neighbor.id] = true;
                    neighbor.doFill();
                }
            }
        } else {
            for (int[] nCoord : NEIGHBORS) {
                Site neighbor = getSite(r + nCoord[0], c + nCoord[1]);
                if (neighbor != null && neighbor.isOpen()) {
                    qf.union(site.id, neighbor.id);
                    if(site.isFull() || neighbor.isFull()) {
                        site.doFill();
                        neighbor.doFill();
                        fullSites[qf.find(site.id)] = true;
                        fullSites[site.id] = true;
                        fullSites[neighbor.id] = true;
                    }
                }
            }
        }
    }

    private Site getSite(int r, int c) {
        if(validInput(n, r) && validInput(n, c)) {
            return this.grid[r][c];
        }
        return null;
    }

    private static boolean validInput(int n, int row) {
        return row >= 0 && row < n;
    }

    private static void validateArguments(int n, int row, int col) {
        if(!validInput(n, row)) {
            throw new IllegalArgumentException(String.format("row must be between [1,%d], received: %d", n, row));
        }
        if(!validInput(n, col)) {
            throw new IllegalArgumentException(String.format("col must be between [1,%d], received: %d", n, col));
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validateArguments(n, row, col);
        return this.grid[row-1][col-1].isOpen();
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        validateArguments(n, row, col);
        return this.grid[row-1][col-1].isFull();
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return this.openSites;
    }

    // does the system percolate?
    public boolean percolates() {
        for (Site bott : this.botts) {
            if(fullSites[qf.find(bott.id)]) {
                return true;
            }
        }
        return false;
    }

    // test client (optional)
    public static void main(String[] args) {
        System.out.println("hello percolator");
        Percolation percolation = new Percolation(8);
        System.out.printf("Open sites: %d%n", percolation.numberOfOpenSites());

        System.out.println("Percolates :" + percolation.percolates());

        percolation.open(1,1);
        percolation.open(2,1);
        percolation.open(3,1);
        percolation.open(4,1);
        percolation.open(5,1);
        percolation.open(6,1);
        percolation.open(7,1);
        percolation.open(7,2);
        percolation.open(8,2);
        System.out.printf("Open sites: %d%n", percolation.numberOfOpenSites());
        System.out.println("Percolates :" + percolation.percolates());
    }
}