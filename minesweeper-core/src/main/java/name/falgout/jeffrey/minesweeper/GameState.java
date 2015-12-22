package name.falgout.jeffrey.minesweeper;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class GameState<T> {
  public interface Result {}

  public static enum BasicResult implements Result {
    WIN, LOSS;
  }

  protected GameState() {}

  public abstract Stream<T> getTransitions();

  public boolean isComplete() {
    return getResult().isPresent();
  }

  public Optional<Result> getResult() {
    return Optional.empty();
  }

  public boolean isWin() {
    return getResult().filter(BasicResult.WIN::equals).isPresent();
  }

  public boolean isLoss() {
    return getResult().filter(BasicResult.LOSS::equals).isPresent();
  }

  public boolean isValid(T transition) {
    return getTransitions().filter(transition::equals).findAny().isPresent();
  }

  public GameState<T> transition(T transition) {
    if (!isValid(transition)) {
      throw new IllegalStateException("Invalid transition: " + transition);
    }

    return updateState(transition);
  }

  protected abstract GameState<T> updateState(T transition);
}
