import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * RunningProcessTable is a structed table with synchronized add and remove
 * method for updating the process list in ProcessManager
 * 
 * @author Guanyu Wang 
 * */
public class RunningProcessTable implements Serializable{  
  private Hashtable<MigratableProcess, String> process_table;
  
  public RunningProcessTable(){
    process_table = new Hashtable<MigratableProcess, String>();
  }
   
  public synchronized void putprocess(MigratableProcess t, String command){
    if(process_table.containsKey(t))
      process_table.remove(t);
    process_table.put(t, command);
  }
  
  public synchronized void removeprocess(MigratableProcess t){
    if(process_table.containsKey(t))
      process_table.remove(t);
  }
  
  public int size(){
    return process_table.size();
  }
  
  public Set<MigratableProcess> keySet(){ 
    return process_table.keySet();
  }
  
  public String get(MigratableProcess t){
    return process_table.get(t);
  }
  
  public synchronized void clear(){
    process_table.clear();
  }
  
  public boolean containsKey(MigratableProcess t){
    return process_table.containsKey(t);
  }
  
  public MigratableProcess getOne(){
    Set<MigratableProcess> s = process_table.keySet();
    Iterator<MigratableProcess> it = s.iterator();
    if(it.hasNext())
      return it.next();
    return null;
  }
}
