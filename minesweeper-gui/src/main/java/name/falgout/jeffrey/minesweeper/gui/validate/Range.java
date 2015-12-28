package name.falgout.jeffrey.minesweeper.gui.validate;

import java.util.Comparator;
import java.util.Optional;

public interface Range<T> extends Comparable<T> {
  public Optional<T> min();

  public Optional<T> max();

  default boolean contains(T obj) {
    return compareTo(obj) == 0;
  }

  public static <T> Range<T> all() {
    return new All<>();
  }

  public static <T extends Comparable<? super T>> Range<T> lessThan(T max) {
    return lessThan(Comparator.naturalOrder(), max);
  }

  public static <T> Range<T> lessThan(Comparator<? super T> comparator, T max) {
    return new LessThan<>(comparator, max);
  }

  public static <T extends Comparable<? super T>> Range<T> greaterThan(T min) {
    return greaterThan(Comparator.naturalOrder(), min);
  }

  public static <T> Range<T> greaterThan(Comparator<? super T> comparator, T min) {
    return new GreaterThan<>(comparator, min);
  }

  public static <T extends Comparable<? super T>> Range<T> between(T min, T max) {
    return between(Comparator.naturalOrder(), min, max);
  }

  public static <T> Range<T> between(Comparator<? super T> comparator, T min, T max) {
    return new Between<>(comparator, min, max);
  }

  public static class All<T> implements Range<T> {
    @Override
    public int compareTo(T o) {
      return 0;
    }

    @Override
    public Optional<T> min() {
      return Optional.empty();
    }

    @Override
    public Optional<T> max() {
      return Optional.empty();
    }
  }

  public static class LessThan<T> implements Range<T> {
    private final Comparator<? super T> comparator;
    private final T max;

    public LessThan(Comparator<? super T> comparator, T max) {
      this.comparator = comparator;
      this.max = max;
    }

    @Override
    public int compareTo(T o) {
      return comparator.compare(o, max) < 0 ? 0 : 1;
    }

    @Override
    public Optional<T> min() {
      return Optional.empty();
    }

    @Override
    public Optional<T> max() {
      return Optional.of(max);
    }

    @Override
    public String toString() {
      return "x < " + max;
    }
  }

  public static class GreaterThan<T> implements Range<T> {
    private final Comparator<? super T> comparator;
    private final T min;

    public GreaterThan(Comparator<? super T> comparator, T min) {
      this.comparator = comparator;
      this.min = min;
    }

    @Override
    public int compareTo(T o) {
      return comparator.compare(min, o) < 0 ? 0 : -1;
    }

    @Override
    public Optional<T> min() {
      return Optional.of(min);
    }

    @Override
    public Optional<T> max() {
      return Optional.empty();
    }

    @Override
    public String toString() {
      return min + " < x";
    }
  }

  public static class Between<T> implements Range<T> {
    private final LessThan<T> lessThan;
    private final GreaterThan<T> greaterThan;

    public Between(Comparator<? super T> comparator, T min, T max) {
      lessThan = new LessThan<>(comparator, max);
      greaterThan = new GreaterThan<>(comparator, min);
    }

    @Override
    public int compareTo(T o) {
      return lessThan.compareTo(o) + greaterThan.compareTo(o);
    }

    @Override
    public Optional<T> min() {
      return greaterThan.min();
    }

    @Override
    public Optional<T> max() {
      return lessThan.max();
    }

    @Override
    public String toString() {
      return min().get() + " < x < " + max().get();
    }
  }
}
