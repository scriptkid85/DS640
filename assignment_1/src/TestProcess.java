import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ZipProcess is a implmentation of MigratableProcess,
 * which can zip a input file to a specific output position.
 * 
 * @author Zeyuan Li
 * */
public class TestProcess implements MigratableProcess {
  private static final long serialVersionUID = 1L;

  private TransactionalFileInputStream inFile;

  private TransactionalFileOutputStream outFile;

  private String id;

  private String pathPrefix;

  private volatile boolean suspending;

  public TestProcess(String args[]) throws Exception {
    if (args.length != 3) {
      System.out.println("usage: ZipProcess <inputFile1> <outputFile>");
      throw new Exception("Invalid Arguments");
    }

  }
  
  /** 
   * default constructor for transfer processes around nodes and resume process
   */
  public TestProcess() {}

  /**
   * Note: some zip code is adapted from Chapter I/O in book "Think in Java"
   * */
  public void run() {
    System.out.println("Testprocess running!");
    
  }

  public void suspend() {
    
  }

  @Override
  public String toString() {
    return id;
  }

}
