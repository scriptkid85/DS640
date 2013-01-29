
import java.io.IOException;
import java.io.InputStream;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import java.net.Socket;
import java.net.UnknownHostException;


public class CommSerializer_test implements Runnable{
  
  InputStream is;
  OutputStream os;
  
  public CommSerializer_test(){
  }


  public RunningProcessTable receive(InputStream is){
    RunningProcessTable temprpt = new RunningProcessTable();
    try {
      ObjectInputStream ois = new ObjectInputStream(is);
      temprpt = (RunningProcessTable)ois.readObject();
      
      ois.close();
      ois.close();
      ois.close();
      ois.close();
      System.out.println("CommSerializer: Received serialized rpt");
    } catch (UnknownHostException e1) {
      System.out.println("CommSerializer: Cannot receive the RPT");
      e1.printStackTrace();
    } catch (IOException e) {
      System.out.println("CommSerializer: Cannot receive the RPT");
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      System.out.println("CommSerializer: Cannot find the RPT class");
      e.printStackTrace();
    }
    return temprpt;
  }
  
  public void send(OutputStream os, RunningProcessTable rpt){

    try {  
      ObjectOutputStream oos = new ObjectOutputStream(os);
      oos.writeObject(rpt);
      oos.flush();
      oos.close();
      oos.close();
      oos.close();
      oos.close();
     
      System.out.println("CommSerializer: Sent serialized rpt");
    } catch (UnknownHostException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      System.out.println("CommSerializer: Cannot send the RPT");
      e.printStackTrace();
    }
    
  }

  @Override
  public void run() {
    // TODO Auto-generated method stub
    
  }
}
