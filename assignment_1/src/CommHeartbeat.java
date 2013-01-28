import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommHeartbeat extends Thread {
    private Socket socket = null;
    private String receivingcontent;

    public CommHeartbeat(Socket socket, String content) {
      this.socket = socket;
      this.receivingcontent = content;
    }

    public void run() {

      try{
          PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
          BufferedReader in = new BufferedReader(
                new InputStreamReader(
                socket.getInputStream()));
    
          String inputLine, outputLine;
    
    
          while ((inputLine = in.readLine()) != null) {
             
            // TODO: deal with the stream
            outputLine = inputLine;
            out.println(outputLine);
            if (outputLine.equals("Bye"))
            break;
          }
          out.close();
          in.close();
          socket.close();
    
      } catch (IOException e) {
          e.printStackTrace();
      }
   }
}