package name.falgout.jeffrey.minesweeper.gui;

import java.time.Clock;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Duration;
import name.falgout.jeffrey.minesweeper.gui.binding.DurationBinding;

public class Timer {
  private final ObjectProperty<Long> start = new SimpleObjectProperty<>();
  private final LongProperty currentTime = new SimpleLongProperty();
  private final DurationBinding duration = new DurationBinding(start, currentTime);

  private final Timeline clockUpdate;
  private final Clock clock;

  public Timer() {
    this(Duration.seconds(1));
  }

  public Timer(Duration updateInterval) {
    this(updateInterval, Clock.systemUTC());
  }

  public Timer(Duration updateInterval, Clock clock) {
    clockUpdate = new Timeline(new KeyFrame(updateInterval, event -> {
      updateTime();
    }));
    clockUpdate.setCycleCount(Animation.INDEFINITE);

    this.clock = clock;
  }

  public Clock getClock() {
    return clock;
  }

  public DurationBinding elapsedTime() {
    return duration;
  }

  protected void updateTime() {
    currentTime.set(clock.millis());
  }

  public void start() {
    updateTime();
    start.set(currentTime.get());
    clockUpdate.play();
  }

  public void stop() {
    // Get an exact duration by updating the time at the end.
    updateTime();
    clockUpdate.pause();
  }

  public boolean isRunning() {
    return clockUpdate.getStatus() == Status.RUNNING;
  }
}
