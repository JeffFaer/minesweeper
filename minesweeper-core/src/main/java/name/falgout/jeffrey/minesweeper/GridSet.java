package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.stream.IntStream;

class GridSet extends AbstractSet<Point> {
  private final int numRows;
  private final int numCols;

  public GridSet(int numRows, int numCols) {
    this.numRows = numRows;
    this.numCols = numCols;
  }

  public boolean contains(int i, int j) {
    return 0 <= i && i < numRows && 0 <= j && j < numCols;
  }

  @Override
  public boolean contains(Object o) {
    if (o instanceof Point) {
      Point p = (Point) o;
      return contains(p.x, p.y);
    } else {
      return false;
    }
  }

  @Override
  public Iterator<Point> iterator() {
    return IntStream.range(0, numRows).boxed().<Point> flatMap(
        row -> IntStream.range(0, numCols).mapToObj(col -> new Point(row, col))).iterator();
  }

  @Override
  public int size() {
    return numRows * numCols;
  }
}