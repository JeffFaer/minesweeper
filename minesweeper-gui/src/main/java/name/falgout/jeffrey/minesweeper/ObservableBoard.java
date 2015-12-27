package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
import java.util.Set;
import java.util.stream.Stream;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.util.Pair;

import name.falgout.jeffrey.minesweeper.FlagMinesweeperState.ExtraSquare;
import name.falgout.jeffrey.minesweeper.board.MutableBoard;

public class ObservableBoard implements MutableBoard {
  public static class SquareUpdate extends Event {
    private static final long serialVersionUID = 5475728033566313676L;
    public static final EventType<SquareUpdate> SQUARE_UPDATE = new EventType<>("SquareUpdate");

    private final Point point;
    private final Square square;

    public SquareUpdate(ObservableBoard source, Point point, Square square) {
      super(source, null, SQUARE_UPDATE);
      this.point = point;
      this.square = square;
    }

    public Point getPoint() {
      return point;
    }

    public Square getSquare() {
      return square;
    }

    @Override
    public ObservableBoard getSource() {
      return (ObservableBoard) super.getSource();
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventType<SquareUpdate> getEventType() {
      return (EventType<SquareUpdate>) super.getEventType();
    }
  }

  private final MutableBoard board;
  private final ObjectProperty<Pair<Point, Square>> updatedSquare = new SimpleObjectProperty<>();
  private final IntegerProperty numFlags = new SimpleIntegerProperty(0);

  public ObservableBoard(MutableBoard board) {
    this.board = board;
  }

  public void addListener(EventHandler<SquareUpdate> listener) {
    updatedSquare.addListener((obs, oldValue, newValue) -> {
      listener.handle(new SquareUpdate(ObservableBoard.this, newValue.getKey(), newValue.getValue()));
    });
  }

  public IntegerProperty numFlags() {
    return numFlags;
  }

  private void update(Point index, Square oldSquare, Square newSquare) {
    updatedSquare.set(new Pair<>(index, newSquare));

    boolean wasFlag = oldSquare == ExtraSquare.FLAG;
    boolean isFlag = newSquare == ExtraSquare.FLAG;
    if (wasFlag ^ isFlag) {
      if (isFlag) {
        numFlags.set(numFlags.get() + 1);
      } else {
        numFlags.set(numFlags.get() - 1);
      }
    }
  }

  @Override
  public void setSquare(Point point, Square s) {
    Square old = getSquare(point);
    board.setSquare(point, s);
    update(point, old, s);
  }

  @Override
  public void setSquare(int row, int col, Square s) {
    setSquare(new Point(row, col), s);
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
  public Stream<? extends Point> getValidIndexes() {
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
  public Set<? extends Point> getNeighbors(Point point) {
    return board.getNeighbors(point);
  }

  @Override
  public Set<? extends Point> getNeighbors(int row, int col) {
    return board.getNeighbors(row, col);
  }
}
