import java.io.IOException;
import java.net.ServerSocket;



public class MasterManager {
  ServerSocket serverSocket;
  static boolean listening;
  
  public MasterManager(){
    serverSocket = null;
    listening = true;
  }
  
  public static void start(int port) throws IOException {
    ServerSocket serverSocket = null;
    listening = true;

    try {
        serverSocket = new ServerSocket(port);
    } catch (IOException e) {
        System.err.println("Could not listen on port: " + port);
        System.exit(-1);
    }

    while (listening)
       new CommThread(serverSocket.accept()).start();

    serverSocket.close();
}
  
}