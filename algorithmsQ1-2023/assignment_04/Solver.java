import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Comparator;

public class Solver {

    private final boolean solvable;
    private final BoardNode solutionNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("Board must not be null");
        }
        this.solutionNode = solving(initial);
        this.solvable = solutionNode != null;
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return this.solutionNode != null ? this.solutionNode.moves : -1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    private static BoardNode solving(Board initial) {
        Comparator<BoardNode> comparator = new Comparator<BoardNode>() {
            @Override
            public int compare(BoardNode o1, BoardNode o2) {
                return o1.priority - o2.priority;
            }
        };
        MinPQ<BoardNode> pq = new MinPQ<>(comparator);
        MinPQ<BoardNode> pqTwin = new MinPQ<>(comparator);
        pq.insert(BoardNode.orphan(initial));
        pqTwin.insert(BoardNode.orphan(initial.twin()));
        while (true) {
            if (pq.min().board.isGoal()) {
                return pq.min();
            }
            if (pqTwin.min().board.isGoal()) {
                return null;
            }
            addNeighbors(pq, pq.delMin());
            addNeighbors(pqTwin, pqTwin.delMin());
        }
    }

    private static void addNeighbors(MinPQ<BoardNode> pq, BoardNode node) {
        Board prevBoard = node.parent != null ? node.parent.board : null;
        for (Board neighbor : node.board.neighbors()) {
            if (prevBoard == null || !prevBoard.equals(neighbor)) {
                pq.insert(BoardNode.withParent(neighbor, node));
            }
        }
    }

    // test client (see below) {}
    public static void main(String[] args) {
        // create initial board from file
        // executeTest("board_solvable.txt", "---- Solvable 5 Moves");
        // executeTest("board_unsolvable.txt", "---- Unsolvable");
        executeTest(args[0]);
    }

    private static void executeTest(String... args) {
        if (args.length > 1) {
            StdOut.println(args[1]);
        }
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                StdOut.println(board);
            }
        }
    }

    public Iterable<Board> solution() {
        return this.solutionNode != null ? this.solutionNode.buildSolution() : null;
    }

    private static class BoardNode {
        private final Board board;
        private final int moves;
        private final int priority;
        private final BoardNode parent;

        private BoardNode(Board board, int moves, BoardNode parent) {
            this.board = board;
            this.moves = moves;
            this.parent = parent;
            this.priority = board.manhattan() + moves;
        }

        public static BoardNode orphan(Board twin) {
            return new BoardNode(twin, 0, null);
        }

        public static BoardNode withParent(Board neighbor, BoardNode node) {
            return new BoardNode(neighbor, node.moves + 1, node);
        }

        public Iterable<Board> buildSolution() {
            Deque<Board> boards = new ArrayDeque<>();
            BoardNode node = this;
            boards.addFirst(node.board);
            while (node.parent != null) {
                node = node.parent;
                boards.addFirst(node.board);
            }
            return boards;
        }
    }
}