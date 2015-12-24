package name.falgout.jeffrey.minesweeper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import name.falgout.jeffrey.minesweeper.Transition.Action;
import name.falgout.jeffrey.minesweeper.board.ArrayBoard;
import name.falgout.jeffrey.minesweeper.board.Board;
import name.falgout.jeffrey.minesweeper.board.MutableBoard;

import org.junit.Before;
import org.junit.Test;

public class MinesweeperTest {
  /**
   * First move new Point(1,1):
   *
   * <pre>
   *   0 1 2 3 4
   *  ┌─┬─┬─┬─┬─┐
   * 0│0│0│2│ │ │
   *  ├─┼─┼─┼─┼─┤
   * 1│0│0│2│ │ │
   *  ├─┼─┼─┼─┼─┤
   * 2│0│1│3│ │ │
   *  ├─┼─┼─┼─┼─┤
   * 3│1│3│ │ │ │
   *  ├─┼─┼─┼─┼─┤
   * 4│ │ │ │ │ │
   *  └─┴─┴─┴─┴─┘
   * </pre>
   *
   * <pre>
   *   0 1 2 3 4
   *  ┌─┬─┬─┬─┬─┐
   * 0│0│0│2│*│*│
   *  ├─┼─┼─┼─┼─┤
   * 1│0│0│2│*│*│
   *  ├─┼─┼─┼─┼─┤
   * 2│0│1│3│5│*│
   *  ├─┼─┼─┼─┼─┤
   * 3│1│3│*│*│3│
   *  ├─┼─┼─┼─┼─┤
   * 4│1│*│*│4│*│
   *  └─┴─┴─┴─┴─┘
   * </pre>
   */
  public static final MutableBoard PLAYER_BOARD = new ArrayBoard(5, 5, NeighborFunction.CIRCLE);
  public static final int NUM_MINES = 10;
  public static final long SEED = 5;
  public static final Point START = new Point(1, 1);

  Minesweeper minesweeper;
  Board board;

  @Before
  public final void before() {
    MinesweeperState state = createState(PLAYER_BOARD, NUM_MINES, SEED);
    init(state);
  }

  public void init(MinesweeperState state) {
    minesweeper = createGame(state);
    minesweeper.reveal(START);
    board = state.getBoard();
  }

  public MinesweeperState createState(MutableBoard player, int numBombs, long seed) {
    return new MinesweeperState(player, numBombs, seed);
  }

  public Minesweeper createGame(MinesweeperState state) {
    return new Minesweeper(state);
  }

  @Test
  public void everythingIsHiddenBeforeFirstFlip() {
    MinesweeperState m = new MinesweeperState(8, 8, 10, NeighborFunction.CIRCLE);
    Board b = m.getBoard();
    assertTrue(b.getValidIndexes().map(b::getSquare).allMatch(s -> !s.isRevealed()));
  }

  @Test
  public void firstMoveIsZero() {
    Random r = new Random();
    for (int i = 0; i < 20; i++) {
      MinesweeperState m = new MinesweeperState(8, 8, 10, NeighborFunction.CIRCLE);
      Board b = m.getBoard();
      Point p = new Point(r.nextInt(8), r.nextInt(8));
      assertSame(m, m.transition(Action.reveal(p)));
      assertEquals(0, b.getSquare(p).getNumber());
    }
  }

  @Test
  public void zeroesFlipNeighbors() {
    // Since the first move is always a zero, just check its neighbors.
    for (Point neighbor : board.getNeighbors(START)) {
      assertTrue(board.getSquare(neighbor).isRevealed());
    }

    assertTrue(board.getSquare(3, 0).isRevealed());
    assertTrue(board.getSquare(3, 1).isRevealed());
  }

  @Test(expected = IllegalStateException.class)
  public void cannotTransitionRevealedSquare() {
    assertFalse(minesweeper.isValid(Action.reveal(START)));
    minesweeper.reveal(START);
  }

  @Test
  public void winning() {
    List<Point> notMines = Arrays.asList(new Point(3, 4), new Point(4, 0), new Point(4, 3));

    for (Point p : notMines) {
      minesweeper.reveal(p);
      assertFalse(minesweeper.isComplete());
    }

    minesweeper.reveal(2, 3);
    assertTrue(minesweeper.isComplete());
    assertTrue(minesweeper.isWin());
  }

  @Test
  public void losing() {
    assertFalse(minesweeper.isComplete());

    minesweeper.reveal(0, 4);
    assertTrue(minesweeper.isComplete());
    assertTrue(minesweeper.isLoss());
  }
}
