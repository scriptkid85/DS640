import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class SlaveMaster {
  
  private static Socket ClientSocket;
  PrintWriter out;
  BufferedReader in;
  
  public static void SlaveMaster(){
    
  }
  
  public static void start(int port) throws IOException {
  
      ClientSocket = null;
      PrintWriter out = null;
      BufferedReader in = null;
  
      try {
          ClientSocket = new Socket("master", port);
          out = new PrintWriter(ClientSocket.getOutputStream(), true);
          in = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));
      } catch (UnknownHostException e) {
          System.err.println("Don't know about host: master.");
          System.exit(1);
      } catch (IOException e) {
          System.err.println("Couldn't get I/O for the connection to: master.");
          System.exit(1);
      }
  
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
      String fromServer;
      String fromUser;
  
      while ((fromServer = in.readLine()) != null) {
          System.out.println("Server: " + fromServer);
          if (fromServer.equals("Bye."))
              break;
      
          fromUser = stdIn.readLine();
          if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
          }
      }
  
      out.close();
      in.close();
      stdIn.close();
      ClientSocket.close();
  }
}
