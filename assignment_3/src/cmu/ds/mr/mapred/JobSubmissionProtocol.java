package cmu.ds.mr.mapred;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import cmu.ds.mr.conf.JobConf;

/**
 * Adapted and changed from Hadoop source code:
 * 
 * */
interface JobSubmissionProtocol extends Remote {

	/**
	 * Allocate a jobid for the job.
	 * 
	 * @return a unique job name for submitting jobs.
	 * @throws IOException
	 */
	public JobID getNewJobId() throws IOException, RemoteException;

	/**
	 * Submit a Job for execution. Returns the latest jobstatus that job. The
	 * job files should be submitted in <b>system-dir</b>/<b>jobName</b>.
	 */
	public JobStatus submitJob(JobID jobName, JobConf jobConf)
			throws IOException, RemoteException;

	/**
	 * Kill the indicated job
	 */
	public boolean killJob(String jobid) throws IOException, RemoteException;

	public boolean killAllJobs() throws IOException, RemoteException;

	/**
	 * Grab a handle to a job that is already known to the JobTracker.
	 * 
	 * @return Status of the job, or null if not found.
	 */
	public JobStatus getJobStatus(JobID jobid) throws IOException,
			RemoteException;

	public JobStatus getJobStatus(String jobid) throws IOException,
			RemoteException;

	public JobStatus[] jobsToComplete() throws IOException, RemoteException;

	/**
	 * Get all the status of jobs submitted.
	 * 
	 * @return array of JobStatus for the submitted jobs
	 */
	public JobStatus[] getAllJobs() throws IOException, RemoteException;
}
