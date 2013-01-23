import java.io.*;

public interface MigratableProcess extends Serializable, Runnable {
  void run();

  void suspend();

  String toString();
}
