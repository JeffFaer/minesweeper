package name.falgout.jeffrey.minesweeper.board;

import java.awt.Point;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractMutableBoard implements MutableBoard {
  private final Function<? super Point, ?extends Set<? extends Point>> neighbors;

  protected AbstractMutableBoard(Function<? super Point, ? extends Set<? extends Point>> neighbors) {
    this.neighbors = neighbors;
  }

  @Override
  public Set<? extends Point> getNeighbors(Point point) {
    Set<? extends Point> neighbors = this.neighbors.apply(point);
    neighbors.removeIf(pt -> !isValid(pt));
    return neighbors;
  }

  @Override
  public Set<? extends Point> getNeighbors(int row, int col) {
    return getNeighbors(new Point(row, col));
  }

  protected boolean isValid(Point point) {
    return 0 <= point.x && point.x < getNumRows() && 0 <= point.y && point.y < getNumColumns();
  }
}
