package cmu.ds.mr.mapred;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import cmu.ds.mr.conf.JobConf;

/**
 * The abstract class for Task
 * 
 * @see MapTask
 * @see RedceTask
 * @author Zeyuan Li
 * */
@SuppressWarnings("serial")
public abstract class Task implements Serializable {
  // JobID is in taskId, use task.getJobid() to get JobId
  // JobID can only be set by new TaskID
  protected TaskID taskId;

  protected JobConf taskConf; // task input and output path is defined in the taskConf

  // taskStatus include state, type and TaskId (a copy of Task.taskId, since we do communicate using
  // taskStatus)
  protected TaskStatus taskStatus;

  // protected int taskID; //id within a job, also means the index of the splited file

  public Task(TaskID taskId, JobConf taskConf, TaskStatus taskStatus) {
    super();
    this.taskId = taskId;
    this.taskConf = taskConf;
    this.taskStatus = taskStatus;
  }

  /**
   * run the task
   */
  public abstract void startTask(Task task, TaskUmbilicalProtocol taskTrackerProxy)
          throws IOException, ClassNotFoundException, InterruptedException, RuntimeException,
          InstantiationException, IllegalAccessException, InvocationTargetException,
          NoSuchMethodException;

  /**
   * Get a runner (in a different thread) to run the task
   **/
  public TaskRunner createRunner(TaskTracker tracker, Task task) throws IOException {
    return new TaskRunner(task, tracker);
  }

  public JobConf getConf() {
    return taskConf;
  }

  public void setConf(JobConf conf) {
    this.taskConf = conf;
  }

  public TaskStatus getTaskStatus() {
    return taskStatus;
  }

  public void setTaskStatus(TaskStatus taskStatus) {
    this.taskStatus = taskStatus;
  }

  public JobID getJobid() {
    return taskId.getJobId();
  }

}
