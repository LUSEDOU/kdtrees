import java.security.Key;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;

public class KdTree {
    private final static boolean VERTICAL = true;
    private final static boolean HORIZONTAL = false;
    private final Node tree;
    private final int size;
    
    // construct an empty set of points
    public KdTree() {
        tree = new Node(
            null, 
            null, 
            null, 
            VERTICAL, 
            0
        );

        size = 0;
    }

    private static class Node {
        private final Key key;
        private final Node left;
        private final Node right;
        private final boolean divide;
        private final int rank;

        public Node(Key key, Node left, Node right, boolean divide, int rank) {
            this.key = key;
            this.divide = divide;
            this.left = left;
            this.right = right;
            this.rank = rank;
        }

        public Key getKey()         {   return key;     }
        public Node getRight()      {   return right;   }
        public Node getLeft()       {   return left;    }
        public boolean getDivide()  {   return divide;  }
        public int getRank()        {   return rank;    }
    }

    // is the set empty?    
    public boolean isEmpty() {
        return size <= 0;
    }
    
    // number of points in the set 
    public int size() {
        return size;
    }
    
    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {

    }
    
    // does the set contain point p? 
    public boolean contains(Point2D p) {
        return false;
    }
    
    // draw all points to standard draw 
    public void draw() {

    }

    // all points that are inside the rectangle (or on the boundary) 
    public Iterable<Point2D> range(RectHV rect) {
        Point2D p = new Point2D(0, 0);
        Stack<Point2D> stack = new Stack<>();

        stack.push(p);
        return stack;
    }
    
    // a nearest neighbor in the set to point p; null if the set is empty 
    public           Point2D nearest(Point2D p) {
        return p;
    }
 
    // unit testing of the methods (optional) 
    public static void main(String[] args) {

    }
}
