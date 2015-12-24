package name.falgout.jeffrey.minesweeper;

import java.awt.Point;

import name.falgout.jeffrey.minesweeper.Transition.Action;

public class Minesweeper extends Game<Transition, Minesweeper> {
  public Minesweeper(MinesweeperState state) {
    super(state);
  }

  @Override
  protected Minesweeper getSelf() {
    return this;
  }

  public void reveal(int row, int col) {
    reveal(new Point(row, col));
  }

  public void reveal(Point point) {
    transition(Action.reveal(point));
  }
}
