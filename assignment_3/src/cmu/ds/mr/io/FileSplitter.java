package cmu.ds.mr.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import cmu.ds.mr.conf.JobConf;
import cmu.ds.mr.util.Util;

/**
 * The class to split the input file/directory of files to FileSplit
 * 
 * @author Zeyuan Li
 * */
public class FileSplitter {

  private Properties prop;

  public FileSplitter() throws FileNotFoundException, IOException {
    prop = new Properties();
    prop.load(new FileInputStream(Util.CONFIG_PATH));
  }

  /**
   * Split input files
   * */
  public List<FileSplit> getSplits(JobConf jobConf) throws IOException {
    List<FileSplit> splitFiles = new ArrayList<FileSplit>();

    long fileLen = 0l, start = 0l; // # of lines
    long blksize = Long.parseLong((String) prop.get(Util.BLOCK_SIZE));
    int cnt = 1;
    String line;
    BufferedReader br = null;

    try {
      File inFile = new File(jobConf.getInpath());
      if (inFile.isDirectory()) {
        // TODO: multiple files
        File[] files = inFile.listFiles();

        for (int i = 0; i < files.length; i++) {
          int nline = 0;
          br = new BufferedReader(new FileReader(files[i]));
          while ((line = br.readLine()) != null) {
            fileLen += line.getBytes().length;
            nline++;

            if (fileLen >= cnt * blksize) {
              FileSplit fs = new FileSplit(files[i].getAbsolutePath(), start, nline);
              splitFiles.add(fs);

              nline = 0;
              start = fileLen;
              cnt++;
            }
          }

          // the remaining part of a file
          if (start > fileLen) {
            FileSplit fs = new FileSplit(files[i].getAbsolutePath(), start, nline);
            splitFiles.add(fs);
          }
        }
      } else {
        int nline = 0;
        br = new BufferedReader(new FileReader(inFile));
        while ((line = br.readLine()) != null) {
          fileLen += line.getBytes().length;
          nline++;

          if (fileLen >= cnt * blksize) {
            FileSplit fs = new FileSplit(inFile.getAbsolutePath(), start, nline);
            splitFiles.add(fs);

            nline = 0;
            start = fileLen;
            cnt++;
          }
        }

        // the remaining part of a file
        if (start < fileLen) {
          FileSplit fs = new FileSplit(inFile.getAbsolutePath(), start, nline);
          splitFiles.add(fs);
        }
      }
    } finally {
      br.close();
    }

    return splitFiles;
  }

}
