import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;

public class KdTree {
    final int a;
    
    // construct an empty set of points
    public KdTree() {
        a = 0;
    }

    // is the set empty?    
    public boolean isEmpty() {
        return false;
    }
    
    // number of points in the set 
    public int size() {
        return a;
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
