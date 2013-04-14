package cmu.ds.mr.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;

import cmu.ds.mr.conf.JobConf;
import cmu.ds.mr.io.OutputCollector;
import cmu.ds.mr.mapred.JobClient;
import cmu.ds.mr.mapred.MapReduceBase;
import cmu.ds.mr.mapred.Mapper;
import cmu.ds.mr.mapred.Reducer;

/**
 * Hadoop sample test program
 * 
 * Adapted from http://code.google.com/p/hadoop-map-reduce-examples/
 * 
 * */
public class Anagram {

  /**
   * The Anagram mapper class gets a word as a line from the HDFS input and sorts the letters in the
   * word and writes its back to the output collector as Key : sorted word (letters in the word
   * sorted) Value: the word itself as the value. When the reducer runs then we can group anagrams
   * togather based on the sorted key.
   * 
   * @author subbu iyer
   */

  public static class AnagramMapper extends MapReduceBase implements
          Mapper<Long, String, String, String> {

    public void map(Long key, String value, OutputCollector<String, String> output)
            throws IOException {
      String word = value.toLowerCase().toString();
      char[] wordChars = word.toCharArray();
      Arrays.sort(wordChars);
      String sortedWord = new String(wordChars);
      output.collect(sortedWord, word);
    }

  }

  /**
   * The Anagram reducer class groups the values of the sorted keys that came in and checks to see
   * if the values iterator contains more than one word. if the values contain more than one word we
   * have spotted a anagram.
   * 
   * @author subbu
   * 
   */

  public static class AnagramReducer extends MapReduceBase implements
          Reducer<String, String, String, String> {

    public void reduce(String key, Iterator<String> values, OutputCollector<String, String> output)
            throws IOException {
      String anastr = "";
      while (values.hasNext()) {
        String anagam = values.next();
        anastr = anastr + anagam + "~";
      }
      StringTokenizer outputTokenizer = new StringTokenizer(anastr, "~");
      if (outputTokenizer.countTokens() >= 2) {
        anastr = anastr.replace("~", "\t");
        output.collect(key, anastr);
      }
    }

  }

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    if (args.length != 3) {
      System.err.println("Usage: Anagram <inPath> <outPath> <numReducer>");
      return;
    }

    JobConf conf = new JobConf();
    conf.setJobName("anagramcount");

    conf.setMapperClass(AnagramMapper.class);
    conf.setReducerClass(AnagramReducer.class);

    conf.setInpath(args[0]);
    conf.setOutpath(args[1]);

    conf.setNumReduceTasks(Integer.parseInt(args[2]));

    JobClient.runJob(conf);

  }

}
