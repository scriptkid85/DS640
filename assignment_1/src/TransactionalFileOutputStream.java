import java.io.*;

public class TransactionalFileOutputStream extends OutputStream implements Serializable {

  private RandomAccessFile raf;
  private int curidx;
  
  public TransactionalFileOutputStream(String fpath) {
    File f = new File(fpath);
    curidx = 0;
    raf = null;
    try {
      raf = new RandomAccessFile(f, "rw");
    } catch (FileNotFoundException e) {
      System.err.println("Output file not found exception.");
      e.printStackTrace();
    }
    
  }
  
  @Override
  public void write(int wbyte) throws IOException {
    raf.seek(curidx);
    curidx++;
    raf.write(wbyte);
    raf.close();
  }

}
