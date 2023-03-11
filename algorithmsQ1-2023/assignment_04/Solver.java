import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.*;

public class Solver {

    private final Board initial;
    private final boolean solvable;
    private final List<Board> solution;
    private final int solutionCount;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        this.initial = initial;
        this.solution = new ArrayList<>();
        Iterable<Board> solution = solution();
        if (solution != null) {
            for (Board board : solution) {
                this.solution.add(board);
            }
        }
        this.solvable = this.solution.size() > 0;
        this.solutionCount = solvable ? this.solution.size() : -1;
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable || !new Solver(this.initial.twin()).isSolvable();
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return solutionCount;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        MinPQ<BoardNode> pq = new MinPQ<>((a, b) -> a.priority - b.priority);
        pq.insert(new BoardNode(this.initial, 0, null));
        BoardNode node;
        Set<String> control = new HashSet<>();
        while (!pq.isEmpty()) {
            node = pq.delMin();
            control.add(node.iHash());
            if (node.board.isGoal()) {
                return node.buildSolution();
            }
            for (Board neighbor : node.board.neighbors()) {
                if (!control.contains(neighbor.toString())) {
                    pq.insert(new BoardNode(neighbor, node.moves + 1, node));
                }
            }

        }
        return null;
    }

    // test client (see below) {}
    public static void main(String[] args) {
        // create initial board from file
        // executeTest("board_solvable.txt", "---- Solvable 5 Moves");
        // executeTest("board_unsolvable.txt", "---- Unsolvable");
        executeTest(args[0]);
    }

    private static void executeTest(String... args) {
        if (args.length > 0) {
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

    private static class BoardNode {
        private final Board board;
        private final int moves;
        private final int priority;
        private final BoardNode parent;

        private BoardNode(Board board, int moves, BoardNode parent) {
            this.board = board;
            this.moves = moves;
            this.priority = moves + board.manhattan();
            this.parent = parent;
        }

        private String iHash() {
            return board.toString();
        }

        public Iterable<Board> buildSolution() {
            List<Board> boards = new ArrayList<>();
            Stack<BoardNode> stack = new Stack<>();
            BoardNode node = this;
            stack.push(node);
            while (node.parent != null) {
                node = node.parent;
                stack.push(node);
            }
            while (!stack.isEmpty()) {
                boards.add(stack.pop().board);
            }
            return boards;
        }
    }
}