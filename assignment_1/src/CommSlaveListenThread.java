import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class CommSlaveListenThread extends Thread {
    private Socket socket = null;
    private String receivingcontent = new String();
    private RunningProcessTable rpt;
    private SlaveTable st;
    
    private PrintWriter out;
    private BufferedReader in;
    

    public CommSlaveListenThread(Socket socket, RunningProcessTable rpt, SlaveTable st) {
      this.socket = socket;
      this.st = st;
      this.rpt = rpt;

    }

    public void run() {
      System.out.println("Slave receving..");
      try{
          in = new BufferedReader(
                new InputStreamReader(
                socket.getInputStream()));
          out = new PrintWriter(socket.getOutputStream(), true);
          //read one line from socket
          receivingcontent = in.readLine();
          
          System.out.println("received: " + receivingcontent);
          if(receivingcontent != null){
            
            if(receivingcontent.equals("Alive?")){
              System.out.println("sending current process number: " + rpt.size());
              out.println(rpt.size());
            }
            else if(receivingcontent.equals("Process")){  
                    // TODO: DESERIALIZE()
            }
            else if(receivingcontent.split(" ")[0].equals("Move:")){
              String dest[] = receivingcontent.split(" ");
              String destname = dest[1];
              int destport = Integer.parseInt(dest[2]);
              int movenum = Integer.parseInt(dest[3]);
                    // TODO: SERIALIZE() and send to dest
            }
          }
          in.close();
          socket.close();
    
      } catch (IOException e) {
          e.printStackTrace();
      }
   }
}