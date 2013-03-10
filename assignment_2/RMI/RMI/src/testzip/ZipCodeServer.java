package testzip;

import java.rmi.RemoteException;

import ror.Remote440;

public interface ZipCodeServer extends Remote440// extends YourRemote or whatever
{
    public void initialise(ZipCodeList newlist) throws RemoteException;
    public String find(String city) throws RemoteException;
    public ZipCodeList findAll() throws RemoteException;
    public void printAll() throws RemoteException;
}

