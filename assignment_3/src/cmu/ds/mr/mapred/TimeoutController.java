package cmu.ds.mr.mapred;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cmu.ds.mr.util.Log;
import cmu.ds.mr.util.Util;

/**
 * @author Guanyu Wang 
 * TimeoutController is check the availability of all tasttrackers.
 * Once a new task tracker is connected to the jobtracker, the 
 * timeoutController will periodically check it
 ** */
public class TimeoutController implements Runnable {

	static final Log LOG = new Log("TimeoutController.class");
	private Map<String, Set<TaskStatus>> tasktrackers;
	private Map<String, Integer> validtasktrackers;
	private TaskScheduler taskscheduler;

	public TimeoutController(Map<String, Set<TaskStatus>> tts,
			TaskScheduler ts, Map<String, Integer> validtasktrackers) {
		this.validtasktrackers = validtasktrackers;
		this.tasktrackers = tts;
		this.taskscheduler = ts;
	}

	@Override
	public void run() {
		List<String> failedtrackers = new ArrayList<String>();
		synchronized (validtasktrackers) {
			synchronized (tasktrackers) {
				for (String name : validtasktrackers.keySet()) {
					if (validtasktrackers.get(name) > Util.TIME_OUT_MAX) {
						LOG.info("TaskTracker:" + name + " gone.");
						failedtrackers.add(name);
						Set<TaskStatus> taskset = tasktrackers.get(name);
						for (TaskStatus ts : taskset) {
							taskscheduler.recoverFailedTask(ts);
						}
						tasktrackers.remove(name);
					} else {
						// increment the time out counter
						validtasktrackers.put(name,
								validtasktrackers.get(name) + 1);
					}
				}

				// remove the lost trackers from the valid track list
				for (String tracker : failedtrackers) {
					validtasktrackers.remove(tracker);
				}
			}
		}
	}
}
