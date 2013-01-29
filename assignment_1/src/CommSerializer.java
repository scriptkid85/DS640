
import java.io.IOException;
import java.io.InputStream;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import java.net.Socket;
import java.net.UnknownHostException;


public class CommSerializer implements Runnable{
  
  private static Socket ClientSocket;
  InputStream is;
  OutputStream os;
  
  public CommSerializer(Socket socket){
    ClientSocket = socket;
    
    try {
      this.is = ClientSocket.getInputStream();
      this.os = ClientSocket.getOutputStream();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  
  public RunningProcessTable receive(){
    RunningProcessTable temprpt = new RunningProcessTable();
    try {
      ObjectInputStream ois = new ObjectInputStream(is);
      temprpt = (RunningProcessTable)ois.readObject();
      
      ois.close();
      System.out.println("Received serialized rpt");
    } catch (UnknownHostException e1) {
      System.out.println("Cannot receive the RPT");

      e1.printStackTrace();
    } catch (IOException e) {
      System.out.println("Cannot receive the RPT");
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      System.out.println("Cannot find the RPT class");
      e.printStackTrace();
    }
    
    try {
      
      os.close();
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return temprpt;
  }
  
  public void send(RunningProcessTable rpt){

    try {
      ObjectOutputStream oos = new ObjectOutputStream(os);
      oos.writeObject(rpt);
      oos.close();
      System.out.println("Sent serialized rpt");
    } catch (UnknownHostException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      System.out.println("Cannot send the RPT");
      e.printStackTrace();
    }
    
    
    try {
      os.close();

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void close(){
    try {
      ClientSocket.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  @Override
  public void run() {
    // TODO Auto-generated method stub
    
  }
}
