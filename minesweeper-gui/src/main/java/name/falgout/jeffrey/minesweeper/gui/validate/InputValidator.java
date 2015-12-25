package name.falgout.jeffrey.minesweeper.gui.validate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextInputControl;
import javafx.util.StringConverter;

public class InputValidator<T> {
  private final StringConverter<T> converter;
  private final ObjectProperty<Range<T>> range = new SimpleObjectProperty<>(Range.all());
  private final ObjectProperty<T> value = new SimpleObjectProperty<>();

  private ChangeListener<Boolean> focusedListener;
  private ChangeListener<T> valueListener;
  private ChangeListener<Range<T>> rangeListener;

  public InputValidator(StringConverter<T> converter) {
    this.converter = converter;
  }

  public ObjectProperty<Range<T>> range() {
    return range;
  }

  public ObjectProperty<T> value() {
    return value;
  }

  public void validate(TextInputControl text) {
    if (focusedListener != null) {
      throw new IllegalStateException("Cannot validate two controls.");
    }

    focusedListener = (observable, oldValue, newValue) -> {
      if (!newValue) {
        try {
          validate(text, converter.fromString(text.getText()));
        } catch (RuntimeException e) {
          text.setText(converter.toString(value.get()));
        }
      }
    };
    valueListener = (obs, oldValue, newValue) -> {
      text.setText(converter.toString(newValue));
    };
    rangeListener = (obs, oldValue, newValue) -> {
      validate(text, value.get());
    };

    value.addListener(valueListener);
    text.focusedProperty().addListener(focusedListener);
  }

  private void validate(TextInputControl text, T originalValue) {
    Range<T> range = InputValidator.this.range.get();
    T replacementValue = null;

    int c = range.compareTo(originalValue);
    if (c < 0) {
      replacementValue = range.min().get();
    } else if (c > 0) {
      replacementValue = range.max().get();
    }

    if (replacementValue != null) {
      text.setText(converter.toString(replacementValue));
      value.set(replacementValue);
    } else {
      value.set(originalValue);
    }
  }

  public void stopValidating(TextInputControl text) {
    text.focusedProperty().removeListener(focusedListener);
    value.removeListener(valueListener);
    range.removeListener(rangeListener);
    focusedListener = null;
    valueListener = null;
    rangeListener = null;
  }
}
