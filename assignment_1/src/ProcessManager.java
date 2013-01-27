import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ProcessManager {

  private static Hashtable<Thread, String> process_table;

  public static void Ps() {
    if (process_table.size() < 1) {
      System.out.println("no running processes");
      System.out.flush();
      return;
    }
    for (Thread t: process_table.keySet()) {
      System.out.println(process_table.get(t));
      System.out.flush();
    }
  }

  private static void updateProcessList(){
    Hashtable<Thread, String> tempProcesstable = new Hashtable<Thread, String>();
    for(Thread t: process_table.keySet()){
      if(!t.isAlive()){
        System.out.println("Process " + process_table.get(t) +  " was terminated");
        System.out.flush();
               
      }
      else{
        tempProcesstable.put(t, process_table.get(t));
      }
    }
    process_table.clear();
    process_table = tempProcesstable;
  }
  
  public static void Runprocess(String command) throws ClassNotFoundException, Exception {
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
      if(process_table.containsKey(t))
        process_table.remove(t);
      process_table.put(t, cmd + " " + arguments);
      t.start();
    } catch (ClassNotFoundException e) {
      System.out.println("Cannot find the input command.");
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
      System.out.println("Please input command.");
      return;
    }
  }

  public static void main(String args[]) throws Exception {

    String commandline;
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    process_table = new Hashtable<Thread, String>();
    
    ScheduledExecutorService schExec = Executors.newScheduledThreadPool(8);
    ScheduledFuture<?> schFuture = schExec.scheduleWithFixedDelay(new Runnable() {
      @Override
      public void run() {
        updateProcessList();
        return;
      }
    }, 0, 5, TimeUnit.SECONDS);
    
    while (true) {
      System.out.println("==>");
      commandline = in.readLine();
      Parser(commandline);
    }
  }
}
