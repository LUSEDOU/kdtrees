import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

/**
 * 
 * 
 * @author LUSEDOU
 */
public class KdTree {
    private static final boolean VERTICAL = true;

    private Node root;      // root of the KdTree
    private int size;       // size of the root

    /**
     *  The {@code Node} class is an immutable data type to encapsulate a
     *  {@code Point2D} and other two {@code Node} lb, and rt.
     *  <p>
     *  Note: this class is only for helper the {@code KdTree} class
     */
    private static class Node {
        private Point2D p;      // Point2D
        private Node lb, rt;    // links to left and right

        /**
         * Initializes a {@code Node} which contains two null {@code Node}
         * lb, rt, and save the point parameter.
         * 
         * @param p the Point2D
         */
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
            checkNull(prnt);
            checkNull(rect);

            // Compares this point with a other node's point
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
    
    /**
     * Construct an empty KdTree and initialize size to zero
     */
    public KdTree() {
        root = null;
        size = 0;
    }

    /**
     * Is the set empty?
     * 
     * @return true if the root is null
     */
    public boolean isEmpty() {   
        return root == null;    
    }

    /**
     * Number of points in the {@code KdTree}
     * 
     * @return  size
     */
    public int size() {
        return size;            
    } 
    
    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        checkNull(p);
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
        checkNull(p);
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
        checkNull(p);
        return isEmpty() 
                ? null 
                : nearest(p, root.getP(), root, root, new RectHV(0, 0, 1, 1), VERTICAL);
    }

    /**
     * idk what this do exactly and why works
     * The API was a little confuse
     * 
     * Credits: Micong Huang
     * 
     * @param trgt
     * @param clst
     * @param node
     * @param prnt
     * @param rect
     * @param or
     * @return
     */
    private Point2D nearest(Point2D trgt, Point2D clst, Node node, Node prnt, RectHV rect, boolean or) {
        if (node == null) return clst;

        double clstDist = clst.distanceTo(trgt);
        rect = node.getRect(prnt, rect, or);

        if (rect.distanceTo(trgt) < clstDist) {
            double nodeDist = node.getP().distanceTo(trgt);

            if (nodeDist < clstDist) clst = node.getP();
            
            int cmp = compare(or, node.getP(), trgt);
            or = !or;

            if (cmp > 0) {
                clst = nearest(trgt, clst, node.getLb(), node, rect, or);
                clst = nearest(trgt, clst, node.getRt(), node, rect, or);
            } else {
                clst = nearest(trgt, clst, node.getRt(), node, rect, or);
                clst = nearest(trgt, clst, node.getLb(), node, rect, or);
            }
        }
        return clst;
    }


    /**
     * Compares two {@code Point2D} p and q. Unlike Point2D's {@code compareTo} method
     * 'tis is compatible with the orientation.
     * 
     * @param or the orientation of the Node (expressed in a boolean)
     * @param p  the point comparator
     * @param q  the point comparable
     * @return   {@code 0} if they're equals;
     *           {@code 1} if their {@code x} are equals but comparator y is greater,
     *                     if their {@code y} are equals but comparator x is greater,
     *                     if comparator a coordinate are greater depending on orientation;
     *           {@code -1} otherwise;
     */
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
            if         (p.x() < q.x())    return -1;
            else /* if (p.x() > q.x()) */ return +1;
            // else return 0;
        } 
        else {
            if         (p.y() < q.y())    return -1;
            else /* if (p.y() > q.y()) */ return +1;
            // else return 0;
        }
    }

    private static void checkNull(Object obj) {
        if (obj == null) 
            throw new NullPointerException();
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
