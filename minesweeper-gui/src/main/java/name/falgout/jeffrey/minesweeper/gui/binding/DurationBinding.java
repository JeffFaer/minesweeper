package name.falgout.jeffrey.minesweeper.gui.binding;

import java.time.Duration;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;

public class DurationBinding extends ObjectBinding<Duration> {
  public static final String DHMS = "%D:%H:%M:%S";
  public static final String HMS = "%H:%M:%S";
  public static final String HMSL = "%H:%M:%S.%L";
  
  private final ObservableValue<? extends Number> start;
  private final ObservableValue<? extends Number> end;

  public DurationBinding(ObservableValue<? extends Number> start, ObservableValue<? extends Number> end) {
    this.start = start;
    this.end = end;
    bind(start, end);
  }

  @Override
  protected Duration computeValue() {
    if (start.getValue() == null || end.getValue() == null) {
      return Duration.ZERO;
    } else {
      return Duration.ofMillis(end.getValue().longValue() - start.getValue().longValue());
    }
  }

  @Override
  public StringBinding asString() {
    return asString("%H:%M:%S");
  }

  @Override
  public StringBinding asString(String format) {
    String actualFormat = format.replace("%D", "%1$03d").replace("%H", "%2$02d").replace("%M", "%3$02d").replace("%S",
        "%4$02d").replace("%L", "%5$03d");
    return new StringBinding() {
      {
        bind(DurationBinding.this);
      }

      @Override
      protected String computeValue() {
        Duration d = DurationBinding.this.get();
        long days = d.toDays();
        long hours = d.toHours();
        long minutes = d.toMinutes();
        long seconds = d.toMillis() / 1000;
        long milliseconds = d.toMillis();

        milliseconds -= seconds * 1000;
        seconds -= minutes * 60;
        minutes -= hours * 60;
        hours -= days * 24;
        return String.format(actualFormat, days, hours, minutes, seconds, milliseconds);
      }
    };
  }
}
