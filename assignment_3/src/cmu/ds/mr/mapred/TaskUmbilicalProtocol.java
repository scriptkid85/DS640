package cmu.ds.mr.mapred;

import java.io.IOException;

/** 
 * Adapted and changed from Hadoop:
 * 
 * Protocol that task child process uses to contact its parent process.  The
 * parent is a daemon which which polls the central master for a new map or
 * reduce task and runs it as a child process.  All communication between child
 * and parent is via this protocol. */ 
interface TaskUmbilicalProtocol{

  /**
   * Report child's failure to parent.
   * 
   * @param taskId task-id of the child
   * @throws IOException
   * @throws InterruptedException
   * @return True if the task is known
   */
  boolean fail(TaskID taskId) 
  throws IOException, InterruptedException;

  /** Report that the task is successfully completed.  Failure is assumed if
   * the task process exits without calling this.
   * @param taskid task's id
   */
  void done(TaskID taskid) throws IOException;
}
