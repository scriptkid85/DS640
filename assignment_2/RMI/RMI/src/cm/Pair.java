package cm;
import java.io.Serializable;

/**
 * The Pair class is used to send a pair of serialized objects, which implements the 
 * Serializable interface.
 * 
 * @author Guanyu Wang
 * */
public class Pair<L,R> implements Serializable{

 
  private static final long serialVersionUID = 1L;
  private final L left;
  private final R right;

  public Pair(L left, R right) {
    this.left = left;
    this.right = right;
  }

  public L getLeft() { return left; }
  public R getRight() { return right; }

  @Override
  public int hashCode() { return left.hashCode() ^ right.hashCode(); }

  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (!(o instanceof Pair)) return false;
    Pair pairo = (Pair) o;
    return this.left.equals(pairo.getLeft()) &&
           this.right.equals(pairo.getRight());
  }

}