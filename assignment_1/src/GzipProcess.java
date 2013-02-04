import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ZipProcess is a implmentation of MigratableProcess, which can gzip a input file to a specific
 * output position.
 * 
 * @author Zeyuan Li
 * */
public class GzipProcess implements MigratableProcess {
  private static final long serialVersionUID = 3L;

  private TransactionalFileInputStream inFile;

  private TransactionalFileOutputStream outFile;

  private String id;

  private String pathPrefix, infilename;

  private volatile boolean suspending;

  public GzipProcess(String args[]) throws Exception {
    if (args.length != 3) {
      System.out.println("usage: ZipProcess <inputFile1> <outputFile>");
      throw new Exception(" Invalid Arguments");
    }

    pathPrefix = "";
    String[] tmp = args[1].split("/");
    id = tmp[tmp.length - 1] + "_ZipProcess";
    infilename = args[1];
    inFile = new TransactionalFileInputStream(args[1]);
    outFile = new TransactionalFileOutputStream(args[2]);
  }

  /**
   * default constructor for transfer processes around nodes and resume process
   */
  public GzipProcess() {
  }

  /**
   * Note: some zip code is adapted from Chapter I/O in book "Think in Java"
   * */
  public void run() {
    try {
      GZIPOutputStream gos = new GZIPOutputStream(outFile);

      while (!suspending) {
        // read and write a byte each time
        int c = inFile.read();
        if (c == -1) {
          // Checksum valid only after the file has been closed!
          System.out.println("[GzipProcess]: Write done! ");
          break;
        }
        gos.write(c);
        gos.flush();

        // Make ZipProcess take longer
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          System.out.println("sleep interrupted");
        }
      }
      // close the stream
      gos.flush();
      gos.close();
    } catch (EOFException e) {
      // End of File
    } catch (IOException e) {
      System.out.println("[ZipProcess]: Error: " + e);
    }

    // wake up suspend() so that we can call suspend() next time.
    suspending = false;
    System.out.println("Gzip: run finished.");
    System.out.flush();
  }

  public void suspend() {
    System.out.println("Gzip: suspend() is called.");
    System.out.flush();
    suspending = true;
    while (suspending)
      ;
    System.out.println("Gzip: suspend() finished.");
    System.out.flush();

  }

  @Override
  public String toString() {
    return id;
  }

  public static void main(String args[]) throws Exception {
    String[] s = { "GzipProcess", "data/test.txt", "data/res.gz" };
    GzipProcess zp = new GzipProcess(s);
    Thread t = new Thread(zp);
    t.start();
    zp.suspend();

    Serializer se = new Serializer();
    byte[] res = se.serializeObj(zp);
    zp = (GzipProcess) se.deserializeObj(res);
    t = new Thread(zp);
    t.start();
  }

}
