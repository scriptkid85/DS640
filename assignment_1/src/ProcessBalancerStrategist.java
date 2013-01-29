
public class ProcessBalancerStrategist {
  private RunningProcessTable rpt;
  private SlaveTable st;
  
  public ProcessBalancerStrategist(SlaveTable st, RunningProcessTable rpt){
    this.rpt = rpt;
    this.st = st;
  }
  
  public void balance(){
    
    System.out.println("Start balancing");
    
    if(st.size() == 0){
      System.out.println("Empty");
      return;
    }
    SlaveTable temptable = st.clone();
    int sumofprocess = 0;
    int tempnum, maxnum = -1, minnum = Integer.MAX_VALUE;
    String[] maxslave = null, minslave = null;
    for(String[] slavehost: temptable.keySet()){
      tempnum = temptable.get(slavehost);
      if(tempnum > maxnum){
        maxnum = tempnum;
        maxslave = slavehost;
      }
      if(tempnum < minnum){
        minnum = tempnum;
        minslave = slavehost;
      }
      sumofprocess += tempnum;
    }
    int averagenum = sumofprocess / temptable.size();
    int offset = Math.max(0, Math.min(maxnum - averagenum, averagenum - minnum));

//  int balancednum = offset / 10;
//  just for test
    int balancednum = offset;
    
    System.out.println("finish offsetting: " + balancednum);
    
    CommMoveProcess  cmp;
    if(balancednum > 0){
      cmp = new CommMoveProcess(maxslave[0], Integer.parseInt(maxslave[1]), minslave[0], Integer.parseInt(minslave[1]), balancednum);
      cmp.run();
      // New thread or not???
    }
  }
}
