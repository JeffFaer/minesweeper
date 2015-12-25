package name.falgout.jeffrey.minesweeper.gui;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import name.falgout.jeffrey.minesweeper.FlagMinesweeper;
import name.falgout.jeffrey.minesweeper.FlagMinesweeperState;
import name.falgout.jeffrey.minesweeper.FlagMinesweeperState.ExtraSquare;
import name.falgout.jeffrey.minesweeper.board.Board;
import name.falgout.jeffrey.minesweeper.board.Board.Square;
import name.falgout.jeffrey.minesweeper.gui.binding.FunctionBindings;

public class MinesweeperPane extends VBox {
  private static final double MIN_GRID_WIDTH = 35;
  private static final PseudoClass REVEALED = PseudoClass.getPseudoClass("revealed");

  private final Map<Point, Button> buttons = new LinkedHashMap<>();
  private final Board board;
  private final FlagMinesweeper game;

  private final Timer timer;

  public MinesweeperPane(ObservableBoard board, int numMines, boolean countDown) {
    game = new FlagMinesweeper(new FlagMinesweeperState(board, numMines, countDown));
    this.board = board;

    timer = new Timer();

    board.updatedSquare().addListener((obs, oldValue, newValue) -> {
      updateButton(newValue.getKey(), newValue.getValue());
    });

    Label flagCount = new Label();
    flagCount.textProperty().bind(board.numFlags().asString("Flags: %d/" + numMines));

    Label elapsedTime = new Label();
    elapsedTime.textProperty().bind(timer.elapsedTime().asString("Time: %H:%M:%S"));

    HBox toolbar = new HBox(15);
    toolbar.getStyleClass().add("toolbar");
    toolbar.setAlignment(Pos.CENTER_RIGHT);
    toolbar.getChildren().addAll(flagCount, elapsedTime);

    DoubleBinding widthPerColumn = widthProperty().divide(board.getNumColumns());
    DoubleBinding usableHeight = heightProperty().subtract(toolbar.heightProperty());
    DoubleBinding heightPerRow = usableHeight.divide(board.getNumRows());
    NumberBinding squareSize = Bindings.max(MIN_GRID_WIDTH,
        Bindings.min(widthPerColumn, heightPerRow));

    NumberBinding fontSize = squareSize.multiply(.5);
    NumberBinding negativeFontSize = fontSize.multiply(.75);

    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    board.getValidIndexes().forEach(
        p -> {
          Button square = new Button();
          square.getStyleClass().add("grid");

          square.prefHeightProperty().bindBidirectional(square.prefWidthProperty());
          square.prefHeightProperty().bind(squareSize);
          square.minHeightProperty().bindBidirectional(square.minWidthProperty());
          square.minHeightProperty().bind(square.prefHeightProperty());

          BooleanBinding isNegative = FunctionBindings.bindInt(square.textProperty(),
              this::safeParseInt).lessThan(0);
          NumberBinding fullFontSize = Bindings.when(isNegative)
              .then(negativeFontSize)
              .otherwise(fontSize);
          square.styleProperty().bind(fullFontSize.asString("-fx-font-size: %f;"));

          square.setFocusTraversable(false);
          square.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> armNeighbors(p, e));
          square.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> disarmNeighbors(p, e));
          square.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> updateGame(p, e));

          grid.add(square, p.y, p.x);
          buttons.put(p, square);
        });

    getChildren().addAll(toolbar, grid);
  }

  private void updateButton(Point p, Square s) {
    Button b = buttons.get(p);
    if (s.isRevealed()) {
      b.pseudoClassStateChanged(REVEALED, true);
    }

    if (s.isNumber()) {
      b.getStyleClass().removeIf(str -> str.startsWith("_"));
      String number = "" + s.getNumber();
      if (s.getNumber() < 0) {
        b.getStyleClass().add("_negative");
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
      b.getStyleClass().remove("flag");
      b.setText("");
    }
  }

  private int safeParseInt(String s) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      return 0;
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
        if (!game.isComplete() && !timer.isRunning()) {
          timer.start();
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
      timer.stop();

      if (game.isWin()) {
        System.out.println("Win");
      } else if (game.isLoss()) {
        System.out.println("Lose");
      }
    }
  }
}
