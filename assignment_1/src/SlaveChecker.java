import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;



/**
 * SlaveChecker: SlaveChecker is used in Balancer which check the slaves' status in
 * the slave table, and remove timeout slaves from the table.
 * 
 * @author Guanyu Wang
 * */

public class SlaveChecker implements Runnable{
  
  private boolean debug = false;
  
  private int die_threshold = 2;
  private SlaveTable st;

  
  public SlaveChecker(SlaveTable st){
    this.st = st;
  }
  
  public void printDebugInfo(String s){
    if(debug)
      System.out.println("SlaveChecker: " + s);
  }
  
  @Override
  public void run() {
    SlaveTable tempst = st.clone();
    for(String slave: tempst.keySet()){
      printDebugInfo("the slave: " + slave + "with lifetime" + tempst.getlifetime(slave.split(" ")));
      if(st.getlifetime(slave.split(" ")) > die_threshold){
        printDebugInfo("the slave: " + slave + "gone" );
        st.removeslave(slave.split(" "));
      }
      else{
        printDebugInfo("update " + slave + "'s lifetime");
        st.updateslavetime(slave.split(" "));
      }
    }
    printDebugInfo("finished");
  }
}
