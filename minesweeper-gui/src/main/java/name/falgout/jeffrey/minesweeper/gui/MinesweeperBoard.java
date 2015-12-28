package name.falgout.jeffrey.minesweeper.gui;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
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

public class MinesweeperBoard extends GridPane {
  private static final double MIN_GRID_WIDTH = 25;
  private static final PseudoClass REVEALED = PseudoClass.getPseudoClass("revealed");

  private final Map<Point, Button> buttons = new LinkedHashMap<>();
  private final FlagMinesweeper game;
  private final Board board;

  private final BooleanProperty gameStarted = new SimpleBooleanProperty(false);
  private final BooleanProperty gameComplete = new SimpleBooleanProperty(false);

  private DoubleBinding squareSize;

  public MinesweeperBoard(ObservableBoard board, int numMines) {
    this(board, numMines, false);
  }

  public MinesweeperBoard(ObservableBoard board, int numMines, boolean countDown) {
    game = new FlagMinesweeper(new FlagMinesweeperState(board, numMines, countDown));
    this.board = board;

    board.addListener(e -> {
      updateButton(e.getPoint(), e.getSquare());
    });

    squareSize = Bindings.createDoubleBinding(this::calculateSquareSize, insetsProperty(),
        heightProperty(), widthProperty());

    board.getValidIndexes().forEach(
        p -> {
          Button square = new Button();
          square.getStyleClass().add("grid");

          square.minHeightProperty().bindBidirectional(square.minWidthProperty());
          square.minHeightProperty().set(MIN_GRID_WIDTH);
          square.prefHeightProperty().bindBidirectional(square.prefWidthProperty());
          square.prefHeightProperty().bind(squareSize);

          DoubleBinding fontSize = Bindings.createDoubleBinding(() -> calculateFontSize(square),
              squareSize, square.textProperty());
          square.styleProperty().bind(fontSize.asString("-fx-font-size: %f;"));

          square.setFocusTraversable(false);
          square.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> armNeighbors(p, e));
          square.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> disarmNeighbors(p, e));
          square.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> updateGame(p, e));

          GridPane.setRowIndex(square, p.x);
          GridPane.setColumnIndex(square, p.y);
          buttons.put(p, square);
        });

    getChildren().addAll(buttons.values());
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

  private double calculateSquareSize() {
    Insets insets = insetsProperty().get();
    double insetsWidth = insets.getLeft() + insets.getRight();
    double insetsHeight = insets.getTop() + insets.getBottom();

    double usableWidth = widthProperty().get() - insetsWidth;
    double usableHeight = heightProperty().get() - insetsHeight;

    double widthPerColumn = usableWidth / board.getNumColumns();
    double heightPerRow = usableHeight / board.getNumRows();

    return Math.min(widthPerColumn, heightPerRow);
  }

  private double calculateFontSize(Button square) {
    double standardFontSize = .45 * squareSize.get();
    if (square.getStyleClass().contains("_negative")) {
      return .75 * standardFontSize;
    } else {
      return standardFontSize;
    }
  }

  private void armNeighbors(Point p, MouseEvent e) {
    if (board.getSquare(p).isRevealed()) {
      Set<? extends Point> neighbors = board.getNeighborsBySquare(p, Square.Basic.UNKNOWN::equals);
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
