
/**
 * ProcessBalancer: ProcessBalancer is used to do the slave checking (SlaveChecker) first and then 
 * to do the job balancing (ProcessBalancerStrategist) according to the updated slave information.
 * 
 * @author Guanyu Wang
 * */

public class ProcessBalancer implements Runnable{
  private boolean debug = false;
  private SlaveChecker schecker;
  private RunningProcessTable rpt;
  private SlaveTable st;
  private ProcessBalancerStrategist pbstrategist;
  
  public ProcessBalancer(SlaveTable st, RunningProcessTable rpt){
    this.rpt = rpt;
    this.st = st;
    schecker = new SlaveChecker(this.st);
    pbstrategist = new ProcessBalancerStrategist(this.st, rpt);
  }
  
  public void printDebugInfo(String s){
    if(debug)
      System.out.println("ProcessBalancer: " + s);
  }
  @Override
  public void run() {
    printDebugInfo("start Balancer");
    schecker.run();
    printDebugInfo("end slave checking");
    pbstrategist.balance();
  }
  
  
}
