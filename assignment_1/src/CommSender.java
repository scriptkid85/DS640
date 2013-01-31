import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class CommSender implements Runnable{
  
  private static Socket ClientSocket;
  private String hostname;
  private int port;
  private PrintWriter out;
  private String sendingcontent;
  private RunningProcessTable rpt;
  private OutputStream os;
  
  // hostname, port, type, info
  public CommSender(String hostname, int port, String content){
    this.hostname = hostname;
    this.sendingcontent = content;
    this.port = port;
    this.rpt = null;
  }
  
  public CommSender(String hostname, int port, String content, RunningProcessTable rpt){
    this.hostname = hostname;
    this.sendingcontent = content;
    this.port = port;
    this.rpt = rpt;
  }
  
  public void run(){
  
      ClientSocket = null;
      PrintWriter out = null;
  
      try {
          ClientSocket = new Socket(hostname, port);
          os = ClientSocket.getOutputStream();

          out = new PrintWriter(ClientSocket.getOutputStream(), true);

      } catch (UnknownHostException e) {
          System.err.println("CommSender: Don't know about host: master.");
          System.exit(1);
      } catch (IOException e) {
          System.err.println("CommSender: Couldn't get I/O for the connection to: master.");
          System.exit(1);
      }
  

      if (sendingcontent != null) {
          System.out.println("CommSender: Sending: " + sendingcontent);
          System.out.println(sendingcontent);
          out.println(sendingcontent);
          out.flush();
          
      }

      try {
        out.close();
        os.close();
        ClientSocket.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
  }
}
