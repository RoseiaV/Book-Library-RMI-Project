package server.model;


import server.view.ServerMainGUI;

import javax.swing.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class ServerMainGUIModel {
    private ServerMainGUI serverMainGUI;
    protected AdminImplication adminImplication;
    public ServerMainGUIModel(){
        this.serverMainGUI = serverMainGUI;
        initComponents();
    }


    private void initComponents() {
        statusTextField = new JTextField();
    }
    private JTextField statusTextField;
    public void disposeFrame(JFrame frame){
        frame.dispose();
    }

    public void startService(){
        try {
            adminImplication = new AdminImplication();
            adminImplication.startServer();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopService(){

    }
    public void updateStatusTextField(String ipAddress) {
        JTextField statusTextField = serverMainGUI.getStatusTextField();
        if (statusTextField != null) {
            statusTextField.setText(ipAddress);
        }
    }
}


