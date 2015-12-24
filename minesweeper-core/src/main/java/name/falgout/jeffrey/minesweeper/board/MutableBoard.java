package name.falgout.jeffrey.minesweeper.board;

import java.awt.Point;

public interface MutableBoard extends Board {
  default void setSquare(Point point, Square s) {
    setSquare(point.x, point.y, s);
  }

  public void setSquare(int row, int col, Square s);
}
