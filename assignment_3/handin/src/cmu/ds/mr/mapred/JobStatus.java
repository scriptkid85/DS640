package cmu.ds.mr.mapred;

import java.io.Serializable;

/**
 * JobStatus is used to record jobs' status which is used to identify and check 
 * the specified job
 * 
 * @author Guanyu Wang 
 ** */

@SuppressWarnings("serial")
public class JobStatus implements Serializable {
	public static enum JobState {
		SUCCEEDED, WAITING, DEFINE, RUNNING, READY, FAILED, KILLED
	};

	private JobID jid;
	private JobState state = JobState.DEFINE;
	private float mapProgress;
	private float reduceProgress;
	private float cleanupProgress;
	private long startTime;

	public JobStatus(JobID jid, JobState runState, float mapProgress,
			float reduceProgress, float cleanupProgress, long startTime) {
		super();
		this.jid = jid;
		this.state = runState;
		this.mapProgress = mapProgress;
		this.reduceProgress = reduceProgress;
		this.cleanupProgress = cleanupProgress;
		this.startTime = startTime;
	}

	public synchronized boolean isJobComplete() {
		return (state == JobState.SUCCEEDED || state == JobState.FAILED || state == JobState.KILLED);
	}

	public JobState getState() {
		return state;
	}

	public void setState(JobState runState) {
		this.state = runState;
	}

	public JobID getJid() {
		return jid;
	}

	public void setJid(JobID jid) {
		this.jid = jid;
	}

	public float getMapProgress() {
		return mapProgress;
	}

	public void setMapProgress(float mapProgress) {
		this.mapProgress = mapProgress;
	}

	public float getReduceProgress() {
		return reduceProgress;
	}

	public void setReduceProgress(float reduceProgress) {
		this.reduceProgress = reduceProgress;
	}

	public float getCleanupProgress() {
		return cleanupProgress;
	}

	public void setCleanupProgress(float cleanupProgress) {
		this.cleanupProgress = cleanupProgress;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

}
