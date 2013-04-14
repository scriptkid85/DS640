package cmu.ds.mr.mapred;

import java.io.Serializable;
import java.util.List;

/**
 * TaskTrackerStatus class used to communication between TaskTracker and JobTracker for all task
 * status in current taskTracker
 * 
 * @author Zeyuan Li
 * */
@SuppressWarnings("serial")
public class TaskTrackerStatus implements Serializable {

  private List<TaskStatus> taskStatusList;

  private int numFreeSlots;
  private String taskTrackername;

  // set only through constructor
  public TaskTrackerStatus(String name, List<TaskStatus> taskStatusList, int numFreeSlots) {
    super();
    this.taskStatusList = taskStatusList;
    this.numFreeSlots = numFreeSlots;
    this.taskTrackername = name;
  }

  public List<TaskStatus> getTaskStatusList() {
    return taskStatusList;
  }

  public int getNumFreeSlots() {
    return numFreeSlots;
  }
  
  public String getTaskTrackername() {
	    return taskTrackername;
  }

}
