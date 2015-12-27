package name.falgout.jeffrey.minesweeper.board;

import java.awt.Point;
import java.util.Set;
import java.util.function.Function;

public class ArrayBoard extends AbstractMutableBoard {
  private final Square[][] board;

  public ArrayBoard(int numRows, int numCols,
      Function<? super Point, ? extends Set<? extends Point>> neighbors) {
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
  public Square getSquare(int row, int col) {
    return board[row][col];
  }

  @Override
  public void setSquare(int row, int col, Square s) {
    board[row][col] = s;
  }
}
