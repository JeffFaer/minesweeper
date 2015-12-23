package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractMutableBoard implements MutableBoard {
  private final Function<Point, Set<Point>> neighbors;

  protected AbstractMutableBoard(Function<Point, Set<Point>> neighbors) {
    this.neighbors = neighbors;
  }

  @Override
  public Set<Point> getNeighbors(Point p) {
    Set<Point> neighbors = this.neighbors.apply(p);
    neighbors.removeIf(pt -> !isValid(pt));
    return neighbors;
  }

  protected boolean isValid(Point p) {
    return 0 <= p.x && p.x < getNumRows() && 0 <= p.y && p.y < getNumColumns();
  }
}
