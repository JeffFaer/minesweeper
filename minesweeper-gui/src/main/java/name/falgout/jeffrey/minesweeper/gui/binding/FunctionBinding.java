package name.falgout.jeffrey.minesweeper.gui.binding;

import java.util.function.Function;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;

public class FunctionBinding<R> extends ObjectBinding<R> {
  private class Helper<T> {
    private final ObservableValue<T> value;
    private final ObservableValue<? extends Function<? super T, ? extends R>> map;

    public Helper(ObservableValue<T> value,
        ObservableValue<? extends Function<? super T, ? extends R>> map) {
      this.value = value;
      this.map = map;
    }

    public R apply() {
      return map.getValue().apply(value.getValue());
    }
  }

  private final Helper<?> helper;

  public <T> FunctionBinding(ObservableValue<T> value,
      ObservableValue<? extends Function<? super T, ? extends R>> map) {
    helper = new Helper<>(value, map);

    bind(value, map);
  }

  @Override
  protected R computeValue() {
    return helper.apply();
  }

  public <RR> FunctionBinding<RR> andThen(Function<? super R, ? extends RR> map) {
    return new FunctionBinding<>(this, FunctionBindings.singleton(map));
  }

  public <RR> FunctionBinding<RR> andThen(
      ObservableValue<? extends Function<? super R, ? extends RR>> map) {
    return new FunctionBinding<>(this, map);
  }
}
