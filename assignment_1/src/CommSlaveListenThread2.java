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


/* Protocol: type definition
 * 0: slave -> master, notify new slave
 * 1: slave -> master, after receiving check message, updating running process table;
 * 2: master-> slave, check alive
 * 3: master-> slave, ask to move process
 * 4: slave -> slave, move process to another slave
 * 
 */


public class CommSlaveListenThread2 extends Thread {
    private boolean debug = true;
  
    private Socket socket = null;
    
    private byte[] bytearray;
    private RunningProcessTable rpt;
    private SlaveTable st;
    private Serializer ser;
    private InputStream is;
    
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
      
      if(command[0] == Byte.valueOf("2")){ // means alive check
        String s = new String(content);
        printDebugInfo("sending current process: Number is " + rpt.size());
        
        for(MigratableProcess mp: rpt.keySet()){
          printDebugInfo(rpt.get(mp));
        }
        
        //TODO: SEND serialized RunningProcessTable rpt;
        byte[] instruction = new byte[1];
        instruction[0] = Byte.valueOf("1");
        
        //byte[] serializedrpt = ser.serialize(rpt);
        ByteSender bsender = new ByteSender(socket, instruction, serializedrpt);
        bsender.run();

        
      }
      else if(command[0] == Byte.valueOf("3")){ //means move instruction
        String receivingcontent = new String(content);
        printDebugInfo("received: " + receivingcontent);
        ser = new Serializer();
        String dest[] = receivingcontent.split(" ");
        String destname = dest[1];
        int destport = Integer.parseInt(dest[2]);
        int movenum = Integer.parseInt(dest[3]);
        
        while(rpt.size() > 0 && movenum > 0){
          
          byte[] instruction = new byte[1];
          instruction[0] = Byte.valueOf("4");
          MigratableProcess mp = rpt.getOne();
          
          String args = rpt.get(mp);
          Pair<MigratableProcess, String> sendcontent = new Pair<MigratableProcess, String>(mp, args);
          printDebugInfo("start to serialize.." + args);
          
          byte[] serializedprocess;
          //TODO:  byte[] serializedprocess = ser.serialize(sendcontent);
                
          rpt.removeprocess(mp);
          -- movenum;
          
          //TODO: send byte[] to destination
          printDebugInfo("start to send serialized process");
          
          ByteSender bsender = new ByteSender(destname, destport, instruction, serializedprocess);
          bsender.run();
          //CommSender csender = new CommSender(destname, destport, message);
          //csender.run();
        }
        
      }
      else if(command[0] == Byte.valueOf("4")){
        printDebugInfo("start deserializing");
        
        
        ser = new Serializer();
        // TODO: Pair<MigratableProcess, String> receivecontent = ser.deserialize(content);         
        // MigratableProcess mp = receivecontent.getLeft();
        // String cmdargs = receivecontent.getRight();
        try{
          ProcessRunner pr = new ProcessRunner(mp, cmdargs, rpt);
          Thread t = new Thread(pr);
          t.start();
        }catch (InvocationTargetException e) {
          System.out.println("");
          System.out.println("Invalid arguments for input command.");
        } catch (Exception e) {
          e.printStackTrace();
        }
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

          
          is.close();
          socket.close();
    
      } catch (IOException e) {
          e.printStackTrace();
      }
   }
}