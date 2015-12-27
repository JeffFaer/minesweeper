package name.falgout.jeffrey.minesweeper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;

import org.junit.Test;

import name.falgout.jeffrey.minesweeper.FlagMinesweeperState.ExtraSquare;
import name.falgout.jeffrey.minesweeper.Transition.Action;
import name.falgout.jeffrey.minesweeper.board.Board.Square;
import name.falgout.jeffrey.minesweeper.board.MutableBoard;

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

  FlagMinesweeper flagMinesweeper;

  @Override
  public void init(MinesweeperState state) {
    super.init(state);
    flagMinesweeper = (FlagMinesweeper) minesweeper;
  }

  @Override
  public MinesweeperState createState(MutableBoard player, int numBombs, long seed) {
    return createState(player, numBombs, seed, false);
  }

  public MinesweeperState createState(MutableBoard player, int numBombs, long seed,
      boolean countDown) {
    return new FlagMinesweeperState(player, numBombs, seed, countDown);
  }

  @Override
  public Minesweeper createGame(MinesweeperState state) {
    return new FlagMinesweeper((FlagMinesweeperState) state);
  }

  @Test
  public void flagToggleTest() {
    Square original = board.getSquare(3, 2);
    assertEquals(Square.Basic.UNKNOWN, original);

    flagMinesweeper.flag(3, 2);
    assertEquals(ExtraSquare.FLAG, board.getSquare(3, 2));

    flagMinesweeper.flag(3, 2);
    assertEquals(original, board.getSquare(3, 2));
  }

  @Test
  public void complexRevealTest() {
    Transition reveal = Action.reveal(new Point(1, 2));
    assertFalse(flagMinesweeper.isValid(reveal));

    flagMinesweeper.flag(0, 3);
    assertFalse(flagMinesweeper.isValid(reveal));
    flagMinesweeper.flag(1, 3);
    assertTrue(flagMinesweeper.isValid(reveal));

    assertEquals(Square.Basic.UNKNOWN, board.getSquare(2, 3));
    flagMinesweeper.transition(reveal);
    assertEquals(5, board.getSquare(2, 3).getNumber());
  }

  @Test
  public void countdownTest() {
    init(createState(MinesweeperTest.PLAYER_BOARD, MinesweeperTest.NUM_MINES, MinesweeperTest.SEED,
        true));
    assertEquals(2, board.getSquare(1, 2).getNumber());

    flagMinesweeper.flag(0, 3);
    assertEquals(1, board.getSquare(1, 2).getNumber());
    flagMinesweeper.flag(1, 3);
    assertEquals(0, board.getSquare(1, 2).getNumber());

    flagMinesweeper.reveal(2, 3);
    assertEquals(4, board.getSquare(2, 3).getNumber());
  }

  @Test
  public void revealsMinesAsFlagsOnWin() {
    winning();
    assertEquals(ExtraSquare.FLAG, board.getSquare(0, 4));
  }
}
