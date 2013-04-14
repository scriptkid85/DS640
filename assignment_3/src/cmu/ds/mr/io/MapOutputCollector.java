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
 * Map output collector class to collect from user's mapper code (e.g. output.collect())
 * 
 * @author Zeyuan Li
 * */
public class MapOutputCollector implements OutputCollector<String, String> {

  public static final Log LOG = new Log("MapOutputCollector.class");

  private List<Map<String, List<String>>> outlist;

  private int numRed;

  private String basePath;

  public MapOutputCollector(String basePath, int nred) throws IOException {

    LOG.setInfo(false);

    this.basePath = basePath;
    File file = new File(basePath);
    if (file.exists())
      file.delete();
    if (!file.exists())
      file.mkdirs();

    numRed = nred;
    outlist = new ArrayList<Map<String, List<String>>>();
    for (int i = 0; i < numRed; i++) {
      outlist.add(new TreeMap<String, List<String>>());
    }
  }

  @Override
  public void collect(String key, String value) throws IOException {
    // Assume key is String
    int k = key.toString().hashCode() % numRed;
    if (k < 0)
      k += numRed;
    if (outlist.get(k).containsKey(key))
      outlist.get(k).get(key).add(value);
    else {
      List<String> list = new ArrayList<String>();
      list.add(value);
      outlist.get(k).put(key, list);
    }

    LOG.debug(String.format("MapOutput: key %s\tval %s", key, value));
  }

  public void writeToDisk() throws IOException {
    for (int i = 0; i < numRed; i++) {
      BufferedWriter bw = new BufferedWriter(new FileWriter(basePath + File.separator + i));
      try {
        Map<String, List<String>> map = outlist.get(i);
        for (Entry<String, List<String>> en : map.entrySet()) {
          for (String num : en.getValue())
            bw.write(String.format("%s\t%s\n", en.getKey(), num));
        }
      } finally {
        bw.close();
      }
    }

    LOG.debug("write to disk finished");
  }

}
