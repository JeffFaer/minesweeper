package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
import java.util.Set;
import java.util.function.Function;

import name.falgout.jeffrey.minesweeper.Board.Square;

public class ConsoleRunner {
  public static void main(String[] args) {
    Console console = Console.SYSTEM_CONSOLE;
    String line = console.readLine("Input num_rows, num_cols, num_mines, [0=plus|1=circle]: ");
    String[] parts = line.split(",");
    int numRows = Integer.parseInt(parts[0].trim());
    int numCols = Integer.parseInt(parts[1].trim());
    int numMines = Integer.parseInt(parts[2].trim());
    Function<Point, Set<Point>> f = NeighborFunction.CIRCLE;
    if (parts.length == 4) {
      f = NeighborFunction.values()[Integer.parseInt(parts[3].trim())];
    }

    if (!new ConsoleRunner(console, new Minesweeper(numRows, numCols, numMines, f)).runGame()) {
      System.exit(1);
    }
  }

  private final Console console;
  private final Minesweeper game;

  public ConsoleRunner(Minesweeper game) {
    this(Console.SYSTEM_CONSOLE, game);
  }

  public ConsoleRunner(Console console, Minesweeper game) {
    super();
    this.console = console;
    this.game = game;
  }

  public boolean runGame() {
    GameState<Point> state = game;
    Board view = game.getBoard();
    do {
      drawBoard(view);

      String line = console.readLine("Input a point row, col: ");
      String[] parts = line.split(",");
      int row = Integer.parseInt(parts[0].trim()) - 1;
      int col = Integer.parseInt(parts[1].trim()) - 1;
      try {
        state = state.transition(new Point(row, col));
      } catch (IllegalStateException e) {
        console.printf("Illegal move. Try again.%n");
      }
    } while (!state.isTerminal());

    drawBoard(view);
    if (state.isWon()) {
      console.printf("You won!%n");
      return true;
    } else {
      console.printf("You lost!%n");
      return false;
    }
  }

  private void drawBoard(Board view) {
    console.printf(" ");
    for (int col = 0; col < view.getNumColumns(); col++) {
      console.printf(" %d", col + 1);
    }
    console.printf("%n");

    // 0th header
    console.printf(" ┌");
    for (int col = 0; col < view.getNumColumns() - 1; col++) {
      console.printf("─┬");
    }
    console.printf("─┐%n");

    for (int row = 0; row < view.getNumRows(); row++) {
      if (row > 0) {
        // 1st - last header
        console.printf(" ├");
        for (int col = 0; col < view.getNumColumns() - 1; col++) {
          console.printf("─┼");
        }
        console.printf("─┤%n");
      }

      console.printf("%d│", row + 1);
      for (int col = 0; col < view.getNumColumns(); col++) {
        Square s = view.getSquare(row, col);
        if (s.isNumber()) {
          console.printf("%d│", s.getNumber());
        } else if (s.isMine()) {
          console.printf("*│");
        } else {
          console.printf(" │");
        }
      }
      console.printf("%n");
    }

    // Trailer
    console.printf(" └");
    for (int col = 0; col < view.getNumColumns() - 1; col++) {
      console.printf("─┴");
    }
    console.printf("─┘%n");
  }
}
