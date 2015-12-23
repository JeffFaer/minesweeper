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
  public Set<Point> getNeighbors(Point point) {
    Set<Point> neighbors = this.neighbors.apply(point);
    neighbors.removeIf(pt -> !isValid(pt));
    return neighbors;
  }

  @Override
  public Set<Point> getNeighbors(int row, int col) {
    return getNeighbors(new Point(row, col));
  }

  protected boolean isValid(Point point) {
    return 0 <= point.x && point.x < getNumRows() && 0 <= point.y && point.y < getNumColumns();
  }
}
