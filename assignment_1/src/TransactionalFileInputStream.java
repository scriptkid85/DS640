import java.io.*;

public class TransactionalFileInputStream extends InputStream implements Serializable {

  //private RandomAccessFile raf;
  private File f;
 
  private int curidx;

  public TransactionalFileInputStream(String fpath) {
    f = new File(fpath);
    curidx = 0;
    //raf = null;
    /*try {
      raf = new RandomAccessFile(f, "rw");
    } catch (FileNotFoundException e) {
      System.err.println("Input file not found exception.");
      e.printStackTrace();
    }*/

  }

  @Override
  public int read() throws IOException {
    RandomAccessFile raf = new RandomAccessFile(f, "rw");
    raf.seek(curidx);
    
    //System.out.println(curidx);
    
    // TODO: bottleneck. Should read n bytes instead of one
    curidx++;
    int res = raf.read();

    raf.close();
    return res;
  }
  
  @Override
  public int read(byte[] b) throws IOException {
    RandomAccessFile raf = new RandomAccessFile(f, "rw");
    raf.seek(curidx);
    
    //System.out.println(curidx);
    
    int res = raf.read(b);
    curidx += res;
    
    raf.close();
    return res;
  }
  
  @Override
  public int read(byte[] b, int len, int off) throws IOException {
    RandomAccessFile raf = new RandomAccessFile(f, "rw");
    raf.seek(curidx);
    
    //System.out.println(curidx);
     
    int res = raf.read(b, len, off);
    curidx += res;
    
    raf.close();
    return res;
  }

}
