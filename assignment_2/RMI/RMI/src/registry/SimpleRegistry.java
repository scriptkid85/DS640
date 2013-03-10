package registry;

import java.rmi.RemoteException;
import java.util.*;
import java.net.*;
import java.io.*;

import cm.MyUtil;

import ror.RemoteObjectRef;

/**
 * Real registry class that provide loopup and rebind service
 * 
 * @author Zeyuan Li
 * */
public class SimpleRegistry {
  // registry holds its port and host, and connects to it each time.
  public String Host;

  public int Port;

  private MyUtil Util = new MyUtil("SimpleRegistry");
  
  
  // ultra simple constructor.
  public SimpleRegistry(String IPAdr, int PortNum) {
    Host = IPAdr;
    Port = PortNum;
  }

  // returns the ROR (if found) or null (if else)
  public RemoteObjectRef lookup(String serviceName) throws IOException, RemoteException {
    // open socket.
    // it assumes registry is already located by locate registry.
    // you should usually do try-catch here (and later).
    Socket soc = new Socket(Host, Port);

    System.out.println("socket made.");

    // get TCP streams and wrap them.
    BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
    PrintWriter out = new PrintWriter(soc.getOutputStream(), true);

    System.out.println("stream made.");

    // it is locate request, with a service name.
    out.println(Util.LOOKUP);
    out.println(serviceName);

    System.out.println("command and service name sent.");

    // branch according to the answer.
    String res = in.readLine();
    RemoteObjectRef ror;

    if (res.equals(Util.FOUND)) {

      System.out.println("it is found!.");

      // receive ROR data, witout check.
      String ro_IPAdr = in.readLine();
      int ro_PortNum = Integer.parseInt(in.readLine());
      int ro_ObjKey = Integer.parseInt(in.readLine());
      String ro_InterfaceName = in.readLine();

      // make ROR.
      ror = new RemoteObjectRef(ro_IPAdr, ro_PortNum, ro_ObjKey, ro_InterfaceName);
    
      Util.printDebugInfo(ror.toString());
    } else {
      throw new RemoteException("Service not found!.");
      //System.out.println("it is not found!.");

      //ror = null;
    }

    // close the socket.
    soc.close();

    // return ROR.
    return ror;
  }

  // rebind a ROR. ROR can be null. again no check, on this or whatever.
  // I hate this but have no time.
  public void rebind(String serviceName, RemoteObjectRef ror) throws IOException {
    if(ror == null) {
      throw new NullPointerException("ror null");
    }
    // open socket. same as before.
    Socket soc = new Socket(Host, Port);

    // get TCP streams and wrap them.
    BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
    PrintWriter out = new PrintWriter(soc.getOutputStream(), true);

    // it is a rebind request, with a service name and ROR.
    out.println("rebind");
    out.println(serviceName);
    out.println(ror.ip);
    out.println(ror.port);
    out.println(ror.objkey);
    out.println(ror.riname);

    // it also gets an ack, but this is not used.
    String ack = in.readLine();

    // close the socket.
    soc.close();
  }
}
