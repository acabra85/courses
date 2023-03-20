import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KdTree {

    private static final Comparator<Point2D> X_COMP = new Comparator<Point2D>() {
        @Override
        public int compare(Point2D o1, Point2D o2) {
            return Double.compare(o1.x(), o2.x());
        }
    };

    private static final Comparator<Point2D> Y_COMP = new Comparator<Point2D>() {
        @Override
        public int compare(Point2D o1, Point2D o2) {
            return Double.compare(o1.y(), o2.y());
        }
    };

    private final KDNodeRoot root;
    private int total;

    // construct an empty set of points
    public KdTree() {
        this.root = new KDNodeRoot();
        this.total = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return this.total == 0;
    }

    // number of points in the set
    public int size() {
        return this.total;
    }

    // add the point to the set (if it is not already in the set){}
    public void insert(Point2D p) {
        throwOnNull(p);
        if (isEmpty()) {
            root.kdNode = new KDNode(p, root.rect);
            ++this.total;
        } else if (root.kdNode.insert(p)) {
            ++this.total;
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        throwOnNull(p);
        if (isEmpty()) return false;
        return root.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        root.draw();
    }

    // all points that are inside the rectangle (or on the boundary){}
    public Iterable<Point2D> range(RectHV rect) {
        throwOnNull(rect);
        if (isEmpty()) {
            return new ArrayList<>();
        }
        return root.range(rect);
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        throwOnNull(p);
        if (isEmpty()) {
            return null;
        }
        return root.nearest(p);
    }

    private static class KDNode {
        final Point2D point;
        final RectHV rect;
        final Point2D midPoint;
        final boolean xCoord;
        KDNode right;
        KDNode left;

        private KDNode(Point2D point, boolean xCoord, RectHV rectParent) {
            this.point = point;
            this.xCoord = xCoord;
            this.rect = rectParent;
            this.midPoint = KDNode.calculateMidPoint(rectParent);
        }

        private static Point2D calculateMidPoint(RectHV rectParent) {
            return new Point2D(
                    (rectParent.xmin() + rectParent.xmax() ) / 2,
                    (rectParent.ymin() + rectParent.ymax() ) / 2);
        }

        public KDNode(Point2D point, RectHV rect) {
            this(point, true, rect);
        }

        private static RectHV buildRectLeft(boolean xCoord, Point2D point, RectHV rect) {
            if (xCoord) {  // left
                return new RectHV(rect.xmin(), rect.ymin(), point.x(), rect.ymax());
            }
            // low
            return new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), point.y());
        }

        private static RectHV buildRectRight(boolean xCoord, Point2D point, RectHV rect) {
            if (xCoord) { // right
                return new RectHV(point.x(), rect.ymin(), rect.xmax(), rect.ymax());
            }
            // up
            return new RectHV(rect.xmin(), point.y(), rect.xmax(), rect.ymax());
        }

        public void draw() {
            if (point != null) {
                point.draw();
            }
            if (left != null) {
                left.draw();
            }
            if (right != null) {
                right.draw();
            }
        }

        public boolean contains(Point2D p) {
            if (p.equals(point)) {
                return true;
            }
            if (compare(p) > 0) {
                return left != null && left.contains(p);
            }
            return right != null && right.contains(p);
        }

        private int compare(Point2D p) {
            return (xCoord ? X_COMP : Y_COMP).compare(point, p);
        }

        public boolean insert(Point2D p) {
            if (!contains(p)) {
                if (compare(p) > 0) {
                    if (this.left == null) {
                        this.left = buildChild(p, buildRectLeft(this.xCoord, p, rect));
                    } else {
                        this.left.insert(p);
                    }
                } else {
                    if (this.right == null) {
                        this.right = buildChild(p, buildRectRight(this.xCoord, p, rect));
                    } else {
                        this.right.insert(p);
                    }
                }
                return true;
            }
            return false;
        }

        private KDNode buildChild(Point2D p, RectHV rect) {
            return new KDNode(p, !xCoord, rect);
        }

        public boolean intersects(RectHV rect) {
            return rect.intersects(this.rect);
        }
    }

    private static void throwOnNull(Object p) {
        if (p == null) {
            throw new IllegalArgumentException("argument must not be null");
        }
    }

    private static void print(String s, Object... val) {
        StdOut.println(String.format(s, val));
    }

    private static class KDNodeRoot {
        final RectHV rect = new RectHV(0, 0, 1, 1);
        KDNode kdNode = null;

        public boolean contains(Point2D p) {
            return kdNode != null && kdNode.contains(p);
        }

        public void draw() {
            if (kdNode != null) {
                kdNode.draw();
            }
        }

        private Point2D nearest(Point2D p) {
            MinPQ<KDNode> stack = new MinPQ<>(new Comparator<KDNode>() {
                @Override
                public int compare(KDNode a, KDNode b) {
                    return Double.compare(p.distanceSquaredTo(a.midPoint), p.distanceSquaredTo(b.midPoint));
                }
            });
            stack.insert(this.kdNode);
            StdOut.println(kdNode.point);
            double minDist = p.distanceSquaredTo(this.kdNode.point);
            KDNode kdNode;
            Point2D x = this.kdNode.point;
            while (!stack.isEmpty()) {
                kdNode = stack.delMin();
                double nDist = p.distanceSquaredTo(kdNode.point);
                if (nDist < minDist) {
                    minDist = nDist;
                    x = kdNode.point;
                }
                if (kdNode.left != null && kdNode.left.midPoint.distanceSquaredTo(p) < minDist) {
                    StdOut.println(kdNode.left.point);
                    stack.insert(kdNode.left);
                }
                if (kdNode.right != null && kdNode.right.midPoint.distanceSquaredTo(p) < minDist) {
                    StdOut.println(kdNode.right.point);
                    stack.insert(kdNode.right);
                }
            }
            return x;
        }

        public Iterable<Point2D> range(RectHV rect) {
            List<Point2D> res = new ArrayList<>();
            if(this.rect.intersects(rect)) {
                Stack<KDNode> stack = new Stack<>();
                stack.push(kdNode);
                KDNode kdNode;
                while (!stack.isEmpty()) {
                    kdNode = stack.pop();
                    if (rect.contains(kdNode.point)) {
                        res.add(kdNode.point);
                    }
                    if (kdNode.left != null && kdNode.left.intersects(rect)) {
                        stack.push(kdNode.left);
                    }
                    if (kdNode.right != null && kdNode.right.intersects(rect)) {
                        stack.push(kdNode.right);
                    }
                }
            }
            return res;
        }
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        KdTree kdTree = new KdTree();

        In in = new In("input5.txt");
        while (!in.isEmpty()) {
            Point2D p = new Point2D(in.readDouble(), in.readDouble());
            StdOut.println(p);
            kdTree.insert(p);
            StdOut.println("size: " + kdTree.size());
        }

        print("isEmpty false: " + kdTree.isEmpty());
        print("size 5: " + kdTree.size());
        print("----- nearest");
        /*
            (0.7, 0.2)
            (0.5, 0.4)
            (0.4, 0.7)
            (0.9, 0.6)
            (0.2, 0.3)

            (0.7, 0.2)
            (0.5, 0.4)
            (0.4, 0.7)
            (0.9, 0.6)
            (0.2, 0.3)
         */
        print("nearest (0.4, 0.7): " + kdTree.nearest(new Point2D(0.51d, 0.78d)));

    }
}