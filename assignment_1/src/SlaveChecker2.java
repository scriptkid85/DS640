import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SlaveChecker2 implements Runnable{
  
  private boolean debug = true;
  
  private SlaveTable st;
  
  private String slavehostname;

  private int slaveport;
  
  private static Socket ClientSocket;
  
  PrintWriter out;
  BufferedReader in;
  String receivedcontent;
  
  public SlaveChecker2(SlaveTable st){
    this.st = st;
  }
  
  public void printDebugInfo(String s){
    if(debug)
      System.out.println("SlaveChecker: " + s);
  }
  
  @Override
  public void run() {
    printDebugInfo("start slave checking");

    if(st.size() == 0){
      printDebugInfo("no slave");
      return;
    }
    
    //save old slavetable
    SlaveTable tempst = st.clone();
    
    for(String[] slavehost: tempst.keySet()){
 
      printDebugInfo("Slave in table: " + slavehost[0] + " " + slavehost[1]);
      if(slavehost.length != 2){
        printDebugInfo("SlaveChecker: Corrupted slave information in the slave table");
        try {
          throw new Exception("SlaveChecker: Invalid Arguments");
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      
      slavehostname = slavehost[0];
      slaveport = Integer.parseInt(slavehost[1]);
      

      PrintWriter out = null;
      try {
          byte[] instruction = new byte[1];
          instruction[0] = Byte.valueOf("1");

          //TODO: sender checkalive bytearray to slave with slavehostname and slaveport;
          
      } catch (UnknownHostException e) {
          System.err.println("SlaveChecker: Don't know about slave: " + slavehostname);
          st.removeslave(slavehost);
          continue;
      } catch (IOException e) {
          System.err.println("SlaveChecker: Couldn't get I/O for the connection to the slave: " + slavehostname);
          st.removeslave(slavehost);
          continue;
      }
      
      try {
        ClientSocket.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    }
  }

}
