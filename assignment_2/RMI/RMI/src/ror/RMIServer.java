package ror;
/* This does not offer the code of the whole communication module 
   (CM) for RMI: but it gives some hints about how you can make 
   it. I call it simply "yourRMI". 

   For example, it  shows how you can get the host name etc.,
   (well you can hardwire it if you like, I should say),
   or how you can make a class out of classname as a string.

   This just shows one design option. Other options are
   possible. We assume there is a unique skeleton for each
   remote object class (not object) which is called by CM 
   by static methods for unmarshalling etc. We can do without
   it, in which case CM does marshalling/unmarhshalling.
   Which is simpler, I cannot say, since both have their
   own simpleness and complexity.
 */

import java.net.*;

import registry.LocateSimpleRegistry;
import registry.SimpleRegistry;

import cm.CommListener;

public class RMIServer {
  static String host = "localhost";

  static int port = 4445;

  // It will use a hash table, which contains ROR together with
  // reference to the remote object.
  // As you can see, the exception handling is not done at all.
  public static void main(String args[]) throws Exception {
    if(args.length != 5) {
      System.out.format("Usage: RMIServer <InitialClassName> <registryHost> <registryPort> <serviceName> <localListenPort>");
    }
    
    String InitialClassName = args[0];
    String registryHost = args[1];
    int registryPort = Integer.parseInt(args[2]);
    String serviceName = args[3];
    
    // it should have its own port. assume you hardwire it.
    host = (InetAddress.getLocalHost()).getHostName();
    port = Integer.parseInt(args[4]);

    // it now have two classes from MainClassName:
    // (1) the class itself (say ZipCpdeServerImpl) and
    // (2) its skeleton.
    Class initialclass = Class.forName(InitialClassName);
    //Class initialskeleton = Class.forName(InitialClassName + "_skel");

    // you should also create a remote object table here.
    // it is a table of a ROR and a skeleton.
    // as a hint, I give such a table's interface as RORtbl.java.
    RORtbl tbl = new RORtbl(registryHost, registryPort, serviceName);

    // after that, you create one remote object of initialclass.
    Object o = initialclass.newInstance();

    // then register it into the table.
    tbl.addObj(host, port, o);

    CommListener serverlisten = new CommListener(port, tbl);
    
    serverlisten.start();
   
  }
}
