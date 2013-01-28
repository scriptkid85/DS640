import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class SlaveChecker implements Runnable{
  
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
  
  @Override
  public void run() {
    
    if(st.size() == 0){
      System.out.println("not slave");
      return;
    }
    
    //save old slavetable
    SlaveTable tempst = st.clone();
    
    
    for(String[] slavehost: tempst.keySet()){
      System.out.println("Slave in table: " + slavehost[0] + " " + slavehost[1]);
      if(slavehost.length != 2){
        System.out.println("Corrupted slave information in the slave table");
        try {
          throw new Exception(" Invalid Arguments");
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
          out = new PrintWriter(ClientSocket.getOutputStream(), true);
          in = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
          out.println("Alive?");
          receivedcontent = in.readLine();
          st.putslave(slavehost, Integer.parseInt(receivedcontent));
          System.out.println("still alive and running process is " + Integer.parseInt(receivedcontent));
      } catch (UnknownHostException e) {
          System.err.println("Don't know about slave: " + slavehostname);
          st.removeslave(slavehost);
          continue;
      } catch (IOException e) {
          System.err.println("Couldn't get I/O for the connection to the slave: " + slavehostname);
          st.removeslave(slavehost);
          continue;
      }
    }
  }

}
