package name.falgout.jeffrey.minesweeper.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import name.falgout.jeffrey.minesweeper.FlagMinesweeperState;
import name.falgout.jeffrey.minesweeper.NeighborFunction;
import name.falgout.jeffrey.minesweeper.board.ArrayBoard;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) {
    int numRows = 16;
    int numCols = 30;
    int numMines = 99;
    ObservableBoard board = new ObservableBoard(new ArrayBoard(numRows, numCols,
        NeighborFunction.CIRCLE.andThen(NeighborFunction.wrapAround(numRows, numCols))));
    FlagMinesweeperState m = new FlagMinesweeperState(board, numMines, true);
    MinesweeperPane p = new MinesweeperPane(board, m);

    StackPane root = new StackPane();
    root.getChildren().add(p);

    Scene scene = new Scene(root);
    scene.getStylesheets().add(Main.class.getResource("minesweeper.css").toExternalForm());

    primaryStage.setTitle("Minesweeper");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
