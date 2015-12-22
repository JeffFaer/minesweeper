package name.falgout.jeffrey.minesweeper;

import java.awt.Point;
import java.io.Console;

import name.falgout.jeffrey.minesweeper.Board.Square;

public class Main implements Runnable {
  public static void main(String[] args) {
    new Main(System.console()).run();
  }

  private final Console console;

  public Main(Console console) {
    this.console = console;
  }

  @Override
  public void run() {
    String line = console.readLine("Input num_rows, num_cols, num_mines, [0=plus|1=circle]: ");
    String[] parts = line.split(",");
    int numRows = Integer.parseInt(parts[0].trim());
    int numCols = Integer.parseInt(parts[1].trim());
    int numMines = Integer.parseInt(parts[2].trim());
    NeighborFunction f = NeighborFunction.CIRCLE;
    if (parts.length == 4) {
      f = NeighborFunction.values()[Integer.parseInt(parts[3].trim())];
    }

    Minesweeper m = new Minesweeper(numRows, numCols, numMines, f);
    GameState<Point> state = m;
    Board view = m.getBoard();
    do {
      drawBoard(view);

      line = console.readLine("Input a point row, col: ");
      parts = line.split(",");
      int row = Integer.parseInt(parts[0].trim()) - 1;
      int col = Integer.parseInt(parts[1].trim()) - 1;
      state = state.transition(new Point(row, col));
    } while (!state.isTerminal());

    drawBoard(view);
    if (state.isWon()) {
      console.printf("You won!%n");
    } else {
      console.printf("You lost!%n");
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
