package cmu.ds.mr.mapred;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import cmu.ds.mr.mapred.JobStatus.JobState;
import cmu.ds.mr.mapred.TaskStatus.TaskState;
import cmu.ds.mr.mapred.TaskStatus.TaskType;
import cmu.ds.mr.util.Log;
import cmu.ds.mr.util.Util;

/**
 * @author Guanyu Wang 
 * TaskScheduler is used assign ready jobs to tasktrackers which have free
 * running slots.
 ** */
class TaskScheduler {
	public static final Log LOG = new Log("TaskScheduler.class");

	private Queue<JobInProgress> jobQueue;
	private Map<JobID, JobInProgress> jobTable;
	private Queue<MapTask> maptaskQueue;
	private Queue<ReduceTask> reducetaskQueue;

	public TaskScheduler(Queue<JobInProgress> jobQueue,
			Map<JobID, JobInProgress> jobTable) {
		this.jobQueue = jobQueue;
		this.jobTable = jobTable;
		this.maptaskQueue = new LinkedList<MapTask>();
		this.reducetaskQueue = new LinkedList<ReduceTask>();
	}

	public synchronized boolean recoverFailedTask(TaskStatus tstatus) {
		JobID jid = tstatus.getTaskId().getJobId();
		TaskType ttype = tstatus.getType();
		JobInProgress jip = jobTable.get(jid);
		boolean ret = false;
		if (tstatus.getTryNum() < Util.MAX_TRY) {
			if (ttype == TaskType.MAP) {
				TaskID tid = new TaskID(jip.getJobid(), TaskType.MAP,
						tstatus.getTaskNum(), tstatus.getTryNum() + 1);
				ret = maptaskQueue.add(new MapTask(tid, jip.getJobconf(),
						new TaskStatus(tid, TaskState.READY, TaskType.MAP)));
			} else if (ttype == TaskType.REDUCE) {
				TaskID tid = new TaskID(jip.getJobid(), TaskType.REDUCE,
						tstatus.getTaskNum(), tstatus.getTryNum() + 1);
				ret = reducetaskQueue.add(new ReduceTask(tid, jip.getJobconf(),
						new TaskStatus(tid, TaskState.READY, TaskType.REDUCE)));
			}
		} else {
			LOG.debug("Task:" + tstatus.getTaskId().toString()
					+ " try time exceed.");
			LOG.debug("Job:" + jip.getJobid().toString() + " fail.");
			jip.getStatus().setState(JobState.FAILED);
		}
		return ret;
	}

	private synchronized boolean addTasks() {
		synchronized (jobQueue) {
			synchronized (jobTable) {
				JobID jid = null;
				while (!jobQueue.isEmpty()) {
					jid = jobQueue.peek().getJobid();
					if (!jobTable.containsKey(jid)
							|| jobTable.get(jid).getStatus().isJobComplete()) {
						jobQueue.poll();
					} else
						break;
				}

				if (jobQueue.isEmpty()) {
					LOG.warn("addTasks(): no more jobs in job queue");
					return false;
				}

				boolean ret = true;
				JobInProgress jip = jobQueue.poll();
				LOG.debug("addTasks(): add tasks from job: "
						+ jip.getJobid().toString());

				LOG.debug("getNumMapTasks: "
						+ jip.getJobconf().getNumMapTasks());
				for (int i = 0; i < jip.getJobconf().getNumMapTasks(); ++i) {
					TaskID tid = new TaskID(jip.getJobid(), TaskType.MAP, i, 1);
					ret = ret
							&& maptaskQueue.add(new MapTask(tid, jip
									.getJobconf(), new TaskStatus(tid,
									TaskState.READY, TaskType.MAP)));

				}
				LOG.debug("getNumReduceTasks:  "
						+ jip.getJobconf().getNumReduceTasks());
				for (int i = 0; i < jip.getJobconf().getNumReduceTasks(); ++i) {
					TaskID tid = new TaskID(jip.getJobid(), TaskType.REDUCE, i,
							1);
					ret = ret
							&& reducetaskQueue.add(new ReduceTask(tid, jip
									.getJobconf(), new TaskStatus(tid,
									TaskState.READY, TaskType.REDUCE)));
				}
				LOG.debug("addTasks(): add tasks from job: "
						+ jip.getJobid().getId() + " " + ret);
				boolean tmp = reducetaskQueue.isEmpty();
				LOG.debug(tmp ? "empty" : reducetaskQueue.peek().getJobid()
						.toString());
				return ret;
			}
		}
	}

	/**
	 * Returns the tasks we'd like the TaskTracker to execute right now.
	 * 
	 * @return A task to run on that TaskTracker, possibly empty.
	 */
	public synchronized Task assignTask() {
		synchronized (jobTable) {
			synchronized (reducetaskQueue) {
				synchronized (maptaskQueue) {

					while (!reducetaskQueue.isEmpty()) {
						JobID jid = reducetaskQueue.peek().getJobid();
						if (!jobTable.containsKey(jid)
								|| jobTable.get(jid).getStatus()
										.isJobComplete()) {
							reducetaskQueue.poll();
						} else
							break;
					}

					while (!maptaskQueue.isEmpty()) {
						JobID jid = maptaskQueue.peek().getJobid();
						if (!jobTable.containsKey(jid)
								|| jobTable.get(jid).getStatus()
										.isJobComplete()) {
							maptaskQueue.poll();
						} else
							break;
					}

					if (reducetaskQueue.isEmpty()) {
						if (!addTasks()) {
							return null;
						}
					}

					JobID toreducejob = reducetaskQueue.peek().getJobid();

					if (jobTable.get(toreducejob).getStatus().getMapProgress() >= 0.99) {
						return reducetaskQueue.poll();
					}

					if (maptaskQueue.isEmpty()) {
						if (!addTasks()) {
							return null;
						}
					}

					return maptaskQueue.poll();
				}
			}
		}
	}

}
