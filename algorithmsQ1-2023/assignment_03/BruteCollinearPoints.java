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
    if (points == null) {
      throw new IllegalArgumentException("null input");
    }
    for (Point point : points) {
      if (null == point) {
        throw new IllegalArgumentException("point is null");
      }
    }
  }

  private static LineSegment[] findSegments(Point[] pointsX) {
    final List<Point[]> control = new ArrayList<>();
    int n = pointsX.length;
    if (n <= 0) {
      return new LineSegment[0];
    }
    Point[] points = Arrays.copyOf(pointsX, n);
    Arrays.sort(points, (a, b) -> a.compareTo(b));
    assertUniqueValues(points, n);
    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < n; ++j) {
        if (j == i) {
          continue;
        }
        double slopeIJ = points[i].slopeTo(points[j]);
        for (int k = 0; k < n; ++k) {
          if (k == j || k == i) {
            continue;
          }
          double slopeIK = points[i].slopeTo(points[k]);
          if (Double.compare(slopeIJ, slopeIK) != 0) {
            continue;
          }
          for (int ldx = 0; ldx < n; ++ldx) {
            if (ldx == k || ldx == j) {
              continue;
            }
            double slopeL = points[i].slopeTo(points[ldx]);
            if (ldx != i && Double.compare(slopeIK, slopeL) == 0) {
              addIfAbsent(control, points[min(i, min(j, min(k, ldx)))],
                  points[max(i, max(j, max(k, ldx)))]);
            }
          }
        }
      }
    }
    return getUniqueLineSegments(control);
  }

  private static void assertUniqueValues(Point[] points, int size) {
    for (int i = 1; i < size; ++i) {
      if (points[i].compareTo(points[i - 1]) == 0) {
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
        new Point(1, 1),
        new Point(2, 3),
        new Point(3, 5),
        new Point(4, 7),

        new Point(0, 6),
        new Point(6, 4),
        new Point(9, 3),

        new Point(1, 4),
        new Point(4, 1),

        new Point(6, 5),
        new Point(6, 3),
        new Point(6, 2),
    };
    BruteCollinearPoints bcp = new BruteCollinearPoints(points);
    print("3[%s]", bcp.numberOfSegments());
    for (LineSegment lineSegment : bcp.lineSegments) {
      print("seg:[%s]", lineSegment);
    }

    // try {
    //   BruteCollinearPoints bruteCollinearPoints = new BruteCollinearPoints(null);
    // } catch (IllegalArgumentException iae) {
    //   print("input is null: %s", iae.getMessage());
    // }

    // try {
    //   Point[] points1 = {new Point(1, 1), null, new Point(2, 2)};
    //   BruteCollinearPoints bruteCollinearPoints = new BruteCollinearPoints(points1);
    //   bruteCollinearPoints.segments();
    // } catch (IllegalArgumentException iae) {
    //   print("point is null: %s", iae.getMessage());
    // }
    //
    // try {
    //   Point[] points1 = {new Point(1, 1), new Point(2, 2), new Point(2, 2)};
    //   BruteCollinearPoints bruteCollinearPoints = new BruteCollinearPoints(points1);
    //   bruteCollinearPoints.segments();
    // } catch (IllegalArgumentException iae) {
    //   print("duplicate point: %s", iae.getMessage());
    // }

    if (args != null && args.length > 0) {
      // sampleClient("collinear/input8.txt");
      sampleClient(args);
    }
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
    // StdDraw.setXscale(0, SCALE_DRAW_HI);
    // StdDraw.setYscale(0, SCALE_DRAW_HI);
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

  private static void addIfAbsent(List<Point[]> control, Point a, Point b) {
    Point[] sci = {a, b};
    if (control.stream().noneMatch(e -> identical(e, sci))) {
      control.add(sci);
    }
  }

  private static boolean identical(Point[] a, Point[] e) {
    return a[0].compareTo(e[0]) == 0 && a[1].compareTo(e[1]) == 0;
  }

  private static LineSegment[] getUniqueLineSegments(List<Point[]> control) {
    final LineSegment[] resp = new LineSegment[control.size()];
    for (int i = 0; i < control.size(); ++i) {
      Point[] segment = control.get(i);
      resp[i] = new LineSegment(segment[0], segment[1]);
    }
    return resp;
  }

  private static void print(String str, Object... args) {
    StdOut.print(String.format(str + "%n", args));
  }
}
