package name.falgout.jeffrey.minesweeper;

import java.awt.Point;

import name.falgout.jeffrey.minesweeper.FlagMinesweeperState.ExtraAction;

public class FlagMinesweeper extends Minesweeper {
  public FlagMinesweeper(FlagMinesweeperState state) {
    super(state);
  }

  @Override
  protected FlagMinesweeper getSelf() {
    return this;
  }

  public void flag(int row, int col) {
    flag(new Point(row, col));
  }

  public void flag(Point point) {
    transition(ExtraAction.flag(point));
  }

  @Override
  public FlagMinesweeper transition(Transition transition) {
    super.transition(transition);
    return getSelf();
  }
}
