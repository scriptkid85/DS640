import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Arrays;


public class CommSlaveListenThread2 extends Thread {
    private boolean debug = true;
  
    private Socket socket = null;
    
    private byte[] bytearray;
    private String receivingcontent = new String();
    private RunningProcessTable rpt;
    private SlaveTable st;
    private Serializer ser;

    
    private PrintWriter out;
    private BufferedReader in;
    private InputStream is;
    private OutputStream os;
    
    public CommSlaveListenThread2(Socket socket, RunningProcessTable rpt, SlaveTable st) {
      this.socket = socket;
      this.st = st;
      this.rpt = rpt;

    }

    public void printDebugInfo(String s){
      if(debug)
        System.out.println("CommSlaveListenThread: " + s);
    }
    
    public void slavehandler(byte[] bytearray){
      printDebugInfo("SlaveHandler start");
      
      byte[] command = Arrays.copyOfRange(bytearray, 0, 1);
      byte[] content = Arrays.copyOfRange(bytearray, 1, bytearray.length - 1);
      
      if(command[0] == Byte.valueOf("0")){
        String s = new String(content);
        printDebugInfo("sending current process number: ");
        out.println(rpt.size());
        out.flush();
        
        for(MigratableProcess mp: rpt.keySet()){
          printDebugInfo(rpt.get(mp));
        }
        
        
      }
      else if(command[0] == Byte.valueOf("1")){
        
      }
      else if(command[0] == Byte.valueOf("2")){
        
      }
      
    }
    
    public void run() {
      printDebugInfo("Slave receving..");
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
          
          slavehandler(bytearray);
          
          printDebugInfo("received: " + receivingcontent);
          
          os = socket.getOutputStream();
          out = new PrintWriter(os, true);
          
          
          out.close();
          os.close();
          in.close();
          is.close();
          socket.close();
    
      } catch (IOException e) {
          e.printStackTrace();
      }
   }
}