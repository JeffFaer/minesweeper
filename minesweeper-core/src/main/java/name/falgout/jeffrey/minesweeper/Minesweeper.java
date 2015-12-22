package name.falgout.jeffrey.minesweeper;

import static java.util.stream.Collectors.toList;

import java.awt.Point;
import java.util.Arrays;
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

import name.falgout.jeffrey.minesweeper.Board.Square;
import name.falgout.jeffrey.minesweeper.Transition.Action;

/**
 * Maintains two views of the game. The first view is the "master" view which
 * knows were every mine is. The second view is the "player" view which only
 * knows what has been revealed.
 */
public class Minesweeper extends GameState<Transition> {
  private final MutableBoard master;
  protected final MutableBoard player;

  private final int numMines;
  private final Random random;

  private int numRevealed = 0;

  public Minesweeper(int numRows, int numCols, int numMines) {
    this(numRows, numCols, numMines, NeighborFunction.CIRCLE);
  }

  public Minesweeper(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors) {
    this(numRows, numCols, numMines, neighbors, System.nanoTime());
  }

  public Minesweeper(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors, long seed) {
    this(numRows, numCols, numMines, neighbors, new Random(seed));
  }

  public Minesweeper(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors, Random random) {
    master = new MutableBoard(numRows, numCols, neighbors);
    player = new MutableBoard(numRows, numCols, neighbors);

    player.getValidIndexes().forEach(p -> {
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
    return master.getValidIndexes().map(Transition::reveal).filter(this::isValid);
  }

  @Override
  public boolean isValid(Transition transition) {
    return transition.getAction() == Action.Basic.REVEAL && !player.getSquare(transition.getPoint()).isRevealed();
  }

  @Override
  protected GameState<Transition> updateState(Transition transition) {
    Point p = transition.getPoint();
    if (master.getSquare(0, 0) == null) {
      generateBoard(p);
    }

    return reveal(p);
  }

  private void generateBoard(Point p) {
    List<Point> points = master.getValidIndexes().collect(toList());
    points.remove(p);
    points.removeAll(master.getNeighbors(p));
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

  protected GameState<Transition> reveal(Point... p) {
    Map<Point, Square> revealed = new LinkedHashMap<>();
    Queue<Point> reveal = new LinkedList<>(Arrays.asList(p));
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
    
    if (revealedMine(revealed, p)) {
      return GameOver.loss();
    } else if (numRevealed + numMines == master.size()) {
      return GameOver.win();
    } else {
      return this;
    }
  }

  private boolean revealedMine(Map<Point, Square> revealed, Point... keys) {
    for (Point k : keys) {
      if (revealed.containsKey(k) && revealed.get(k).isMine()) {
        return true;
      }
    }

    return false;
  }
}
