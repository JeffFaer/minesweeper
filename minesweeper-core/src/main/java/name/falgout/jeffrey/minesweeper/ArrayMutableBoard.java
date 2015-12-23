package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
import java.util.Set;
import java.util.function.Function;

public class ArrayMutableBoard extends AbstractMutableBoard {
  private final Square[][] board;

  public ArrayMutableBoard(int numRows, int numCols, Function<Point, Set<Point>> neighbors) {
    super(neighbors);
    board = new Square[numRows][numCols];
  }

  @Override
  public int getNumRows() {
    return board.length;
  }

  @Override
  public int getNumColumns() {
    return board[0].length;
  }

  @Override
  public Square getSquare(int i, int j) {
    return board[i][j];
  }

  @Override
  public void setSquare(int i, int j, Square s) {
    board[i][j] = s;
  }
}
