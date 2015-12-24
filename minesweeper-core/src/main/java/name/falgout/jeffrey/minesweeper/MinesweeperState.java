package name.falgout.jeffrey.minesweeper;

import static java.util.stream.Collectors.toList;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import name.falgout.jeffrey.minesweeper.Transition.Action;
import name.falgout.jeffrey.minesweeper.board.ArrayBoard;
import name.falgout.jeffrey.minesweeper.board.Board;
import name.falgout.jeffrey.minesweeper.board.Board.Square;
import name.falgout.jeffrey.minesweeper.board.MutableBoard;

public class MinesweeperState extends AbstractGameState<Transition> {
  private static final NeighborFunction DEFAULT_NEIGHBOR_FUNCTION = NeighborFunction.CIRCLE;

  private static MutableBoard createDefaultPlayerBoard(int numRows, int numCols, Function<Point, Set<Point>> neighbors) {
    return new ArrayBoard(numRows, numCols, neighbors);
  }

  private static long getDefaultSeed() {
    return System.nanoTime();
  }

  private static Random createDefaultRandom(long seed) {
    return new Random(seed);
  }

  private final Set<Point> mines;
  protected final MutableBoard board;

  private final int numMines;
  private final Random random;

  private int numRevealed = 0;

  public MinesweeperState(int numRows, int numCols, int numMines) {
    this(createDefaultPlayerBoard(numRows, numCols, DEFAULT_NEIGHBOR_FUNCTION), numMines);
  }

  public MinesweeperState(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors) {
    this(createDefaultPlayerBoard(numRows, numCols, neighbors), numMines);
  }

  public MinesweeperState(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors, long seed) {
    this(createDefaultPlayerBoard(numRows, numCols, neighbors), numMines, seed);
  }

  public MinesweeperState(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors, Random random) {
    this(createDefaultPlayerBoard(numRows, numCols, neighbors), numMines, random);
  }

  public MinesweeperState(MutableBoard board, int numMines) {
    this(board, numMines, getDefaultSeed());
  }

  public MinesweeperState(MutableBoard board, int numMines, long seed) {
    this(board, numMines, createDefaultRandom(seed));
  }

  public MinesweeperState(MutableBoard board, int numMines, Random random) {
    mines = new LinkedHashSet<>(numMines);
    this.board = board;

    board.getValidIndexes().forEach(p -> {
      board.setSquare(p, Square.Basic.UNKNOWN);
    });

    this.numMines = numMines;
    this.random = random;
  }

  public int getNumMines() {
    return numMines;
  }

  public Board getBoard() {
    return board;
  }

  @Override
  public Stream<Transition> getTransitions() {
    return board.getValidIndexes().map(Action::reveal).filter(this::isValid);
  }

  @Override
  public boolean isValid(Transition transition) {
    return transition.getAction() == Action.Basic.REVEAL && !board.getSquare(transition.getPoint()).isRevealed();
  }

  @Override
  protected GameState<Transition> updateState(Transition transition) {
    Point point = transition.getPoint();
    if (mines.isEmpty()) {
      generateBoard(point);
    }

    return nextState(reveal(point));
  }

  private void generateBoard(Point point) {
    List<Point> points = board.getValidIndexes().collect(toList());
    points.remove(point);
    points.removeAll(board.getNeighbors(point));
    Collections.shuffle(points, random);

    for (int i = 0; i < numMines; i++) {
      Point mine = points.get(i);
      mines.add(mine);
    }
  }

  protected Map<Point, Square> reveal(Point point) {
    Map<Point, Square> revealed = new LinkedHashMap<>();
    Queue<Point> reveal = new LinkedList<>();
    reveal.add(point);

    do {
      Point revealPoint = reveal.poll();

      if (!board.getSquare(revealPoint).isRevealed()) {
        Square s;
        if (mines.contains(revealPoint)) {
          s = Square.Basic.MINE;
        } else {
          int numMines = board.getNeighbors(revealPoint, mines::contains).size();
          s = new Square.Number(numMines);
        }

        revealed.put(revealPoint, s);
        board.setSquare(revealPoint, s);

        if (s.isNumber() && s.getNumber() == 0) {
          reveal.addAll(board.getNeighbors(revealPoint));
        }
      }
    } while (!reveal.isEmpty());

    numRevealed += revealed.size();
    return revealed;
  }

  protected GameState<Transition> nextState(Map<Point, Square> revealed) {
    if (revealed.containsValue(Square.Basic.MINE)) {
      return GameOver.loss();
    } else if (numRevealed + numMines == board.size()) {
      return GameOver.win();
    } else {
      return this;
    }
  }
}
