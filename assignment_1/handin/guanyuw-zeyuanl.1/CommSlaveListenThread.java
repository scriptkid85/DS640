import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Arrays;

/**
 * CommSlaveListenThread: MasterListenThread class, which handles the incoming communication
 * from master to check the alive status, asking for ps operation and send local process table
 * to master machine.
 * 
 * Protocol: type definition
 * 0: slave -> master, notify new slave or update rpt
 * 1: slave -> master, ask for ps
 * 2: master-> slave, return ps
 * 3: master-> slave, ask to move process
 * 4: slave -> slave, move process to another slave
 * 
 * @author Guanyu Wang
 * */

public class CommSlaveListenThread extends Thread {
  private boolean debug = false;
  private Socket socket = null;
  private RunningProcessTable rpt;
  private SlaveTable st;
  private Serializer ser;
  private InputStream is;

  public CommSlaveListenThread(Socket socket, RunningProcessTable rpt, SlaveTable st) {
    this.socket = socket;
    this.st = st;
    this.rpt = rpt;

  }

  public void printDebugInfo(String s) {
    if (debug)
      System.out.println("CommSlaveListenThread: " + s);
  }

  public void slavehandler(byte[] bytearray) {
    printDebugInfo("SlaveHandler start");

    byte[] command = Arrays.copyOfRange(bytearray, 0, 1);
    byte[] content = Arrays.copyOfRange(bytearray, 1, bytearray.length);
    printDebugInfo("content size: " + content.length);

    if (command[0] == Byte.valueOf("2")) { // receive ps and print
      System.out.println("global processes:");
      ser = new Serializer();
      SlaveTable remotest = (SlaveTable) ser.deserializeObj(content);
      for (String slavehost : remotest.keySet()) {
        System.out.println("Slave name and port number: " + slavehost);
        RunningProcessTable temprpt = remotest.get(slavehost.split(" "));
        if (temprpt.size() < 1) {
          System.out.println("no running processes");
        } else {
          for (MigratableProcess mp : temprpt.keySet()) {
            System.out.println(temprpt.get(mp));
          }
        }
      }
    } else if (command[0] == Byte.valueOf("3")) { // means move instruction
      String receivingcontent = new String(content);
      printDebugInfo("received***: " + receivingcontent);
      ser = new Serializer();
      String dest[] = receivingcontent.split(" ");
      String destname = dest[1];
      int destport = Integer.parseInt(dest[2]);
      int movenum = Integer.parseInt(dest[3]);
      printDebugInfo("destname: " + destname + "port: " + destport + "movenum: " + movenum);
      while (rpt.size() > 0 && movenum > 0) {

        byte[] instruction = new byte[1];
        instruction[0] = Byte.valueOf("4");
        MigratableProcess mp = rpt.getOne();

        String args = rpt.get(mp);
        Pair<MigratableProcess, String> sendcontent = new Pair<MigratableProcess, String>(mp, args);
        rpt.removeprocess(mp);
        mp.suspend();
        printDebugInfo("start to serialize.." + args);
        byte[] serializedprocess;
        serializedprocess = ser.serializeObj(sendcontent);
        printDebugInfo("size of seriallized file: " + serializedprocess.length);

        --movenum;

        printDebugInfo("start to send serialized process");

        ByteSender bsender = new ByteSender(destname, destport, instruction, serializedprocess);
        bsender.run();

      }

    } else if (command[0] == Byte.valueOf("4")) { // means restart the process
      printDebugInfo("start deserializing");

      ser = new Serializer();
      Pair<MigratableProcess, String> receivecontent = (Pair<MigratableProcess, String>) ser
              .deserializeObj(content);
      MigratableProcess mp = receivecontent.getLeft();
      String cmdargs = receivecontent.getRight();
      try {
        ProcessRunner pr = new ProcessRunner(mp, cmdargs, rpt);
        Thread t = new Thread(pr);
        t.start();
        this.socket.close();
      } catch (InvocationTargetException e) {
        System.out.println("");
        System.out.println("Invalid arguments for input command.");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  public void run() {
    printDebugInfo("Slave receving..");

    try {
      is = socket.getInputStream();
      DataInputStream dis = new DataInputStream(is);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte buffer[] = new byte[1024];
      int s;
      byte[] bytearray = null;

      int cnt = 0;

      printDebugInfo("SlaveListen: total num: " + cnt);

      for (; (s = dis.read(buffer)) != -1;) {
        printDebugInfo("SlaveListen: " + s);
        baos.write(buffer, 0, s);
        cnt += s;
      }
      printDebugInfo("SlaveListen: total num: " + cnt);
      bytearray = baos.toByteArray();

      if (bytearray != null && cnt != 0)
        slavehandler(bytearray);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
