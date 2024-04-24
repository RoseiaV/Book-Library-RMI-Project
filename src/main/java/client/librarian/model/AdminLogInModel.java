package client.librarian.model;

import client.librarian.controller.AdminLoginController;


import client.librarian.view.AdminFrame;
import common.Accounts;
import common.Admin;
import shared.ClientInterface;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class AdminLogInModel extends UnicastRemoteObject implements Serializable {
    public static ClientInterface clientInterface;
    public static Registry registry;
    public static String ip;
    public AdminLogInModel() throws RemoteException {}

    public void logInAuth(JFrame log, AdminFrame view, String userName, String userPass, String ip) throws RemoteException, NotBoundException{
        Admin admin = new Admin(userName, userPass);
        AdminLogInModel.ip = ip;
        registry = LocateRegistry.getRegistry(ip, 10000);
        clientInterface = (ClientInterface) registry.lookup("SERVER");

        try {
            if (clientInterface.adminLogInAuth(admin)){
                JOptionPane.showMessageDialog(null, "Welcome Administrator");
                log.dispose();
                view.setVisible(true);

                //Store the username & password on the labels temporarly
                view.setUserNameLabel(userName);
                view.setUserPassWordLabel(userPass);
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Password or Username");
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (HeadlessException e) {
            throw new RuntimeException(e);
        }
    }
    public static ClientInterface getClientInterface(){
        try {
            registry = LocateRegistry.getRegistry(ip, 10000);
            return clientInterface = (ClientInterface) registry.lookup("SERVER");
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


}
