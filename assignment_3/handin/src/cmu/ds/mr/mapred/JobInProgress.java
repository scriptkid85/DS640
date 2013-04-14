package cmu.ds.mr.mapred;



import java.io.Serializable;

import cmu.ds.mr.conf.JobConf;
import cmu.ds.mr.mapred.JobStatus.JobState;
import cmu.ds.mr.util.Log;


/**
 * JobInProgress is used to record running jobs with its status and config on 
 * JobTracker
 * 
 * @author Guanyu Wang 
 * */
@SuppressWarnings("serial")
public class JobInProgress implements Comparable<JobInProgress>, Serializable{
  static final Log LOG = new Log("JobInProgress.class");
  
  private JobID jobid;
  private JobStatus status;
  private JobConf jobconf;
  
  private JobTracker jobtracker;
  
 
  JobInitKillStatus jobInitKillStatus = new JobInitKillStatus();
  
  long startTime;
  long launchTime;
  long finishTime;
  
  
  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }
  
  public JobInProgress(JobID jobid, JobTracker tracker, JobConf jobconf){
    this.jobid = jobid;
    this.jobtracker = tracker;
    this.jobconf = jobconf;
    this.status = new JobStatus(jobid, JobState.READY, 0, 0,
            0, 0);
    
    this.startTime = System.currentTimeMillis();
    this.status.setStartTime(startTime);
  }

  public JobID getJobid() {
    return jobid;
  }

  public void setJobid(JobID jobid) {
    this.jobid = jobid;
  }

  public JobStatus getStatus() {
    return status;
  }

  public void setStatus(JobStatus status) {
    this.status = status;
  }

  public JobConf getJobconf() {
    return jobconf;
  }

  public void setJobconf(JobConf jobconf) {
    this.jobconf = jobconf;
  }

  public JobTracker getJobtracker() {
    return jobtracker;
  }

  public void setJobtracker(JobTracker jobtracker) {
    this.jobtracker = jobtracker;
  }
  
  public void kill(){
    boolean killNow = false;
    synchronized(jobInitKillStatus) {
      if(jobInitKillStatus.killed) {
    	//job is already marked for killing
        return;
      }
      jobInitKillStatus.killed = true;
      //if not in middle of init, terminate it now
      if(!jobInitKillStatus.initStarted || jobInitKillStatus.initDone) {
        //avoiding nested locking by setting flag
        killNow = true;
      }
    }
    if(killNow) {
      terminate(JobStatus.JobState.KILLED);
    }
  }
  
  private synchronized void terminate(JobStatus.JobState jobTerminationState) {
	  if(this.getStatus().isJobComplete()) {
		  System.out.println("Job already finished, cannot be killed");
		  return;
	  }
	  this.status.setState(JobState.KILLED);
  }
  
  
  private static class JobInitKillStatus {
    //flag to be set if kill is called
    boolean killed;
    boolean initStarted;
    boolean initDone;
  }



	@Override
	public int compareTo(JobInProgress o) {
		return this.jobid.compareTo(o.jobid);
	}
}
