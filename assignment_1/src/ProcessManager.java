import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ProcessManager {

  private static ArrayList<String> running_process;

  public static void Ps() {
    if (running_process.size() < 1) {
      System.out.println("no running processes");
      return;
    }
    for (String process : running_process) {
      System.out.println(process);
    }
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
      running_process.add(cmd + " " + arguments);
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
    running_process = new ArrayList<String>();
    
    while (true) {
      System.out.println("==>");
      commandline = in.readLine();
      Parser(commandline);
    }
  }
}
