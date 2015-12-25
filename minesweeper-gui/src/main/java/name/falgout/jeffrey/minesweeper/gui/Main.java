package name.falgout.jeffrey.minesweeper.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import name.falgout.jeffrey.minesweeper.NeighborFunction;
import name.falgout.jeffrey.minesweeper.board.ArrayBoard;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) {
    int numRows = 8;
    int numCols = 8;
    int numMines = 10;
    ObservableBoard board = new ObservableBoard(new ArrayBoard(numRows, numCols,
        NeighborFunction.CIRCLE.andThen(NeighborFunction.wrapAround(numRows, numCols))));
    MinesweeperPane p = new MinesweeperPane(board, numMines, true);

    Scene scene = new Scene(p);

    addStylesheet(scene);

    primaryStage.setTitle("Minesweeper");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void addStylesheet(Scene scene) {
    scene.getStylesheets().add(Main.class.getResource("minesweeper.css").toExternalForm());

    Path p = Paths.get("minesweeper.css");
    if (Files.exists(p)) {
      scene.getStylesheets().add(p.toUri().toString());
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
