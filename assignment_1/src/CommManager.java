
public class CommManager {
  private String model;
  
  public CommManager(String model) throws Exception{
    if(model.equals("Master") || model.equals("Slave"))
      this.model = model;
    else{
      System.out.println("Argument of CommManager can only be \"Master\" or \"Slave\"");
      throw new Exception("Invalid Arguments");
    }
  }
}
