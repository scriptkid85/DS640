package cmu.ds.mr.mapred;

import java.io.IOException;

import cmu.ds.mr.conf.JobConf;

/**
 * Base class for Mapper and Reducer. Define two methods but haven't implement them
 * 
 * */
public class MapReduceBase {

  public void configure(JobConf job) {
  }

  public void close() throws IOException {
  }

}
