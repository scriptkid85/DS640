import java.io.Serializable;
import java.util.Hashtable;
import java.util.Set;


public class SlaveTable implements Serializable{
  //String[0] is the slave hostname, String[1] is the port
  //and integer is the process running on corresponding slave.
  public Hashtable<String, RunningProcessTable> slave_table;
  public Hashtable<String, Integer>slave_lifetime;
  
  public SlaveTable(){
    slave_table = new Hashtable<String, RunningProcessTable>();
    slave_lifetime = new Hashtable<String, Integer>();
  }

  
  public RunningProcessTable get(String[] slavehost){
    return slave_table.get(slavehost[0] + " " + slavehost[1]);
  }
  
  public int getlifetime(String[] slavehost){
    return slave_lifetime.get(slavehost[0] + " " + slavehost[1]);
  }
  
  public synchronized void putslave(String[] slavehost, RunningProcessTable rpt){
    slave_table.put(slavehost[0] + " " + slavehost[1], rpt);
    slave_lifetime.put(slavehost[0] + " " + slavehost[1], 0);
  }
  
  
  public synchronized void putslavewithlifetime(String[] slavehost, RunningProcessTable rpt, int lifetime){
    if(slave_table.containsKey(slavehost[0] + " " + slavehost[1]))
      slave_table.remove(slavehost[0] + " " + slavehost[1]);
    slave_table.put(slavehost[0] + " " + slavehost[1], rpt);
    if(slave_lifetime.containsKey(slavehost[0] + " " + slavehost[1]))
      slave_lifetime.remove(slavehost[0] + " " + slavehost[1]);
    slave_lifetime.put(slavehost[0] + " " + slavehost[1], lifetime);
  }
  
  public synchronized void removeslave(String[] slavehost){
    if(slave_table.containsKey(slavehost[0] + " " + slavehost[1]))
      slave_table.remove(slavehost[0] + " " + slavehost[1]);
    if(slave_lifetime.containsKey(slavehost[0] + " " + slavehost[1]))
      slave_lifetime.remove(slavehost[0] + " " + slavehost[1]);
  }
  
  public synchronized void updateslavetime(String[] slavehost){
    if(slave_lifetime.containsKey(slavehost[0] + " " + slavehost[1]))
      this.slave_lifetime.put(slavehost[0] + " " + slavehost[1], slave_lifetime.get(slavehost[0] + " " + slavehost[1]) + 1);    
    else slave_lifetime.put(slavehost[0] + " " + slavehost[1], 0); 
  }
  
  public synchronized void resetlifetime_updaterpt(String[] slavehost, RunningProcessTable rpt){
    this.slave_table.put(slavehost[0] + " " + slavehost[1], rpt);
    this.slave_lifetime.put(slavehost[0] + " " + slavehost[1], 0);    
  }
  
  public int size(){
    return slave_table.size();
  }
  
  public Set<String> keySet(){
    return slave_table.keySet();
  }
  

  
  public synchronized void clear(){
    slave_table.clear();
    slave_lifetime.clear();
  }
  
  public boolean containsKey(String[] slavehost){
    return slave_table.containsKey(slavehost[0] + " " + slavehost[1]);
  }
  
  public SlaveTable clone(){
    SlaveTable newst = new SlaveTable();
    for(String key: this.keySet()){
      newst.putslavewithlifetime(key.split(" "), this.get(key.split(" ")), this.getlifetime(key.split(" ")));
    }
    return newst;
  }
}
