package cmu.ds.mr.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.TreeMap;

/**
 * Read records from each lines in FileSplit file Read records as input to feed Mapper
 * 
 * @author Zeyuan Li
 * */
public class LineRecordReader {

  /**
   * Read records as input to feed Mapper. Records sorted.
   * 
   * @return records<posInBytes, lineStr>
   * */
  public Map<Long, String> readAllRecordInFile(FileSplit file) throws IOException {
    Map<Long, String> res = new TreeMap<Long, String>();
    RandomAccessFile raf = null;

    try {
      raf = new RandomAccessFile(file.getPath(), "r");
      raf.seek(file.getStart());
      long len = file.getLen(); // # of lines
      long pos = 0l;

      while (len-- > 0) {
        String line = raf.readLine();
        res.put(pos, line);
        pos += line.getBytes().length;
      }

      assert pos == file.getStart() + file.getLen() : file.getPath() + " " + file.getStart() + " "
              + file.getLen();
    } finally {
      raf.close();
    }

    return res;
  }
}
