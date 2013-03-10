package registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import ror.RemoteObjectRef;

import cm.MyUtil;

/**
 * Separate RMI registry request handler class. Handle request in a new class
 * 
 * @author Zeyuan Li
 * */
public class RMIRegistryHdler implements Runnable {

  private Socket cltsoc = null;
  private ConcurrentHashMap<String, RemoteObjectRef> srvmap = null;
  private MyUtil Util = new MyUtil("RMIRegistryHdler");
  
  
  public RMIRegistryHdler(Socket soc, ConcurrentHashMap<String, RemoteObjectRef> srvmap) {
    cltsoc = soc;
    this.srvmap = srvmap;
  }

  @Override
  public void run() {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(cltsoc.getInputStream()));
      PrintWriter out = new PrintWriter(cltsoc.getOutputStream(), true);
      
      // cmd: LOOKUP, REBIND, WHO
      String cmd = in.readLine();
      if(cmd.equals(Util.WHO)) {
        out.println(Util.IAMREG);
      }
      else if(cmd.equals(Util.LOOKUP)) {
        String srvname = in.readLine();
        if(srvmap.containsKey(srvname)) {
          out.println(Util.FOUND);
          
          // output ror
          RemoteObjectRef ror = srvmap.get(srvname);
          out.println(ror.ip);
          out.println(ror.port);
          out.println(ror.objkey);
          out.println(ror.riname);
          
          Util.printDebugInfo("sent ror info after look up");
        }
        else {
          out.println(Util.NOTFOUND);
          Util.printDebugInfo(Util.NOTFOUND);
        }
      }
      else if(cmd.equals(Util.REBIND)) {
        RemoteObjectRef ror = new RemoteObjectRef();
        String srvname = in.readLine();
        ror.ip = in.readLine();
        ror.port = Integer.parseInt(in.readLine());
        ror.objkey = Integer.parseInt(in.readLine());
        ror.riname = in.readLine();
        
        srvmap.put(srvname, ror);
        // ACK
        out.println(Util.ACK);
        
        Util.printDebugInfo("rebind: " + ror.toString());
      }
      else {
        System.err.println("Unrecognised input");
      }
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
