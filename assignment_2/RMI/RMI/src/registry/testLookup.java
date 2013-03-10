package registry;

import java.io.*;

import ror.RemoteObjectRef;

// we test simple registry by looking up a service.

public class testLookup {

  public static void main(String args[]) throws IOException {
    // it takes three arguments.
    // these are it wishes to connect to.
    String host = args[0];
    int port = Integer.parseInt(args[1]);

    // these is the service name.
    String ServiceName = args[2];

    System.out.println("We lookup " + ServiceName);

    // locate.
    SimpleRegistry sr = LocateSimpleRegistry.getRegistry(host, port);

    System.out.println("located." + sr + "/n");

    if (sr != null) {
      // lookup.
      RemoteObjectRef ror = sr.lookup(ServiceName);

      if (ror != null) {
        System.out.println(ror.toString());
      } else {
        System.out.println("The service is bound to no remote object.");
      }
    } else {
      System.out.println("no registry found.");
    }

  }
}
