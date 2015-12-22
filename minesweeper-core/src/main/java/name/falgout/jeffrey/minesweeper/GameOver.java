package name.falgout.jeffrey.minesweeper;

import java.util.stream.Stream;

public class GameOver<T> extends GameState<T> {
  private final boolean win;

  public GameOver(boolean win) {
    this.win = win;
  }

  @Override
  public Stream<T> getTransitions() {
    return Stream.empty();
  }

  @Override
  public boolean isWon() {
    return win;
  }

  @Override
  public boolean isLost() {
    return win;
  }

  @Override
  protected GameState<T> updateState(T transition) {
    throw new UnsupportedOperationException();
  }

  public static <T> GameOver<T> win() {
    return new GameOver<>(true);
  }

  public static <T> GameOver<T> lose() {
    return new GameOver<>(false);
  }
}
