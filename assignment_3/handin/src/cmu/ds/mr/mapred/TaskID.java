package cmu.ds.mr.mapred;

import java.io.Serializable;

import cmu.ds.mr.mapred.TaskStatus.TaskType;

/**
 * Task ID class: An example TaskID is : <code>task_200707121733_0003_m_000005_01</code> , which
 * represents the first try of fifth map task in the third job running at the jobtracker started at
 * <code>200707121733</code>. (Adapted from javadoc)
 * 
 * @author Zeyuan Li
 * */
@SuppressWarnings("serial")
public class TaskID implements Comparable<TaskID>, Serializable {

  private static final String taskStr = "task";

  // all the field is read-only and can only be initialized in constructor.
  // Do NOT set in this class other than constructor!
  private JobID jobId;

  private TaskType type;

  private int taskNum;

  private int tryNum;

  public TaskID(JobID jobId, TaskType type, int taskNum, int tryNum) {
    super();
    this.jobId = jobId;
    this.type = type;
    this.taskNum = taskNum;
    this.tryNum = tryNum;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
    result = prime * result + taskNum;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TaskID other = (TaskID) obj;
    if (jobId == null) {
      if (other.jobId != null)
        return false;
    } else if (!jobId.equals(other.jobId))
      return false;
    if (taskNum != other.taskNum)
      return false;
    return true;
  }

  public String toString() {
    return String.format("%s_%s_%s_%06d_%02d", taskStr, jobId.toString(), type, taskNum, tryNum);
  }

  @Override
  public int compareTo(TaskID other) {
    int res = this.jobId.compareTo(other.jobId);
    if (res == 0) {
      int res1 = this.taskNum - other.taskNum;
      if (res1 == 0)
        return this.tryNum - other.tryNum;
      else
        return res1;
    } else
      return res;
  }

  public JobID getJobId() {
    return jobId;
  }

  public TaskType getType() {
    return type;
  }

  public int getTaskNum() {
    return taskNum;
  }

  public int getTryNum() {
    return tryNum;
  }

}
