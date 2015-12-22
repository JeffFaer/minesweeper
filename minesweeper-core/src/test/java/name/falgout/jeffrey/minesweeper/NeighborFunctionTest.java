package name.falgout.jeffrey.minesweeper;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.awt.Point;
import java.util.Set;
import java.util.function.Function;

import org.junit.Test;

public class NeighborFunctionTest {
  @Test
  public void negativeWrapAroundTest() {
    Function<Point, Set<Point>> neighborFunction = NeighborFunction.CIRCLE.andThen(NeighborFunction.wrapAround(5, 5));
    Set<Point> neighbors = neighborFunction.apply(new Point(0, 0));
    assertEquals(8, neighbors.size());
    assertThat(neighbors, hasItems(new Point(4, 0), new Point(4, 1), new Point(0, 4), new Point(1, 4), new Point(4, 4)));
  }

  @Test
  public void positiveWrapAroundTest() {
    Function<Point, Set<Point>> neighborFunction = NeighborFunction.CIRCLE.andThen(NeighborFunction.wrapAround(5, 5));
    Set<Point> neighbors = neighborFunction.apply(new Point(4, 4));
    assertEquals(8, neighbors.size());
    assertThat(neighbors, hasItems(new Point(0, 0), new Point(4, 0), new Point(3, 0), new Point(0, 3), new Point(0, 4)));
  }

  @Test
  public void smallWrapAroundTest() {
    Function<Point, Set<Point>> neighborFunction = NeighborFunction.CIRCLE.andThen(NeighborFunction.wrapAround(2, 2));
    Set<Point> neighbors = neighborFunction.apply(new Point(0, 0));
    assertEquals(3, neighbors.size());
    assertThat(neighbors, containsInAnyOrder(new Point(0, 1), new Point(1, 0), new Point(1, 1)));
  }
}
