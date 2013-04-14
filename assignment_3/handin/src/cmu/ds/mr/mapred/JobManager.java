package cmu.ds.mr.mapred;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

import cmu.ds.mr.mapred.JobStatus.JobState;
import cmu.ds.mr.util.Log;
import cmu.ds.mr.util.Util;

/**
 * JobManager is used to check and kill running jobs on JobTracker
 * 
 * @author Guanyu Wang 
 * */
public class JobManager {

	private static final Log LOG = new Log("JobManager.class");

	public JobSubmissionProtocol jobTrackerProxy;

	private Properties prop = new Properties();

	public JobManager() throws FileNotFoundException, IOException,
			NotBoundException {
		readConfig();
		// initialize remote object (jobTrackerProxy)
		initProxy();
	}

	public void initProxy() throws NotBoundException {
		try {
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}

			Registry registry = LocateRegistry.getRegistry(prop
					.getProperty(Util.JOBTRACK_ADDR));

			jobTrackerProxy = (JobSubmissionProtocol) registry
					.lookup(Util.SERVICE_NAME);
		} catch (RemoteException re) {
			LOG.error("Remote exception! JobTracker not started");
			System.exit(Util.EXIT_JT_NOTSTART);
		}
	}

	public void readConfig() throws FileNotFoundException, IOException {
		prop.load(new FileInputStream(Util.CONFIG_PATH));
	}

	/**
	 * Monitor and print job status
	 * */
	public boolean checkAndPrintJob(RunningJob job) throws IOException {
		try {
			JobID jid = job.getID();

			// ask job tracker for new job status
			JobStatus jobStatusNew = jobTrackerProxy.getJobStatus(jid);
			job.setJobStatus(jobStatusNew);

			if (job.getJobState() == JobState.FAILED) {
				LOG.info("Job failed");
				return false;
			}

			String logstr = String.format("%s: map %.1f\treduce %.1f",
					jid.toString(), job.mapProgress(), job.reduceProgress());
			System.out.println(logstr);

		} catch (RemoteException re) {
			LOG.error("Remote exception! JobTracker not started or down!");
			System.exit(Util.EXIT_JT_DOWN);
		}
		return true;
	}

	public boolean checkAndPrint(String jid) throws IOException {

		try {

			// ask job tracker for new job status
			JobStatus jobStatusNew = jobTrackerProxy.getJobStatus(jid);

			if (jobStatusNew.getState() == JobState.FAILED) {
				LOG.info("Job failed");
				return false;
			}

			String logstr = String.format("%s: map %.1f\treduce %.1f", jid,
					jobStatusNew.getMapProgress(),
					jobStatusNew.getReduceProgress());
			System.out.println(logstr);

		} catch (RemoteException re) {
			LOG.error("Remote exception! JobTracker not started or down!");
			System.exit(Util.EXIT_JT_DOWN);
		}
		return true;
	}

	public boolean kill(String jid) throws IOException {
		boolean ret = false;
		try {
			ret = jobTrackerProxy.killJob(jid);
		} catch (RemoteException re) {
			LOG.error("Remote exception! JobTracker not started or down!");
			System.exit(Util.EXIT_JT_DOWN);
		}
		return ret;
	}

	public boolean checkAndPrintAll() throws IOException {

		try {

			JobStatus[] jobStatusNew = jobTrackerProxy.getAllJobs();
			if (jobStatusNew.length < 1) {
				System.out.println("No running job now");
				return true;
			}
			for (JobStatus jsn : jobStatusNew) {
				String logstr = String.format("%s: map %.1f\treduce %.1f", jsn
						.getJid().toString(), jsn.getMapProgress(), jsn
						.getReduceProgress());
				System.out.println(logstr);
			}
		} catch (RemoteException re) {
			LOG.error("Remote exception! JobTracker not started or down!");
			System.exit(Util.EXIT_JT_DOWN);
		}
		return true;
	}

	public boolean killAll() throws IOException {
		boolean ret = false;
		try {
			ret = jobTrackerProxy.killAllJobs();
		} catch (RemoteException re) {
			LOG.error("Remote exception! JobTracker not started or down!");
			System.exit(Util.EXIT_JT_DOWN);
		}
		return ret;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException, NotBoundException {
		if (args.length > 2) {
			System.out.println("JobManager");
			System.out.println("Usage 1: JobManager -checkall");
			System.out.println("Usage 2: JobManager -check jobID");
			System.out.println("Usage 3: JobManager -killall");
			System.out.println("Usage 4: JobManager -kill jobID");
		}

		JobManager jm = new JobManager();

		if (args[0].equals("-checkall") && args.length == 1) {
			jm.checkAndPrintAll();
		}

		else if (args[0].equals("-killall") && args.length == 1) {
			jm.killAll();
		} else if (args[0].equals("-check") && args.length == 2) {
			jm.checkAndPrint(args[1]);
		}

		else if (args[0].equals("-kill") && args.length == 2) {
			jm.kill(args[1]);
		}

		else {
			System.out.println("JobManager");
			System.out.println("Usage 1: JobManager -checkall");
			System.out.println("Usage 2: JobManager -check jobID");
			System.out.println("Usage 3: JobManager -killall");
			System.out.println("Usage 4: JobManager -kill jobID");
		}
	}
}
