import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {
    private Point pivot;
    private List<List<Point>> combinations;
    private int size = 0;
    private LineSegment[] segs;
    private LineSegment[] segsArr;

    
    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException("points array is null");
        }

        combinations = new ArrayList<>();
        
        segs = new LineSegment[(int) Math.pow(points.length, 4)];
        
        // create a list of 4 four point lists in ascending order
        for (Point point1: points) {
            
            if (point1 == null) {
                throw new IllegalArgumentException("a point in the array is null");
            }
            
            for (Point point2: points) {
                if (point1 != point2 && point2.equals(point1)) {
                    throw new IllegalArgumentException("two elements in the array are the same point");
                }
                
                for (Point point3: points) {
                    for (Point point4: points) {
                        if (lessThan(point1, point2) && lessThan(point2, point3) && lessThan(point3, point4)) {
                            combinations.add(Arrays.asList(point1, point2, point3, point4));
                        }
                    }
                }
            }
        }
        
        // checks if a list of four points has the same slope then makes it into a line segment 
        // and adds it to the segs array
        for (List<Point> fourPoints: combinations) {
            pivot = fourPoints.get(0);
            double slope = pivot.slopeTo(fourPoints.get(1));
            if (slope == pivot.slopeTo(fourPoints.get(2)) && slope == pivot.slopeTo(fourPoints.get(3))) {
                LineSegment line = new LineSegment(pivot, fourPoints.get(3));
                segs[size++] = line;
            }
        }
        
        segsArr = new LineSegment[size];
        
        int s = 0;
        for (LineSegment segment: segs) {
            if (segment != null) segsArr[s++] = segment;
        }
        
    }    

    private static boolean lessThan(Point p, Point q) {
        if (p.compareTo(q) < 0)
            return true;
        return false;
    }
    
    // the number of line segments
    public int numberOfSegments() {
        return segsArr.length;
    }
    
    // the line segments
    public LineSegment[] segments() {
        LineSegment[] lineSegments = new LineSegment[size];
        for (int i = 0; i < segsArr.length; i++) {
            lineSegments[i] = segsArr[i];
        }
        return lineSegments;
    }           


    public static void main(String[] args) {

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
}
