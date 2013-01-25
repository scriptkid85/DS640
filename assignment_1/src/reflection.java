import java.lang.reflect.*;

public class reflection {

  public static void main(String[] args) {
    
    Class c = "foo".getClass();
    System.out.println("c = " + c);

    Integer x = 4; // "int x" would not work because we need an object
    System.out.println("int class = " + (x.getClass()));

    boolean b;
    System.out.println("bool class=  " + boolean.class);

    try {
      Class<?> flower = Class.forName("Flower");
      Constructor[] a = flower.getConstructors();
      for (int i=0; i<a.length; i++)
        System.out.println("constructors: " + a[i]);
      Object o = flower.newInstance();
      //Object o = (flower.getConstructor(new Class[0])).newInstance(new Object[0]);
      Flower f = (Flower)o;
      f.numPetals = 5;
      System.out.println("Our flower has " + f.numPetals + " petals");
    } catch (Exception e) {
      System.out.println("problem!");
      e.printStackTrace();
    }
    // } catch (ClassNotFoundException cnfe) {
    //  System.out.println("Couldn't find class ``Flower''");
    // } catch (InstantiationException ie) {
    //  System.out.println("Couldn't instantiate class");
    // } catch (IllegalAccessException iae) {
    //  System.out.println("problem illegal access");
    // } catch (InvocationTargetException ite) {
    //  System.out.println("problem invocation target");
    // } catch (NoSuchMethodException nsme) {
    //  System.out.println("problem no such method name");
    // }


  }

}

class Flower {
  public int numPetals;
  public boolean isUltraviolet;

  public Flower() {
    numPetals = 0;
    isUltraviolet = false;
  }
}