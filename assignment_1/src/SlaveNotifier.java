/**
 * SlaveNotifier: SlaveNotifier is the separated thread class for sending alive (heartbeat) message
 * to master every 5 seconds.
 * 
 * @author Guanyu Wang
 * */
public class SlaveNotifier implements Runnable{
  private boolean debug = false;

  private RunningProcessTable rpt;
  private String[] localhost;
  private String masterhostname;
  private int masterport;
  
  public SlaveNotifier(String masterhostname, int masterport, String[] localhost, RunningProcessTable rpt){
    this.rpt = rpt;
    this.localhost = localhost;
    this.masterport = masterport;
    this.masterhostname = masterhostname;
  }
  
  public void printDebugInfo(String s){
    if(debug)
      System.out.println("SlaveNotifier: " + s);
  }
  @Override
  public void run() {
    Serializer ser = new Serializer();
    byte[] instruction = new byte[1];
    instruction[0] = Byte.valueOf("0");
    
    Pair<String[], RunningProcessTable> slaveinfo = new Pair<String[], RunningProcessTable>(this.localhost, this.rpt);
    printDebugInfo(masterhostname + " " + masterport);
    byte[] content = ser.serializeObj(slaveinfo);
    ByteSender bsender = new ByteSender(masterhostname, masterport, instruction, content);
    bsender.run();
    bsender.close();
  }
  
}
