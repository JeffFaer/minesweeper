package name.falgout.jeffrey.minesweeper;

import java.awt.Point;

public class Transition {
  public interface Action {
    public static enum Basic implements Action {
      REVEAL;
    }

    public static Transition reveal(Point p) {
      return new Transition(Action.Basic.REVEAL, p);
    }
  }

  private final Action action;
  private final Point point;

  public Transition(Action action, Point point) {
    this.action = action;
    this.point = point;
  }

  public Action getAction() {
    return action;
  }

  public Point getPoint() {
    return point;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((action == null) ? 0 : action.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Transition)) {
      return false;
    }
    Transition other = (Transition) obj;
    if (action == null) {
      if (other.action != null) {
        return false;
      }
    } else if (!action.equals(other.action)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(action);
    builder.append(": ");
    builder.append(point);
    return builder.toString();
  }
}
