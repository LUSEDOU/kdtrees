import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;

public class PointSET {
    private final SET<Point2D> set;
    
    // construct an empty set of points
    public PointSET() {
        set = new SET<>();
    }

    // is the set empty?    
    public boolean isEmpty() {
        return set.isEmpty();
    }
    
    // number of points in the set 
    public int size() {
        if (set.isEmpty()) return 0;

        return set.size();
    }
    
    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        set.add(p);
    }
    
    // does the set contain point p? 
    public boolean contains(Point2D p) {
        if (set.isEmpty()) return false;
        if (p == null) throw new IllegalArgumentException();

        return set.contains(p);
    }
    
    // draw all points to standard draw 
    public void draw() {
        if (set.isEmpty()) return;

        for (Point2D point2d : set)
            point2d.draw();
    }

    // all points that are inside the rectangle (or on the boundary) 
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        if (set.isEmpty()) return null;

        Stack<Point2D> stack = new Stack<>();
        for (Point2D p : set) {
            if (p.x() > rect.xmax()) continue;
            if (p.x() < rect.xmin()) continue;
            if (p.y() > rect.ymax()) continue;
            if (p.y() < rect.ymin()) continue;
            stack.push(p);
        }
        return stack;
    }
    
    // a nearest neighbor in the set to point p; null if the set is empty 
    public           Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (set.isEmpty()) return null;

        return set.floor(p);
    }

    // unit testing of the methods (optional) 
    public static void main(String[] args) {

    }
}
