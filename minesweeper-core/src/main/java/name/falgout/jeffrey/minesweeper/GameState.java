package name.falgout.jeffrey.minesweeper;

import java.util.stream.Stream;

public interface GameState<T> {
  public abstract Stream<T> getTransitions();

  default boolean isValid(T transition) {
    return getTransitions().filter(transition::equals).findAny().isPresent();
  }

  public GameState<T> transition(T transition);
}
