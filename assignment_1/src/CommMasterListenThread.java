import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;



/* Protocol: type definition
 * 0: slave -> master, notify new slave or update rpt
 * 1: slave -> master, ask for ps
 * 2: master-> slave, return ps
 * 3: master-> slave, ask to move process
 * 4: slave -> slave, move process to another slave
 * 
 */


public class CommMasterListenThread extends Thread {
  
    private boolean debug = false;
  
    private Socket socket = null;
    private String receivingcontent = new String();
    private RunningProcessTable rpt;
    private SlaveTable st;
    private InetAddress Address; 
    private BufferedReader in;
    private InputStream is;
    private OutputStream os;
    private Serializer ser;
    private byte[] bytearray;

    public CommMasterListenThread(Socket socket, SlaveTable st) {
      this.socket = socket;
      this.st = st;
    }

    public void printDebugInfo(String s){
      if(debug)
        System.out.println("CommMasterListen: " + s);
    }
    
    
    public void masterhandler(byte[] bytearray){
      printDebugInfo("start");
      
      byte[] command = Arrays.copyOfRange(bytearray, 0, 1);
      byte[] content = Arrays.copyOfRange(bytearray, 1, bytearray.length);
      
      if(command[0] == Byte.valueOf("0")){ // means new slave or updated rpt
        ser = new Serializer();
        Pair<String[], RunningProcessTable> serializeslave = (Pair<String[], RunningProcessTable>)ser.deserializeObj(content);
        String[] slavehost = serializeslave.getLeft();
        printDebugInfo("received slave: " + slavehost[0] + " " + slavehost[1]);
        
        RunningProcessTable temprpt = serializeslave.getRight();
        printDebugInfo("received slave's rpt_size : " + temprpt.size());
        
        if(st.containsKey(slavehost)){
          printDebugInfo("receiving old slave update");
          st.putslave(slavehost, temprpt);//st.resetlifetime_updaterpt(slavehost, temprpt); //same as st.putslave()
        }
        else{
          printDebugInfo("receiving new slave+");
          st.putslave(slavehost, temprpt); 
        }
      }
      else if(command[0] == Byte.valueOf("1")){ //receive global ps request
        Serializer ser = new Serializer();
        byte[] instruction = new byte[1];
        String remoteps = new String(content);
        String remotename = remoteps.split(" ")[0];
        int remoteport = Integer.parseInt(remoteps.split(" ")[1]);
        printDebugInfo("received slave: " + remotename + remoteport + "'s ps request");
        
        instruction[0] = Byte.valueOf("2");
        
        byte[] sendst = ser.serializeObj(st);
        
        ByteSender bsender = new ByteSender(remotename, remoteport, instruction, sendst);
        bsender.run();
        printDebugInfo("finished sending gps");
        bsender.close();
      }
    }
    
    public void run() {
      printDebugInfo("Master receving..");
      try{
        printDebugInfo("start get input..");
        is = socket.getInputStream();
        DataInputStream dis = new DataInputStream(is);
          
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[1024];
        int cnt = 0;
        for(int s; (s = dis.read(buffer)) != -1; )
        {
          baos.write(buffer, 0, s);
          cnt += s;
        }
        printDebugInfo("Master receving total num: " + cnt);
        bytearray = baos.toByteArray();
        
        masterhandler(bytearray);
        
        dis.close();
        is.close();
        socket.close();
        printDebugInfo("Finish receiving");
      } catch (IOException e) {
          e.printStackTrace();
      }
   }
}