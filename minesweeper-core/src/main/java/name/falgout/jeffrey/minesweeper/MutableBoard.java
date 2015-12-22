package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class MutableBoard implements Board {
  private final Square[][] board;
  private final Function<Point, Set<Point>> neighbors;

  public MutableBoard(int numRows, int numCols, Function<Point, Set<Point>> neighbors) {
    board = new Square[numRows][numCols];
    this.neighbors = neighbors;
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

  public void setSquare(Point p, Square s) {
    setSquare(p.x, p.y, s);
  }

  public void setSquare(int i, int j, Square s) {
    board[i][j] = s;
  }

  @Override
  public Set<Point> getNeighbors(Point p) {
    Set<Point> neighbors = this.neighbors.apply(p);
    
    Predicate<Point> valid = this::isValid;
    Predicate<Point> notValid = valid.negate();
    neighbors.removeIf(notValid);
    return neighbors;
  }

  private boolean isValid(Point p) {
    return 0 <= p.x && p.x < getNumRows() && 0 <= p.y && p.y < getNumColumns();
  }
}
