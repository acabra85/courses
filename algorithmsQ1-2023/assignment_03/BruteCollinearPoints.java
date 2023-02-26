import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints {

  private final LineSegment[] lineSegments;

  public BruteCollinearPoints(Point[] points) {
    assertNonNull(points);
    lineSegments = BruteCollinearPoints.findSegments(points);
  }

  private static void assertNonNull(Point[] points) {
    if(points == null) throw new IllegalArgumentException("null input");
    for (Point point : points) {
      if(null == point) {
        throw new IllegalArgumentException("point is null");
      }
    }
  }

  private static LineSegment[] findSegments(Point[] points) {
    int N = points.length;
    if(N < 4) return new LineSegment[0];
    Arrays.sort(points, (a,b) -> a.compareTo(b));
    assertUniqueValues(points, N);
    SegControl sc = SegControl.of();
    for (int i = 0; i < N; ++i) {
      for (int j = 0; j < N; ++j) {
        if(j == i) continue;
        Double slopeIJ = points[i].slopeTo(points[j]);
        for (int k = 0; k < N; ++k) {
          Double slopeIK;
          if (k == j || k == i || slopeIJ.compareTo(slopeIK = points[i].slopeTo(points[k])) != 0) continue;
          for (int l = 0; l < N; ++l) {
            Double slopeL = points[i].slopeTo(points[l]);
            if (l == k || l == j || l == i || slopeIK.compareTo(slopeL) != 0) continue;
            sc.addIfAbsent(points[min(i, min(j, min(k, l)))], points[max(i, max(j, max(k, l)))]);
          }
        }
      }
    }
    return sc.getUniqueLineSegments();
  }

  private static void assertUniqueValues(Point[] points, int N) {
    for (int i = 1; i < N; ++i) {
      if(points[i].compareTo(points[i-1]) == 0) {
        throw new IllegalArgumentException("duplicated point: " + points[i]);
      }
    }
  }

  private static int min(int a, int b) {
    return a <= b ? a : b;
  }

  private static int max(int a, int b) {
    return a > b ? a : b;
  }

  public int numberOfSegments() {
    return lineSegments.length;
  }

  public LineSegment[] segments() {
    return Arrays.copyOf(lineSegments, lineSegments.length);
  }

  public static void main(String... args) {
    Point[] points = {
            new Point(1,1),
            new Point(2,3),
            new Point(3,5),
            new Point(4,7),

            new Point(0,6),
            new Point(6,4),
            new Point(9,3),

            new Point(1,4),
            new Point(4,1),

            new Point(6,5),
            new Point(6,3),
            new Point(6,2),
    };
    BruteCollinearPoints bcp = new BruteCollinearPoints(points);
    print("3[%s]", bcp.numberOfSegments());
    for (LineSegment lineSegment : bcp.lineSegments) {
      print("seg:[%s]", lineSegment);
    }

    try {
      new BruteCollinearPoints(null);
    } catch (IllegalArgumentException iae) {
      print("input is null: %s", iae.getMessage());
    }

    try {
      Point[] points1 = {new Point(1, 1), null, new Point(2,2)};
      new BruteCollinearPoints(points1);
    } catch (IllegalArgumentException iae) {
      print("point is null: %s", iae.getMessage());
    }

    try {
      Point[] points1 = {new Point(1, 1), new Point(2, 2), new Point(2,2)};
      new BruteCollinearPoints(points1);
    } catch (IllegalArgumentException iae) {
      print("duplicate point: %s", iae.getMessage());
    }

    sampleClient("collinear/input8.txt");
  }

  private static void sampleClient(String... args) {
    // read the n points from a file
    In in = new In(args[0]);
    int n = in.readInt();
    Point[] points = new Point[n];
    for (int i = 0; i < n; i++) {
      int x = in.readInt();
      int y = in.readInt();
      points[i] = new Point(x, y);
    }

    // draw the points
    StdDraw.enableDoubleBuffering();
    StdDraw.setXscale(0, 32768);
    StdDraw.setYscale(0, 32768);
    for (Point p : points) {
      p.draw();
    }
    StdDraw.show();

    // print and draw the line segments
    BruteCollinearPoints collinear = new BruteCollinearPoints(points);
    for (LineSegment segment : collinear.segments()) {
      StdOut.println(segment);
      segment.draw();
    }
    StdDraw.show();
  }

  private static void print(String str, Object... args) {
    StdOut.print(String.format(str + "%n", args));
  }

  private static class SegControl {

    private final List<SegControlItem> control;

    private SegControl() {
      this.control = new ArrayList<>();
    }
    private static SegControl of() {
      return new SegControl();
    }

    private void addIfAbsent(Point a, Point b) {
      SegControlItem sci = new SegControlItem(a, b);
      if(control.stream().noneMatch(sci::identical)) {
        this.control.add(sci);
      }
    }

    private LineSegment[] getUniqueLineSegments() {
      final LineSegment[] resp = new LineSegment[control.size()];
      for (int i = 0; i < control.size(); ++i) {
        resp[i] = control.get(i).toLineSegment();
      }
      return resp;
    }

    private static class SegControlItem {
      final Point a;
      final Point b;

      private SegControlItem(Point a1, Point b1) {
        this.a = a1;
        this.b = b1;
      }

      private LineSegment toLineSegment() {
        return new LineSegment(this.a, this.b);
      }

      private boolean identical(SegControlItem e) {
        return a.compareTo(e.a) == 0 && b.compareTo(e.b) == 0;
      }
    }
  }
}
