package cmu.ds.mr.mapred;

import java.io.IOException;

import cmu.ds.mr.io.OutputCollector;

/**
 * The mapper class of MapReduce framework
 * 
 * */
public interface Mapper<K1, V1, K2, V2> {

  void map(K1 key, V1 value, OutputCollector<K2, V2> output) throws IOException;
}
