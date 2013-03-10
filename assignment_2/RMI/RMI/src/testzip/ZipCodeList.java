package testzip;

import ror.Remote440;

public class ZipCodeList implements Remote440{
  String city;

  String ZipCode;

  ZipCodeList next;

  public ZipCodeList(String c, String z, ZipCodeList n) {
    city = c;
    ZipCode = z;
    next = n;
  }
}
