import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private Node root;

    // construct KdTree
    public KdTree() {
        root = null;
    }

    private static class Node {
        private Point2D p; // associated value
        private Node left, right; // links to subtrees
        private RectHV rect; // axis-aligned rectangle
        private int count; // tree size

        // Constructor for Node
        public Node(Point2D point, RectHV rect, int count) {
            this.p = point;
            this.rect = rect;
            this.count = count;
            this.left = null;
            this.right = null;
        }
    }

    // is the tree empty?
    public boolean isEmpty() {
        return size() == 0;
    }

    // number of points in the tree
    public int size() {
        return size(root);
    }

    private int size(Node n) {
        if (n == null)
            return 0;
        else
            return n.count;
    }

    // add the point to the tree (if it is not already in the tree)
    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        root = insert(root, p, true, 0, 0, 1, 1);
    }

    private Node insert(Node n, Point2D val, boolean vertical, double xmin, double ymin, double xmax, double ymax) {
        if (n == null) {
            return new Node(val, new RectHV(xmin, ymin, xmax, ymax), 1);
        }

        if (n.p.equals(val)) {
            n.count = size(n.left) + size(n.right) + 1;
            return n;
        }

        // if vertical compare x else compare y
        if (vertical) {
            // compare x-coordinates
            int cmp = Double.compare(val.x(), n.p.x());

            // if val.x is to the left of n.p.x go left else go right
            if (cmp < 0) {
                n.left = insert(n.left, val, !vertical, xmin, ymin, n.p.x(), ymax);
            } else {
                n.right = insert(n.right, val, !vertical, n.p.x(), ymin, xmax, ymax);
            }
        } else {
            // compare y-coordinates
            int cmp = Double.compare(val.y(), n.p.y());

            // if val.y is to the bottom of n.p.x go down (left) else go up (right)
            if (cmp < 0) {
                n.left = insert(n.left, val, !vertical, xmin, ymin, xmax, n.p.y());
            } else {
                n.right = insert(n.right, val, !vertical, xmin, n.p.y(), xmax, ymax);
            }
        }

        if (n.p == null) {
            n.p = val;
        }

        n.count = size(n.left) + size(n.right) + 1;
        return n;
    }

    // does the tree contain point p?
    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        return contains(root, p, true);
    }

    // recursive method to check if tree contains a value
    private boolean contains(Node n, Point2D val, boolean vertical) {

        if (n == null)
            return false;

        if (n.p.equals(val))
            return true;

        // if vertical compare x else compare y then return this function
        // recursively using the opposite orientation to the current one
        if (vertical) {
            // compare x-coordinates
            int cmp = Double.compare(val.x(), n.p.x());

            if (cmp < 0)
                return contains(n.left, val, !vertical);
            else
                return contains(n.right, val, !vertical);

        } else {
            // compare y-coordinates
            int cmp = Double.compare(val.y(), n.p.y());

            if (cmp < 0)
                return contains(n.left, val, !vertical);
            else
                return contains(n.right, val, !vertical);
        }
    }

    // public void drawTree() {
    // drawTree(root, "");
    // }

    // Function to draw the KD-tree
    // private void drawTree(Node n, String indent) {
    // if (n == null) {
    // return;
    // }

    // // Print the current node
    // System.out.println(indent + n.p.toString());

    // // Print the left child
    // System.out.println(indent + "|");
    // System.out.println(indent + "|-- left");
    // drawTree(n.left, indent + "| ");

    // // Print the right child
    // System.out.println(indent + "|");
    // System.out.println(indent + "|-- right");
    // drawTree(n.right, indent + "| ");
    // }

    // draw all points to standard draw
    public void draw() {
        draw(root, true);
    }

    private void draw(Node n, boolean vertical) {
        if (n == null)
            return;

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.point(n.p.x(), n.p.y());

        StdDraw.setPenRadius();
        if (vertical) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(n.p.x(), n.rect.ymin(), n.p.x(), n.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(n.rect.xmin(), n.p.y(), n.rect.xmax(), n.p.y());
        }

        draw(n.left, !vertical);
        draw(n.right, !vertical);

    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException();

        Queue<Point2D> queue = new Queue<Point2D>();
        range(root, queue, rect);
        return queue;
    }

    private void range(Node n, Queue<Point2D> queue, RectHV rect) {
        if (n == null)
            return;

        if (rect.intersects(n.rect)) {
            boolean withinX = n.p.x() >= rect.xmin() && n.p.x() <= rect.xmax();
            boolean withinY = n.p.y() >= rect.ymin() && n.p.y() <= rect.ymax();

            if (withinX && withinY) {
                queue.enqueue(n.p);
            }
            range(n.left, queue, rect);
            range(n.right, queue, rect);
        }
    }

    // a nearest neighbor in the tree to point p; null if the tree is empty
    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        if (isEmpty())
            return null;

        return nearest(root, p, root.p, true);
    }

    // fix logic and pruning rule
    private Point2D nearest(Node n, Point2D p, Point2D nearestP, boolean vertical) {
        if (n == null)
            return nearestP;

        // Pruning rule:
        // if the closest point discovered so far is closer than
        // the distance between the query point and the rectangle
        // corresponding to a node, there is no need to explore that
        // node (or its subtrees).
        double closestSoFar = nearestP.distanceSquaredTo(p);
        boolean rule = closestSoFar < n.rect.distanceSquaredTo(p);
        if (!rule) {
            int cmpDistance = Double.compare(n.p.distanceSquaredTo(p), closestSoFar);

            if (cmpDistance < 0) {
                nearestP = n.p;
            }

            if (vertical) {
                int cmp = Double.compare(p.x(), n.p.x());
                if (cmp < 0) {
                    nearestP = nearest(n.left, p, nearestP, !vertical);
                    nearestP = nearest(n.right, p, nearestP, !vertical);
                } else {
                    nearestP = nearest(n.right, p, nearestP, !vertical);
                    nearestP = nearest(n.left, p, nearestP, !vertical);
                }
            } else {
                int cmp = Double.compare(p.y(), n.p.y());
                if (cmp < 0) {
                    nearestP = nearest(n.left, p, nearestP, !vertical);
                    nearestP = nearest(n.right, p, nearestP, !vertical);
                } else {
                    nearestP = nearest(n.right, p, nearestP, !vertical);
                    nearestP = nearest(n.left, p, nearestP, !vertical);
                }
            }
        }
        return nearestP;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // initialize the data structures from file
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();

        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        // kdtree.draw();

        System.out.println(kdtree.size());
        // System.out.println(kdtree.contains(new Point2D(0.5, 0.5)));
        // System.out.println(kdtree.contains(new Point2D(0.2, 0.7)));
        // System.out.println(kdtree.contains(new Point2D(0.1, 0.3)));
        // System.out.println();

        // Point2D nearest = kdtree.nearest(new Point2D(0.5, 0.5));
        // kdtree.insert(new Point2D(0.5, 0.5));

        // System.out.println(kdtree.contains(new Point2D(0.5, 0.5)));
        // System.out.println(nearest);
        // kdtree.insert(nearest);
        // System.out.println(kdtree.size());

        for (Point2D p : kdtree.range(new RectHV(0, 0, 1, 1))) {
            System.out.println(p.toString());
        }
        System.out.println(kdtree.size());

        System.out.println();
        System.out.println("draw start");
        // kdtree.drawTree();
        kdtree.draw();
        System.out.println("draw end");

    }
}
