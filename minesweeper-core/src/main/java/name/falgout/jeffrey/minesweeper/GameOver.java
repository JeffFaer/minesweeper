package name.falgout.jeffrey.minesweeper;

import java.util.Collections;
import java.util.Set;

public class GameOver<T> extends GameState<T> {
  public GameOver() {}

  @Override
  public Set<T> getTransitions() {
    return Collections.emptySet();
  }

  @Override
  protected GameState<T> updateState(T transition) {
    throw new UnsupportedOperationException();
  }

  public static <T> GameOver<T> create() {
    return new GameOver<>();
  }
}
