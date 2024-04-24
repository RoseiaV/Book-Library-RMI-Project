package server;

import server.controller.ServerMainGUIController;
import server.model.ServerMainGUIModel;
import server.view.ServerMainGUI;

import java.rmi.RemoteException;

public class ServerApplication {
    public static void main(String[] args) throws RemoteException {

        ServerMainGUI serverMainGUI = new ServerMainGUI();
        serverMainGUI.setIpAddressToStatusTextField();
        ServerMainGUIModel serverMainGUIModel = new ServerMainGUIModel();
        ServerMainGUIController serverMainGUIController = new ServerMainGUIController(serverMainGUI, serverMainGUIModel);
    }
}
