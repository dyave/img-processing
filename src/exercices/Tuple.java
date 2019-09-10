package exercices;

import java.lang.NullPointerException;

public class Tuple<X, Y> implements Comparable<Tuple<X,Y>> { 
    public final X left; 
    public final Y right; 

    public Tuple(X x, Y y) { 
        this.left = x; 
        this.right = y; 
    }

    public boolean equals(Object obj) {
        if (obj instanceof Tuple) {
            if (this.left.equals(((Tuple) obj).left) &&
                this.right.equals(((Tuple) obj).right)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Tuple<X,Y> t) {
        if (t == null) {
            throw new NullPointerException();
        } else if (this.equals(t)) {
            return 0;
        } else {
            try {
                return 
                    ((Integer) t.left  - (Integer) this.left) / 
                    ((Integer) t.right - (Integer) this.right);
                
            } catch (ArithmeticException e) {
                return ((Integer) t.left  - (Integer) this.left);
            }
        }
    }
}