import java.util.Arrays;

public class BruteCollinearPoints {

  private final LineSegment[] lineSegments;

  public BruteCollinearPoints(Point[] points) {
    if(points == null) throw new IllegalArgumentException("null input");
    lineSegments = BruteCollinearPoints.findSegments(points);
  }

  private static LineSegment[] findSegments(Point[] points) {
    Arrays.sort(points, points[0].slopeOrder());
    return null;
  }

  public int numberOfSegments() {
    return lineSegments.length;
  }

  public LineSegment[] segments() {
    return lineSegments;
  }
}
