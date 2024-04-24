package client.librarian.model;

import common.Admin;
import shared.ClientInterface;

import javax.swing.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminFrameModel extends UnicastRemoteObject implements Serializable {

    private ClientInterface clientInterface;
    public AdminFrameModel(ClientInterface clientInterface) throws RemoteException {
        this.clientInterface = clientInterface;
    }

    public void switchPanel(JPanel bodyPanel, JPanel switchedPanel){
        bodyPanel.removeAll();
        bodyPanel.add(switchedPanel);
        bodyPanel.repaint();
        bodyPanel.revalidate();
    }

    public void setAvailabilityComboBox(JComboBox<String> comboBox){
        DefaultComboBoxModel<String> defaultComboBoxModel = (DefaultComboBoxModel<String>) comboBox.getModel();
        defaultComboBoxModel.addElement("Available");
        defaultComboBoxModel.addElement("Not Available");
    }

    public static String getDateToday(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDateTime.format(dateTimeFormatter);
    }

    public void logOut(Admin admin){
        try {
            clientInterface.adminLogOut(admin);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
