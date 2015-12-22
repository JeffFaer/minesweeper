package name.falgout.jeffrey.minesweeper;

import java.util.Set;

public abstract class GameState<T> {
  protected GameState() {}

  public abstract Set<T> getTransitions();

  public boolean isTerminal() {
    return getTransitions().isEmpty();
  }

  public abstract boolean isWon();

  public abstract boolean isLost();

  public boolean isValid(T transition) {
    return getTransitions().contains(transition);
  }

  public GameState<T> transition(T transition) {
    if (!isValid(transition)) {
      throw new IllegalStateException("Invalid transition.");
    }

    return updateState(transition);
  }

  protected abstract GameState<T> updateState(T transition);
}
