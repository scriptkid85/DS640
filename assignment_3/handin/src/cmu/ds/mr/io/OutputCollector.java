package cmu.ds.mr.io;

import java.io.IOException;

/**
 * The output collector of Mapper and Reducer. 
 * For Mapper, output to local fileSystem.
 * For Reducer, output to HDFS (AFS).
 * */
public interface OutputCollector<K, V> {
  
  void collect(K key, V value) throws IOException;
  
}
