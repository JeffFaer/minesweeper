package name.falgout.jeffrey.minesweeper.gui;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import name.falgout.jeffrey.minesweeper.FlagMinesweeper;
import name.falgout.jeffrey.minesweeper.FlagMinesweeperState;
import name.falgout.jeffrey.minesweeper.FlagMinesweeperState.ExtraSquare;
import name.falgout.jeffrey.minesweeper.board.Board;
import name.falgout.jeffrey.minesweeper.board.Board.Square;

public class MinesweeperPane extends Pane {
  private static final PseudoClass DEPRESSED = PseudoClass.getPseudoClass("depressed");

  private final Map<Point, Button> buttons = new LinkedHashMap<>();
  private final Board board;
  private final FlagMinesweeper game;

  public MinesweeperPane(ObservableBoard board, FlagMinesweeperState state) {
    game = new FlagMinesweeper(state);
    this.board = board;
    board.updatedSquare().addListener((obs, oldValue, newValue) -> {
      updateButton(newValue.getKey(), newValue.getValue());
    });

    GridPane grid = new GridPane();
    state.getBoard().getValidIndexes().forEach(p -> {
      Button square = new Button();

      square.setFocusTraversable(false);
      square.setPrefSize(30, 30);
      square.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> armNeighbors(p, e));
      square.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> disarmNeighbors(p, e));
      square.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> updateGame(p, e));

      grid.add(square, p.y, p.x);
      buttons.put(p, square);
    });

    getChildren().add(grid);
  }

  private void updateButton(Point p, Square s) {
    Button b = buttons.get(p);
    if (s.isRevealed()) {
      b.pseudoClassStateChanged(DEPRESSED, true);
    }

    b.getStyleClass().removeIf(c -> !"button".equals(c));

    if (s.isNumber()) {
      String number = "" + s.getNumber();
      b.getStyleClass().add("_" + number);
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
      Set<Point> neighbors = board.getNeighbors(p);
      neighbors.removeIf(pt -> board.getSquare(pt) != Square.Basic.UNKNOWN);
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
      // Do nothing.
    }
  }

  private void checkEnd() {
    if (game.isWin()) {
      System.out.println("Win");
    } else if (game.isLoss()) {
      System.out.println("Lose");
    }
  }
}
