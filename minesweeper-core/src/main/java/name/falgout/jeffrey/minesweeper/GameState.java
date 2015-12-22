package name.falgout.jeffrey.minesweeper;

import java.util.stream.Stream;

public abstract class GameState<T> {
  protected GameState() {}

  public abstract Stream<T> getTransitions();

  public boolean isTerminal() {
    return getTransitions().count() == 0;
  }

  public abstract boolean isWon();

  public abstract boolean isLost();

  public boolean isValid(T transition) {
    return getTransitions().filter(transition::equals).findAny().isPresent();
  }

  public GameState<T> transition(T transition) {
    if (!isValid(transition)) {
      throw new IllegalStateException("Invalid transition.");
    }

    return updateState(transition);
  }

  protected abstract GameState<T> updateState(T transition);
}
