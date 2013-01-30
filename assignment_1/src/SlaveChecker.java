import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SlaveChecker implements Runnable{
  
  private boolean debug = true;
  
  private SlaveTable st;
  
  private String slavehostname;

  private int slaveport;
  
  private static Socket ClientSocket;
  
  PrintWriter out;
  BufferedReader in;
  String receivedcontent;
  
  public SlaveChecker(SlaveTable st){
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
      
      ClientSocket = null;
      PrintWriter out = null;
      try {
          ClientSocket = new Socket(slavehostname, slaveport);
          
          OutputStream os = ClientSocket.getOutputStream();
          out = new PrintWriter(os, true);
          out.println("Alive?");
          out.flush();
          
          
          InputStream is = ClientSocket.getInputStream();
          InputStreamReader isr = new InputStreamReader(is);
          in = new BufferedReader(isr);
          
          receivedcontent = in.readLine();
          printDebugInfo("SlaveChecker: received " + receivedcontent);
          printDebugInfo("SlaveChecker: start receving rpt");
 
          printDebugInfo("SlaveChecker: finish receving rpt");
          
          st.putslave(slavehost, Integer.parseInt(receivedcontent));
          printDebugInfo("SlaveChecker: still alive and running process is " + Integer.parseInt(receivedcontent));

          in.close();
          out.close();
          isr.close();
          is.close();
          os.close();
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
