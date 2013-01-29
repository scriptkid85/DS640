import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SlaveChecker2 implements Runnable{
  
  private SlaveTable2 st;
  
  private String slavehostname;
  
  private int slaveport;
  
  private static Socket ClientSocket;
  PrintWriter out;
  BufferedReader in;
  String receivedcontent;
  
  public SlaveChecker2(SlaveTable2 st){
    this.st = st;
  }
  
  @Override
  public void run() {
    System.out.println("start slave checking 2");

    if(st.size() == 0){
      System.out.println("no slave");
      return;
    }
    
    //save old slavetable
    SlaveTable2 tempst = st.clone();
    
    
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
          InputStream is = ClientSocket.getInputStream();
//          in = new BufferedReader(new InputStreamReader(is));
          out.println("Alive?");
//          receivedcontent = in.readLine();
          RunningProcessTable rpt = new RunningProcessTable();
          ObjectInputStream ois = new ObjectInputStream(is);
          try {
            rpt = (RunningProcessTable) ois.readObject();
          } catch (ClassNotFoundException e) {
            System.out.println("Cannot receive the Running Process Table from slave...");
            e.printStackTrace();
          }
          
          ois.close();
          
          st.putslave(slavehost, rpt);
          System.out.println("still alive and running process: ");
          for(Thread process: rpt.keySet()){
            System.out.println(rpt.get(process));
          }
            
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
