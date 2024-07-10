import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class FastCollinearPoints {
    private Point pivot;
    private int size = 0;
    private List<Point[]> segments;
    private List<Point[]> mergedPoints;
    private List<Point[]> pointPairs;
    private List<Point[]> pointPairsUnique;
    private LineSegment[] segsArr;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException("points array is null");
        }

        pivot = new Point(0, 0);
        segments = new ArrayList<>();
        mergedPoints = new ArrayList<>();
        pointPairs = new ArrayList<>();
        pointPairsUnique = new ArrayList<>();
        Point[] pointsCopy = new Point[points.length];

        Arrays.sort(points, 0, points.length, pivot.slopeOrder());

        int s1 = 0;
        for (Point point : points) {
            pointsCopy[s1++] = point;
        }

        for (Point point1 : pointsCopy) {

            // check if point is null has null arguments
            if (point1 == null)
                throw new IllegalArgumentException();

            // // check if point is equal to the point after it
            // if (i < points.length-1) {
            // if (points[i].compareTo(points[i+1]) == 0) throw new
            // IllegalArgumentException();
            // }

            int count = 1;
            pivot = point1;

            Arrays.sort(points, 0, points.length, pivot.slopeOrder());

            for (int j = 1; j < points.length; j++) {
                if (pivot.slopeTo(points[j]) == pivot.slopeTo(points[j - 1])) {
                    count++;
                } else {
                    if (count >= 3) {
                        segments.add(new Point[count]);
                        for (int k = j - count; k < j; k++) {
                            segments.get(size)[k - (j - count)] = points[k];
                        }
                        size++;
                    }
                    count = 1;
                }
            }
            // handles the case when the array ends without meeting different elements
            if (count >= 3) {
                segments.add(new Point[count]);
                for (int j = points.length - count; j < points.length; j++) {
                    segments.get(size)[j - (points.length - count)] = (points[j]);
                }
                size++;
            }
        }

        // add separate segments that have the same slope
        for (Point[] pointList1 : segments) {
            Arrays.sort(pointList1);
            for (Point[] pointList2 : segments) {
                Arrays.sort(pointList2);
                if (pointList1[0].slopeTo(pointList1[2]) == pointList2[0].slopeTo(pointList2[2])) {
                    if (pointList1[0] == pointList2[0] && lessThan(pointList1[2], pointList2[2])) {
                        mergedPoints.add(merge(reverse(pointList1), pointList2));
                    }
                    if (pointList1[2] == pointList2[2] && lessThan(pointList1[0], pointList2[0])) {
                        mergedPoints.add(merge(pointList1, reverse(pointList2)));
                    }
                    if (pointList1[0] == pointList2[2] && lessThan(pointList1[2], pointList2[0])) {
                        mergedPoints.add(merge(reverse(pointList1), reverse(pointList2)));
                    }
                    if (pointList1[2] == pointList2[0] && lessThan(pointList1[0], pointList2[2])) {
                        mergedPoints.add(merge(pointList1, pointList2));
                    }
                }
            }
        }

        int s2 = 0;
        for (Point[] pointList : mergedPoints) {
            Arrays.sort(pointList);
            if (pointList != null) {
                Point first = pointList[0];
                Point last = pointList[pointList.length - 1];
                if (!first.equals(last)) {
                    pointPairs.add(new Point[2]);
                    pointPairs.get(s2)[0] = first;
                    pointPairs.get(s2)[1] = last;
                }
                s2++;
            }
        }

        segsArr = new LineSegment[size];

        int s3 = 0;
        for (Point[] pointPair : pointPairs) {
            if (pointPair == null)
                break;

            if (!hasElement(pointPairsUnique, pointPair)) {
                pointPairsUnique.add(pointPair);
                segsArr[s3++] = new LineSegment(pointPair[0], pointPair[1]);
            }
        }

        size = s3;

    }

    // the number of line segments
    public int numberOfSegments() {
        return size;
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] lineSegments = new LineSegment[size];
        for (int i = 0; i < size; i++) {
            lineSegments[i] = segsArr[i];
        }
        return lineSegments;
    }

    private static Point[] reverse(Point[] p) {
        Point[] reversedP = new Point[p.length];
        for (int i = 0; i < reversedP.length; i++) {
            reversedP[i] = p[p.length - 1 - i];
        }
        return reversedP;
    }

    private static Point[] merge(Point[] p1, Point[] p2) {
        int mergeSize = p1.length + p2.length;
        Point[] mergedP = new Point[mergeSize];
        for (int i = 0; i < p1.length; i++) {
            mergedP[i] = p1[i];
        }
        for (int j = p1.length; j < mergedP.length; j++) {
            mergedP[j] = p2[j - p1.length];
        }
        return mergedP;
    }

    private static boolean hasElement(List<Point[]> pointPairs, Point[] element) {
        if (pointPairs.isEmpty())
            return false;
        for (Point[] pair : pointPairs) {
            if (pair[0].equals(element[0]) && pair[1].equals(element[1])) {
                return true;
            }
        }
        return false;
    }

    private static boolean lessThan(Point p, Point q) {
        if (p.compareTo(q) < 0)
            return true;
        return false;
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}