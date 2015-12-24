package name.falgout.jeffrey.minesweeper.gui.binding;

import static org.mockito.Matchers.any;
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

    end.set(Duration.ofHours(1).plusMinutes(23).plusSeconds(45).toMillis());

    verify(l, times(1)).changed(any(), any(), any());
    verify(l2, times(1)).changed(any(), any(), any());
  }
}
