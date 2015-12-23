package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
import java.util.Set;
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
}
