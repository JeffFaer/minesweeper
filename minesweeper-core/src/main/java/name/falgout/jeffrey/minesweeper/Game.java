package name.falgout.jeffrey.minesweeper;

import java.util.stream.Stream;

public abstract class Game<T, G extends Game<T, G>> implements GameState<T> {
  private GameState<T> state;

  protected Game(GameState<T> state) {
    this.state = state;
  }

  protected abstract G getSelf();

  public boolean isComplete() {
    return !state.getTransitions().findAny().isPresent();
  }

  public boolean isWin() {
    return state == GameOver.WIN;
  }

  public boolean isLoss() {
    return state == GameOver.LOSS;
  }

  @Override
  public Stream<? extends T> getTransitions() {
    return state.getTransitions();
  }

  @Override
  public boolean isValid(T transition) {
    return state.isValid(transition);
  }

  @Override
  public G transition(T transition) {
    state = state.transition(transition);
    return getSelf();
  }
}
