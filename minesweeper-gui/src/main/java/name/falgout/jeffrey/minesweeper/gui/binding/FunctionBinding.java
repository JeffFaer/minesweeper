package name.falgout.jeffrey.minesweeper.gui.binding;

import java.util.function.BiFunction;
import java.util.function.Function;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;

public class FunctionBinding<R> extends ObjectBinding<R> {
  private interface Helper<R> {
    public R apply();
  }

  private class UniHelper<T> implements Helper<R> {
    private final ObservableValue<? extends Function<? super T, ? extends R>> map;
    private final ObservableValue<T> value;

    public UniHelper(ObservableValue<? extends Function<? super T, ? extends R>> map,
        ObservableValue<T> value) {
      this.map = map;
      this.value = value;

      bind(map, value);
    }

    @Override
    public R apply() {
      return map.getValue().apply(value.getValue());
    }
  }

  private class BiHelper<T, U> implements Helper<R> {
    private final ObservableValue<? extends BiFunction<? super T, ? super U, ? extends R>> map;
    private final ObservableValue<T> value1;
    private final ObservableValue<U> value2;

    public BiHelper(ObservableValue<? extends BiFunction<? super T, ? super U, ? extends R>> map,
        ObservableValue<T> value1, ObservableValue<U> value2) {
      this.map = map;
      this.value1 = value1;
      this.value2 = value2;

      bind(map, value1, value2);
    }

    @Override
    public R apply() {
      return map.getValue().apply(value1.getValue(), value2.getValue());
    }
  }

  private final Helper<R> helper;

  public <T> FunctionBinding(ObservableValue<? extends Function<? super T, ? extends R>> map,
      ObservableValue<T> value) {
    helper = new UniHelper<>(map, value);
  }

  public <T, U> FunctionBinding(
      ObservableValue<? extends BiFunction<? super T, ? super U, ? extends R>> map,
      ObservableValue<T> value1, ObservableValue<U> value2) {
    helper = new BiHelper<>(map, value1, value2);
  }

  @Override
  protected R computeValue() {
    return helper.apply();
  }

  public <RR> FunctionBinding<RR> andThen(Function<? super R, ? extends RR> map) {
    return andThen(FunctionBindings.singleton(map));
  }

  public <RR> FunctionBinding<RR> andThen(
      ObservableValue<? extends Function<? super R, ? extends RR>> map) {
    return new FunctionBinding<>(map, this);
  }
}
