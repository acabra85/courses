import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PointSET {

    private final TreeSet<Point2D> holder;

    // construct an empty set of points
    public PointSET() {
        holder = new TreeSet<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return holder.isEmpty();
    }

    // number of points in the set
    public int size() {
        return holder.size();
    }

    // add the point to the set (if it is not already in the set){}
    public void insert(Point2D p) {
        throwOnNull(p);
        holder.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        throwOnNull(p);
        return holder.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D point2D : holder) {
            point2D.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary){}
    public Iterable<Point2D> range(RectHV rect) {
        throwOnNull(rect);
        return holder.stream().filter(rect::contains).collect(Collectors.toList());
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        throwOnNull(p);
        if (holder.isEmpty()) {
            return null;
        }
        MinPQ<Point2D> pq = new MinPQ<>(Comparator.comparingDouble(p::distanceTo));
        holder.forEach(pq::insert);
        return pq.min();
    }

    private static void throwOnNull(Object p) {
        if (p == null) {
            throw new IllegalArgumentException("argument must not be null");
        }
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
    }
}