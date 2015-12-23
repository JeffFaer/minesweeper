package name.falgout.jeffrey.minesweeper.cli;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

public abstract class Console {
  public void flush() {
    writer().flush();
  }

  public Console format(String fmt, Object... args) {
    return printf(fmt, args);
  }

  public Console printf(String fmt, Object... args) {
    writer().printf(fmt, args);
    return this;
  }

  public abstract Reader reader();

  public abstract String readLine();

  public String readLine(String fmt, Object... args) {
    printf(fmt, args);
    return readLine();
  }

  public abstract char[] readPassword();

  public char[] readPassword(String fmt, Object... args) {
    printf(fmt, args);
    return readPassword();
  }

  public abstract PrintWriter writer();

  public static Console SYSTEM_CONSOLE = System.console() == null ? null : new Console() {
    private final java.io.Console console = System.console();

    @Override
    public PrintWriter writer() {
      return console.writer();
    }

    @Override
    public Reader reader() {
      return console.reader();
    }

    @Override
    public Console format(String fmt, Object... args) {
      console.format(fmt, args);
      return this;
    }

    @Override
    public Console printf(String format, Object... args) {
      console.printf(format, args);
      return this;
    }

    @Override
    public String readLine(String fmt, Object... args) {
      return console.readLine(fmt, args);
    }

    @Override
    public String readLine() {
      return console.readLine();
    }

    @Override
    public char[] readPassword(String fmt, Object... args) {
      return console.readPassword(fmt, args);
    }

    @Override
    public char[] readPassword() {
      return console.readPassword();
    }

    @Override
    public void flush() {
      console.flush();
    }
  };

  public static Console create(Reader in, Writer out) {
    BufferedReader bin = in instanceof BufferedReader ? (BufferedReader) in : new BufferedReader(in);
    PrintWriter pout = out instanceof PrintWriter ? (PrintWriter) out : new PrintWriter(out, true);
    return new Console() {
      @Override
      public Reader reader() {
        return bin;
      }

      @Override
      public String readLine() {
        try {
          return bin.readLine();
        } catch (IOException e) {
          throw new IOError(e);
        }
      }

      @Override
      public char[] readPassword() {
        return readLine().toCharArray();
      }

      @Override
      public PrintWriter writer() {
        return pout;
      }
    };
  }

  public static Console standardConsole() {
    return SYSTEM_CONSOLE != null ? SYSTEM_CONSOLE : Console.create(new InputStreamReader(System.in),
        new OutputStreamWriter(System.out));
  }
}
