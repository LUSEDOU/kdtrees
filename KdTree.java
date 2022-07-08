import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
    private static final boolean VERTICAL = true;

    private Node root;      // root of the KdTree
    private int size;       // size of the root
    
    /**
     * Initializes an empty symbol table and set 
     * size to zero
     */
    public KdTree() {
        size = 0;
    }

    // KdTree helper node data type
    private static class Node {
        private Point2D p;      // Point2D
        private Node lb, rt;    // links to left and right

        public Node(Point2D p) {
            setP(p);
        }

        public Point2D getP()               {   return p;           }
        public void setP(Point2D p)         {   this.p = p;         }
        public Node getRt()                 {   return rt;          }
        public void setRt(Node rt)          {   this.rt = rt;       }
        public Node getLb()                 {   return lb;          }
        public void setLb(Node lb)          {   this.lb = lb;       }

        public RectHV getRect(Node prnt, RectHV rect, boolean or) {
            if (prnt == null || rect == null) 
                    throw new IllegalArgumentException();

            int cmp = compare(!or, this.getP(), prnt.getP());

            if      (cmp > 0) {
                if (or)
                    return new RectHV(rect.xmin(), 
                        prnt.getP().y(), rect.xmax(), rect.ymax());
                else 
                    return new RectHV(prnt.getP().x(), 
                        rect.ymin(), rect.xmax(), rect.ymax());
            }
            else if (cmp < 0) {
                if  (or)
                    return new RectHV(rect.xmin(), 
                        rect.ymin(), rect.xmax(), prnt.getP().y());
                else 
                    return new RectHV(rect.xmin(), 
                        rect.ymin(), prnt.getP().x(), rect.ymax());
            }
            else return rect;
        }
    }

    public boolean isEmpty()    {   return root == null;    }   // is the set empty?    
    public int size()           {   return size;            }   // number of points in the set 
    
    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        root = put(
            root, 
            p, 
            VERTICAL
        );
        size++;
    }

    // does the set contain point p? 
    public boolean contains(Point2D p) {
        //StdOut.println("   Point2D: "+p.toString());
        return get(p) != null;
    }

    private Point2D get(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return get(root, p, VERTICAL);
    }

    private static Point2D get(Node prnt, Point2D p, boolean or) {
        while (prnt != null) {
            int cmp = compare(or, p, prnt.getP());
            or = !or;
            if          (cmp < 0)   prnt = prnt.getLb();
            else if     (cmp > 0)   prnt = prnt.getRt();
            else                    return prnt.getP();
        }
        return null;
    }

    private static Node put(Node h, Point2D p, boolean orientation) {
        //StdOut.println(rect.toString());
        if (h == null)
            //StdOut.println("  final RECT: ["+rect.xmin()+", "+rect.ymin()+"] x ["+rect.xmax()+" ,"+rect.ymax()+"]" );
            return new Node(p);

        int cmp = compare(orientation, p, h.getP());
        
        if      (cmp < 0) h.setLb(put(h.getLb(), p, !orientation));
        else if (cmp > 0) h.setRt(put(h.getRt(), p, !orientation));
        //else              return null;

        return h;
    }

    private static int compare(boolean or, Point2D p, Point2D q) {
        if (p.equals(q)) return 0;

        else if (p.x() == q.x()) {
            if (p.y() > q.y()) return +1;
            else return -1;
        }
        else if (p.y() == q.y()) {
            if (p.x() > q.x()) return +1;
            else return -1;
        } 
        else if (or) {
            if (p.x() < q.x()) return -1;
            else if (p.x() > q.x()) return +1;
            else return 0;
        } 
        else {
            if (p.y() < q.y()) return -1;
            else if (p.y() > q.y()) return +1;
            else return 0;
        }
    }

    // draw all points to standard draw 
    public void draw() {
        recursiveDraw(root, root, VERTICAL, new RectHV(0, 0, 1, 1));
    }

    private static void recursiveDraw(Node prnt, Node chld, boolean or, RectHV rect) {
        if (chld == null) return;

        rect = chld.getRect(prnt, rect, or);

        double width = rect.xmax() - rect.xmin();
        double height = rect.ymax() - rect.ymin();
        if (or) 
            StdDraw.setPenColor(StdDraw.CYAN);
        else 
            StdDraw.setPenColor(StdDraw.ORANGE);
        StdDraw.filledRectangle(
            rect.xmax() - (width / 2), rect.ymax() - (height / 2), 
            width / 2, height / 2
        );

        recursiveDraw(chld, chld.getRt(), !or, rect);
        recursiveDraw(chld, chld.getLb(), !or, rect);
        
        Point2D p = chld.getP();
        StdDraw.setPenRadius();
        if (or) {
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
        
        stack = recursiveRange(root, root, stack, VERTICAL, new RectHV(0, 0, 1, 1));
        return stack;
    }
    
    private Stack<Point2D> recursiveRange(Node prnt, Node chld, Stack<Point2D> stack, boolean or, RectHV r) {
        if (chld == null) return stack;

        r = chld.getRect(prnt, r, or);
        Point2D p = chld.getP();
        or = !or;
        if (!or) {
            if (r.xmin() < p.x()) 
                stack = recursiveRange(chld, chld.getLb(), stack, or, r);
            if (r.xmax() > p.x()) 
                stack = recursiveRange(chld, chld.getRt(), stack, or, r);
        }
        else {
            if (r.ymin() < p.y())
                stack = recursiveRange(chld, chld.getLb(), stack, or, r);
            if (r.ymax() > p.y()) 
                stack = recursiveRange(chld, chld.getRt(), stack, or, r);
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
