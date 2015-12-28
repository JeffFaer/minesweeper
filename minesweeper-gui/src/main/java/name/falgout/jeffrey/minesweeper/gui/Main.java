package name.falgout.jeffrey.minesweeper.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import name.falgout.jeffrey.minesweeper.ObservableBoard;
import name.falgout.jeffrey.minesweeper.board.MutableBoard;
import name.falgout.jeffrey.minesweeper.gui.binding.DurationBinding;

public class Main extends Application {
  private VBox content;
  private HBox toolbar;
  private Stage stage;

  private GameCreation creation;

  @Override
  public void start(Stage primaryStage) {
    stage = primaryStage;

    toolbar = new HBox();
    VBox.setVgrow(toolbar, Priority.NEVER);
    toolbar.getStyleClass().add("toolbar");

    content = new VBox();
    content.setAlignment(Pos.CENTER);
    content.getChildren().addAll(toolbar);

    createGame();

    Scene scene = new Scene(content);
    addStylesheet(scene);

    primaryStage.setTitle("Minesweeper");
    primaryStage.getIcons().add(new Image(Main.class.getResource("mine.png").toExternalForm()));
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void createGame() {
    if (creation == null) {
      creation = new GameCreation();
      creation.addEventHandler(ActionEvent.ACTION, e -> {
        creation.setDisable(true);
        content.getChildren().remove(creation);
        beginGame(creation.board().get(), creation.numMines().get(), creation.countDown().get());
      });
    }

    creation.setDisable(false);
    content.getChildren().add(creation);
    stage.sizeToScene();
  }

  private void beginGame(MutableBoard board, int numMines, boolean countDown) {
    ObservableBoard obsBoard = new ObservableBoard(board);

    GameStatus status = new GameStatus(obsBoard, numMines);
    HBox.setHgrow(status, Priority.ALWAYS);
    status.setAlignment(Pos.CENTER_RIGHT);

    MinesweeperBoard boardView = new MinesweeperBoard(obsBoard, numMines, countDown);
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

            toolbar.getChildren().remove(status);
            content.getChildren().remove(boardView);
            createGame();
          }
        });

    toolbar.getChildren().add(status);
    content.getChildren().add(boardView);
    stage.sizeToScene();
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
