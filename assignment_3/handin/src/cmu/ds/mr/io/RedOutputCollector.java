package cmu.ds.mr.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import cmu.ds.mr.util.Log;

/**
 * Reduce output class: the class to collect from user's reducer code
 * 
 * @author Zeyuan Li
 * */
public class RedOutputCollector implements OutputCollector<String, String> {

  public static final Log LOG = new Log("RedOutputCollector.class");

  private Map<String, String> outlist;

  private String basePath;

  private int taskNum;

  public RedOutputCollector(String basePath, int taskNum) throws IOException {
    this.taskNum = taskNum;
    this.basePath = basePath;
    File file = new File(basePath);
    if (file.exists())
      file.delete();
    if (!file.exists())
      file.mkdirs();

    outlist = new TreeMap<String, String>();
  }

  @Override
  public void collect(String key, String value) throws IOException {
    // Assume key is String
    outlist.put(key, value);

    LOG.debug(String.format("key %s\tval %s", key, value));
  }

  public void writeToDisk() throws IOException {
    String name = String.format("part-%05d", taskNum);
    BufferedWriter bw = new BufferedWriter(new FileWriter(basePath + File.separator + name));
    try {
      for (Entry<String, String> en : outlist.entrySet()) {
        bw.write(String.format("%s\t%s\n", en.getKey(), en.getValue()));
      }
    } finally {
      bw.close();
    }

    LOG.debug("write to disk finished");
  }

}
