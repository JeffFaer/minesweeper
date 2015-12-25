package name.falgout.jeffrey.minesweeper.gui;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import name.falgout.jeffrey.minesweeper.FlagMinesweeper;
import name.falgout.jeffrey.minesweeper.FlagMinesweeperState;
import name.falgout.jeffrey.minesweeper.FlagMinesweeperState.ExtraSquare;
import name.falgout.jeffrey.minesweeper.ObservableBoard;
import name.falgout.jeffrey.minesweeper.board.Board;
import name.falgout.jeffrey.minesweeper.board.Board.Square;
import name.falgout.jeffrey.minesweeper.gui.binding.FunctionBindings;

public class MinesweeperBoard extends GridPane {
  private static final double MIN_GRID_WIDTH = 30;
  private static final PseudoClass REVEALED = PseudoClass.getPseudoClass("revealed");

  private final Map<Point, Button> buttons = new LinkedHashMap<>();
  private final FlagMinesweeper game;
  private final Board board;

  private final BooleanProperty gameStarted = new SimpleBooleanProperty(false);
  private final BooleanProperty gameComplete = new SimpleBooleanProperty(false);

  public MinesweeperBoard(ObservableBoard board, int numMines) {
    this(board, numMines, false);
  }

  public MinesweeperBoard(ObservableBoard board, int numMines, boolean countDown) {
    game = new FlagMinesweeper(new FlagMinesweeperState(board, numMines, countDown));
    this.board = board;

    board.updatedSquare().addListener((obs, oldValue, newValue) -> {
      updateButton(newValue.getKey(), newValue.getValue());
    });

    DoubleBinding insetsLeft = FunctionBindings.bindDouble(insetsProperty(), Insets::getLeft);
    DoubleBinding insetsRight = FunctionBindings.bindDouble(insetsProperty(), Insets::getRight);
    DoubleBinding insetsTop = FunctionBindings.bindDouble(insetsProperty(), Insets::getTop);
    DoubleBinding insetsBottom = FunctionBindings.bindDouble(insetsProperty(), Insets::getBottom);

    DoubleBinding insetsWidth = insetsLeft.add(insetsRight);
    DoubleBinding insetsHeight = insetsTop.add(insetsBottom);

    DoubleBinding usableWidth = widthProperty().subtract(insetsWidth);
    DoubleBinding widthPerColumn = usableWidth.divide(board.getNumColumns());
    DoubleBinding usableHeight = heightProperty().subtract(insetsHeight);
    DoubleBinding heightPerRow = usableHeight.divide(board.getNumRows());

    NumberBinding squareSize = Bindings.min(widthPerColumn, heightPerRow);

    NumberBinding fontSize = squareSize.multiply(.45);
    NumberBinding negativeFontSize = fontSize.multiply(.75);

    board.getValidIndexes().forEach(
        p -> {
          Button square = new Button();
          square.getStyleClass().add("grid");

          square.prefHeightProperty().bindBidirectional(square.prefWidthProperty());
          square.prefHeightProperty().bind(squareSize);
          square.minHeightProperty().bindBidirectional(square.minWidthProperty());
          square.minHeightProperty().set(MIN_GRID_WIDTH);

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

          add(square, p.y, p.x);
          buttons.put(p, square);
        });
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
        if (!game.isComplete()) {
          gameStarted.set(true);
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
      gameComplete.set(true);
    }
  }

  public FlagMinesweeper getGame() {
    return game;
  }

  public ReadOnlyBooleanProperty gameStarted() {
    return gameStarted;
  }

  public ReadOnlyBooleanProperty gameComplete() {
    return gameComplete;
  }
}
