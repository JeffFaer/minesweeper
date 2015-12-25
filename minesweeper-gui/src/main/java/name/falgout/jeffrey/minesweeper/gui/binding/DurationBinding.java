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

  public DurationBinding(ObservableValue<? extends Number> start,
      ObservableValue<? extends Number> end) {
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
    return asString(HMS);
  }

  @Override
  public StringBinding asString(String format) {
    return new StringBinding() {
      {
        bind(DurationBinding.this);
      }

      @Override
      protected String computeValue() {
        return format(format, DurationBinding.this.get());
      }
    };
  }

  /**
   * <pre>
   * %D = days
   * %H = hours
   * %M = minutes
   * %S = seconds
   * %L = milliseconds
   * </pre>
   *
   * @param format
   *          The format string.
   * @param d
   *          The duration to format.
   * @return A formatted string.
   */
  public static String format(String format, Duration d) {
    String actualFormat = format.replace("%D", "%1$03d")
        .replace("%H", "%2$02d")
        .replace("%M", "%3$02d")
        .replace("%S", "%4$02d")
        .replace("%L", "%5$03d");

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
}
