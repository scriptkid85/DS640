import java.io.*;

/**
 * TransactionalFileInputStream can do transactional file write to facilitate process migration
 * 
 * @author Zeyuan Li
 * */
public class TransactionalFileOutputStream extends OutputStream implements Serializable {
  private File f;

  private int curidx;

  public TransactionalFileOutputStream(String fpath) {
    String dir = fpath.substring(0, fpath.lastIndexOf("/"));
    File fdir = new File(dir);
    if (!fdir.exists())
      fdir.mkdir();

    f = new File(fpath);
    if (f.exists())
      f.delete();

    curidx = 0;
    try {
      f.createNewFile();
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
    RandomAccessFile raf = new RandomAccessFile(f, "rw");
    raf.seek(curidx);

    curidx++;
    raf.write(wbyte);
    raf.close();
  }

  /*
   * @Override public void write(byte[] b, int len, int off) { try { RandomAccessFile raf = new
   * RandomAccessFile(f, "rw"); raf.seek(curidx); raf.write(b, len, off); curidx += len;
   * raf.close(); } catch (IOException e) { // TODO Auto-generated catch block e.printStackTrace();
   * } }
   * 
   * @Override public void write(byte[] b) { try { RandomAccessFile raf = new RandomAccessFile(f,
   * "rw"); raf.seek(curidx); raf.write(b); curidx += b.length; raf.close(); } catch (IOException e)
   * { // TODO Auto-generated catch block e.printStackTrace(); } }
   */

}
