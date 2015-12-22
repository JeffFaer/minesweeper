package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import name.falgout.jeffrey.minesweeper.Board.Square;
import name.falgout.jeffrey.minesweeper.Transition.Action;

public class FlagMinesweeper extends Minesweeper {
  public static enum ExtraAction implements Action {
    FLAG;
  }

  public static enum ExtraSquare implements Square {
    FLAG;

    @Override
    public boolean isMine() {
      return false;
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
      return false;
    }
  }

  public static Transition flag(Point p) {
    return new Transition(ExtraAction.FLAG, p);
  }

  private final boolean countDown;

  public FlagMinesweeper(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors, long seed) {
    this(numRows, numCols, numMines, neighbors, seed, false);
  }

  public FlagMinesweeper(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors, Random random) {
    this(numRows, numCols, numMines, neighbors, random, false);
  }

  public FlagMinesweeper(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors) {
    this(numRows, numCols, numMines, neighbors, false);
  }

  public FlagMinesweeper(int numRows, int numCols, int numMines) {
    this(numRows, numCols, numMines, false);
  }

  public FlagMinesweeper(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors, Random random,
      boolean countDown) {
    super(numRows, numCols, numMines, neighbors, random);
    this.countDown = countDown;
  }

  public FlagMinesweeper(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors, long seed,
      boolean countDown) {
    super(numRows, numCols, numMines, neighbors, seed);
    this.countDown = countDown;
  }

  public FlagMinesweeper(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors,
      boolean countDown) {
    super(numRows, numCols, numMines, neighbors);
    this.countDown = countDown;
  }

  public FlagMinesweeper(int numRows, int numCols, int numMines, boolean countDown) {
    super(numRows, numCols, numMines);
    this.countDown = countDown;
  }

  @Override
  public Stream<Transition> getTransitions() {
    Stream<Transition> flags = getBoard().getValidIndexes().map(FlagMinesweeper::flag);
    Stream<Transition> reveals = getBoard().getValidIndexes().map(Transition::reveal);

    return Stream.concat(flags, reveals).filter(this::isValid);
  }

  @Override
  public boolean isValid(Transition transition) {
    Point p = transition.getPoint();
    if (transition.getAction() == ExtraAction.FLAG) {
      // Toggle a flag on a hidden square.
      return isHidden(p);
    } else if (transition.getAction() == Action.Basic.REVEAL) {
      // Reveal a single square from the game that isn't a flag.
      // or
      // If the given square has enough flags, reveal its neighbors.
      return (!isFlag(p) && super.isValid(transition)) || hasEnoughFlags(p);
    } else {
      return false;
    }
  }

  private boolean isHidden(Point p) {
    return !getBoard().getSquare(p).isRevealed();
  }

  private boolean isFlag(Point p) {
    return getBoard().getSquare(p) == ExtraSquare.FLAG;
  }

  private boolean hasEnoughFlags(Point p) {
    Square s = getBoard().getSquare(p);
    if (!s.isNumber()) {
      return false;
    }

    if (countDown) {
      return s.getNumber() == 0;
    } else {
      Set<Point> neighbors = getBoard().getNeighbors(p);
      int numFlags = 0;
      for (Point neighbor : neighbors) {
        if (getBoard().getSquare(neighbor) == ExtraSquare.FLAG) {
          numFlags++;
          if (numFlags > s.getNumber()) {
            return false;
          }
        }
      }

      return numFlags == s.getNumber();
    }
  }

  @Override
  protected Map<Point, Square> reveal(Point... p) {
    Map<Point, Square> revealed = super.reveal(p);

    if (countDown) {
      for (Entry<Point, Square> e : revealed.entrySet()) {
        if (e.getValue().isNumber()) {
          int numFlags = (int) getBoard().getNeighbors(e.getKey()).stream().filter(this::isFlag).count();
          if (numFlags > 0) {
            Square newNumber = new Square.Number(e.getValue().getNumber() - numFlags);
            player.setSquare(e.getKey(), newNumber);
            e.setValue(newNumber);
          }
        }
      }
    }

    return revealed;
  }

  @Override
  protected GameState<Transition> updateState(Transition transition) {
    Point p = transition.getPoint();
    if (transition.getAction() == ExtraAction.FLAG) {
      Square s = getBoard().getSquare(p);
      int delta;
      if (s == ExtraSquare.FLAG) {
        player.setSquare(p, Square.Basic.UNKNOWN);
        delta = +1;
      } else {
        player.setSquare(p, ExtraSquare.FLAG);
        delta = -1;
      }

      if (countDown) {
        for (Point neighbor : getBoard().getNeighbors(p)) {
          Square neighborSquare = getBoard().getSquare(neighbor);
          if (neighborSquare.isNumber()) {
            Square newNumber = new Square.Number(neighborSquare.getNumber() + delta);
            player.setSquare(neighbor, newNumber);
          }
        }
      }

      return this;
    } else if (transition.getAction() == Action.Basic.REVEAL) {
      Square s = getBoard().getSquare(p);
      if (s.isNumber()) {
        // Flip neighbors since it has enough flags.
        Set<Point> neighbors = getBoard().getNeighbors(p);
        neighbors.removeIf(this::isFlag);
        return nextState(reveal(neighbors.toArray(new Point[neighbors.size()])));
      } else {
        return super.updateState(transition);
      }
    } else {
      throw new Error("Invalid transition.");
    }
  }
}
