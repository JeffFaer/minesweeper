package name.falgout.jeffrey.minesweeper.gui.binding;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;

public final class FunctionBindings {
  private FunctionBindings() {}

  public static <T> ObjectBinding<T> singleton(T obj) {
    return Bindings.createObjectBinding(() -> obj);
  }

  public static <T, R> FunctionBinding<R> apply(
      ObservableValue<? extends Function<? super T, ? extends R>> map, ObservableValue<T> obs) {
    return new FunctionBinding<>(map, obs);
  }

  public static <T, R> FunctionBinding<R> apply(Function<? super T, ? extends R> map,
      ObservableValue<T> obs) {
    return apply(singleton(map), obs);
  }

  public static <T, R> FunctionBinding<R> apply(
      ObservableValue<? extends Function<? super T, ? extends R>> map, T obs) {
    return apply(map, singleton(obs));
  }

  public static <T, U, R> FunctionBinding<R> apply(
      ObservableValue<? extends BiFunction<? super T, ? super U, ? extends R>> map,
      ObservableValue<T> o1, ObservableValue<U> o2) {
    return new FunctionBinding<>(map, o1, o2);
  }

  public static <T, U, R> FunctionBinding<R> apply(
      BiFunction<? super T, ? super U, ? extends R> map, ObservableValue<T> o1,
      ObservableValue<U> o2) {
    return apply(singleton(map), o1, o2);
  }

  public static <T, U, R> FunctionBinding<R> apply(
      BiFunction<? super T, ? super U, ? extends R> map, T o1, ObservableValue<U> o2) {
    return apply(singleton(map), singleton(o1), o2);
  }

  public static <T, U, R> FunctionBinding<R> apply(
      BiFunction<? super T, ? super U, ? extends R> map, ObservableValue<T> o1, U o2) {
    return apply(singleton(map), o1, singleton(o2));
  }

  public static <T> IntegerBinding applyAsInt(ObservableValue<T> obs, ToIntFunction<? super T> map) {
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

  public static <T> DoubleBinding applyAsDouble(ObservableValue<T> obs,
      ToDoubleFunction<? super T> map) {
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
