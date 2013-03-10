package ror;

import java.io.Serializable;

/**
 * RemoteObjectReference class to indentify process across JVM
 * 
 * @author Zeyuan Li
 * */
public class RemoteObjectRef implements Remote440 {
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((ip == null) ? 0 : ip.hashCode());
    result = prime * result + objkey;
    result = prime * result + port;
    result = prime * result + ((riname == null) ? 0 : riname.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RemoteObjectRef other = (RemoteObjectRef) obj;
    if (ip == null) {
      if (other.ip != null)
        return false;
    } else if (!ip.equals(other.ip))
      return false;
    if (objkey != other.objkey)
      return false;
    if (port != other.port)
      return false;
    if (riname == null) {
      if (other.riname != null)
        return false;
    } else if (!riname.equals(other.riname))
      return false;
    return true;
  }

  // default value
  public String ip = "localhost";

  public int port = 4444;

  public int objkey = 0;

  public String riname = "Remote440"; // remote interface name

  public RemoteObjectRef() {
  }
  
  public RemoteObjectRef(String ip, int port, int obj_key, String riname) {
    this.ip = ip;
    this.port = port;
    this.objkey = obj_key;
    this.riname = riname;
  }

  // this method is important, since it is a stub creator.
  //
  public Object localise() throws ClassNotFoundException, InstantiationException,
          IllegalAccessException {
    // Implement this as you like: essentially you should
    // create a new stub object and returns it.
    // Assume the stub class has the name e.g.
    //
    // Remote_Interface_Name + "_stub".
    //
    // Then you can create a new stub as follows:
    //
    // Class c = Class.forName(Remote_Interface_Name + "_stub");
    // Object o = c.newinstance()
    //
    // For this to work, your stub should have a constructor without arguments.
    // You know what it does when it is called: it gives communication module
    // all what it got (use CM's static methods), including its method name,
    // arguments etc., in a marshalled form, and CM (yourRMI) sends it out to
    // another place.
    // Here let it return null.
    Class c = Class.forName(riname + "_stub");
    Object o = c.newInstance();
    
    return o;
  }
  
  @Override
  public String toString() {
    return String.format("ip:%s\t port:%d \tobjkey:%d \t riname:%s\n", ip, port, objkey, riname);
  }
}
