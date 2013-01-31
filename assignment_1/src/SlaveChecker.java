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



/* Protocol: type definition
 * 0: slave -> master, notify new slave
 * 1: slave -> master, after receiving check message, updating running process table;
 * 2: master-> slave, check alive
 * 3: master-> slave, ask to move process
 * 4: slave -> slave, move process to another slave
 * 
 */

public class SlaveChecker implements Runnable{
  
  private boolean debug = true;
  
  private SlaveTable st;
  
  private String slavehostname;

  private int slaveport;

  private Serializer ser;
  
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
        printDebugInfo("Corrupted slave information in the slave table");
        try {
          throw new Exception("SlaveChecker: Invalid Arguments");
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      
      slavehostname = slavehost[0];
      slaveport = Integer.parseInt(slavehost[1]);

      byte[] instruction = new byte[1];
      instruction[0] = Byte.valueOf("2");
      byte[] meaninglessmsg = new byte[1];
      meaninglessmsg[0] = Byte.valueOf("0");
      //TODO: sender checkalive bytearray to slave with slavehostname and slaveport;
      ByteSender bsender = new ByteSender(slavehostname, slaveport, instruction, meaninglessmsg);
      bsender.run();
      
      InputStream is = null;
      DataInputStream dis = null;;
      
      try {
        is = bsender.socket().getInputStream();
        dis = new DataInputStream(is);
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      
      printDebugInfo("start receving rpt");
      try {
        Thread.sleep(10);
      } catch (InterruptedException e2) {
        // TODO Auto-generated catch block
        e2.printStackTrace();
      }
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte buffer[] = new byte[1024];
      int cnt = 0;
      int s;
      try{
        for(; (dis.available() != 0); )
        { s = dis.read(buffer);
          System.out.println("SlaveChecker: " + s);
          cnt += s;
          baos.write(buffer, 0, s);
        }
        System.out.println("SlaveChecker: total num: " + cnt);
      }catch (IOException e) {

        e.printStackTrace();
      }
      
      
//      try {
//        for(int s; (s = dis.read(buffer)) != -1; )
//        {
//          baos.write(buffer, 0, s);
//          cnt += s;
//        }
//      } catch (IOException e) {
//
//        e.printStackTrace();
//      }
      
      

      if(baos != null && cnt != 0){
        byte[] receivebytes = baos.toByteArray();
        byte[] command = Arrays.copyOfRange(receivebytes, 0, 1);
        byte[] content = Arrays.copyOfRange(receivebytes, 1, receivebytes.length);
        ser = new Serializer();
        RunningProcessTable temprpt = (RunningProcessTable)ser.deserializeObj(content);
        printDebugInfo("finish receving rpt");
        printDebugInfo("received rptsize: " + temprpt.size());
       
        st.putslave(slavehost, temprpt);
        printDebugInfo("finished saving slave in table");
        try {
          dis.close();
          is.close();
          bsender.close();
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    }
    printDebugInfo("finished");
  }

}
