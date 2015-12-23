package name.falgout.jeffrey.minesweeper.cli;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import name.falgout.jeffrey.minesweeper.FlagMinesweeper;
import name.falgout.jeffrey.minesweeper.FlagMinesweeperTest;

import org.junit.Before;
import org.junit.Test;

public class ConsoleRunnerTest {
  /**
   * First move new Point(1,1):
   *
   * <pre>
   *   1 2 3 4 5
   *  ┌─┬─┬─┬─┬─┐
   * 1│0│0│2│ │ │
   *  ├─┼─┼─┼─┼─┤
   * 2│0│0│2│ │ │
   *  ├─┼─┼─┼─┼─┤
   * 3│0│1│3│ │ │
   *  ├─┼─┼─┼─┼─┤
   * 4│1│3│ │ │ │
   *  ├─┼─┼─┼─┼─┤
   * 5│ │ │ │ │ │
   *  └─┴─┴─┴─┴─┘
   * </pre>
   *
   * <pre>
   *   1 2 3 4 5
   *  ┌─┬─┬─┬─┬─┐
   * 1│0│0│2│*│*│
   *  ├─┼─┼─┼─┼─┤
   * 2│0│0│2│*│*│
   *  ├─┼─┼─┼─┼─┤
   * 3│0│1│3│5│*│
   *  ├─┼─┼─┼─┼─┤
   * 4│1│3│*│*│3│
   *  ├─┼─┼─┼─┼─┤
   * 5│1│*│*│4│*│
   *  └─┴─┴─┴─┴─┘
   * </pre>
   */
  private FlagMinesweeper minesweeper;

  private ConsoleRunner main;
  private PrintWriter cmd;

  @Before
  public void before() throws IOException {
    PipedReader in = new PipedReader();
    PipedWriter out = new PipedWriter(in);
    cmd = new PrintWriter(out);

    minesweeper = FlagMinesweeperTest.createMinesweeperGame(false);
    main = new ConsoleRunner(Console.create(in, new OutputStreamWriter(System.out)), minesweeper);
  }

  @Test
  public void winningGame() {
    List<Point> notMines = Arrays.asList(new Point(1, 1), new Point(2, 3), new Point(3, 4), new Point(4, 0), new Point(
        4, 3));
    for (Point p : notMines) {
      cmd.printf("%d,%d%n", p.x + 1, p.y + 1);
    }

    assertTrue(main.runGame());
  }

  @Test
  public void losingGame() {
    cmd.printf("2,2%n");
    cmd.printf("1,5%n");

    assertFalse(main.runGame());
  }
}
