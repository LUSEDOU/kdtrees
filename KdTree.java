import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
    private static final boolean VERTICAL = true;
    //private static final boolean HORIZONTAL = false;

    private Node tree;
    private int size;
    public int n = 10;
    
    // construct an empty set of points
    public KdTree() {
        size = 0;
    }

    private static class Node {
        private Point2D p;
        private RectHV rect; 
        private Node lb;
        private Node rt;

        public Node(Point2D p, RectHV rect) {
            setP(p);
            setRect(rect);
        }

        public Point2D getP()               {   return p;           }
        public void setP(Point2D p)         {   this.p = p;         }
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
        tree = put(
            tree, 
            p, 
            VERTICAL, 
            new RectHV(0, 0, 1, 1)
        );
        size++;
    }

    private static Node put(Node h, Point2D p, boolean orientation, RectHV rect) {
        if (h == null)
            //StdOut.println("  final RECT: ["+rect.xmin()+", "+rect.ymin()+"] x ["+rect.xmax()+" ,"+rect.ymax()+"]" );
            return new Node(p, rect);

        int cmp = compare(orientation, p, h);
        
        if      (cmp < 0) h.setLb(setLb(h, p, !orientation, rect));
        else if (cmp > 0) h.setRt(setRt(h, p, !orientation, rect));
        // else              h.setPoint(p);
        return h;
    }
    
    private static int compare(boolean or, Point2D p, Node h) {
        if (or) {
            if (p.x() < h.getP().x()) return -1;
            else                      return +1;
        } else {
            if (p.y() < h.getP().y()) return -1;
            else                      return +1;
        }
    }

    // does the set contain point p? 
    public boolean contains(Point2D p) {
        //StdOut.println("   Point2D: "+p.toString());
        return get(p) != null;
    }

    private Point2D get(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return get(tree, p, VERTICAL);
    }

    private static Point2D get(Node x, Point2D p, boolean or) {
        while (x != null) {
            StdOut.print(p.toString() +" compare to " +x.getP().toString());
            int cmp = p.compareTo(x.getP());
            if          (cmp < 0) {
                x = x.getRt();
                StdOut.println("  RIGHT/UP");
            }
            else if     (cmp > 0) {
                x = x.getLb();
                StdOut.println("  LEFT/BOTTOM");
            }
            else              return x.getP();
        }
        return null;
    }

    private static Node setLb(Node h, Point2D p, boolean orientation, RectHV parent) {
        if (orientation)
            parent = new RectHV(parent.xmin(), parent.ymin(), parent.xmax(), h.getP().y());
        else 
            parent = new RectHV(parent.xmin(), parent.ymin(), h.getP().x(), parent.ymax());

        return put(h.getLb(), p, orientation, parent);
    }

    private static Node setRt(Node h, Point2D p, boolean orientation, RectHV parent) {
        if (orientation)
            parent = new RectHV(parent.xmin(), h.getP().y(), parent.xmax(), parent.ymax());
        else 
            parent = new RectHV(h.getP().x(), parent.ymin(), parent.xmax(), parent.ymax());

        return put(h.getRt(), p, orientation, parent);
    }

    // draw all points to standard draw 
    public void draw() {
        recursiveDraw(tree, VERTICAL);
    }

    private static void recursiveDraw(Node h, boolean orientation) {
        if (h == null) return;

        recursiveDraw(h.getRt(), !orientation);
        recursiveDraw(h.getLb(), !orientation);
        
        Point2D p = h.getP();
        RectHV rect = h.getRect();
        StdDraw.setPenRadius();
        if (orientation) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(p.x(), rect.ymin(), p.x(), rect.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(rect.xmin(), p.y(), rect.xmax(), p.y());
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        p.draw();
    }

    // all points that are inside the rectangle (or on the boundary) 
    public Iterable<Point2D> range(RectHV rect) {
        Point2D p = new Point2D(0, 0);
        Stack<Point2D> stack = new Stack<>();

        stack.push(p);
        return stack;
    }
    
    // a nearest neighbor in the set to point p; null if the set is empty 
    public Point2D nearest(Point2D p) {
        return p;
    }
 
    // unit testing of the methods (optional) 
    public static void main(String[] args) {
        KdTree kdtTree = new KdTree();
        In in = new In(args[0]);
        In in2 = new In(args[0]);
        int n = kdtTree.n;
        for (int i = 0; i < n; i++) {
            StdOut.print("insert #"+(i + 1)+" in PROGRESS ");
            double x = in.readDouble();
            double y = in.readDouble();
            
            kdtTree.insert(new Point2D(x, y));
            StdOut.println(" --SUCCESS");
            //StdOut.println("size: "+kdtTree.size());
        }

        StdOut.println();
        StdOut.println("-------------CONTAINS---------------");
        StdOut.println();

        for (int i = 0; i < n; i++) {
            double x = in2.readDouble();
            double y = in2.readDouble();
            Point2D p = new Point2D(x, y);
            StdOut.println(p.toString());
            StdOut.println(kdtTree.contains(p));
            StdOut.println();
        }
        kdtTree.draw();
    }
}
