import edu.princeton.cs.algs4.Draw;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;

    private Node tree;
    private int size;
    
    // construct an empty set of points
    public KdTree() {
        size = 0;
        tree = new Node(null, new RectHV(0, 0, 1, 1));
    }

    private static class Node {
        private Point2D p;
        private RectHV rect; 
        private Node lb;
        private Node rt;

        public Node(Point2D p, RectHV rect) {
            setPoint(p);
            setRect(rect);
        }

        public Point2D getPoint()           {   return p;           }
        public void setPoint(Point2D p)     {   this.p = p;         }
        public Node getRt()                 {   return rt;          }
        public void setRt(Node rt)          {   this.rt = rt;       }
        public Node getLb()                 {   return lb;          }
        public void setLb(Node lb)          {   this.lb = lb;       }
        public RectHV getRect()             {   return rect;        }
        public void setRect(RectHV rect)    {   this.rect = rect;   }
    }

    public boolean isEmpty()    {   return tree == null;    }   // is the set empty?    
    public int size()           {   return size;            }   // number of points in the set 
    
    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        tree = put(tree, p, VERTICAL, null);
        size++;
    }

    private static Node put(Node h, Point2D p, boolean orientation, RectHV rect) {
        if (h == null) return new Node(p, rect);

        int cmp = p.compareTo(h.getPoint());
        orientation = !orientation;

        rect = getRectHV(rect, h.getPoint(), cmp, orientation);

        if      (cmp < 0) h.setLb(put(h.getLb(), p, orientation, rect));
        else if (cmp > 0) h.setRt(put(h.getRt(), p, orientation, rect));
        // else              h.setPoint(p);

        return h;
    }
    
    // does the set contain point p? 
    public boolean contains(Point2D p) {
        return get(p) != null;
    }

    private Point2D get(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return get(tree, p);
    }

    private static Point2D get(Node x, Point2D p) {
        while (x != null) {
            int cmp = p.compareTo(x.getPoint());
            if          (cmp > 0) x = x.getRt();
            else if     (cmp < 0) x = x.getLb();
            else              return x.getPoint();
        }
        return null;
    }

    private static RectHV getRectHV(RectHV parent, Point2D p, int cmp, boolean orientation) {
        if (parent == null) return new RectHV(0, 0, 1, 1);

        double xmin = parent.xmin(), ymin = parent.ymin();
        double xmax = parent.xmax(), ymax = parent.ymax();

        if (orientation) {
            if (cmp > 0) xmax = p.x();
            else         xmin = p.x();
        }
        else if (cmp > 0)   ymax = p.y();
            else            ymin = p.y();

        return new RectHV(xmin, ymin, xmax, ymax);
    }

    // draw all points to standard draw 
    public void draw() {
        recursiveDraw(tree, VERTICAL);
    }

    private static void recursiveDraw(Node h, boolean orientation) {
        if (h == null) return;

        recursiveDraw(h.getLb(), !orientation);
        recursiveDraw(h.getRt(), !orientation);
        
        Point2D p = h.getPoint();
        RectHV rect = h.getRect();

        if (orientation) StdDraw.line(p.x(), rect.ymax(), p.x(), rect.ymin());
        else StdDraw.line(rect.xmin(), p.y(), rect.xmax(), p.y());
        h.getPoint().draw();
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
        KdTree kdtTree = new KdTree();
        In in = new In(args[0]);
        In in2 = new In(args[0]);
        for (int i = 0; i < 10; i++) {
            StdOut.print("insert #"+(i + 1)+" in PROGRESS ");
            double x = in.readDouble();
            double y = in.readDouble();
            
            kdtTree.insert(new Point2D(x, y));
            StdOut.println(" --SUCCESS");
            StdOut.println("size: "+kdtTree.size());
        }

        StdOut.println();
        StdOut.println("-------------CONTAINS---------------");
        StdOut.println();

        for (int i = 0; i < 10; i++) {
            double x = in2.readDouble();
            double y = in2.readDouble();
            Point2D p = new Point2D(x, y);
            StdOut.println(p.toString());
            StdOut.println(kdtTree.contains(p));
            StdOut.println();
        }
    }
}
