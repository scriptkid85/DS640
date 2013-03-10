package registry;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import ror.RORtbl;
import ror.RemoteObjectRef;

/**
 * RMI registry server (centrilized)
 * 
 * @author Zeyuan Li
 * */
public class RMIRegistry {
  
  private static int port = 4443;  // registry listen port
  private static ConcurrentHashMap<String, RemoteObjectRef> srvmap = new ConcurrentHashMap<String, RemoteObjectRef>();
  
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    if(args.length == 1)
      port = Integer.parseInt(args[0]);
    
    ServerSocket serverSoc = null;
    try {
      serverSoc = new ServerSocket(port);
      InetAddress addr = InetAddress.getLocalHost();
      String localhostname = addr.getCanonicalHostName();
      System.out.printf("Registry host name: %s\n", localhostname);
      
      while(true) {
        Socket clientSoc = serverSoc.accept();
        Thread t = new Thread(new RMIRegistryHdler(clientSoc, srvmap));
        t.start();
      }
      
      //serverSoc.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  
  }

}
