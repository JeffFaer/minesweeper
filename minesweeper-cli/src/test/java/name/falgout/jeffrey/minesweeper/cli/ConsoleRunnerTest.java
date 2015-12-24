package name.falgout.jeffrey.minesweeper.cli;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;

import name.falgout.jeffrey.minesweeper.FlagMinesweeperState;
import name.falgout.jeffrey.minesweeper.MinesweeperTest;

import org.junit.Before;
import org.junit.Test;

public class ConsoleRunnerTest {
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
  private FlagMinesweeperState minesweeper;

  private ConsoleRunner main;
  private PrintWriter cmd;

  @Before
  public void before() throws IOException {
    PipedReader in = new PipedReader();
    PipedWriter out = new PipedWriter(in);
    cmd = new PrintWriter(out);

    minesweeper = new FlagMinesweeperState(MinesweeperTest.PLAYER_BOARD, MinesweeperTest.NUM_MINES, MinesweeperTest.SEED);
    main = new ConsoleRunner(Console.create(in, new OutputStreamWriter(System.out)), minesweeper);
  }

  public void runGame(Point... points) {
    for (Point p : points) {
      cmd.printf("%d,%d%n", p.x + 1, p.y + 1);
    }
  }

  @Test
  public void winningGame() {
    runGame(MinesweeperTest.START, new Point(2, 3), new Point(3, 4), new Point(4, 0), new Point(4, 3));

    assertTrue(main.runGame());
  }

  @Test
  public void losingGame() {
    runGame(MinesweeperTest.START, new Point(0, 4));

    assertFalse(main.runGame());
  }
}
