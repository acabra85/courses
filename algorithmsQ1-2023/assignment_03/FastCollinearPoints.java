import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FastCollinearPoints {

  private static final Comparator<Point> POINT_COMPARATOR = (a, b) -> a.compareTo(b);
  private static final Comparator<Object[]> SLOPE_COMPARATOR = (a, b) -> Double.compare(
      (Double) a[1], (Double) b[1]);
  private final LineSegment[] lineSegments;

  public FastCollinearPoints(Point[] points) {
    assertNonNull(points);
    this.lineSegments = FastCollinearPoints.solve(points);
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

  private static void assertUniqueValues(Point[] points, int size) {
    for (int i = 1; i < size; ++i) {
      if (points[i].compareTo(points[i - 1]) == 0) {
        throw new IllegalArgumentException("duplicated point: " + points[i]);
      }
    }
  }

  private static LineSegment[] solve(Point[] pointsX) {
    final List<Point[]> control = new ArrayList<>();
    int n = pointsX.length;
    if (n <= 0) {
      return new LineSegment[0];
    }
    Point[] points = Arrays.copyOf(pointsX, n);
    Arrays.sort(points, POINT_COMPARATOR);
    assertUniqueValues(points, n);
    for (Point origin : points) {
      List<Object[]> collect = Arrays.stream(points)
          .filter(e -> e.compareTo(origin) != 0)
          //.map(e -> new PointSlope(e, origin.slopeTo(e)))
          .map(e -> new Object[]{e, origin.slopeTo(e)})
          .sorted(SLOPE_COMPARATOR)
          .collect(Collectors.toList());
      int sameSlope = 1;
      int i;
      for (i = 1; i < n - 1; ++i) {
        if (0 == Double.compare((Double) collect.get(i)[1], (Double) collect.get(i - 1)[1])) {
          ++sameSlope;
        } else {
          if (sameSlope >= 3) {
            addIfAbsent(control, origin, collect, i - sameSlope, i - 1);
          }
          sameSlope = 1;
        }
      }
      if (sameSlope >= 3) {
        addIfAbsent(control, origin, collect, i - sameSlope, i - 1);
      }
    }

    return getUniqueLineSegments(control);
  }

  private static void addIfAbsent(List<Point[]> control, Point origin, List<Object[]> collect,
      int lo,
      int hi) {
    Point[] collinear = new Point[hi - lo + 2];
    collinear[0] = origin;
    for (int i = 1, j = lo; i < collinear.length; ++i, ++j) {
      collinear[i] = (Point) collect.get(j)[0];
    }
    Arrays.sort(collinear, POINT_COMPARATOR);
    Point[] sci = {collinear[0], collinear[collinear.length - 1]};
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

  public LineSegment[] segments() {
    return Arrays.copyOf(lineSegments, lineSegments.length);
  }

  public int numberOfSegments() {
    return lineSegments.length;
  }

  public static void main(String[] args) {
    // read the n points from a file
    executeTest(args[0], 0, true);
    // executeMyTests();
  }

  // private static void executeMyTests() {
  //   executeTest("collinear/input8.txt", 2, false);
  //   executeTest("collinear/equidistant.txt", 4, false);
  //   executeTest("collinear/input40.txt", 4, false);
  //   executeTest("collinear/input48.txt", 6, false);
  //   executeTest("collinear/horizontal5.txt", 5, false);
  //   executeTest("collinear/horizontal25.txt", 25, false);
  //   executeTest("collinear/horizontal50.txt", 50, false);
  //   executeTest("collinear/horizontal75.txt", 75, false);
  //   executeTest("collinear/horizontal100.txt", 100, false);
  //   executeTest("collinear/vertical100.txt", 100, false);
  //   executeTest("collinear/input9.txt", 1, false);
  // }
  private static void print(String str, Object... args) {
    StdOut.print(String.format(str + "%n", args));
  }

  private static void executeTest(String name, int expected, boolean draw) {
    In in = new In(name);
    //In in = new In(args[0]);
    int n = in.readInt();
    int scale = 32676;
    Point[] points = new Point[n];
    for (int i = 0; i < n; i++) {
      int x = in.readInt();
      int y = in.readInt();
      points[i] = new Point(x, y);
    }

    // draw the points
    if (draw) {
      StdDraw.enableDoubleBuffering();
      StdDraw.setXscale(0, scale);
      StdDraw.setYscale(0, scale);
      for (Point p : points) {
        p.draw();
      }
      StdDraw.show();
    }

    // print and draw the line segments
    FastCollinearPoints collinear = new FastCollinearPoints(points);
    print("%d----[%d]", expected, collinear.numberOfSegments());
    for (LineSegment segment : collinear.segments()) {
      StdOut.println(segment);
      if (draw) {
        segment.draw();
      }
    }
    if (draw) {
      StdDraw.show();
    }
  }
}
