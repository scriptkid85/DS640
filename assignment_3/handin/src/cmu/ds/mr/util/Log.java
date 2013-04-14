package cmu.ds.mr.util;


/**
 * Log is used to debug
 * 
 * @author Guanyu Wang 
 ** */
public class Log {

  private boolean Debug = false;
  private boolean Info = true;
  private boolean Warn = false;


  private boolean Error = true;
  
  private String scope;
  
  public Log(String scope){
    this.scope = scope;
  }
  
  
  public void info(String s){
    if(Info){
      System.out.println(scope + ":" + s);
    }
  }
  
  public void warn(String s){
    if(Warn){
      System.out.println(scope + ":" + s);
    }
  }
  
  public void error(String s){
    if(Error){
      System.out.println(scope + ":" +s);
    }
  }
  
  public void debug(String s){
    if(Debug){
      System.out.println(scope + ":" +s);
    }
  }
  
  
  public boolean isDebug() {
    return Debug;
  }

  public void setDebug(boolean debug) {
    Debug = debug;
  }

  public boolean isInfo() {
    return Info;
  }

  public void setInfo(boolean info) {
    Info = info;
  }

  public boolean isError() {
    return Error;
  }

  public void setError(boolean error) {
    Error = error;
  }
  
  
  public boolean isWarn() {
    return Warn;
  }


  public void setWarn(boolean warn) {
    Warn = warn;
  }
}
