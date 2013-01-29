import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class CommSender implements Runnable{
  
  private static Socket ClientSocket;
  private String hostname;
  private int port;
  private PrintWriter out;
  private BufferedReader in;
  private String sendingcontent;
  
  public CommSender(String hostname, int port, String content){
    this.hostname = hostname;
    this.sendingcontent = content;
    this.port = port;
  }
  
  public void run(){
  
      ClientSocket = null;
      PrintWriter out = null;
  
      try {
          ClientSocket = new Socket(hostname, port);
          out = new PrintWriter(ClientSocket.getOutputStream(), true);
          in = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
      } catch (UnknownHostException e) {
          System.err.println("Don't know about host: master.");
          System.exit(1);
      } catch (IOException e) {
          System.err.println("Couldn't get I/O for the connection to: master.");
          System.exit(1);
      }
  

      if (sendingcontent != null) {
          System.out.println("Sending: " + sendingcontent);
          System.out.println(sendingcontent);
          out.println(sendingcontent);
          out.flush();
          
      }
  
      out.close();
      try {
        ClientSocket.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
  }
}
