package cmu.ds.mr.mapred;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Properties;

import cmu.ds.mr.conf.JobConf;
import cmu.ds.mr.io.FileSplit;
import cmu.ds.mr.io.FileSplitter;
import cmu.ds.mr.mapred.JobStatus.JobState;
import cmu.ds.mr.util.Log;
import cmu.ds.mr.util.Util;

/**
 * JobClient is used to submit jobs from local to jobtracker
 * 
 * @author Guanyu Wang 
 * */
public class JobClient {

	private static final Log LOG = new Log("JobClient.class");

	private JobConf jobConf;
	private JobSubmissionProtocol jobTrackerProxy;
	private Properties prop = new Properties();

	public JobClient() {
	}

	public JobClient(JobConf jobConf) throws FileNotFoundException,
			IOException, NotBoundException {
		super();
		this.jobConf = jobConf;
		readConfig();
		// initialize remote object (jobTrackerProxy)
		initProxy();
	}

	public void initProxy() throws NotBoundException {
		try {
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
			// get job tracker start address from jobConf
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

	public static RunningJob runJob(JobConf jobConf) {

		try {
			JobClient jc = new JobClient(jobConf);
			RunningJob job = jc.submitJob(jobConf);

			// query status and state every second
			if (!jc.monitorAndPrintJob(jobConf, job)) {
				LOG.error("Job failed.");
				System.exit(Util.EXIT_JT_DOWN);
			}

			// job done
			return job;
		} catch (IOException ie) {
			LOG.error("Job cannot be submitted.");
			System.exit(Util.EXIT_JT_NOTSTART);
		} catch (NotBoundException ne) {
			LOG.error("Job cannot be submitted.");
			System.exit(Util.EXIT_JT_NOTSTART);
		} catch (Exception e) {
			LOG.error("Job failed.");
			System.exit(Util.EXIT_JT_DOWN);
		}

		return null;
	}

	/**
	 * Monitor and print job status
	 * */
	private boolean monitorAndPrintJob(JobConf jobConf, RunningJob job)
			throws IOException {
		try {
			JobID jid = job.getID();
			String logstrPre = "";

			while (!job.isComplete()) {
				Thread.sleep(Util.TIME_INTERVAL_MONITOR);

				// ask job tracker for new job status
				JobStatus jobStatusNew = jobTrackerProxy.getJobStatus(jid);
				job.setJobStatus(jobStatusNew);

				if (job.getJobState() == JobState.FAILED) {
					// LOG.debug("Job failed");
					return false;
				}

				String logstr = String
						.format("%s: map %.1f\treduce %.1f", jid.toString(),
								job.mapProgress(), job.reduceProgress());
				if (!logstr.equals(logstrPre)) {
					System.out.println(logstr);
					logstrPre = logstr;
				}
			}

			if (job.getJobState() == JobState.SUCCEEDED) {
				LOG.info("Job completed with success");
			} else if (job.getJobState() == JobState.KILLED) {
				LOG.info("Job was killed.");
			} else {
				LOG.info("Job completed without success");
			}

		} catch (RemoteException re) {
			LOG.error("Remote exception! JobTracker not started or down!");
			System.exit(Util.EXIT_JT_DOWN);
		} catch (InterruptedException ie) {
			LOG.error("JobClient down!");
			System.exit(Util.EXIT_JC_DOWN);
		}

		return true;
	}

	public RunningJob submitJob(JobConf jobConf) throws IOException {
		try {
			// step 2: get new job ID
			JobID jid = jobTrackerProxy.getNewJobId();
			System.out.println(jid.getId());
			FileSplitter splitter = new FileSplitter();
			List<FileSplit> splitFiles = splitter.getSplits(jobConf);
			jobConf.setSplitFiles(splitFiles);
			jobConf.setNumMapTasks(splitFiles.size()); // set # of maps

			// step 3: submit job
			JobStatus status = jobTrackerProxy.submitJob(jid, jobConf);
			if (status != null) {
				// return a RunningJob (Job class)
				return new Job(jid, jobConf, status);
			} else {
				LOG.error("Could not launch job");
				throw new IOException("Could not launch job");
			}
		} catch (RemoteException re) {
			LOG.error("Remote exception! JobTracker not started or down!");
			System.exit(Util.EXIT_JT_DOWN);
		}

		return null;
	}
}
