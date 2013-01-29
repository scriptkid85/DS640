import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class CommMasterListenThread extends Thread {
    private Socket socket = null;
    private String receivingcontent = new String();
    private RunningProcessTable rpt;
    private SlaveTable st;
    private InetAddress Address;; 
    private String localhost;
    
    private PrintWriter out;
    private BufferedReader in;
    

    public CommMasterListenThread(Socket socket, SlaveTable st) {
      this.socket = socket;
      this.st = st;
      this.rpt = rpt;
      try{
        Address = InetAddress.getLocalHost();
        localhost = Address.getHostName();
      } catch (UnknownHostException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    public void run() {
      System.out.println("Master receving..");
      try{
          in = new BufferedReader(
                new InputStreamReader(
                socket.getInputStream()));
          out = new PrintWriter(socket.getOutputStream(), true);
          //read one line from socket
          receivingcontent = in.readLine();
          System.out.println("received: " + receivingcontent);
          if(receivingcontent != null){
            System.out.println("enter saving step: " + receivingcontent);
            String[] contents = receivingcontent.split(" ");
            System.out.println(contents.length);
            for(String content: contents){
              System.out.println(content);
              System.out.flush();
            }
            
            if(contents.length == 3){
              if(contents[0].equals("NewSlave:")){
                String[] tempkey = new String[2];
                tempkey[0] = contents[1];
                tempkey[1] = contents[2];
                st.putslave(tempkey, 0);
              }
            }
          }
            // TODO: deal with the stream
          in.close();
          socket.close();
          System.out.println("Finish receiving");
      } catch (IOException e) {
          e.printStackTrace();
      }
   }
}