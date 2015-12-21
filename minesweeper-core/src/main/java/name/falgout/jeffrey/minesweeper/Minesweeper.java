package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

import name.falgout.jeffrey.minesweeper.Minesweeper.MasterBoard.Square;

public class Minesweeper extends GameState<Point> {
  public static interface Board {
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

  /**
   * The {@code MasterBoard} maintains two views of the game. The first view is
   * the "master" view which knows were every mine is. The second view is the
   * "player" view which only knows what has been {@link #reveal(Point)
   * revealed}.
   * 
   * @author Jeffrey Falgout
   */
  public static class MasterBoard implements Board {
    public static interface Square {
      public boolean isMine();

      public boolean isNumber();

      public int getNumber();

      public boolean isRevealed();
    }

    private static enum BasicSquare implements Square {
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

    private static class NumberSquare implements Square {
      private final int number;

      public NumberSquare(int number) {
        super();
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

    private final Square[][] board;
    private final int numMines;
    private final Function<Point, Set<Point>> neighbors;
    private final Random random;

    private final GridSet indexes;

    private final Square[][] playerBoard;
    private final Board playerView = new Board() {
      @Override
      public int getNumRows() {
        return MasterBoard.this.getNumRows();
      }

      @Override
      public int getNumColumns() {
        return MasterBoard.this.getNumColumns();
      }

      @Override
      public Set<Point> getValidIndexes() {
        return MasterBoard.this.getValidIndexes();
      }

      @Override
      public Square getSquare(int i, int j) {
        return playerBoard[i][j];
      }

      @Override
      public Set<Point> getNeighbors(Point p) {
        return MasterBoard.this.getNeighbors(p);
      }
    };

    public MasterBoard(int numRows, int numCols, int numMines) {
      this(numRows, numCols, numMines, NeighborFunction.CIRCLE, System.nanoTime());
    }

    public MasterBoard(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors) {
      this(numRows, numCols, numMines, neighbors, System.nanoTime());
    }

    public MasterBoard(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors, long seed) {
      board = new Square[numRows][numCols];
      this.numMines = numMines;
      this.neighbors = neighbors;
      random = new Random(seed);

      indexes = new GridSet(numRows, numCols);

      playerBoard = new Square[numRows][numCols];
      for (Square[] row : playerBoard) {
        Arrays.fill(row, BasicSquare.UNKNOWN);
      }
    }

    private void generateBoard(Point p) {
      List<Point> points = new ArrayList<>(indexes);
      points.remove(p);
      points.removeAll(getNeighbors(p));
      Collections.shuffle(points, random);

      for (int i = 0; i < numMines; i++) {
        Point mine = points.get(i);
        board[mine.x][mine.y] = BasicSquare.MINE;
        System.out.println(mine);
      }

      for (int row = 0; row < board.length; row++) {
        for (int col = 0; col < board[0].length; col++) {
          if (board[row][col] == null) {
            Set<Point> neighbors = getNeighbors(new Point(row, col));
            int numMines = (int) neighbors.stream().map(this::getSquare).filter(s -> s != null && s.isMine()).count();
            board[row][col] = new NumberSquare(numMines);
          }
        }
      }
    }

    public Map<Point, Square> reveal(Point p) {
      if (board[0][0] == null) {
        generateBoard(p);
      }

      Map<Point, Square> revealed = new LinkedHashMap<>();
      revealed.put(p, getSquare(p));
      
      Queue<Point> reveal = new LinkedList<>();
      reveal.add(p);
      do {
        Point revealPoint = reveal.poll();
        Square s = getSquare(revealPoint);

        if (!playerView.getSquare(revealPoint).isRevealed()) {
          revealed.put(revealPoint, s);
          playerBoard[revealPoint.x][revealPoint.y] = s;

          if (s.isNumber() && s.getNumber() == 0) {
            reveal.addAll(getNeighbors(revealPoint));
          }
        }
      } while (!reveal.isEmpty());

      return revealed;
    }

    public Board getPlayerBoard() {
      return playerView;
    }

    @Override
    public int getNumColumns() {
      return board.length;
    }

    @Override
    public int getNumRows() {
      return board[0].length;
    }

    @Override
    public Set<Point> getValidIndexes() {
      return indexes;
    }

    @Override
    public Square getSquare(int i, int j) {
      return board[i][j];
    }

    @Override
    public Set<Point> getNeighbors(Point p) {
      Set<Point> neighbors = this.neighbors.apply(p);
      neighbors.retainAll(indexes);
      return neighbors;
    }
  }

  static class GridSet extends AbstractSet<Point> {
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

  private final MasterBoard board;

  public Minesweeper(MasterBoard board) {
    this.board = board;
  }

  @Override
  public Set<Point> getTransitions() {
    return board.getValidIndexes();
  }

  @Override
  public boolean isWon() {
    return false;
  }

  @Override
  public boolean isLost() {
    return false;
  }

  @Override
  protected GameState<Point> updateState(Point transition) {
    if (board.reveal(transition).get(transition).isMine()) {
      return GameOver.lose();
    } else {
      // TODO win condition
      return this;
    }
  }
}
