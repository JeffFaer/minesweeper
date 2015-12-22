package name.falgout.jeffrey.minesweeper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
  static final long seed = 5;
  static final Point start = new Point(1, 1);

  public static Minesweeper createMinesweeperGame() {
    return createMinesweeperGame(true);
  }

  public static Minesweeper createMinesweeperGame(boolean firstMove) {
    Minesweeper game = new Minesweeper(5, 5, 10, NeighborFunction.CIRCLE, seed);
    if (firstMove) {
      game.transition(start);
    }

    return game;
  }

  Minesweeper minesweeper;
  Board board;

  @Before
  public void before() {
    minesweeper = createMinesweeperGame();
    board = minesweeper.getBoard();
  }

  @Test
  public void everythingIsHiddenBeforeFirstFlip() {
    Minesweeper m = new Minesweeper(8, 8, 10, NeighborFunction.CIRCLE);
    Board b = m.getBoard();
    assertTrue(b.getValidIndexes().map(b::getSquare).allMatch(s -> !s.isRevealed()));
  }

  @Test
  public void firstMoveIsZero() {
    Random r = new Random();
    for (int i = 0; i < 20; i++) {
      Minesweeper m = new Minesweeper(8, 8, 10, NeighborFunction.CIRCLE);
      Board b = m.getBoard();
      Point p = new Point(r.nextInt(8), r.nextInt(8));
      assertFalse(m.transition(p).isTerminal());
      assertEquals(0, b.getSquare(p).getNumber());
    }
  }

  @Test
  public void zeroesFlipNeighbors() {
    // Since the first move is always a zero, just check its neighbors.
    for (Point neighbor : board.getNeighbors(start)) {
      assertTrue(board.getSquare(neighbor).isRevealed());
    }

    assertTrue(board.getSquare(new Point(3, 0)).isRevealed());
    assertTrue(board.getSquare(new Point(3, 1)).isRevealed());
  }

  @Test(expected = IllegalStateException.class)
  public void cannotTransitionRevealedSquare() {
    assertFalse(minesweeper.isValid(start));
    minesweeper.transition(start);
  }

  @Test
  public void winning() {
    List<Point> notMines = Arrays.asList(new Point(3, 4), new Point(4, 0), new Point(4, 3));
    Point lastNotMine = new Point(2, 3);

    for (Point p : notMines) {
      assertFalse(minesweeper.transition(p).isTerminal());
    }

    assertTrue(minesweeper.transition(lastNotMine).isWon());
  }

  @Test
  public void losing() {
    assertTrue(minesweeper.transition(new Point(0, 4)).isLost());
  }
}
