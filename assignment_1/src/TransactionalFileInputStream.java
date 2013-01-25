import java.io.*;

public class TransactionalFileInputStream extends InputStream implements Serializable {

  private RandomAccessFile raf;
  private int curidx;
  
  public TransactionalFileInputStream(String fpath) {
    File f = new File(fpath);
    curidx = 0;
    raf = null;
    try {
      raf = new RandomAccessFile(f, "rw");
    } catch (FileNotFoundException e) {
      System.err.println("Input file not found exception.");
      e.printStackTrace();
    }
    
  }
  
  @Override
  public int read() throws IOException {
    raf.seek(curidx);
    // TODO: bottleneck. Should read n bytes instead of one
    curidx++;
    int res = raf.read();
    
    raf.close();
    return res;
  }

}
