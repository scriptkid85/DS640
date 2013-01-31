import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * Serializer can do serialization and deserialization
 * 
 * @author Zeyuan Li
 * */
public class Serializer {
  private String pathPrefix = ""; // afs string
  
  public byte[] serializeBytes(MigratableProcess mp) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutput out = null;
    byte[] outbytes = null;
    
    System.out.println("Byte Serializer: start");
    
    // suspend process
    mp.suspend();
    
    try {
      out = new ObjectOutputStream(bos);   
      out.writeObject(mp);
      out.flush();
      outbytes = bos.toByteArray();
      
      out.close(); 
 
    } catch (FileNotFoundException e1) {
      System.err.println(e1);
      e1.printStackTrace();
    } catch (IOException e1) {
      System.err.println(e1);
      e1.printStackTrace();
    }
    return outbytes;
  }
  
  public MigratableProcess deserializeBytes(byte[] objbytes) {
    MigratableProcess mp = null;
    ByteArrayInputStream bis = new ByteArrayInputStream(objbytes);
    ObjectInput in = null;

    // if it resumes running, read object in
    if (objbytes != null) {
      try {
        in = new ObjectInputStream(bis);
        mp = (MigratableProcess) in.readObject(); 
        
      } catch (IOException e) {
        System.err.println("Deserialize IOException");
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        System.err.println("Deserialize ClassNotFoundException ");
        e.printStackTrace();
      }
    }
    else {
      System.out.println("Deserialize failed: obj byte array null");
    }
    if(mp == null)
      System.out.println("Deserialized result null");
    return mp;
  }

  // TODO: serialize serializable object
  public String serialize(MigratableProcess mp) {
    System.out.println("Serializer: start");
    String id = UUID.randomUUID().toString();
    
    File dir = new File(pathPrefix + "data/serialize/");
    if(!dir.exists())
      dir.mkdir();
    String objname = pathPrefix + "data/serialize/" + id + ".dat";
    // suspend process
    mp.suspend();
    
    try {
      ObjectOutput s = new ObjectOutputStream(new TransactionalFileOutputStream(objname));
      s.writeObject(mp);
      s.flush();
      s.close(); 
 
    } catch (FileNotFoundException e1) {
      System.err.println("Serialize file not found. id:" + id);
      e1.printStackTrace();
    } catch (IOException e1) {
      System.err.println("Serialize file io exception. id:" + id);
      System.err.println(e1);
      e1.printStackTrace();
    }
    return objname;
  }

  public MigratableProcess deserialize(String objname) {
    File objFile = new File(objname);
    MigratableProcess mp = null;

    // if it resumes running, read object in
    if (objFile.exists()) {
      try {
        ObjectInputStream in = new ObjectInputStream(new TransactionalFileInputStream(objname));
        mp = (MigratableProcess) in.readObject();
        
        // delete serialized file
        objFile.delete();
      } catch (IOException e) {
        System.err.println("Deserialize IOException " + objname);
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        System.err.println("Deserialize ClassNotFoundException " + objname);
        e.printStackTrace();
      }
    }
    else {
      System.out.println("Deserialize failed: no object file avaliable");
    }
    if(mp == null)System.out.println("Deserialized result null");
    return mp;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
