import java.io.*;

/**
 * MigratableProcess Interface
 * */
public interface MigratableProcess extends Serializable, Runnable {
  void run();
   
  void suspend();

  String toString();
}
 