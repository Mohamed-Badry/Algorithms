// length and ancestor need to be fixed

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;

public class SAP {
    private static final int INFINITY = Integer.MAX_VALUE;
    private final Digraph sap;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null)
            throw new IllegalArgumentException("SAP constructer argument can't be null");

        // call copy constructer to make defensive copy of Digraph
        sap = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        return sapVertexAndDistance(v, w)[1];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path;
    // -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        return sapVertexAndDistance(v, w)[0];
    }

    // stack implementation
    private int[] sapVertexAndDistance(int v, int w) {

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(sap, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(sap, w);

        int shortestDist = INFINITY;
        int sca = -1;
        for (int i = 0; i < sap.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                int iDist = bfsV.distTo(i) + bfsW.distTo(i);
                if (iDist < shortestDist) {
                    shortestDist = iDist;
                    sca = i;
                }
            }
        }
        shortestDist = shortestDist == INFINITY ? -1 : shortestDist;
        return new int[] { sca, shortestDist };
    }

    // length of shortest ancestral path between any vertex in v and any vertex in
    // w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);

        return sapVertexAndDistance(v, w)[1];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such
    // path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);
        return sapVertexAndDistance(v, w)[0];
    }

    // returns the common ancestor and length respectively of the shortest ancestral
    // path between 2 Itarables v and w; {-1, -1} if no such path
    private int[] sapVertexAndDistance(Iterable<Integer> v, Iterable<Integer> w) {

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(sap, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(sap, w);

        int shortestDist = INFINITY;
        int sca = -1;
        for (int i = 0; i < sap.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                int iDist = bfsV.distTo(i) + bfsW.distTo(i);
                if (iDist < shortestDist) {
                    shortestDist = iDist;
                    sca = i;
                }
            }
        }
        shortestDist = shortestDist == INFINITY ? -1 : shortestDist;
        return new int[] { sca, shortestDist };
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int vertCount = sap.V();
        if (v < 0 || v >= vertCount)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (vertCount - 1));
    }

    // throw an IllegalArgumentException if vertices is null, has zero vertices,
    // or has a vertex not between 0 and V-1
    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        int vertexCount = 0;
        for (Integer v : vertices) {
            vertexCount++;
            if (v == null) {
                throw new IllegalArgumentException("vertex is null");
            }
            validateVertex(v);
        }
        if (vertexCount == 0) {
            throw new IllegalArgumentException("zero vertices");
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        // test length and and ancestor for array inputs
        while (!StdIn.isEmpty()) {
            Stack<Integer> left = new Stack<>();

            int leftSize = StdIn.readInt();
            for (int i = 0; i < leftSize; i++) {
                left.push(StdIn.readInt());
            }

            StdOut.println("left stack");
            for (int n : left) {
                StdOut.printf("%d ", n);
            }
            StdOut.println();
            StdOut.println();

            Stack<Integer> right = new Stack<>();
            int rightSize = StdIn.readInt();
            for (int i = 0; i < rightSize; i++) {
                right.push(StdIn.readInt());
            }

            StdOut.println("right stack");
            for (int n : right) {
                StdOut.printf("%d ", n);
            }
            StdOut.println();

            int len = sap.length(left, right);
            int a = sap.ancestor(left, right);
            StdOut.printf("length = %d\t ancestor = %d\n", len, a);
        }

        // test length and ancestor for single inputs
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
