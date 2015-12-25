package name.falgout.jeffrey.minesweeper.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import name.falgout.jeffrey.minesweeper.NeighborFunction;
import name.falgout.jeffrey.minesweeper.ObservableBoard;
import name.falgout.jeffrey.minesweeper.board.ArrayBoard;
import name.falgout.jeffrey.minesweeper.board.MutableBoard;
import name.falgout.jeffrey.minesweeper.gui.binding.DurationBinding;

public class Main extends Application {
  private VBox content;
  private HBox toolbar;

  @Override
  public void start(Stage primaryStage) {
    int numRows = 8;
    int numCols = 8;
    int numMines = 10;

    toolbar = new HBox();
    VBox.setVgrow(toolbar, Priority.NEVER);
    toolbar.getStyleClass().add("toolbar");

    content = new VBox();
    content.setAlignment(Pos.CENTER);
    content.getChildren().addAll(toolbar);

    beginGame(new ArrayBoard(numRows, numCols, NeighborFunction.CIRCLE), numMines);

    Scene scene = new Scene(content);
    addStylesheet(scene);

    primaryStage.setTitle("Minesweeper");
    primaryStage.getIcons().add(new Image(Main.class.getResource("mine.png").toExternalForm()));
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

  private void beginGame(MutableBoard board, int numMines) {
    ObservableBoard obsBoard = new ObservableBoard(board);

    GameStatus status = new GameStatus(obsBoard, numMines);
    HBox.setHgrow(status, Priority.ALWAYS);
    status.setAlignment(Pos.CENTER_RIGHT);

    MinesweeperBoard boardView = new MinesweeperBoard(obsBoard, numMines);
    VBox.setVgrow(boardView, Priority.ALWAYS);
    boardView.setAlignment(Pos.CENTER);

    boardView.gameStarted().addListener((obs, oldValue, newValue) -> {
      if (newValue) {
        status.getTimer().start();
      }
    });
    boardView.gameComplete().addListener(
        (obs, oldValue, newValue) -> {
          if (newValue) {
            status.getTimer().stop();
            System.out.println((boardView.getGame().isWin() ? "Win " : "Lose ") + "in "
                + status.getTimer().elapsedTime().asString(DurationBinding.HMSL).get());
          }
        });

    toolbar.getChildren().add(status);
    content.getChildren().add(boardView);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
