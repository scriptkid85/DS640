import java.io.*;
import java.lang.reflect.*;
import java.util.StringTokenizer;


/**
 * ProcessManager is a process management tool which support Migratable process and load
 * re-distribution.
 * 
 * @author Guanyu Wang
 * */
public class ProcessManager {

  private static RunningProcessTable process_table;
  
  public static void Ps() {
    if (process_table.size() < 1) {
      System.out.println("no running processes");
      System.out.flush();
      return;
    }
    for (Thread t : process_table.keySet()) {
      System.out.println(process_table.get(t));
      System.out.flush();
    }
    System.out.print("");
  }

  /*
  private static void updateProcessList() {
    RunningProcessTable tempProcesstable = new RunningProcessTable();
    for (Thread t : process_table.keySet()) {
      if (!t.isAlive()) {
        System.out.println("Process " + process_table.get(t) + " was terminated");
        System.out.flush();

      } else {
        tempProcesstable.putprocess(t, process_table.get(t));
      }
    }
    process_table.clear();
    process_table = tempProcesstable;
  }
  */
  
  /*
  public static void RunprocessinThread(String command) throws ClassNotFoundException, Exception {
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
    } catch (ClassNotFoundException e) {
      System.out.println("Cannot find the input command.");
    } catch (InvocationTargetException e) {
      System.out.println("Invalid arguments for input command.");
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
*/

  public static void Runprocess(String command) throws ClassNotFoundException, Exception {
    
    try{
      ProcessRunner pr = new ProcessRunner(command, process_table);
      Thread t = new Thread(pr);
      t.start();
    }catch (InvocationTargetException e) {
      System.out.println("");
      System.out.println("Invalid arguments for input command.");
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }

  private static void Parser(String command) throws Exception {
    StringTokenizer stoken;
    String cmd;
    stoken = new StringTokenizer(command);
    if (stoken.hasMoreTokens()) {
      cmd = stoken.nextToken();
      if (cmd.equals("ps") && !stoken.hasMoreElements()) {
        Ps();
      } else if (cmd.equals("quit") && !stoken.hasMoreElements()) {
        System.exit(0);
      } else {
        Runprocess(command);
      }

    } else {
      System.out.println("");
      System.out.println("Please input command.");
      return;
    }
  }

  public static void main(String args[]) throws Exception {

    String commandline;
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    process_table = new RunningProcessTable();

    
    /*
    ScheduledExecutorService schExec = Executors.newScheduledThreadPool(8);
    ScheduledFuture<?> schFuture = schExec.scheduleWithFixedDelay(new Runnable() {
      @Override
      public void run() {
        updateProcessList();
        return;
      }
    }, 0, 5, TimeUnit.SECONDS);
    */
    
    while (true) {
      System.out.print("==> ");
      commandline = in.readLine();
      Parser(commandline);
    }
  }
}
