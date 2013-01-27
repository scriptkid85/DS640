import java.io.*;

public class TransactionalFileOutputStream extends OutputStream implements Serializable {

  private RandomAccessFile raf;
  private File f;

  private int curidx;

  public TransactionalFileOutputStream(String fpath) {
    f = new File(fpath);
    if(f.exists())
      f.delete();
    
    curidx = 0;
    raf = null;
    try {
      f.createNewFile();
      raf = new RandomAccessFile(f, "rw");
    } catch (FileNotFoundException e) {
      System.err.println("Output file not found exception.");
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("Create output file error.");
      e.printStackTrace();
    }

  }

  @Override
  public void write(int wbyte) throws IOException {
    raf = new RandomAccessFile(f, "rw");
    raf.seek(curidx);
    curidx++;
    raf.write(wbyte);
    raf.close();
  }

}
