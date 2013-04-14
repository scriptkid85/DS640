package cmu.ds.mr.io;

import java.io.Serializable;

/**
 * FileSplit: the class to represent split files Assume the input file format is fixed to lines of
 * texts. Input and output are all String
 * 
 * @author Zeyuan Li
 * */
public class FileSplit implements Serializable {
  private String path;
  private long start; // start position, in bytes
  private long len; // len == # of lines
  
  public FileSplit(String path, long start, long len) {
    super();
    this.path = path;
    this.start = start;
    this.len = len;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public long getStart() {
    return start;
  }

  public void setStart(long start) {
    this.start = start;
  }

  public long getLen() {
    return len;
  }

  public void setLen(long len) {
    this.len = len;
  }
  
  
}
