import java.io.*;

/**
 * TransactionalFileInputStream can do transactional file read to facilitate process migration
 * 
 * @author Zeyuan Li
 * */
public class TransactionalFileInputStream extends InputStream implements Serializable {
 
  private File f;
 
  private int curidx;

  public TransactionalFileInputStream(String fpath) {
    f = new File(fpath);
    curidx = 0;
  }

  @Override
  public int read() throws IOException {
    RandomAccessFile raf = new RandomAccessFile(f, "rw");
    raf.seek(curidx);
    
    curidx++;
    int res = raf.read();

    raf.close();
    return res;
  }
  
  @Override
  public int read(byte[] b) throws IOException {
    RandomAccessFile raf = new RandomAccessFile(f, "rw");
    raf.seek(curidx);
    
    int res = raf.read(b);
    curidx += res;
    
    raf.close();
    return res;
  }
  
  @Override
  public int read(byte[] b, int len, int off) throws IOException {
    RandomAccessFile raf = new RandomAccessFile(f, "rw");
    raf.seek(curidx);
    
    int res = raf.read(b, len, off);
    curidx += res;
    
    raf.close();
    return res;
  }

}
