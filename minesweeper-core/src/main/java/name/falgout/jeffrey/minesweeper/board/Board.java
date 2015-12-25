package name.falgout.jeffrey.minesweeper.board;

import java.awt.Point;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface Board {
  public static interface Square {
    public static enum Basic implements Square {
      MINE, UNKNOWN;

      @Override
      public boolean isMine() {
        return this == MINE;
      }

      @Override
      public boolean isNumber() {
        return false;
      }

      @Override
      public int getNumber() {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean isRevealed() {
        return isMine();
      }
    }

    public static class Number implements Square {
      private final int number;

      public Number(int number) {
        this.number = number;
      }

      @Override
      public boolean isMine() {
        return false;
      }

      @Override
      public boolean isNumber() {
        return true;
      }

      @Override
      public int getNumber() {
        return number;
      }

      @Override
      public boolean isRevealed() {
        return true;
      }

      @Override
      public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Number [number=");
        builder.append(number);
        builder.append("]");
        return builder.toString();
      }
    }

    public boolean isMine();

    public boolean isNumber();

    public int getNumber();

    public boolean isRevealed();
  }

  public int getNumRows();

  public int getNumColumns();

  default int size() {
    return getNumRows() * getNumColumns();
  }

  default Stream<Point> getValidIndexes() {
    return IntStream.range(0, getNumRows()).boxed().flatMap(
        row -> IntStream.range(0, getNumColumns()).mapToObj(col -> new Point(row, col)));
  }

  default Square getSquare(Point point) {
    return getSquare(point.x, point.y);
  }

  public Square getSquare(int row, int col);

  default Set<Point> getNeighbors(Point point) {
    return getNeighbors(point.x, point.y);
  }

  public Set<Point> getNeighbors(int row, int col);

  default Set<Point> getNeighborsBySquare(Point point, Predicate<? super Square> test) {
    return getNeighbors(point, (Point p) -> test.test(getSquare(p)));
  }

  default Set<Point> getNeighbors(Point point, Predicate<? super Point> test) {
    Set<Point> neighbors = getNeighbors(point);
    neighbors.removeIf(p -> !test.test(p));
    return neighbors;
  }

  default Stream<Point> getSquares(Predicate<? super Square> test) {
    return getValidIndexes().filter(p -> test.test(getSquare(p)));
  }
}
