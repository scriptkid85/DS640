package cm;

import java.io.IOException;
import java.net.ServerSocket;
import ror.RORtbl;


public class CommListener{

  private static boolean listening;
  private RORtbl tbl;
  private int port; 
  
  public CommListener(int port, RORtbl tbl) throws Exception{
    listening = true;
    this.port = port;
    this.tbl = tbl;
  }
  
  public void start(){
    ServerSocket serverSocket = null;
    listening = true;

    try {
        serverSocket = new ServerSocket(port);
        System.out.println("Start listening...");
    } catch (IOException e) {
        System.err.println("Could not listen on port: " + port);
        System.exit(-1);
    }

    while (listening){
      try {
        new CommServerListenThread(serverSocket.accept(), tbl).start();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    try {
      serverSocket.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
}