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


public class CommMasterListenThread2 extends Thread {
  
    private boolean debug = false;
  
    private Socket socket = null;
    private String receivingcontent = new String();
    private RunningProcessTable rpt;
    private SlaveTable st;
    private InetAddress Address; 
    private BufferedReader in;
    private InputStream is;
    private OutputStream os;
    private byte[] bytearray;

    public CommMasterListenThread2(Socket socket, SlaveTable st) {
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
      byte[] content = Arrays.copyOfRange(bytearray, 1, bytearray.length - 1);
      
      if(command[0] == Byte.valueOf("0")){ // means new slave
        String s = new String(content);
        printDebugInfo("sending current process: Number is " + rpt.size());
        
        for(MigratableProcess mp: rpt.keySet()){
          printDebugInfo(rpt.get(mp));
        }
      }
      else if(command[0] == Byte.valueOf("1")){ //means
        
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
        

        in.close();
        is.close();
        socket.close();
          printDebugInfo("Finish receiving");
      } catch (IOException e) {
          e.printStackTrace();
      }
   }
}