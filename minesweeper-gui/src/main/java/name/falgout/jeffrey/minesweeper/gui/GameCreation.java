package name.falgout.jeffrey.minesweeper.gui;

import java.awt.Point;
import java.util.Set;
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import name.falgout.jeffrey.minesweeper.NeighborFunction;
import name.falgout.jeffrey.minesweeper.board.ArrayBoard;
import name.falgout.jeffrey.minesweeper.board.MutableBoard;
import name.falgout.jeffrey.minesweeper.gui.binding.FunctionBindings;
import name.falgout.jeffrey.minesweeper.gui.validate.InputValidator;
import name.falgout.jeffrey.minesweeper.gui.validate.Range;

public class GameCreation extends VBox {
  private static final KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
  private static final StringConverter<Integer> STRING_INT_CONVERTER = new IntegerStringConverter();

  private final IntegerProperty numRows = new SimpleIntegerProperty(16);
  private final IntegerProperty numCols = new SimpleIntegerProperty(30);
  private final IntegerProperty numMines = new SimpleIntegerProperty(99);

  private final IntegerBinding maxMines;

  private final ObjectProperty<Function<Point, Set<Point>>> neighbors = new SimpleObjectProperty<>(
      NeighborFunction.CIRCLE);
  private final BooleanProperty wrapAround = new SimpleBooleanProperty(false);
  private final ObjectBinding<Function<Point, Set<Point>>> actualNeighbors;

  public GameCreation() {
    ObjectBinding<Function<Set<Point>, Set<Point>>> wrapAroundFunction = FunctionBindings.bind(
        numRows.asObject(), numCols.asObject(), NeighborFunction::wrapAround);
    actualNeighbors = Bindings.when(wrapAround)
        .then(FunctionBindings.bind(neighbors, wrapAroundFunction, Function::andThen))
        .otherwise(neighbors);

    NumberBinding size = numRows.multiply(numCols);
    IntegerExpression numNeighbors = IntegerExpression.integerExpression(FunctionBindings.bind(
        new Point(0, 0), actualNeighbors).andThen(Set::size));
    maxMines = (IntegerBinding) Bindings.max(0,
        IntegerExpression.integerExpression(size.subtract(numNeighbors.add(1))));

    TextField rowEntry = createEntry(numRows, 1);
    Label rows = new Label("Rows: ");
    rows.setLabelFor(rowEntry);

    TextField colEntry = createEntry(numCols, 1);
    Label cols = new Label("Columns: ");
    cols.setLabelFor(colEntry);

    TextField minesEntry = createEntry(numMines, 0, maxMines.asObject());
    Label mines = new Label("Mines: ");
    mines.setLabelFor(minesEntry);

    GridPane entries = new GridPane();
    entries.add(rows, 0, 0);
    entries.add(rowEntry, 1, 0);
    entries.add(cols, 0, 1);
    entries.add(colEntry, 1, 1);
    entries.add(mines, 0, 2);
    entries.add(minesEntry, 1, 2);

    HBox selections = new HBox();
    selections.getChildren().addAll(entries);

    Button create = new Button("_Create");
    create.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
      if (ENTER.match(e)) {
        create.fire();
      }
    });

    setAlignment(Pos.CENTER);
    getChildren().addAll(selections, create);

    createFocusTraversal(ENTER, rowEntry, colEntry, minesEntry, create);
  }

  private TextField createEntry(IntegerProperty prop, int min) {
    return createEntry(prop, FunctionBindings.singleton(Range.greaterThan(min)));
  }

  private TextField createEntry(IntegerProperty prop, int min,
      ObservableValue<? extends Integer> max) {
    return createEntry(prop, FunctionBindings.bind(min, max, Range::between));
  }

  private TextField createEntry(IntegerProperty prop, ObjectBinding<Range<Integer>> range) {
    TextField entry = new TextField();
    entry.setAlignment(Pos.CENTER);
    entry.setPrefColumnCount(3);
    entry.addEventFilter(ActionEvent.ACTION, e -> {
      e.consume();
    });

    InputValidator<Integer> validator = new InputValidator<>(STRING_INT_CONVERTER);
    validator.range().bind(range);
    validator.validate(entry);

    validator.value().bindBidirectional(prop.asObject());

    return entry;
  }

  private void createFocusTraversal(KeyCombination keyCode, Node... nodes) {
    for (int i = 0; i < nodes.length; i++) {
      Node next = nodes[(i + 1) % nodes.length];
      nodes[i].addEventHandler(KeyEvent.KEY_PRESSED, e -> {
        if (keyCode.match(e)) {
          next.requestFocus();
        }
      });
    }
  }

  public ObjectBinding<MutableBoard> board() {
    return Bindings.createObjectBinding(() -> new ArrayBoard(numRows.get(), numCols.get(),
        actualNeighbors.get()), numRows, numCols, actualNeighbors);
  }

  public IntegerProperty numRows() {
    return numRows;
  }

  public IntegerProperty numColumns() {
    return numCols;
  }

  public IntegerProperty numMines() {
    return numMines;
  }

  public ObjectBinding<Function<Point, Set<Point>>> neighbors() {
    return actualNeighbors;
  }

  public void setNeighbors(NeighborFunction func) {
    neighbors.set(func);
  }

  public BooleanProperty wrapAround() {
    return wrapAround;
  }
}
