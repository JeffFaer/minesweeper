package name.falgout.jeffrey.minesweeper.gui.binding;

import java.util.function.ToIntFunction;

import javafx.beans.binding.IntegerBinding;
import javafx.beans.value.ObservableValue;

public final class FunctionBindings {
  private FunctionBindings() {}

  public static <T> IntegerBinding bindInt(ObservableValue<T> obs, ToIntFunction<T> map) {
    return new IntegerBinding() {
      {
        bind(obs);
      }

      @Override
      protected int computeValue() {
        return map.applyAsInt(obs.getValue());
      }
    };
  }
}
