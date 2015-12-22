package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
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

  public FlagMinesweeper(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors, long seed) {
    super(numRows, numCols, numMines, neighbors, seed);
  }

  public FlagMinesweeper(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors, Random random) {
    super(numRows, numCols, numMines, neighbors, random);
  }

  public FlagMinesweeper(int numRows, int numCols, int numMines, Function<Point, Set<Point>> neighbors) {
    super(numRows, numCols, numMines, neighbors);
  }

  public FlagMinesweeper(int numRows, int numCols, int numMines) {
    super(numRows, numCols, numMines);
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
    } else if (s.getNumber() == 0) {
      return false;
    }

    Set<Point> neighbors = getBoard().getNeighbors(p);
    int numFlags = 0;
    for (Point neighbor : neighbors) {
      if (getBoard().getSquare(neighbor) == ExtraSquare.FLAG) {
        numFlags++;
      }
    }

    return s.getNumber() == numFlags;
  }

  @Override
  protected GameState<Transition> updateState(Transition transition) {
    Point p = transition.getPoint();
    if (transition.getAction() == ExtraAction.FLAG) {
      Square s = getBoard().getSquare(p);
      if (s == ExtraSquare.FLAG) {
        player.setSquare(p, Square.Basic.UNKNOWN);
      } else {
        player.setSquare(p, ExtraSquare.FLAG);
      }

      return this;
    } else if (transition.getAction() == Action.Basic.REVEAL) {
      Square s = getBoard().getSquare(p);
      if (s.isNumber()) {
        // Flip neighbors since it has enough flags.
        Set<Point> neighbors = getBoard().getNeighbors(p);
        neighbors.removeIf(this::isFlag);
        return reveal(neighbors.toArray(new Point[neighbors.size()]));
      } else {
        return super.updateState(transition);
      }
    } else {
      throw new Error("Invalid transition.");
    }
  }
}
