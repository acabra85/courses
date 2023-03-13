import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final int[][] tiles;
    private final int hammingDist;
    private final int manhattanDist;
    private final int zeroRIdx;
    private final int zeroCIdx;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null || tiles.length == 0) {
            throw new IllegalArgumentException("Tiles must not be null or empty");
        }
        int[] distances = {0, 0, 0, 0, 0};
        this.tiles = cloneTiles(tiles, tiles.length, distances);
        this.hammingDist = distances[0];
        this.manhattanDist = distances[1];
        this.zeroRIdx = distances[2];
        this.zeroCIdx = distances[3];
    }

    private int[][] cloneTiles(int[][] tiles, int r, int[] distances) {
        int[][] cloned = new int[r][r];
        int hamming = 0;
        int manhattan = 0;
        int correctIdx = r * r;
        for (int i = r - 1; i >= 0; --i) {
            for (int j = r - 1; j >= 0; --j, --correctIdx) {
                int value = tiles[i][j];
                if (value != 0) {
                    if (value != correctIdx) {
                        hamming++;
                        int targetR = (value - 1) / r;
                        int targetC = (value - 1) % r;
                        manhattan += Math.abs(i - targetR) + Math.abs(j - targetC);
                    }
                } else {
                    distances[2] = i;
                    distances[3] = j;
                }
                cloned[i][j] = value;
            }
        }
        distances[1] = manhattan;
        distances[0] = hamming;
        return cloned;
    }

    // string representation of this board
    public String toString() {
        int n = tiles.length;
        StringBuilder sb = new StringBuilder().append(n).append("\n");
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                sb.append(tiles[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // board dimension n
    public int dimension() {
        return this.tiles.length;
    }

    // number of tiles out of place
    public int hamming() {
        return hammingDist;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return this.manhattanDist;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    @Override
    public boolean equals(Object other) {
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        if (this == other) {
            return true;
        }
        Board castOther = (Board) other;
        return this.zeroRIdx == castOther.zeroRIdx
                && this.zeroCIdx == castOther.zeroCIdx
                && this.hammingDist == castOther.hammingDist
                && this.manhattanDist == castOther.manhattanDist
                && this.tiles.length == castOther.tiles.length
                && equalContents(this.tiles, castOther.tiles);
    }

    private static boolean equalContents(int[][] arr, int[][] arr2) {
        for (int i = 0; i < arr.length; ++i) {
            for (int j = 0; j < arr.length; ++j) {
                if (arr[i][j] != arr2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        int n = this.tiles.length;
        List<Board> boards = new ArrayList<>();
        // Move zero up
        if (this.zeroRIdx > 0) {
            boards.add(new Board(moveZeroUp()));
        }
        // Move zero down
        if (this.zeroRIdx < n - 1) {
            boards.add(new Board(moveZeroDown()));
        }
        // Move zero left
        if (this.zeroCIdx > 0) {
            boards.add(new Board(moveZeroLeft()));
        }
        // Move zero right
        if (this.zeroCIdx < n - 1) {
            boards.add(new Board(moveZeroRight()));
        }
        return boards;
    }

    private static int[][] simpleClone(int[][] tiles, int n) {
        int[][] cloned = new int[n][n];
        for (int i = 0; i < n; ++i) {
            cloned[i] = tiles[i].clone();
        }
        return cloned;
    }

    private int[][] moveZeroRight() {
        int[][] other = Board.simpleClone(this.tiles, this.tiles.length);
        swap(other, this.zeroRIdx, this.zeroCIdx + 1, this.zeroRIdx, this.zeroCIdx);
        return other;
    }

    private int[][] moveZeroLeft() {
        int[][] other = Board.simpleClone(this.tiles, this.tiles.length);
        swap(other, this.zeroRIdx, this.zeroCIdx - 1, this.zeroRIdx, this.zeroCIdx);
        return other;
    }

    private int[][] moveZeroDown() {
        int[][] other = Board.simpleClone(this.tiles, this.tiles.length);
        swap(other, this.zeroRIdx + 1, this.zeroCIdx, this.zeroRIdx, this.zeroCIdx);
        return other;
    }

    private int[][] moveZeroUp() {
        int[][] other = Board.simpleClone(this.tiles, this.tiles.length);
        swap(other, this.zeroRIdx - 1, this.zeroCIdx, this.zeroRIdx, this.zeroCIdx);
        return other;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] other = Board.simpleClone(this.tiles, this.tiles.length);
        if (this.zeroRIdx == 0) {
            swap(other, 1, 0, 1, 1);
        } else {
            swap(other, 0, 0, 0, 1);
        }
        return new Board(other);
    }

    private static void swap(int[][] arr, int i1, int j1, int i2, int j2) {
        int tmp = arr[i1][j1];
        arr[i1][j1] = arr[i2][j2];
        arr[i2][j2] = tmp;
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
        print("manhattan 3 [%d]", board.dimension());
        print("Board \n%s", board);

        for (Board neighbor : board.neighbors()) {
            print("%s", neighbor);
        }
    }

    private static void print(String s, Object... val) {
        StdOut.println(String.format(s, val));
    }
}