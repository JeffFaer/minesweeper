package name.falgout.jeffrey.minesweeper.gui.binding;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Duration;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DurationBindingTest {
  LongProperty start = new SimpleLongProperty();
  LongProperty end = new SimpleLongProperty();
  DurationBinding duration = new DurationBinding(start, end);

  @Mock ChangeListener<Duration> l;
  @Mock ChangeListener<String> l2;

  @Test
  public void singleUpdatePerChange() {
    duration.addListener(l);
    duration.asString().addListener(l2);

    Duration d = Duration.ofHours(1).plusMinutes(23).plusSeconds(45);
    end.set(d.toMillis());

    verify(l, times(1)).changed(same(duration), eq(Duration.ZERO), eq(d));
    verify(l2, times(1)).changed(any(), eq("00:00:00"), eq("01:23:45"));
  }

  @Test
  public void calculatesDifference() {
    start.set(500);
    assertEquals(-500, duration.get().toMillis());

    end.set(1500);
    assertEquals(1000, duration.get().toMillis());
  }
}
