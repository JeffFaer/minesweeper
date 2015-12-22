package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public enum NeighborFunction implements Function<Point, Set<Point>> {
  PLUS {
    @Override
    public Set<Point> apply(Point t) {
      Set<Point> neighbors = new LinkedHashSet<>(4);
      neighbors.add(new Point(t.x - 1, t.y));
      neighbors.add(new Point(t.x + 1, t.y));
      neighbors.add(new Point(t.x, t.y - 1));
      neighbors.add(new Point(t.x, t.y + 1));
      return neighbors;
    }
  },
  CIRCLE {
    @Override
    public Set<Point> apply(Point t) {
      Set<Point> neighbors = new LinkedHashSet<>(8);
      neighbors.addAll(PLUS.apply(t));
      neighbors.add(new Point(t.x - 1, t.y - 1));
      neighbors.add(new Point(t.x + 1, t.y + 1));
      neighbors.add(new Point(t.x + 1, t.y - 1));
      neighbors.add(new Point(t.x - 1, t.y + 1));

      return neighbors;
    }
  };

  @Override
  public abstract Set<Point> apply(Point t);

  public static Function<Point, Set<Point>> wrapAround(Function<Point, Set<Point>> neighborFunction, int numRows,
      int numColumns) {
    return neighborFunction.andThen(neighbors -> {
      for (Point neighbor : neighbors) {
        if (neighbor.x < 0) {
          neighbor.x %= numRows;
          neighbor.x += numRows;
        }
        if (neighbor.y < 0) {
          neighbor.y %= numColumns;
          neighbor.y += numColumns;
        }
      }

      return neighbors;
    });
  }
}
