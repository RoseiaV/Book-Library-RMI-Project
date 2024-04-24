package client.librarian.model;

import common.Accounts;
import common.Role;
import shared.ClientInterface;
import client.librarian.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.RemoteException;
import java.util.List;

public class ManageAccountModel {
    protected static ClientInterface serverInterface;
    public ManageAccountModel(ClientInterface serverInterface){
        ManageAccountModel.serverInterface = serverInterface;
    }
    public void addAccount(Accounts accounts){
        try {
            serverInterface.addAccount(accounts);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //update
    public void updateAccount(String id, String username, String password, String role, boolean updatedStatus) {
        try {
            serverInterface.updateAccount(id, username, password, role, updatedStatus);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAccount(int selectedRow) {
        try {
            serverInterface.deleteAccount(selectedRow);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void populateAccountTable(JTable table){
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        String [] col = {"#", "Username", "Date Created", "Date Updated", "Status"};
        for (String colm : col){
            tableModel.addColumn(colm);
        }

        String status = "";
        int i = 0;
        for (Accounts row : getListOfAccounts()) {
            if (row.getStatus()){
                status = "Active";
            } else if (!row.getStatus()) {
                status = "Inactive";
            }
            i++;
            tableModel.addRow(new Object[]{
                    i,
                    row.getUsername(),
                    AdminFrameModel.getDateToday(),
                    "",
                    status
            });
        }
    }

    public void populateRoleComboBox(JComboBox<String> comboBox){
        DefaultComboBoxModel<String> comboBoxModel = (DefaultComboBoxModel<String> ) comboBox.getModel();
        comboBoxModel.removeAllElements();
        for (int i = 0; i < getListOfRole().size(); i++){
            String item = getListOfRole().get(i).getRole();
            comboBoxModel.addElement(item);
        }
    }
    public void setStatusActivityComboBox(JComboBox<String> comboBox){
        DefaultComboBoxModel<String> defaultComboBoxModel = (DefaultComboBoxModel<String>) comboBox.getModel();
        defaultComboBoxModel.addElement("Active");
        defaultComboBoxModel.addElement("Inactive");
    }
    public static List<Accounts> getListOfAccounts(){
        try {
            return serverInterface.getAccountData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Role> getListOfRole(){
        try {
            return serverInterface.getRoleData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
