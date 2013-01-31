import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ByteSender {

  private static Socket ClientSocket;

  private String hostname;

  private int port;
  
  private Socket socket;

  private OutputStream os;

  private byte[] msg;

  private byte[] type;

  // hostname, port, type, info
  public ByteSender(String hostname, int port, byte[] msg, byte[] type) {
    this.hostname = hostname;
    this.msg = msg;
    this.type = type;
    this.port = port;
    this.socket = null;
  }

  public ByteSender(Socket socket, byte[] msg, byte type[], RunningProcessTable rpt) {
    this.socket = socket;
    this.msg = msg;
    this.type = type;
  }

  public void run() {
    ClientSocket = null;
    // PrintWriter out = null;
    DataOutputStream out = null;
    if(socket != null){
      ClientSocket = socket;
      try {
        os = ClientSocket.getOutputStream();
        out = new DataOutputStream(os);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    else{
      try {
        ClientSocket = new Socket(hostname, port);
        os = ClientSocket.getOutputStream();
        out = new DataOutputStream(os);
  
      } catch (UnknownHostException e) {
        System.err.println("ByteSender: Don't know about host: master.");
        System.exit(1);
      } catch (IOException e) {
        System.err.println("ByteSender: Couldn't get I/O for the connection to: master.");
        System.exit(1);
      }
    }
    if (msg != null && type != null) {
      System.out.println("ByteSender: Sending: ");
      byte[] sendingbarray = new byte[type.length + msg.length];
      System.arraycopy(type, 0, sendingbarray, 0, type.length);
      System.arraycopy(msg, 0, sendingbarray, type.length, msg.length);
      // write type
      try {

        for (byte b : sendingbarray)
          out.writeByte(b);

        out.flush();
      } catch (IOException e) {
        System.err.println(e);
        e.printStackTrace();
      }

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
