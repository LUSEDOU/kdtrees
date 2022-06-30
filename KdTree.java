import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;

public class KdTree {
    private final Node tree;
    private int size;
    
    // construct an empty set of points
    public KdTree() {
        tree = new Node(
            null, 
            null, 
            null
        );

        size = 0;
    }

    private static class Node {
        private Point2D p;
        //private RectHV rect; 
        private Node lb;
        private Node rt;

        public Node(Point2D p, Node lb, Node rt) {
            setPoint(p);
            setLb(lb);
            setRt(rt);
        }

        public Point2D getPoint()       {   return p;       }
        public void setPoint(Point2D p) {   this.p = p;     }
        public Node getRt()             {   return rt;      }
        public void setRt(Node rt)      {   this.rt = rt;   }
        public Node getLb()             {   return lb;      }
        public void setLb(Node lb)      {   this.lb = lb;   }
    }

    public boolean isEmpty()    {   return size <= 0;   }   // is the set empty?    
    public int size()           {   return size;        }   // number of points in the set 
    
    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if  (isEmpty()) tree.setPoint(p);
        else            insert(tree, p, 0);

        size++;
    }

    private static Node insert(Node h, Point2D p, int controlator) {
        if (controlator > 1) return h;

        controlator++;
        int cmp = p.compareTo(h.getPoint());
        if      (cmp < 0) h.setLb(insert(h.getLb(), p, controlator - 1));
        else if (cmp > 0) h.setRt(insert(h.getRt(), p, controlator - 1));
        controlator--;
        return h;
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
