package name.falgout.jeffrey.minesweeper;

import java.awt.Point;

public interface MutableBoard extends Board {
  default void setSquare(Point p, Square s) {
    setSquare(p.x, p.y, s);
  }

  public void setSquare(int i, int j, Square s);
}
