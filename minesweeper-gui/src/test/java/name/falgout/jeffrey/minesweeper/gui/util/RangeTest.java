package name.falgout.jeffrey.minesweeper.gui.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import name.falgout.jeffrey.minesweeper.gui.validate.Range;

public class RangeTest {
  @Test
  public void lessThanTest() {
    Range<Integer> range = Range.lessThan(5);

    assertTrue(range.contains(3));
    assertFalse(range.contains(5));
    assertFalse(range.contains(7));
  }

  @Test
  public void greaterThanTest() {
    Range<Integer> range = Range.greaterThan(5);

    assertFalse(range.contains(3));
    assertFalse(range.contains(5));
    assertTrue(range.contains(7));
  }

  @Test
  public void betweenTest() {
    Range<Integer> range = Range.between(4, 6);

    assertFalse(range.contains(3));
    assertFalse(range.contains(4));
    assertTrue(range.contains(5));
    assertFalse(range.contains(6));
    assertFalse(range.contains(7));
  }

  @Test
  public void allTest() {
    Range<Integer> range = Range.all();

    assertTrue(range.contains(3));
    assertTrue(range.contains(4));
    assertTrue(range.contains(5));
    assertTrue(range.contains(6));
    assertTrue(range.contains(7));
  }
}
