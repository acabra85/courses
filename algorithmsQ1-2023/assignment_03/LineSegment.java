public class LineSegment {

  private final Point p;
  private final Point q;

  public LineSegment(Point p, Point q) {
    this.p = p;
    this.q = q;
  }       // constructs the line segment between points p and q

  public void draw() {
    p.drawTo(q);
  }                       // draws this line segment

  public String toString() {
    return p + "->" + q;
  }                   // string representation
}