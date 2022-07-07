import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
    private static final boolean VERTICAL = true;

    private Node tree;
    private int size;
    
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
        Node insertTree = put(
            tree, 
            p, 
            VERTICAL, 
            new RectHV(0, 0, 1, 1)
        );
        if (insertTree != null) {
            tree = insertTree;
            size++;
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
            int cmp = compare(or, p, x);
            or = !or;
            if          (cmp < 0)   x = x.getLb();
            else if     (cmp > 0)   x = x.getRt();
            else                    return x.getP();
        }
        return null;
    }

    private static Node put(Node h, Point2D p, boolean orientation, RectHV rect) {
        //StdOut.println(rect.toString());
        if (h == null)
            //StdOut.println("  final RECT: ["+rect.xmin()+", "+rect.ymin()+"] x ["+rect.xmax()+" ,"+rect.ymax()+"]" );
            return new Node(p, rect);

        int cmp = compare(orientation, p, h);
        
        if      (cmp < 0) h.setLb(setLb(h, p, !orientation, rect));
        else if (cmp > 0) h.setRt(setRt(h, p, !orientation, rect));
        //else              return null;

        return h;
    }

    private static int compare(boolean or, Point2D p, Node h) {
        if (p.equals(h.getP())) return 0;

        else if (p.x() == h.getP().x()) {
            if (p.y() > h.getP().y()) return +1;
            else return -1;
        }
        else if (p.y() == h.getP().y()) {
            if (p.x() > h.getP().x()) return +1;
            else return -1;
        } 
        else if (or) {
            if (p.x() < h.getP().x()) return -1;
            else if (p.x() > h.getP().x()) return +1;
            else return 0;
        } 
        else {
            if (p.y() < h.getP().y()) return -1;
            else if (p.y() > h.getP().y()) return +1;
            else return 0;
        }
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
        RectHV rect = h.getRect();
        double width = rect.xmax() - rect.xmin();
        double height = rect.ymax() - rect.ymin();
        if (orientation) 
            StdDraw.setPenColor(StdDraw.CYAN);
        else 
            StdDraw.setPenColor(StdDraw.ORANGE);
        StdDraw.filledRectangle(
            rect.xmax() - (width / 2), rect.ymax() - (height / 2), 
            width / 2, height / 2
        );

        recursiveDraw(h.getRt(), !orientation);
        recursiveDraw(h.getLb(), !orientation);
        
        Point2D p = h.getP();
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
        if (rect == null) throw new IllegalArgumentException();

        Stack<Point2D> stack = new Stack<>();
        if (!tree.getRect().intersects(rect)) return stack;
        
        stack = recursiveRange(tree, stack, VERTICAL);
        return stack;
    }
    
    private Stack<Point2D> recursiveRange(Node h, Stack<Point2D> stack, boolean or) {
        if (h == null) return stack;

        RectHV r = h.getRect();
        Point2D p = h.getP();
        or = !or;
        if (!or) {
            if (r.xmin() < p.x()) 
                stack = recursiveRange(h.getLb(), stack, or);
            if (r.xmax() > p.x()) 
                stack = recursiveRange(h.getRt(), stack, or);
        }
        else {
            if (r.ymin() < p.y())
                stack = recursiveRange(h.getLb(), stack, or);
            if (r.ymax() > p.y()) 
                stack = recursiveRange(h.getRt(), stack, or);
        }

        if (r.contains(p)) stack.push(p);

        return stack;
    }

    // a nearest neighbor in the set to point p; null if the set is empty 
    public Point2D nearest(Point2D p) {
        return p;
    }
 
    // unit testing of the methods (optional) 
    public static void main(String[] args) {
        In in = new In(args[0]);
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        StdOut.println(kdtree.size);
        kdtree.draw();
    }
}
