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

  public static <T, R> FunctionBinding<R> bind(ObservableValue<T> obs,
      ObservableValue<? extends Function<? super T, ? extends R>> map) {
    return new FunctionBinding<>(obs, map);
  }

  public static <T, R> FunctionBinding<R> bind(T obs,
      ObservableValue<? extends Function<? super T, ? extends R>> map) {
    return bind(singleton(obs), map);
  }

  public static <T, R> FunctionBinding<R> bind(ObservableValue<T> obs,
      Function<? super T, ? extends R> map) {
    return bind(obs, singleton(map));
  }

  public static <T, U, R> FunctionBinding<R> bind(ObservableValue<T> o1, ObservableValue<U> o2,
      ObservableValue<? extends BiFunction<? super T, ? super U, ? extends R>> map) {
    Function<T, R> curried = t -> map.getValue().apply(t, o2.getValue());
    return new FunctionBinding<>(o1, Bindings.createObjectBinding(() -> curried, o2, map));
  }

  public static <T, U, R> FunctionBinding<R> bind(ObservableValue<T> o1, ObservableValue<U> o2,
      BiFunction<? super T, ? super U, ? extends R> map) {
    return bind(o1, o2, singleton(map));
  }

  public static <T, U, R> FunctionBinding<R> bind(T o1, ObservableValue<U> o2,
      BiFunction<? super T, ? super U, ? extends R> map) {
    return bind(singleton(o1), o2, singleton(map));
  }

  public static <T, U, R> FunctionBinding<R> bind(ObservableValue<T> o1, U o2,
      BiFunction<? super T, ? super U, ? extends R> map) {
    return bind(o1, singleton(o2), singleton(map));
  }

  public static <T> IntegerBinding bindInt(ObservableValue<T> obs, ToIntFunction<? super T> map) {
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

  public static <T> DoubleBinding
      bindDouble(ObservableValue<T> obs, ToDoubleFunction<? super T> map) {
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
