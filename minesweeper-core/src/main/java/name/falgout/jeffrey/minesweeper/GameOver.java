package name.falgout.jeffrey.minesweeper;

import java.util.Optional;
import java.util.stream.Stream;

public class GameOver<T> extends GameState<T> {
  private final Result result;

  public GameOver(Result result) {
    this.result = result;
  }

  @Override
  public Optional<Result> getResult() {
    return Optional.of(result);
  }

  @Override
  public Stream<T> getTransitions() {
    return Stream.empty();
  }

  @Override
  protected GameState<T> updateState(T transition) {
    throw new UnsupportedOperationException();
  }

  public static <T> GameOver<T> win() {
    return new GameOver<>(BasicResult.WIN);
  }

  public static <T> GameOver<T> loss() {
    return new GameOver<>(BasicResult.LOSS);
  }

}
