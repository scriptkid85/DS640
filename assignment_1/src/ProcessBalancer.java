
public class ProcessBalancer implements Runnable{
  private SlaveChecker schecker;
  private RunningProcessTable rpt;
  private SlaveTable st;
  
  public ProcessBalancer(SlaveTable st, RunningProcessTable rpt){
    this.rpt = rpt;
    this.st = st;
    schecker = new SlaveChecker(this.st);
  }

  @Override
  public void run() {
    schecker.run();
    
  }
  
  
}
