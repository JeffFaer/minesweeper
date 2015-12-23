package name.falgout.jeffrey.minesweeper.cli;

import java.awt.Point;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.falgout.jeffrey.minesweeper.Board;
import name.falgout.jeffrey.minesweeper.Board.Square;
import name.falgout.jeffrey.minesweeper.FlagMinesweeper;
import name.falgout.jeffrey.minesweeper.FlagMinesweeper.ExtraSquare;
import name.falgout.jeffrey.minesweeper.GameState;
import name.falgout.jeffrey.minesweeper.NeighborFunction;
import name.falgout.jeffrey.minesweeper.Transition;

public class ConsoleRunner {
  public static void main(String[] args) {
    Console console = Console.standardConsole();

    String line = console.readLine("Input num_rows, num_cols, num_mines, [0=plus|1=circle]: ");
    String[] parts = line.split(",");
    int numRows = Integer.parseInt(parts[0].trim());
    int numCols = Integer.parseInt(parts[1].trim());
    int numMines = Integer.parseInt(parts[2].trim());
    Function<Point, Set<Point>> f = NeighborFunction.CIRCLE;
    if (parts.length == 4) {
      f = NeighborFunction.values()[Integer.parseInt(parts[3].trim())];
    }

    if (!new ConsoleRunner(console, new FlagMinesweeper(numRows, numCols, numMines, f)).runGame()) {
      System.exit(1);
    }
  }

  private final Console console;
  private final FlagMinesweeper game;

  public ConsoleRunner(FlagMinesweeper game) {
    this(Console.standardConsole(), game);
  }

  public ConsoleRunner(Console console, FlagMinesweeper game) {
    this.console = console;
    this.game = game;
  }

  public boolean runGame() {
    GameState<Transition> state = game;
    Board view = game.getBoard();
    Pattern regex = Pattern.compile("(?<flag>f(?:l(?:ag?)?)?)?,?\\s*(?<row>\\d+),\\s*(?<col>\\d+)");
    do {
      drawBoard(view);

      String line = console.readLine("Input a point [flag] row, col: ");
      Matcher m = regex.matcher(line);
      if (m.matches()) {
        boolean flag = m.group("flag") != null;
        int row = Integer.parseInt(m.group("row")) - 1;
        int col = Integer.parseInt(m.group("col")) - 1;
        Point p = new Point(row, col);
        try {
          if (flag) {
            state = game.flag(p);
          } else {
            state = game.reveal(p);
          }
        } catch (IllegalStateException e) {
          console.printf("Illegal move. Try again.%n");
        }
      } else {
        console.printf("Malformed input.%n");
      }
    } while (!state.isComplete());

    drawBoard(view);
    if (state.isWin()) {
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
          console.printf("%d", s.getNumber());
        } else if (s.isMine()) {
          console.printf("*");
        } else if (s == ExtraSquare.FLAG) {
          console.printf("⚑");
        } else {
          console.printf(" ");
        }
        console.printf("│");
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
