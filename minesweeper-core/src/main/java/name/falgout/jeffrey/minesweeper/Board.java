package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
import java.util.Set;

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

  public Set<Point> getValidIndexes();

  default Square getSquare(Point point) {
    return getSquare(point.x, point.y);
  }

  public Square getSquare(int i, int j);

  public Set<Point> getNeighbors(Point p);

  default Set<Point> getNeighbors(int i, int j) {
    return getNeighbors(new Point(i, j));
  }
}
