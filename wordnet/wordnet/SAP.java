import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;

public class SAP {
    private Digraph sap;
    private Digraph reversed;
    private static final int INFINITY = Integer.MAX_VALUE;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null)
            throw new IllegalArgumentException("SAP constructer argument can't be null");

        // call copy constructer to make defensive copy of Digraph
        sap = new Digraph(G);
        reversed = sap.reverse();
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

        Stack<Integer> s = new Stack<>();
        s.push(v);
        // Digraph reversed = sap.reverse();

        while (!s.isEmpty()) {

            int currenVertex = s.pop();
            BreadthFirstDirectedPaths bfs = new BreadthFirstDirectedPaths(reversed, currenVertex);
            if (bfs.hasPathTo(w)) {
                int[] result = new int[] { currenVertex, bfs.distTo(v) + bfs.distTo(w) };
                return result;
            } else {
                for (int vertex : sap.adj(currenVertex)) {
                    s.push(vertex);
                }
            }
        }

        return new int[] { -1, -1 };
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

        Stack<Integer> s = new Stack<>();
        for (int vertex : v) {
            s.push(vertex);
        }
        // Digraph reversed = sap.reverse();

        int[] currentShortest = new int[] { -1, INFINITY };
        while (!s.isEmpty()) {

            int currenVertex = s.pop();
            BreadthFirstDirectedPaths bfs = new BreadthFirstDirectedPaths(reversed, currenVertex);
            int[] found = shortestPathTo(bfs, w);
            if (found[0] != -1 && found[1] <= currentShortest[1]) {
                currentShortest[0] = currenVertex;
                currentShortest[1] = found[1] + shortestPathTo(bfs, v)[1];
            } else {
                for (int vertex : sap.adj(currenVertex)) {
                    s.push(vertex);
                }
            }
        }

        if (currentShortest[0] == -1) {
            return new int[] { -1, -1 };
        } else {
            return currentShortest;
        }
    }

    // might have to optimise to return the path with shortest distTo
    private int[] shortestPathTo(BreadthFirstDirectedPaths bfs, Iterable<Integer> w) {
        int shortestVertex = -1;
        int shortestDist = INFINITY;
        for (int vertex : w) {
            if (bfs.hasPathTo(vertex)) {
                int distVertex = bfs.distTo(vertex);
                if (distVertex <= shortestDist) {
                    shortestVertex = vertex;
                    shortestDist = distVertex;
                }
            }
        }
        return new int[] { shortestVertex, shortestDist };
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = sap.V();
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
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

        while (!StdIn.isEmpty()) {
            Stack<Integer> left = new Stack<>();

            int left_size = StdIn.readInt();
            for (int i = 0; i < left_size; i++) {
                left.push(StdIn.readInt());
            }

            StdOut.println("left stack");
            for (int n : left) {
                StdOut.printf("%d ", n);
            }
            StdOut.println();
            StdOut.println();

            Stack<Integer> right = new Stack<>();
            int right_size = StdIn.readInt();
            for (int i = 0; i < right_size; i++) {
                right.push(StdIn.readInt());
            }

            StdOut.println("right stack");
            for (int n : right) {
                StdOut.printf("%d ", n);
            }
            StdOut.println();

            int l = sap.length(left, right);
            int a = sap.ancestor(left, right);
            StdOut.printf("length = %d\t ancestor = %d\n", l, a);
        }

        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
