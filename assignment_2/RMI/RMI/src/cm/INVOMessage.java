package cm;

import java.io.Serializable;

import ror.RemoteObjectRef;

public class INVOMessage implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  //type 1 : invoke msg
  //type 2 : return msg
  //type 3:  wrong msg (remote exception)
  private int type;
  private String method;
  private Object[] args;
  private String[] argtypes;
  private String returntype;
  
  private Object result;
  public RemoteObjectRef ror;
  
  public INVOMessage(RemoteObjectRef ror, String method, Object[] args, String[] argtypes, String returntype){
    this.type = 1;
    this.ror = ror;
    this.method = method;
    this.args = args;
    this.argtypes = argtypes;
    this.returntype = returntype;
  }
  
  public INVOMessage(Serializable result){
    this.type = 2;
    this.result = result;
  }
  
  public INVOMessage(){
    this.type = 3;
  }
  
  
  public RemoteObjectRef getror(){
    return this.ror;
  } 
  
  public int gettype(){
    return this.type;
  }
  
  public String getmethod(){
    return this.method;
  }
  
  public Object[] getargs(){
    return this.args;
  }
  
  public String[] getargtype(){
    return this.argtypes;
  }
  
  public String getreturntype(){
    return this.returntype;
  }
  
  
  
  public Object getresult(){
    return this.result;
  }  
  
  @Override
  public String toString() {
    if(args != null && returntype != null && ror != null)
      return String.format("type:%d\tmethod:%s\targs:%s\targtypes:%s\treturntype:%s\nror:%s\n", type, method, args[0], argtypes[0], returntype, ror.toString());
    else
      return String.format("type:%d\tmethod:%s\nror:%s\n", type, method, ror.toString());
  }
}
