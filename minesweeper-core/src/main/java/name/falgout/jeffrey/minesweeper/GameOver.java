package name.falgout.jeffrey.minesweeper;

import java.util.stream.Stream;

public enum GameOver implements GameState<Object> {
  WIN, LOSS;

  @Override
  public Stream<Object> getTransitions() {
    return Stream.empty();
  }

  @Override
  public boolean isValid(Object transition) {
    return false;
  }

  @Override
  public GameState<Object> transition(Object transition) {
    throw new IllegalStateException("Game over!");
  }

  @SuppressWarnings("unchecked")
  public static <T> GameState<T> win() {
    return (GameState<T>) WIN;
  }

  @SuppressWarnings("unchecked")
  public static <T> GameState<T> loss() {
    return (GameState<T>) LOSS;
  }
}
