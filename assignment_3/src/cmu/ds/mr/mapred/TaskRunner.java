package cmu.ds.mr.mapred;

import java.io.IOException;

import cmu.ds.mr.util.Log;

/**
 * Class for running mappaer and reducer in a separate thread
 * 
 * @author Zeyuan Li
 * */
public class TaskRunner implements Runnable {

  public static final Log LOG = new Log("TaskRunner.class");

  private Task task;

  private TaskTracker taskTrackerProxy;

  public TaskRunner(Task task, TaskTracker taskTrackerProxy) {
    super();
    this.task = task;
    this.taskTrackerProxy = taskTrackerProxy;
  }

  @Override
  public void run() {
    try {
      task.startTask(task, taskTrackerProxy);
      LOG.info(String.format("Task %s successful.", task.taskId.toString()));

    } catch (Exception e) {
      try {
        taskTrackerProxy.fail(task.getTaskStatus().getTaskId());
      } catch (IOException e1) {
        LOG.error("Task fails.");
      } catch (InterruptedException e1) {
        LOG.error("Task fails.");
      }
      LOG.error("Task fails.");
    }
  }

}
