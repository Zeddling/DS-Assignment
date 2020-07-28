package com.rmi;

import com.dao.h2.ToyDatabase;

import java.rmi.RemoteException;
import java.util.List;

public class ServerRMIHandler implements ServerRMI {

    @Override
    public List<String> showAllRecords() throws RemoteException {
        ToyDatabase database = new ToyDatabase();
        List<String> list = database.showAll();
        return list;
    }
}
