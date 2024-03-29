package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import name.falgout.jeffrey.minesweeper.Transition.Action;
import name.falgout.jeffrey.minesweeper.board.Board.Square;
import name.falgout.jeffrey.minesweeper.board.MutableBoard;

public class FlagMinesweeperState extends MinesweeperState {
  public static enum ExtraAction implements Action {
    FLAG;

    public static Transition flag(Point p) {
      return new Transition(FLAG, p);
    }
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

  private final boolean countDown;

  public FlagMinesweeperState(int numRows, int numCols, int numMines,
      Function<? super Point, ? extends Set<? extends Point>> neighbors, long seed) {
    this(numRows, numCols, numMines, neighbors, seed, false);
  }

  public FlagMinesweeperState(int numRows, int numCols, int numMines,
      Function<? super Point, ? extends Set<? extends Point>> neighbors, Random random) {
    this(numRows, numCols, numMines, neighbors, random, false);
  }

  public FlagMinesweeperState(int numRows, int numCols, int numMines,
      Function<? super Point, ? extends Set<? extends Point>> neighbors) {
    this(numRows, numCols, numMines, neighbors, false);
  }

  public FlagMinesweeperState(int numRows, int numCols, int numMines) {
    this(numRows, numCols, numMines, false);
  }

  public FlagMinesweeperState(MutableBoard player, int numMines, long seed) {
    this(player, numMines, seed, false);
  }

  public FlagMinesweeperState(MutableBoard player, int numMines, Random random) {
    this(player, numMines, random, false);
  }

  public FlagMinesweeperState(MutableBoard player, int numMines) {
    this(player, numMines, false);
  }

  public FlagMinesweeperState(MutableBoard player, int numMines, Random random, boolean countDown) {
    super(player, numMines, random);
    this.countDown = countDown;
  }

  public FlagMinesweeperState(MutableBoard player, int numMines, long seed, boolean countDown) {
    super(player, numMines, seed);
    this.countDown = countDown;
  }

  public FlagMinesweeperState(MutableBoard player, int numMines, boolean countDown) {
    super(player, numMines);
    this.countDown = countDown;
  }

  public FlagMinesweeperState(int numRows, int numCols, int numMines,
      Function<? super Point, ? extends Set<? extends Point>> neighbors, Random random,
      boolean countDown) {
    super(numRows, numCols, numMines, neighbors, random);
    this.countDown = countDown;
  }

  public FlagMinesweeperState(int numRows, int numCols, int numMines,
      Function<? super Point, ? extends Set<? extends Point>> neighbors, long seed,
      boolean countDown) {
    super(numRows, numCols, numMines, neighbors, seed);
    this.countDown = countDown;
  }

  public FlagMinesweeperState(int numRows, int numCols, int numMines,
      Function<? super Point, ? extends Set<? extends Point>> neighbors, boolean countDown) {
    super(numRows, numCols, numMines, neighbors);
    this.countDown = countDown;
  }

  public FlagMinesweeperState(int numRows, int numCols, int numMines, boolean countDown) {
    super(numRows, numCols, numMines);
    this.countDown = countDown;
  }

  @Override
  public Stream<? extends Transition> getTransitions() {
    Stream<Transition> flags = getBoard().getValidIndexes().map(ExtraAction::flag);
    Stream<Transition> reveals = getBoard().getValidIndexes().map(Action::reveal);

    return Stream.concat(flags, reveals).filter(this::isValid);
  }

  @Override
  public boolean isValid(Transition transition) {
    Point point = transition.getPoint();
    if (transition.getAction() == ExtraAction.FLAG) {
      // Toggle a flag on a hidden square.
      return isHidden(point);
    } else if (transition.getAction() == Action.Basic.REVEAL) {
      // Reveal a single square from the game that isn't a flag.
      // or
      // If the given square has enough flags, reveal its neighbors.
      return (!isFlag(point) && super.isValid(transition))
          || (isNumber(point) && hasEnoughFlags(point));
    } else {
      return false;
    }
  }

  private boolean isHidden(Point point) {
    return !getBoard().getSquare(point).isRevealed();
  }

  private boolean isFlag(Point point) {
    return getBoard().getSquare(point) == ExtraSquare.FLAG;
  }

  private boolean isNumber(Point point) {
    return getBoard().getSquare(point).isNumber();
  }

  private boolean hasEnoughFlags(Point point) {
    Square s = getBoard().getSquare(point);

    if (countDown) {
      return s.getNumber() == 0;
    } else if (s.getNumber() == 0) {
      return false;
    } else {
      return countFlags(point) == s.getNumber();
    }
  }

  private int countFlags(Point point) {
    return getBoard().getNeighbors(point, this::isFlag).size();
  }

  public GameState<Transition> flag(Point point) {
    return transition(ExtraAction.flag(point));
  }

  @Override
  protected GameState<Transition> updateState(Transition transition) {
    Point point = transition.getPoint();
    if (transition.getAction() == ExtraAction.FLAG) {
      toggleFlag(point);
      return this;
    } else if (transition.getAction() == Action.Basic.REVEAL) {
      return super.updateState(transition);
    } else {
      throw new Error("Invalid transition.");
    }
  }

  @Override
  protected Map<Point, Square> reveal(Point point) {
    Square s = getBoard().getSquare(point);
    Map<Point, Square> revealed;
    if (s.isNumber()) {
      // Flip neighbors since it has enough flags.
      Set<? extends Point> neighbors = getBoard().getNeighbors(point, p -> !isFlag(p));

      revealed = new LinkedHashMap<>(neighbors.size());
      for (Point neighbor : neighbors) {
        revealed.putAll(super.reveal(neighbor));
      }
    } else {
      revealed = super.reveal(point);
    }

    if (countDown) {
      for (Entry<Point, Square> e : revealed.entrySet()) {
        if (e.getValue().isNumber()) {
          int numFlags = countFlags(e.getKey());
          if (numFlags > 0) {
            Square newNumber = new Square.Number(e.getValue().getNumber() - numFlags);
            board.setSquare(e.getKey(), newNumber);
            e.setValue(newNumber);
          }
        }
      }
    }

    return revealed;
  }

  protected void toggleFlag(Point point) {
    Square s = getBoard().getSquare(point);
    int delta;
    if (s == ExtraSquare.FLAG) {
      board.setSquare(point, Square.Basic.UNKNOWN);
      delta = +1;
    } else {
      board.setSquare(point, ExtraSquare.FLAG);
      delta = -1;
    }

    if (countDown) {
      for (Point neighbor : getBoard().getNeighbors(point)) {
        Square neighborSquare = getBoard().getSquare(neighbor);
        if (neighborSquare.isNumber()) {
          Square newNumber = new Square.Number(neighborSquare.getNumber() + delta);
          board.setSquare(neighbor, newNumber);
        }
      }
    }
  }

  @Override
  protected GameState<Transition> nextState(Map<Point, Square> revealed) {
    GameState<Transition> state = super.nextState(revealed);
    if (state.equals(GameOver.WIN)) {
      // Reveal remaining mines as flags.
      Stream<? extends Point> mines = getBoard().getValidIndexes()
          .filter(this::isHidden)
          .filter(p -> !isFlag(p));
      mines.forEach(p -> {
        transition(ExtraAction.flag(p));
      });
    }

    return state;
  }
}
