package name.falgout.jeffrey.minesweeper;

import static java.util.stream.Collectors.toList;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedHashMap;
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
import name.falgout.jeffrey.minesweeper.board.MutableBoard;
import name.falgout.jeffrey.minesweeper.board.Board.Square;

/**
 * Maintains two views of the game. The first view is the "master" view which
 * knows were every mine is. The second view is the "player" view which only
 * knows what has been revealed.
 */
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

  private final MutableBoard master;
  protected final MutableBoard player;

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

  public MinesweeperState(MutableBoard player, int numMines) {
    this(player, numMines, getDefaultSeed());
  }

  public MinesweeperState(MutableBoard player, int numMines, long seed) {
    this(player, numMines, createDefaultRandom(seed));
  }

  public MinesweeperState(MutableBoard player, int numMines, Random random) {
    master = new ArrayBoard(player.getNumRows(), player.getNumColumns(), player::getNeighbors);
    this.player = player;

    this.player.getValidIndexes().forEach(p -> {
      player.setSquare(p, Square.Basic.UNKNOWN);
    });

    this.numMines = numMines;
    this.random = random;
  }

  public Board getBoard() {
    return player;
  }

  @Override
  public Stream<Transition> getTransitions() {
    return master.getValidIndexes().map(Action::reveal).filter(this::isValid);
  }

  @Override
  public boolean isValid(Transition transition) {
    return transition.getAction() == Action.Basic.REVEAL && !player.getSquare(transition.getPoint()).isRevealed();
  }

  @Override
  protected GameState<Transition> updateState(Transition transition) {
    Point point = transition.getPoint();
    if (master.getSquare(0, 0) == null) {
      generateBoard(point);
    }

    return nextState(reveal(point));
  }

  private void generateBoard(Point point) {
    List<Point> points = master.getValidIndexes().collect(toList());
    points.remove(point);
    points.removeAll(master.getNeighbors(point));
    Collections.shuffle(points, random);

    for (int i = 0; i < numMines; i++) {
      Point mine = points.get(i);
      master.setSquare(mine, Square.Basic.MINE);
    }

    for (int row = 0; row < master.getNumRows(); row++) {
      for (int col = 0; col < master.getNumColumns(); col++) {
        if (master.getSquare(row, col) == null) {
          Set<Point> neighbors = master.getNeighbors(row, col);
          int numMines = (int) neighbors.stream().map(master::getSquare).filter(s -> s != null && s.isMine()).count();
          master.setSquare(row, col, new Square.Number(numMines));
        }
      }
    }
  }

  protected Map<Point, Square> reveal(Point point) {
    Map<Point, Square> revealed = new LinkedHashMap<>();
    Queue<Point> reveal = new LinkedList<>();
    reveal.add(point);

    do {
      Point revealPoint = reveal.poll();
      Square s = master.getSquare(revealPoint);

      if (!player.getSquare(revealPoint).isRevealed()) {
        revealed.put(revealPoint, s);
        player.setSquare(revealPoint, s);

        if (s.isNumber() && s.getNumber() == 0) {
          reveal.addAll(master.getNeighbors(revealPoint));
        }
      }
    } while (!reveal.isEmpty());

    numRevealed += revealed.size();
    return revealed;
  }

  protected GameState<Transition> nextState(Map<Point, Square> revealed) {
    if (revealed.containsValue(Square.Basic.MINE)) {
      return GameOver.loss();
    } else if (numRevealed + numMines == master.size()) {
      return GameOver.win();
    } else {
      return this;
    }
  }
}
