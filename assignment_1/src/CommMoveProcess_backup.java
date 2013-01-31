import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


//public class CommMoveProcess implements Runnable{
public class CommMoveProcess_backup implements MigratableProcess{
  
  private static Socket ClientSocket;
  private String hostname;
  private int port;
  private String destname;
  private int destport;
  private int movenum;
  private volatile boolean suspending;
  
  public CommMoveProcess_backup(String slavename, int slaveport, String destname, int destport, int movenum){
      this.hostname = slavename;
      this.port = slaveport;
      this.destname = destname;
      this.destport = destport;
      this.movenum = movenum;
    }
  
  public void run(){
    ClientSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;
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

    String sendingcontent = new String("Move: " + destname + " " + destport + " " + movenum);
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

  @Override
  public void suspend() {
    // TODO Auto-generated method stub
    suspending = true;
    while (suspending)
      ;
  }
  
}
