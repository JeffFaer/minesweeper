package name.falgout.jeffrey.minesweeper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;

import org.junit.Test;

public class GridSetTest {

  GridSet grid = new GridSet(4, 3);

  @Test
  public void gridSetHasSquareSize() {
    assertEquals(4 * 3, grid.size());
    assertEquals(4 * 3, grid.toArray().length);
  }

  @Test
  public void gridContainsCorners() {
    assertTrue(grid.contains(new Point(0, 0)));
    assertTrue(grid.contains(new Point(3, 2)));
  }

  @Test
  public void gridContainsMiddle() {
    assertTrue(grid.contains(new Point(1, 1)));
    assertTrue(grid.contains(new Point(2, 2)));
  }

  @Test
  public void gridDoesNotContainBadPoints() {
    assertFalse(grid.contains(new Object()));
    assertFalse(grid.contains(new Point(-1, -1)));
    assertFalse(grid.contains(new Point(4, 3)));
    assertFalse(grid.contains(new Point(500, 500)));
  }
}
