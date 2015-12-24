package name.falgout.jeffrey.minesweeper.gui;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import name.falgout.jeffrey.minesweeper.FlagMinesweeper;
import name.falgout.jeffrey.minesweeper.FlagMinesweeperState;
import name.falgout.jeffrey.minesweeper.FlagMinesweeperState.ExtraSquare;
import name.falgout.jeffrey.minesweeper.board.Board;
import name.falgout.jeffrey.minesweeper.board.Board.Square;
import name.falgout.jeffrey.minesweeper.gui.binding.DurationBinding;

public class MinesweeperPane extends Pane {
  private static final PseudoClass REVEALED = PseudoClass.getPseudoClass("revealed");

  private final Map<Point, Button> buttons = new LinkedHashMap<>();
  private final Board board;
  private final FlagMinesweeper game;

  private final ObjectProperty<Long> start = new SimpleObjectProperty<>();
  private final LongProperty currentTime = new SimpleLongProperty();
  private final Timeline clockUpdate;

  public MinesweeperPane(ObservableBoard board, int numMines, boolean countDown) {
    game = new FlagMinesweeper(new FlagMinesweeperState(board, numMines, countDown));
    this.board = board;

    clockUpdate = new Timeline(new KeyFrame(Duration.millis(1), event -> {
      currentTime.set(System.currentTimeMillis());
    }));
    clockUpdate.setCycleCount(Animation.INDEFINITE);

    board.updatedSquare().addListener((obs, oldValue, newValue) -> {
      updateButton(newValue.getKey(), newValue.getValue());
    });

    Label flagCount = new Label();
    flagCount.setFont(Font.font(25));
    flagCount.textProperty().bind(board.numFlags().asString("Flags: %d/" + numMines));

    Label elapsedTime = new Label();
    elapsedTime.textProperty().bind(new DurationBinding(start, currentTime).asString("Time: %H:%M:%S"));
    elapsedTime.fontProperty().bindBidirectional(flagCount.fontProperty());

    HBox toolbar = new HBox(15);
    toolbar.setAlignment(Pos.CENTER_RIGHT);
    toolbar.getChildren().addAll(flagCount, elapsedTime);

    GridPane grid = new GridPane();
    board.getValidIndexes().forEach(p -> {
      Button square = new Button();

      square.setFocusTraversable(false);
      square.setPrefSize(35, 35);
      square.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> armNeighbors(p, e));
      square.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> disarmNeighbors(p, e));
      square.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> updateGame(p, e));

      grid.add(square, p.y, p.x);
      buttons.put(p, square);
    });

    VBox vbox = new VBox();
    vbox.getChildren().addAll(toolbar, grid);

    getChildren().add(vbox);
  }

  private void updateButton(Point p, Square s) {
    Button b = buttons.get(p);
    if (s.isRevealed()) {
      b.pseudoClassStateChanged(REVEALED, true);
    }

    b.getStyleClass().removeIf(c -> !"button".equals(c));

    if (s.isNumber()) {
      String number = "" + s.getNumber();
      if (s.getNumber() < 0) {
        b.getStyleClass().add("negative");
      } else {
        b.getStyleClass().add("_" + number);
      }

      b.setText(number);
    } else if (s.isMine()) {
      b.getStyleClass().add("mine");
      b.setText("*");
    } else if (s == ExtraSquare.FLAG) {
      b.getStyleClass().add("flag");
      b.setText("⚑");
    } else {
      b.getStyleClass().add("unknown");
      b.setText(" ");
    }
  }

  private void armNeighbors(Point p, MouseEvent e) {
    if (board.getSquare(p).isRevealed()) {
      Set<Point> neighbors = board.getNeighborsBySquare(p, Square.Basic.UNKNOWN::equals);
      for (Point neighbor : neighbors) {
        buttons.get(neighbor).arm();
      }
    }
  }

  private void disarmNeighbors(Point p, MouseEvent e) {
    if (board.getSquare(p).isRevealed()) {
      for (Point neighbor : board.getNeighbors(p)) {
        buttons.get(neighbor).disarm();
      }
    }
  }

  private void updateGame(Point p, MouseEvent e) {
    try {
      switch (e.getButton()) {
      case PRIMARY:
        if (start.get() == null) {
          start.set(System.currentTimeMillis());
          currentTime.set(start.get());
          clockUpdate.play();
        }

        game.reveal(p);
        checkEnd();
        break;
      case SECONDARY:
        game.flag(p);
        checkEnd();
        break;
      default:
        // Do nothing.
      }
    } catch (IllegalStateException ex) {
      ex.printStackTrace();
    }
  }

  private void checkEnd() {
    if (game.isComplete()) {
      clockUpdate.pause();

      if (game.isWin()) {
        System.out.println("Win");
      } else if (game.isLoss()) {
        System.out.println("Lose");
      }
    }
  }
}
