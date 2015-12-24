package name.falgout.jeffrey.minesweeper.gui;

import java.awt.Point;
import java.util.Set;
import java.util.stream.Stream;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.util.Pair;
import name.falgout.jeffrey.minesweeper.board.MutableBoard;

public class ObservableBoard implements MutableBoard {
  private final MutableBoard board;
  private final SimpleObjectProperty<Pair<Point, Square>> updatedSquare = new SimpleObjectProperty<>();

  public ObservableBoard(MutableBoard board) {
    this.board = board;
  }

  public ObservableObjectValue<Pair<Point, Square>> updatedSquare() {
    return updatedSquare;
  }

  private void update(Point index, Square s) {
    updatedSquare.set(new Pair<>(index, s));
  }

  @Override
  public void setSquare(Point point, Square s) {
    board.setSquare(point, s);
    update(point, s);
  }

  @Override
  public void setSquare(int row, int col, Square s) {
    board.setSquare(row, col, s);
    update(new Point(row, col), s);
  }

  @Override
  public int getNumRows() {
    return board.getNumRows();
  }

  @Override
  public int getNumColumns() {
    return board.getNumColumns();
  }

  @Override
  public int size() {
    return board.size();
  }

  @Override
  public Stream<Point> getValidIndexes() {
    return board.getValidIndexes();
  }

  @Override
  public Square getSquare(Point point) {
    return board.getSquare(point);
  }

  @Override
  public Square getSquare(int row, int col) {
    return board.getSquare(row, col);
  }

  @Override
  public Set<Point> getNeighbors(Point point) {
    return board.getNeighbors(point);
  }

  @Override
  public Set<Point> getNeighbors(int row, int col) {
    return board.getNeighbors(row, col);
  }
}
