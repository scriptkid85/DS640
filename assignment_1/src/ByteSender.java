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

  private PrintWriter out;

  private RunningProcessTable rpt;

  private OutputStream os;

  private byte[] msg;

  private byte type;

  // hostname, port, type, info
  public ByteSender(String hostname, int port, byte[] msg, byte type) {
    this.hostname = hostname;
    this.msg = msg;
    this.type = type;
    this.port = port;
    this.rpt = null;
  }

  public ByteSender(String hostname, int port, byte[] msg, byte type, RunningProcessTable rpt) {
    this.hostname = hostname;
    this.msg = msg;
    this.type = type;
    this.port = port;
    this.rpt = rpt;
  }

  public void run() {
    ClientSocket = null;
    // PrintWriter out = null;
    DataOutputStream out = null;

    try {
      ClientSocket = new Socket(hostname, port);
      os = ClientSocket.getOutputStream();

      out = new DataOutputStream(ClientSocket.getOutputStream());

    } catch (UnknownHostException e) {
      System.err.println("CommSender: Don't know about host: master.");
      System.exit(1);
    } catch (IOException e) {
      System.err.println("CommSender: Couldn't get I/O for the connection to: master.");
      System.exit(1);
    }

    if (msg != null) {
      System.out.println("CommSender: Sending: ");
      // write type
      try {
        out.writeByte(type);

        // write msg
        for (byte b : msg)
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
