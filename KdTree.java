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
    private enum Orientation {
        VERTICAL, 
        HORIZONTAL,
    };

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
        
        public RectHV getRectRightOrTop(RectHV rect, Orientation or) {
            return isVertical(or)
                    ? new RectHV(rect.xmin(), this.getP().y(), 
                                 rect.xmax(), rect.ymax())
                    : new RectHV(this.getP().x(), rect.ymin(), 
                                 rect.xmax(), rect.ymax());
        }

        public RectHV getRectLeftOrBottom(RectHV rect, Orientation or) {
            return isVertical(or)
                    ? new RectHV(rect.xmin(), rect.ymin(), 
                                 rect.xmax(), this.getP().y())
                    : new RectHV(rect.xmin(), rect.ymin(), 
                                 this.getP().x(), rect.ymax());
        }

        public boolean isRightOrTop(Point2D q, Orientation or) {
            return isVertical(or)
                ? isRight(q)
                : isTop(q);
        }

        public boolean isLeftOrBottom(Point2D q, Orientation or) {            
            return isVertical(or)
                ? isLeft(q)
                : isBottom(q);
    
        }

        private boolean isLeft(Point2D q) {
            return this.getP().x() == q.x()
                    ? this.getP().y() > q.y()
                    : this.getP().x() > q.x();
        }

        private boolean isRight(Point2D q) {
            return this.getP().x() == q.x()
                    ? this.getP().y() < q.y()
                    : this.getP().x() < q.x();
        }

        private boolean isTop(Point2D q) {
            return this.getP().y() == q.y()
                    ? this.getP().x() < q.x()
                    : this.getP().y() < q.y();
        }
        
        private boolean isBottom(Point2D q) {
            return this.getP().y() == q.y()
                    ? this.getP().x() > q.x()
                    : this.getP().y() > q.y();
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

        Object[] put = insert(root, p, Orientation.VERTICAL);
        root = (Node) put[0];
        if ((boolean) put[1]) ++size;
    }

    private static Object[] insert(
        Node current, 
        Point2D p, 
        Orientation orientation
    ) {
        if (current == null) {
            Object[] put = {
                new Node(p),
                true,
            };
            return put;
        }

        boolean newPoint = false;
        if (current.isRightOrTop(p, orientation)) {
            orientation = changeOr(orientation);
            Object[] response = insert(current.getRt(), p, orientation);

            current.setRt((Node) response[0]);
            newPoint = (boolean) response[1];
        }
        else if (current.isLeftOrBottom(p, orientation)) {
            orientation = changeOr(orientation);
            Object[] response = insert(current.getLb(), p, orientation);

            current.setLb((Node) response[0]);
            newPoint = (boolean) response[1];
        }

        Object[] put = {current, newPoint};
        return put;
    }

    // does the set contain point p? 
    public boolean contains(Point2D p) {
        checkNull(p);
        return contains(root, p, Orientation.HORIZONTAL);
    }

    private static boolean contains(Node current, Point2D p, Orientation or) {
        while (current != null) {
            or = changeOr(or);
            
            if      (current.isLeftOrBottom(p, or)) current = current.getLb();
            else if (current.isRightOrTop(p, or))   current = current.getRt();
            else                                    return true;
        }
        return false;
    }

    // draw all points to standard draw 
    public void draw() {
        if (root == null) return;

        recursiveDraw(
            root, 
            Orientation.VERTICAL, 
            new RectHV(0, 0, 1, 1)
        );
    }

    private static void recursiveDraw(
        Node current, 
        Orientation orientation, 
        RectHV rect
    ) {
        if (current == null) return;

        // Draw this Node
        Point2D p = current.getP();
        StdDraw.setPenRadius();
        if (isVertical(orientation)) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(p.x(), rect.ymin(), p.x(), rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(rect.xmin(), p.y(), rect.xmax(), p.y());
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        p.draw();

        // Draw children nodes
        orientation = changeOr(orientation);
        
        // Right node
        recursiveDraw(
            current.getRt(), 
            orientation, 
            current.getRectRightOrTop(rect, orientation)
        );

        // Left Node
        recursiveDraw(
            current.getLb(), 
            orientation, 
            current.getRectLeftOrBottom(rect, orientation)
        );
    }

    // all points that are inside the rectangle (or on the boundary) 
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();

        return putInStack(
            new Stack<>(), 
            range(
                root, 
                new Stack<>(), 
                Orientation.VERTICAL, 
                rect
            )
        );
    }
    
    private Stack<Point2D> range(
        Node current, 
        Stack<Point2D> stack,
        Orientation or,
        RectHV rect
    ) {
        if (current == null) return stack;

        Point2D p = current.getP();
        Stack<Point2D> child;

        // Vertical Point
        if (isVertical(or)) {
            or = changeOr(or);

            //  Left child
            if (rect.xmin() <= p.x()) {
                child = range(
                    current.getLb(), 
                    new Stack<>(), 
                    or, rect
                );
                stack = putInStack(stack, child);
            }
            // Right child
            if (rect.xmax() >= p.x()) {
                child = range(
                    current.getRt(), 
                    new Stack<>(), 
                    or, rect
                );
                stack = putInStack(stack, child);
            }
        // Horizontal Point
        } else {
            or = changeOr(or);

            // Bottom child
            if (rect.ymin() <= p.y()) {
                child = range(
                    current.getLb(), 
                    new Stack<>(), 
                    or, rect
                );
                stack = putInStack(stack, child);
            }
            // Top child
            if (rect.ymax() >= p.y()) {
                child = range(
                    current.getRt(), 
                    new Stack<>(), 
                    or, rect
                );
                stack = putInStack(stack, child);
            }
        }

        // Put this point in the stack
        if (rect.contains(p)) stack.push(p);

        return stack;
    }

    // a nearest neighbor in the set to point p; null if the set is empty 
    public Point2D nearest(Point2D p) {
        checkNull(p);

        return isEmpty() 
                ? null 
                : nearest(
                    p, 
                    root.getP(), 
                    Orientation.VERTICAL,
                    root,
                    new RectHV(0, 0, 1, 1)
                );
    }

    /**
     * idk what this do exactly and why works
     * The API was a little confuse
     * 
     * Credits: Micong Huang
     * 
     * @param target
     * @param closest
     * @param node
     * @param parent
     * @param rect
     * @param or
     * @return
     */
    private Point2D nearest(
        Point2D target, 
        Point2D closest, 
        Orientation or,
        Node current, 
        RectHV rect
    ) {
        if (current == null) return closest;

        double closestDist = closest.distanceSquaredTo(target);

        if (rect.distanceSquaredTo(target) < closestDist) {
            double nodeDist = current.getP().distanceSquaredTo(target);

            if (nodeDist < closestDist) closest = current.getP();
            
            if (current.isRightOrTop(target, or)) {
                or = changeOr(or);

                // Left or Bottom child
                closest = nearest(
                    target, closest, or,
                    current.getLb(), 
                    current.getRectLeftOrBottom(rect, or)
                );

                // Right or Top child
                closest = nearest(
                    target, closest, or, 
                    current.getRt(), 
                    current.getRectRightOrTop(rect, or)
                );
            } else {
                or = changeOr(or);

                // Right or Top child
                closest = nearest(
                    target, closest, or, 
                    current.getRt(), 
                    current.getRectRightOrTop(rect, or)
                );

                // Left or Bottom child
                closest = nearest(
                    target, closest, or,
                    current.getLb(), 
                    current.getRectLeftOrBottom(rect, or)
                );
            }
        }
        return closest;
    }

    private static Stack<Point2D> putInStack(Stack<Point2D> parent, Stack<Point2D> child) {
        if (child.isEmpty()) return parent;

        for (Point2D point2d : child)
            parent.push(point2d);
        return parent;
    }

    private static void checkNull(Object obj) {
        if (obj == null) 
            throw new NullPointerException();
    }

    private static Orientation changeOr(Orientation or) {
        Orientation[] values = Orientation.values();
        int newOrdinal = (or.ordinal() + 1) % values.length;
        return values[newOrdinal];
    }

    private static boolean isVertical(Orientation or) {
        return or == Orientation.VERTICAL;
    }

    // unit testing of the methods (optional) 
    public static void main(String[] args) {
        In in = new In(args[0]);
        KdTree kdTree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdTree.insert(p);
        }
        StdOut.println(kdTree.size);

        RectHV rect = new RectHV(0.25, 0.25, 1, 0.625);
        for (Point2D p : kdTree.range(rect)) {
            StdOut.print(p+" ");
        }
        kdTree.draw();
    }
}
