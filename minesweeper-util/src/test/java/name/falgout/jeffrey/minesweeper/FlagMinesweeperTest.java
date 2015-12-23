package name.falgout.jeffrey.minesweeper;

import static name.falgout.jeffrey.minesweeper.Transition.reveal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;

import name.falgout.jeffrey.minesweeper.Board.Square;
import name.falgout.jeffrey.minesweeper.FlagMinesweeper.ExtraAction;
import name.falgout.jeffrey.minesweeper.FlagMinesweeper.ExtraSquare;

import org.junit.Before;
import org.junit.Test;

public class FlagMinesweeperTest extends MinesweeperTest {
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

  public static FlagMinesweeper createMinesweeperGame() {
    return createMinesweeperGame(true);
  }

  public static FlagMinesweeper createMinesweeperGame(boolean firstMove) {
    return createMinesweeperGame(true, false);
  }

  public static FlagMinesweeper createMinesweeperGame(boolean firstMove, boolean countDown) {
    FlagMinesweeper game = new FlagMinesweeper(5, 5, 10, NeighborFunction.CIRCLE, MinesweeperTest.seed, countDown);
    if (firstMove) {
      game.transition(reveal(MinesweeperTest.start));
    }

    return game;
  }

  @Override
  @Before
  public void before() {
    init(createMinesweeperGame());
  }

  @Test
  public void flagToggleTest() {
    Point p = new Point(3, 2);

    Square original = board.getSquare(p);
    assertEquals(Square.Basic.UNKNOWN, original);

    minesweeper.transition(ExtraAction.flag(p));
    assertEquals(ExtraSquare.FLAG, board.getSquare(p));

    minesweeper.transition(ExtraAction.flag(p));
    assertEquals(original, board.getSquare(p));
  }

  @Test
  public void complexRevealTest() {
    Transition reveal = Transition.reveal(new Point(1, 2));
    assertFalse(minesweeper.isValid(reveal));

    minesweeper.transition(ExtraAction.flag(new Point(0, 3)));
    assertFalse(minesweeper.isValid(reveal));
    minesweeper.transition(ExtraAction.flag(new Point(1, 3)));

    assertTrue(minesweeper.isValid(reveal));
    Point p = new Point(2, 3);
    assertEquals(Square.Basic.UNKNOWN, board.getSquare(p));
    minesweeper.transition(reveal);
    assertEquals(5, board.getSquare(p).getNumber());
  }

  @Test
  public void countdownTest() {
    init(createMinesweeperGame(true, true));
    Point p = new Point(1, 2);
    assertEquals(2, board.getSquare(p).getNumber());

    minesweeper.transition(ExtraAction.flag(new Point(0, 3)));
    assertEquals(1, board.getSquare(p).getNumber());
    minesweeper.transition(ExtraAction.flag(new Point(1, 3)));
    assertEquals(0, board.getSquare(p).getNumber());

    Point p2 = new Point(2, 3);
    minesweeper.transition(Transition.reveal(p2));
    assertEquals(4, board.getSquare(p2).getNumber());
  }
}
