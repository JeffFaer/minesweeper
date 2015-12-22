package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import name.falgout.jeffrey.minesweeper.Board.Square;

/**
 * Maintains two views of the game. The first view is the "master" view which
 * knows were every mine is. The second view is the "player" view which only
 * knows what has been revealed.
 */
public class Minesweeper extends GameState<Point> {
  private final MutableBoard master;
  private final MutableBoard player;

  private final int numMines;
  private final Random random;

  public Minesweeper(int numRows, int numCols, int numMines) {
    this(numRows, numCols, numMines, NeighborFunction.CIRCLE);
  }

  public Minesweeper(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors) {
    this(numRows, numCols, numMines, neighbors, System.nanoTime());
  }

  public Minesweeper(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors, long seed) {
    master = new MutableBoard(numRows, numCols, neighbors);
    player = new MutableBoard(numRows, numCols, neighbors);
    
    for (Point p : player.getValidIndexes()) {
      player.setSquare(p, Square.Basic.UNKNOWN);
    }

    this.numMines = numMines;
    random = new Random(seed);
  }

  public Board getBoard() {
    return player;
  }

  @Override
  public Set<Point> getTransitions() {
    return master.getValidIndexes();
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
    if (reveal(transition).get(transition).isMine()) {
      return GameOver.lose();
    } else {
      // TODO win condition
      return this;
    }
  }

  private Map<Point, Square> reveal(Point p) {
    if (master.getSquare(0, 0) == null) {
      generateBoard(p);
    }

    Map<Point, Square> revealed = new LinkedHashMap<>();
    revealed.put(p, master.getSquare(p));

    Queue<Point> reveal = new LinkedList<>();
    reveal.add(p);
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

    return revealed;
  }

  private void generateBoard(Point p) {
    List<Point> points = new ArrayList<>(master.getValidIndexes());
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
}
