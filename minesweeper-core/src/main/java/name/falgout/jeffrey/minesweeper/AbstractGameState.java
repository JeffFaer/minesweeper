package name.falgout.jeffrey.minesweeper;

public abstract class AbstractGameState<T> implements GameState<T> {
  protected AbstractGameState() {}

  @Override
  public GameState<T> transition(T transition) {
    if (!isValid(transition)) {
      throw new IllegalStateException("Invalid transition: " + transition);
    }

    return updateState(transition);
  }

  protected abstract GameState<T> updateState(T transition);
}
