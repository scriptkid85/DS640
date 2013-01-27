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

  private static int sleeptime;

  private String id;



  /** 
   * default constructor for transfer processes around nodes and resume process
   * @throws Exception 
   */
  public TestProcess (String[] sleeptime) throws Exception {
    if(sleeptime.length != 2){
      System.out.println("usage: TestProcess sleeptime");
      throw new Exception("Invalid Arguments");
    }
      
    this.sleeptime = Integer.parseInt(sleeptime[1]);
  }

  public TestProcess() {
    this.sleeptime = 0;
  }

  /**
   * Note: some zip code is adapted from Chapter I/O in book "Think in Java"
   * @throws InterruptedException 
   * */
  public void run(){
    System.out.println("Testprocess starts running!");
    try{
      Thread.sleep(sleeptime * 1000);
    }catch(InterruptedException e){
      
    }
    
    
  }

  public void suspend() {
    
  }

  @Override
  public String toString() {
    return id;
  }

}
