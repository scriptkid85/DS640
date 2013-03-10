package cm;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.lang.reflect.Type;

import ror.RORtbl;
import ror.RemoteObjectRef;

public class CommServerListenThread extends Thread {
  private Socket socket;

  private RORtbl tbl;

  private InputStream is;

  private MyUtil myutil = new MyUtil("CommServerListenThread");

  public CommServerListenThread(Socket socket, RORtbl tbl) {
    this.socket = socket;
    this.tbl = tbl;
  }

  public void serverhandler(INVOMessage invomsg) {

    if (invomsg.gettype() != 1) {
      System.out.println("Error type in INVOMessage.");
      return;
    }
    
    myutil.printDebugInfo(invomsg.toString());
    
    RemoteObjectRef ror = invomsg.getror();
    String methodname = invomsg.getmethod();
    Object[] args = invomsg.getargs();
    String[] argtypes = invomsg.getargtype();
    String returntype = invomsg.getreturntype();
    int argnum;
    boolean returnvoid = false;
    boolean remoteexception = false;
    
    if (args != null) {
      argnum = args.length;
    } else
      argnum = 0;
    if (returntype == null) {
      returnvoid = true;
    }

    // (4) gets the real object reference from tbl.
    Object calledObj = tbl.findObj(ror);
    
    // Handle the case: callObj == null
    if(calledObj == null){
      System.out.println("cannot find the called object in the server.");
      INVOMessage errormsg = new INVOMessage();
      ObjectOutputStream out = null;
      try {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(errormsg);
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
        myutil.printDebugInfo("sending errormsg error.");
      }
      try {
        socket.close();
      } catch (IOException e) {
        myutil.printDebugInfo("close socket error");
        e.printStackTrace();
      }
      return;
    }
    
    // (5) Either:
    // -- using the interface name, asks the skeleton,
    // together with the object reference, to unmartial
    // and invoke the real object.
    // -- or do unmarshalling directly and involkes that
    // object directly.
    INVOMessage returnmsg = null;
    try {
      Method[] allMethods = calledObj.getClass().getDeclaredMethods();
      // Find the corresponding method
      // reason for not using getDeclaredMethod(name) is the type object hard to be reversed.
      for (Method m : allMethods) {
        String mname = m.getName();
        if (!mname.equals(methodname)) {
          continue;
        }
        if (!returnvoid) {
          if (!(m.getGenericReturnType().toString().equals(returntype))) {
            continue;
          }
        }

        Type[] pType = m.getGenericParameterTypes();
        if (pType.length != argnum) {
          continue;
        }

        boolean unmatch = false;
        for (int i = 0; i < argnum; i++) {
          if (!(pType[i].toString().equals(argtypes[i]))) {
            unmatch = true;
            break;
          }
        }

        if (unmatch)
          continue;

        myutil.printDebugInfo("invoking " + mname);

        try {
          m.setAccessible(true);
          // (6) receives the return value, which (if not marshalled
          Object o;
          if (!returnvoid) {
            if (argnum != 0)
              o = m.invoke(calledObj, args);
            else
              o = m.invoke(calledObj, args);
            returnmsg = new INVOMessage((Serializable) o);
          //handle the case that return void functions
          } else {
            if (argnum != 0)
              m.invoke(calledObj, args);
            else
              m.invoke(calledObj, args);
            return;
          }

          // Handle any exceptions thrown by method to be invoked.
        } catch (InvocationTargetException x) {
          System.out.println("invocation of " + mname + "failed.");
          remoteexception = true;
        }
      }
    } catch (IllegalAccessException x) {
      x.printStackTrace();
      System.out.println("IllegalAccessException");
      remoteexception = true;
    }

    // marshal the return value here and send it out to the
    // the source of the invoker.
    if (returnmsg == null && !remoteexception) {
      System.out.println("no return object from invoked method: " + methodname);
      
      //send a success result message with void return value
      returnmsg = new INVOMessage(null);
      return;
    }
    
    if(remoteexception){
      //send remoteexception message
      INVOMessage errormsg = new INVOMessage();
      ObjectOutputStream out = null;
      try {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(errormsg);
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
        myutil.printDebugInfo("sending errormsg error.");
      }
    }
    else{
      ObjectOutputStream out = null;
      try {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(returnmsg);
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
        myutil.printDebugInfo("sending return bytes error.");
      }
    }
    
    // (7) closes the socket.
    try {
      socket.close();
    } catch (IOException e) {
      myutil.printDebugInfo("close socket error");
      e.printStackTrace();
    }
  }

  public void run() {
    try {
      // (1) receives an invocation request.
      // (2) creates a socket and input/output streams.
      ObjectInputStream in = null;
      INVOMessage recvmsg = null;
      in = new ObjectInputStream(socket.getInputStream());

      recvmsg = (INVOMessage) in.readObject();

      if (recvmsg != null)
        serverhandler(recvmsg);

    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  };
}
