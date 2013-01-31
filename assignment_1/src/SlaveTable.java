import java.io.Serializable;
import java.util.Hashtable;
import java.util.Set;


public class SlaveTable implements Serializable{
  //String[0] is the slave hostname, String[1] is the port
  //and integer is the process running on corresponding slave.
  public Hashtable<String[], RunningProcessTable> slave_table;
  
  public SlaveTable(){
    slave_table = new Hashtable<String[], RunningProcessTable>();
  }
  
  public synchronized void putslave(String[] slavehost, RunningProcessTable rpt){
    if(slave_table.containsKey(slavehost))
      slave_table.remove(slavehost);
    slave_table.put(slavehost, rpt);
  }
  
  public synchronized void removeslave(String[] slavehost){
    if(slave_table.containsKey(slavehost))
      slave_table.remove(slavehost);
  }
  
  public int size(){
    return slave_table.size();
  }
  
  public Set<String[]> keySet(){
    return slave_table.keySet();
  }
  
  public RunningProcessTable get(String[] slavehost){
    return slave_table.get(slavehost);
  }
  
  public synchronized void clear(){
    slave_table.clear();
  }
  
  public boolean containsKey(String slavehost){
    return slave_table.containsKey(slavehost);
  }
  
  public SlaveTable clone(){
    SlaveTable newst = new SlaveTable();
    for(String[] key: this.keySet()){
      newst.putslave(key, this.get(key));
    }
    return newst;
  }
}
