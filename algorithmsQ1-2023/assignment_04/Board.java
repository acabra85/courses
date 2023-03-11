import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private static final int EMPTY_SPACE = 0;

    private static final int HAMMING_IDX = 0;
    private static final int MANHATTAN_IDX = 1;
    private static final int ZERO_R_IDX = 2;
    private static final int ZERO_C_IDX = 3;

    private final int[][] tiles;
    private final String asString;
    private final int n;
    private final int[] distances;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null || tiles.length == 0) {
            throw new IllegalArgumentException("Tiles must not be null or empty");
        }
        StringBuilder sb = new StringBuilder();
        int[] distances = {0, 0, 0, 0};
        this.tiles = cloneTiles(tiles, tiles.length, sb, distances);
        this.asString = sb.toString();
        this.n = tiles.length;
        this.distances = distances;
    }

    private int[][] cloneTiles(int[][] tiles, int r, StringBuilder sb, int[] distances) {
        int[][] cloned = new int[r][r];
        int hamming = 0;
        int manhattan = 0;
        sb.append(r).append("\n ");
        int correctIdx = 1;
        for (int i = 0; i < r; ++i) {
            for (int j = 0; j < r; ++j, ++correctIdx) {
                int value = tiles[i][j];
                if (value != EMPTY_SPACE) {
                    if (value != correctIdx) {
                        hamming++;
                        int targetR = (value - 1) / r;
                        int targetC = (value - 1) % r;
                        manhattan += Math.abs(i - targetR) + Math.abs(j - targetC);
                    }
                    sb.append(value);
                } else {
                    distances[ZERO_R_IDX] = i;
                    distances[ZERO_C_IDX] = j;
                    sb.append(" ");
                }
                sb.append(" ");
                cloned[i][j] = value;
            }
            sb.append("\n ");
        }
        distances[MANHATTAN_IDX] = manhattan;
        distances[HAMMING_IDX] = hamming;
        return cloned;
    }

    // string representation of this board
    public String toString() {
        return this.asString;
    }

    // board dimension n
    public int dimension() {
        return this.n;
    }

    // number of tiles out of place
    public int hamming() {
        return this.distances[HAMMING_IDX];
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return this.distances[MANHATTAN_IDX];
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    @Override
    public boolean equals(Object y) {
        return y != null && Board.class.equals(y.getClass()) && this.asString.equals(y.toString());
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        List<Board> boards = new ArrayList<>();
        // Move zero up
        if (distances[ZERO_R_IDX] > 0) {
            boards.add(new Board(moveZeroUp()));
        }
        // Move zero down
        if (distances[ZERO_R_IDX] < this.n - 1) {
            boards.add(new Board(moveZeroDown()));
        }
        // Move zero left
        if (distances[ZERO_C_IDX] > 0) {
            boards.add(new Board(moveZeroLeft()));
        }
        // Move zero right
        if (distances[ZERO_C_IDX] < this.n - 1) {
            boards.add(new Board(moveZeroRight()));
        }
        return boards;
    }

    private static int[][] simpleClone(int[][] tiles, int n) {
        int[][] cloned = new int[n][n];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                cloned[i][j] = tiles[i][j];
            }
        }
        return cloned;
    }

    private int[][] moveZeroRight() {
        int[][] other = Board.simpleClone(this.tiles, this.n);
        int tmpR = tiles[distances[ZERO_R_IDX]][distances[ZERO_C_IDX] + 1];
        other[distances[ZERO_R_IDX]][distances[ZERO_C_IDX] + 1] = EMPTY_SPACE;
        other[distances[ZERO_R_IDX]][distances[ZERO_C_IDX]] = tmpR;
        return other;
    }

    private int[][] moveZeroLeft() {
        int[][] other = Board.simpleClone(this.tiles, this.n);
        int tmpR = tiles[distances[ZERO_R_IDX]][distances[ZERO_C_IDX] - 1];
        other[distances[ZERO_R_IDX]][distances[ZERO_C_IDX] - 1] = EMPTY_SPACE;
        other[distances[ZERO_R_IDX]][distances[ZERO_C_IDX]] = tmpR;
        return other;
    }

    private int[][] moveZeroDown() {
        int[][] other = Board.simpleClone(this.tiles, this.n);
        int tmpR = tiles[distances[ZERO_R_IDX] + 1][distances[ZERO_C_IDX]];
        other[distances[ZERO_R_IDX] + 1][distances[ZERO_C_IDX]] = EMPTY_SPACE;
        other[distances[ZERO_R_IDX]][distances[ZERO_C_IDX]] = tmpR;
        return other;
    }

    private int[][] moveZeroUp() {
        int[][] other = Board.simpleClone(this.tiles, this.n);
        int tmpR = tiles[distances[ZERO_R_IDX] - 1][distances[ZERO_C_IDX]];
        other[distances[ZERO_R_IDX] - 1][distances[ZERO_C_IDX]] = EMPTY_SPACE;
        other[distances[ZERO_R_IDX]][distances[ZERO_C_IDX]] = tmpR;
        return other;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] other = Board.simpleClone(this.tiles, this.n);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (other[i][j] != EMPTY_SPACE && j + 1 < n && other[i][j + 1] != EMPTY_SPACE) {
                    int tmp = other[i][j];
                    other[i][j] = other[i][j + 1];
                    other[i][j + 1] = tmp;
                    return new Board(other);
                }
                if (other[i][j] != EMPTY_SPACE && i + 1 < n && other[i + 1][j] != EMPTY_SPACE) {
                    int tmp = other[i][j];
                    other[i][j] = other[i + 1][j];
                    other[i + 1][j] = tmp;
                    return new Board(other);
                }
            }
        }
        return new Board(other);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        Board board;
        board = new Board(new int[][]{
                {8, 1, 3},
                {4, 0, 2},
                {7, 6, 5},
        });
        print("hamming 5 [%d]", board.hamming());
        print("manhattan 10 [%d]", board.manhattan());
        print("Board \n%s", board);

        for (Board neighbor : board.neighbors()) {
            print("%s", neighbor);
        }
    }

    private static void print(String s, Object... val) {
        StdOut.println(String.format(s, val));
    }
}