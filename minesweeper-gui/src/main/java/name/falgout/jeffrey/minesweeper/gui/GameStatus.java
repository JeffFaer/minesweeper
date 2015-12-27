package name.falgout.jeffrey.minesweeper.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import name.falgout.jeffrey.minesweeper.ObservableBoard;

public class GameStatus extends HBox {
  private final Timer timer;

  public GameStatus(ObservableBoard board, int numMines) {
    super(15);
    timer = new Timer();

    Label flagCount = new Label();
    flagCount.textProperty().bind(board.numFlags().asString("Flags: %d/" + numMines));

    Label elapsedTime = new Label();
    elapsedTime.textProperty().bind(timer.elapsedTime().asString("Time: %H:%M:%S"));

    getChildren().addAll(flagCount, elapsedTime);
  }

  public Timer getTimer() {
    return timer;
  }
}
