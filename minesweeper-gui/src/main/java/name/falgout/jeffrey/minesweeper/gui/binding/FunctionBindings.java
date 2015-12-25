package name.falgout.jeffrey.minesweeper.gui.binding;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;

public final class FunctionBindings {
  private FunctionBindings() {}

  public static <T, R> ObjectBinding<R> bind(ObservableValue<T> o1, Function<T, R> map) {
    return new ObjectBinding<R>() {
      {
        bind(o1);
      }

      @Override
      protected R computeValue() {
        return map.apply(o1.getValue());
      }
    };
  }

  public static <T, U, R> ObjectBinding<R> bind(ObservableValue<T> o1, ObservableValue<U> o2,
      BiFunction<T, U, R> map) {
    return new ObjectBinding<R>() {
      {
        bind(o1, o2);
      }

      @Override
      protected R computeValue() {
        return map.apply(o1.getValue(), o2.getValue());
      }
    };
  }

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

  public static <T> DoubleBinding bindDouble(ObservableValue<T> obs, ToDoubleFunction<T> map) {
    return new DoubleBinding() {
      {
        bind(obs);
      }

      @Override
      protected double computeValue() {
        return map.applyAsDouble(obs.getValue());
      }
    };
  }
}
