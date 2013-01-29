
public class ProcessBalancer implements Runnable{
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

  @Override
  public void run() {
    System.out.println("start Balancer");
    schecker.run();
    System.out.println("end slave checking");
    pbstrategist.balance();
  }
  
  
}
