/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.util.Comparator;

public class Point implements Comparable<Point> {

  private final int x;     // x-coordinate of this point
  private final int y;     // y-coordinate of this point

  /**
   * Initializes a new point.
   *
   * @param x the <em>x</em>-coordinate of the point
   * @param y the <em>y</em>-coordinate of the point
   */
  public Point(int x, int y) {
    /* DO NOT MODIFY */
    this.x = x;
    this.y = y;
  }

  /**
   * Draws this point to standard draw.
   */
  public void draw() {
    /* DO NOT MODIFY */
    StdDraw.point(x, y);
  }

  /**
   * Draws the line segment between this point and the specified point to standard draw.
   *
   * @param that the other point
   */
  public void drawTo(Point that) {
    /* DO NOT MODIFY */
    StdDraw.line(this.x, this.y, that.x, that.y);
  }

  /**
   * Returns the slope between this point and the specified point. Formally, if the two points are
   * (x0, y0) and (x1, y1), then the slope is (y1 - y0) / (x1 - x0). For completeness, the slope is
   * defined to be +0.0 if the line segment connecting the two points is horizontal;
   * Double.POSITIVE_INFINITY if the line segment is vertical; and Double.NEGATIVE_INFINITY if (x0,
   * y0) and (x1, y1) are equal.
   *
   * @param that the other point
   * @return the slope between this point and the specified point
   */
  public double slopeTo(Point that) {
    /* YOUR CODE HERE */
    if (that == null) {
      throw new NullPointerException("that point is null");
    }
    if (this.compareTo(that) == 0) {
      return Double.NEGATIVE_INFINITY;
    }
    if (x == that.x) {
      return Double.POSITIVE_INFINITY;
    }
    if (y == that.y) {
      return 0.0d;
    }
    return (that.y - y) / (1.0 * (that.x - x));
  }

  private boolean hasEqualComps(Point that) {
    return x == that.x && y == that.y;
  }

  /**
   * Compares two points by y-coordinate, breaking ties by x-coordinate. Formally, the invoking
   * point (x0, y0) is less than the argument point (x1, y1) if and only if either y0 < y1 or if y0
   * = y1 and x0 < x1.
   *
   * @param that the other point
   * @return the value <tt>0</tt> if this point is equal to the argument point (x0 = x1 and y0 =
   * y1); a negative integer if this point is less than the argument point; and a positive integer
   * if this point is greater than the argument point
   */
  public int compareTo(Point that) {
    /* YOUR CODE HERE */
    if (this == that || hasEqualComps(that)) {
      return 0;
    }
    if (y < that.y || (x < that.x && y == that.y)) {
      return -1;
    }
    return 1;
  }

  /**
   * Compares two points by the slope they make with this point. The slope is defined as in the
   * slopeTo() method.
   *
   * @return the Comparator that defines this ordering on points
   */
  public Comparator<Point> slopeOrder() {
    /* YOUR CODE HERE */
    return (p1, p2) -> Double.compare(this.slopeTo(p1), this.slopeTo(p2));
  }


  /**
   * Returns a string representation of this point. This method is provide for debugging; your
   * program should not rely on the format of the string representation.
   *
   * @return a string representation of this point
   */
  public String toString() {
    /* DO NOT MODIFY */
    return "(" + x + ", " + y + ")";
  }

  /**
   * Unit tests the Point data type.
   */
  public static void main(String[] args) {
    Point point1 = new Point(0, 0);
    Point point2 = new Point(0, 0);
    Point point3 = new Point(1, 2);
    Point p = new Point(0, 5);
    Point q = new Point(0, 6);
    Point r = new Point(8, 4);
    print("True{%s}", point1.hasEqualComps(point2));
    print("True{%s}", point1.hasEqualComps(point1));
    print("False{%s}", point1.hasEqualComps(point3));
    print("0{%s}", point1.compareTo(point2));
    print("-1{%s}", point1.compareTo(point3));
    print("-1{%s}", point2.compareTo(point3));
    print("-1{%s}", point2.compareTo(point3));

    print("------------ Test 3a");
    print("1 {%s}", p.slopeOrder().compare(q, r));
    print("Infinity {%s}", p.slopeTo(q));
    print("-0.125 {%s}", p.slopeTo(r));

    print("------------ Test 3b");
    Point p2 = new Point(9548, 20053);
    Point q2 = new Point(21081, 11752);
    Point r2 = new Point(1280, 28877);
    print("1 {%s}", p2.slopeOrder().compare(q2, r2));
    print("-0.7197606867250499 {%s}", p2.slopeTo(q2));
    print("-1.0672472181906145 {%s}", p2.slopeTo(r2));

  }

  private static void print(String s, Object... val) {
    StdOut.println(String.format(s, val));
  }
}