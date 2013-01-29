import java.io.Serializable;
import java.util.Hashtable;
import java.util.Set;

/**
 * RunningProcessTable is a structed table with synchronized add and remove
 * method for updating the process list in ProcessManager
 * 
 * @author Guanyu Wang
 * */
public class RunningProcessTable implements Serializable{
  private Hashtable<Thread, String> process_table;
  
  public RunningProcessTable(){
    process_table = new Hashtable<Thread, String>();
  }
  
  public synchronized void putprocess(Thread t, String command){
    if(process_table.containsKey(t))
      process_table.remove(t);
    process_table.put(t, command);
  }
  
  public synchronized void removeprocess(Thread t){
    if(process_table.containsKey(t))
      process_table.remove(t);
  }
  
  public int size(){
    return process_table.size();
  }
  
  public Set<Thread> keySet(){
    return process_table.keySet();
  }
  
  public String get(Thread t){
    return process_table.get(t);
  }
  
  public synchronized void clear(){
    process_table.clear();
  }
  
  public boolean containsKey(Thread t){
    return process_table.containsKey(t);
  }
}
