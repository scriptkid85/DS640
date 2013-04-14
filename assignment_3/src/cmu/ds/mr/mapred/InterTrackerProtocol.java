package cmu.ds.mr.mapred;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 * Adapted and changed from Hadoop source code:
 * 
 * */
interface InterTrackerProtocol extends Remote {

	/**
	 * Commuincation between TaskTracker and JobTracker
	 * 
	 * @param TaskTrackerStatus status
	 * @return new Task
	 * @throws IOException
	 */
	Task heartbeat(TaskTrackerStatus status) throws IOException;

	/**
	 * Commuincation between TaskTracker and JobTracker
	 * 
	 * @return new TaskTracker's index according to Jobtracker's current counter
	 * @throws IOException
	 * @throws RemoteException
	 */
	public int getNewTaskTrackerId() throws IOException, RemoteException;

}
