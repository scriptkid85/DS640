import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
    private InetAddress Address;
    private BufferedReader in;
    

    public CommMasterListenThread(Socket socket, SlaveTable st) {
      this.socket = socket;
      this.st = st;
      this.rpt = rpt;
    }

    public void run() {
      System.out.println("CommMasterListen: Master receving..");
      try{
          InputStream is = socket.getInputStream();
          in = new BufferedReader(
                new InputStreamReader(
                is));
          //read one line from socket
          receivingcontent = in.readLine();
          
          System.out.println("CommMasterListen: received: " + receivingcontent);
          if(receivingcontent != null){
            System.out.println("CommMasterListen: enter saving step: " + receivingcontent);
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
          is.close();
          socket.close();
          System.out.println("CommMasterListen: Finish receiving");
      } catch (IOException e) {
          e.printStackTrace();
      }
   }
}