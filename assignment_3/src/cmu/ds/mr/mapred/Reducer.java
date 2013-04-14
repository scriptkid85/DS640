package cmu.ds.mr.mapred;

import java.io.IOException;

import java.util.Iterator;

import cmu.ds.mr.io.OutputCollector;

/**
 * The reducer class of the MapReduce framework
 * 
 * */
public interface Reducer<K1, V1, K2, V2> {

  void reduce(K1 key, Iterator<V1> values, OutputCollector<K2, V2> output)
          throws IOException;

}
