import java.io.IOException;
import java.net.ServerSocket;

/* Protocol: type definition
 * 0: slave -> master, notify new slave or update rpt
 * 1: slave -> master, ask for ps
 * 2: master-> slave, return ps
 * 3: master-> slave, ask to move process
 * 4: slave -> slave, move process to another slave
 * 
 */

public class CommListener implements Runnable {

  private static boolean listening;
  private RunningProcessTable rpt;
  private SlaveTable st;
  private String mode;
  private int port; 
  
  public CommListener(String mode, int port, RunningProcessTable rpt, SlaveTable st) throws Exception{
    if(mode.equals("Master") || mode.equals("Slave")){
      listening = true;
      this.port = port;
      this.rpt = rpt;
      this.st = st;
      this.mode = mode;
    }
    else{
      System.out.println("Invalid model arg for CommListener");
      throw new Exception(" Invalid Arguments");
    }
  }
  
  public void run(){
    ServerSocket serverSocket = null;
    listening = true;

    try {
        serverSocket = new ServerSocket(port);
        System.out.println("Start listening...");
    } catch (IOException e) {
        System.err.println("Could not listen on port: " + port);
        System.exit(-1);
    }

    while (listening){
      try {
        if(mode == "Master"){
          new CommMasterListenThread(serverSocket.accept(), st).start();
        }
        else new CommSlaveListenThread(serverSocket.accept(), rpt, st).start();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    try {
      serverSocket.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
}