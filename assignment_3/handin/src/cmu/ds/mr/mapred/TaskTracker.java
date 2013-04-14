package cmu.ds.mr.mapred;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import cmu.ds.mr.conf.JobConf;
import cmu.ds.mr.mapred.TaskStatus.TaskState;
import cmu.ds.mr.util.Log;
import cmu.ds.mr.util.Util;

/**
 * TaskTracker: the class for launch, kill tasks and heartbeating with jobTracker
 * 
 * @author Zeyuan Li
 * */
public class TaskTracker implements TaskUmbilicalProtocol {

  public static final Log LOG = new Log("TaskTracker.class");

  // running task table
  private String taskTrackerName; // taskTrackerName assigned by jobtracker to uniquely identify a
                                  // taskTracker

  private Map<TaskID, Task> taskMap; // running tasks in taskTracker

  private Map<TaskID, Task> taskDoneMap; // finisehed task map

  private Map<TaskID, Thread> threadMap; // running thread map

  private String localRootDir; // local map output root dir

  private String jobTrackerAddrStr; // job tracker address

  private AtomicInteger numFreeSlots;

  private AtomicInteger numMaxSlots;

  // JobTracker stub (using RMI)
  private InterTrackerProtocol jobTrackerProxy;

  public TaskTracker(JobConf conf, String jobTrackerAddrStr) throws NotBoundException,
          UnknownHostException {
    try {
      taskTrackerName = InetAddress.getLocalHost().getCanonicalHostName();
      taskMap = new ConcurrentHashMap<TaskID, Task>();
      taskDoneMap = new ConcurrentHashMap<TaskID, Task>();
      threadMap = new ConcurrentHashMap<TaskID, Thread>();

      LOG.info("create TaskTracker");
      this.jobTrackerAddrStr = jobTrackerAddrStr;
      Registry registry = LocateRegistry.getRegistry(jobTrackerAddrStr);
      jobTrackerProxy = (InterTrackerProtocol) registry.lookup(Util.SERVICE_NAME_INTERTRACKER);

      localRootDir = (String) conf.getProperties().get(Util.LOCAL_ROOT_DIR);

      numFreeSlots = new AtomicInteger();
      numMaxSlots = new AtomicInteger();
      numFreeSlots.set(Integer.parseInt((String) conf.getProperties().get(Util.NUM_TASK_MAX)));
      numMaxSlots.set(numFreeSlots.get());
    } catch (RemoteException re) {
      LOG.error("JobTracker hasn't been established!");
      System.exit(Util.EXIT_JT_NOTSTART);
    }
  }

  @Override
  public synchronized boolean fail(TaskID taskId) throws IOException, InterruptedException {
    Task ts = taskMap.get(taskId);
    ts.taskStatus.setState(TaskState.FAILED);
    // put into finished task map
    taskDoneMap.put(taskId, ts);

    numFreeSlots.incrementAndGet();
    return false;
  }

  @Override
  public synchronized void done(TaskID taskid) throws IOException {
    // notify JobTracker
    Task ts = taskMap.get(taskid);
    ts.taskStatus.setState(TaskState.SUCCEEDED);
    // put into finished task map
    taskDoneMap.put(taskid, ts);

    numFreeSlots.incrementAndGet();
  }

  private void startTaskTracker() throws IOException {
    try {
      taskTrackerName = InetAddress.getLocalHost().getCanonicalHostName() + "-"
              + Integer.toString(jobTrackerProxy.getNewTaskTrackerId());
      LOG.info("Tasktracker name: " + taskTrackerName);
      LOG.debug("Starting taskTracker...");

      while (true) {
        Thread.sleep(Util.TIME_INTERVAL_HEARTBEAT);

        // build current task tracker status
        List<TaskStatus> taskStatusList = getAllTaskStatus();
        TaskTrackerStatus tts = new TaskTrackerStatus(taskTrackerName, taskStatusList,
                numFreeSlots.get());

        // transmit heartbeat
        LOG.debug("TaskTracker: start heartbeat");
        Task retTask = jobTrackerProxy.heartbeat(tts);
        LOG.debug("TaskTracker: recv heartbeat");

        // retTask == null means JobTracker has no available task to assign
        if (retTask != null) {

          // if trynum = -1, then this is a intialize response, clean up the tasklist;
          if (retTask.getTaskStatus().getTryNum() == -1) {
            taskMap = new HashMap<TaskID, Task>();
            taskDoneMap = new HashMap<TaskID, Task>();
            continue;
          }
          // check if it is a kill instruction
          else if (retTask.getTaskStatus().getState() == TaskState.KILLED) {
            TaskID taskid = retTask.getTaskStatus().getTaskId();
            // kill task
            if (threadMap.containsKey(taskid))
              threadMap.get(taskid).interrupt();

            LOG.info("task killed: " + taskid.toString());
            threadMap.remove(taskid);
            continue;
          }

          LOG.debug("get new task id: " + retTask.taskId.toString());
          // put it in the taskTracker's table
          taskMap.put(retTask.taskId, retTask);

          // launch the task when we have free slot
          if (numFreeSlots.get() > 0) {
            numFreeSlots.decrementAndGet();

            TaskRunner runner = retTask.createRunner(TaskTracker.this, retTask);
            Thread th = new Thread(runner);
            threadMap.put(retTask.taskId, th);
            th.start();
          } else
            assert numFreeSlots.get() > 0 : String.format("numFreeSlots:%d", numFreeSlots.get());
        }
      }
    } catch (RemoteException re) {
      LOG.error("Remote exception! JobTracker not started or down!");
      System.exit(Util.EXIT_JT_DOWN);
    } catch (InterruptedException ie) {
      LOG.error("TaskTracker down!");
      System.exit(Util.EXIT_JT_DOWN);
    }
  }

  public synchronized List<TaskStatus> getAllTaskStatus() {
    List<TaskStatus> res = new ArrayList<TaskStatus>();
    for (Entry<TaskID, Task> en : taskMap.entrySet()) {
      res.add(en.getValue().getTaskStatus());
    }

    // delete finished task once it has used after heartbeat
    for (Iterator<Entry<TaskID, Task>> it = taskDoneMap.entrySet().iterator(); it.hasNext();) {
      Entry<TaskID, Task> en = it.next();
      if (taskMap.containsKey(en.getKey())) {
        taskMap.remove(en.getKey());
        threadMap.remove(en.getKey());
        it.remove();
      }
    }

    return res;
  }

  public static void main(String[] args) throws FileNotFoundException, IOException,
          NotBoundException, InterruptedException {
    if (args.length != 1) {
      LOG.error("Usage: TaskTracker <JobTrackerAddress>");
      return;
    }
    // read configure file
    LOG.setInfo(true);
    LOG.setDebug(false);
    JobConf conf = new JobConf();
    LOG.debug("prepare to create TaskTracker");
    TaskTracker tt = new TaskTracker(conf, args[0]);
    tt.startTaskTracker();
  }

}
