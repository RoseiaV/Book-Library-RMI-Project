package client.librarian.model;

import common.Role;
import shared.ClientInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.RemoteException;
import java.util.List;

public class ManageRoleModel {
    protected static ClientInterface serverInterface;
    public ManageRoleModel(ClientInterface serverInterface){
        ManageRoleModel.serverInterface = serverInterface;
    }

    public void addRole(Role role){
        try {
            serverInterface.addRole(role);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    // Update
    public void updateRole(String roleName, Role updatedRole) throws RemoteException {
        try {
            serverInterface.updateRole(roleName, updatedRole);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //delete
    public void deleteRole(String roleName){//added this - Don
        try {
            serverInterface.deleteRole(roleName);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void populateRoleTable(JTable table){
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        String [] col = {"#", "Role", "Date Created", "Date Updated", "Status"};
        for (String colm : col){
            tableModel.addColumn(colm);
        }

        String status = "";
        int number = 0;
        for (Role role : getRoleOfList()){
            if (role.getStatus()){
                status = "Active";
            } else if (!role.getStatus()) {
                status = "Inactive";
            }
            number++;
            tableModel.addRow(new Object[] {number, role.getRole(), role.getDateCreated(), role.getDateUpdated(), status});
        }
    }
    public void setStatusActivityComboBox(JComboBox<String> comboBox){
        DefaultComboBoxModel<String> defaultComboBoxModel = (DefaultComboBoxModel<String>) comboBox.getModel();
        defaultComboBoxModel.addElement("Active");
        defaultComboBoxModel.addElement("Inactive");
    }

    public static List<Role> getRoleOfList(){
        try {
            return serverInterface.getRoleData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Role getRole(String roleName) {
        for (Role role : getRoleOfList()) {
            if (role.getRole().equals(roleName)) {
                return role;
            }
        }
        return null; // Role not found
    }

}
