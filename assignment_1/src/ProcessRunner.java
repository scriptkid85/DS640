import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;

/**
 * ProcessRunner is a separated thread for running command process
 * 
 * @author Guanyu Wang
 * */
public class ProcessRunner extends Thread{
  
  private String command;
  private RunningProcessTable process_table;
  
  public ProcessRunner(String commandline, RunningProcessTable process_table) throws Exception {
    this.command = commandline;
    this.process_table = process_table;
  }
  
  public void run() {
    StringTokenizer stoken = new StringTokenizer(command);
    String cmd = stoken.nextToken();
    String arguments = new String();
    while (stoken.hasMoreTokens())
      arguments += (stoken.nextToken() + " ");

    try {
      Class<?> ProcessClass = Class.forName(cmd);
      Constructor[] ProcessCtor = ProcessClass.getConstructors();
      Object Cmdprocess = ProcessCtor[0].newInstance((Object) command.split(" "));
      Thread t = new Thread((Runnable) Cmdprocess);
      if (process_table.containsKey(t))
        process_table.removeprocess(t);
      process_table.putprocess(t, cmd + " " + arguments);
      t.start();
      t.join();
      if(process_table.containsKey(t)){
        System.out.println("");
        System.out.println("Process " + process_table.get(t) + "was terminated");
        System.out.print("==> ");
        process_table.removeprocess(t);
      }
      else{
        
        // TODO: suspend case, no need to output termination info.
        
      }
    } catch (ClassNotFoundException e) {
      System.out.println("");
      System.out.println("Cannot find the input command.");
    } catch (InvocationTargetException e) {
      System.out.println("");
      System.out.println("Invalid arguments for input command.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
