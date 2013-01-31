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
 * 0: slave -> master, notify new slave
 * 1: slave -> master, after receiving check message, updating running process table;
 * 2: master-> slave, check alive
 * 3: master-> slave, ask to move process
 * 4: slave -> slave, move process to another slave
 * 
 */

public class CommMasterListenThread extends Thread {
  
    private boolean debug = true;
  
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
      printDebugInfo("SlaveHandler start");
      
      byte[] command = Arrays.copyOfRange(bytearray, 0, 1);
      byte[] content = Arrays.copyOfRange(bytearray, 1, bytearray.length);
      
      if(command[0] == Byte.valueOf("0")){ // means new slave
        ser = new Serializer();
        String[] slavehost = (String[])ser.deserializeObj(content);
        printDebugInfo("receiving slave :" + slavehost[0] + " " + slavehost[1]);
        
        RunningProcessTable empty = new RunningProcessTable();
        st.putslave(slavehost, empty);
      }
      else{
      }
    }
    
    public void run() {
      printDebugInfo("Master receving..");
      try{
        is = socket.getInputStream();
        DataInputStream dis = new DataInputStream(is);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[1024];
        for(int s; (s = dis.read(buffer)) != -1; )
        {
          baos.write(buffer, 0, s);
        }
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