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
 * TestProcess which is used to test the ProcessManager
 * It can sleep (run) an arbitrary time period 
 * 
 * @author Guanyu Wang
 * */
public class TestProcess implements MigratableProcess {
  private static final long serialVersionUID = 1L;

  private static int sleeptime;

  private volatile boolean suspending = false;
  private String id;




  public TestProcess (String[] sleeptime) throws Exception {
    if(sleeptime.length != 2){
      System.out.println("usage: TestProcess sleeptime");
      throw new Exception("Invalid Arguments");
    }
    id = sleeptime[0] + sleeptime[1];
    this.sleeptime = Integer.parseInt(sleeptime[1]);
  }

  public TestProcess() {
    this.sleeptime = 0;
  }


  public void run(){
    System.out.println("Testprocess starts running!");
    System.out.println(sleeptime);
    while(!suspending && sleeptime > 0){
      try{
        Thread.sleep(1000);
        sleeptime --;
      }catch(InterruptedException e){
        
      }
    }
    
  }

  public void suspend() {
    suspending = true;
    
  }

  @Override
  public String toString() {
    return id;
  }
  
  
  public static void main(String args[]) throws Exception{
    TestProcess zp = new TestProcess(args);
    Thread t = new Thread(zp);
    t.start();
    Thread.sleep(2000);
    zp.suspend();

    Serializer se = new Serializer();
    String fpath = se.serialize(zp);
    zp = (TestProcess) se.deserialize(fpath);
    t = new Thread(zp);
    t.start();
  }
}
