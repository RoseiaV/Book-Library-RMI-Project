package client.librarian.controller;

import client.librarian.model.AdminFrameModel;
import client.librarian.model.ManageRoleModel;
import client.librarian.view.ManageRolePanel;
import common.Role;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

public class ManageRoleController {
    protected ManageRoleModel roleModel;
    protected ManageRolePanel rolePanel;

    public ManageRoleController(ManageRolePanel rolePanel,ManageRoleModel roleModel){
        this.rolePanel = rolePanel;
        this.roleModel = roleModel;
        manageRoleController();
        addMouseListener();
    }

    private void addMouseListener() {
        rolePanel.getRoleTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedRow = rolePanel.getRoleTable().rowAtPoint(e.getPoint());
                    if (selectedRow!= -1) {
                        String roleName = rolePanel.getRoleTable().getModel().getValueAt(selectedRow, 1).toString();
                        rolePanel.setRoleTextField(roleName);
                    }
                }
            }
        });
    }

    public void manageRoleController(){
        this.rolePanel.setRoleUpdateButtonListener( e -> {
            int selectedRow = rolePanel.getRoleTable().getSelectedRow();
            if (selectedRow != -1) {
                String roleName = rolePanel.getRoleTable().getModel().getValueAt(selectedRow, 1).toString();

                // Get the original role information from the model
                Role originalRole = roleModel.getRole(roleName);
                if (originalRole != null) {
                    // Get the updated role information from the view
                    String updatedRoleName = rolePanel.getRoleTextField();
                    boolean getStatus = rolePanel.getStatusComboBox().getSelectedItem().equals("Active");

                    // Preserve the original dateCreated value
                    String dateCreated = originalRole.getDateCreated();

                    // Create the updated role object
                    Role updatedRole = new Role(
                            updatedRoleName,
                            dateCreated, // Preserve the original dateCreated value
                            AdminFrameModel.getDateToday(), // Update the dateModified field
                            getStatus
                    );

                    // Update the role's information in the model
                    try {
                        roleModel.updateRole(roleName, updatedRole);
                        JOptionPane.showMessageDialog(null, "Role has been Updated");
                        roleModel.populateRoleTable(rolePanel.getRoleTable());
                        rolePanel.setRoleTextField("");
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Role not found");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a role to update");
            }

            roleModel.populateRoleTable(rolePanel.getRoleTable());

            rolePanel.setRoleTextField("");
        });
        this.rolePanel.setRoleDeleteButtonListener(e -> { //added this - Don
            int selectedRow = rolePanel.getRoleTable().getSelectedRow();
            if (selectedRow != -1) {
                String roleName = rolePanel.getRoleTable().getModel().getValueAt(selectedRow, 1).toString();
                int confirmDialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this category?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirmDialogResult == JOptionPane.YES_OPTION)
                    roleModel.deleteRole(roleName);
                JOptionPane.showMessageDialog(null, "Role has been Deleted");
                roleModel.populateRoleTable(rolePanel.getRoleTable());
                rolePanel.setRoleTextField("");
            } else {
                JOptionPane.showMessageDialog(null, "Please select a role to delete");
            }
            rolePanel.setRoleTextField("");
        });

        this.rolePanel.setRoleCreateButtonListener( e -> {

            boolean getRoleStatus = rolePanel.getStatusComboBox().getSelectedItem().equals("Active");
            if (rolePanel.getRoleTextField().isEmpty()){
                JOptionPane.showMessageDialog(null, "Please Fill the Role Field before adding it");
            } else {

                Role role = new Role(
                        rolePanel.getRoleTextField(),
                        AdminFrameModel.getDateToday(),
                        "",
                        getRoleStatus
                );

                roleModel.addRole(role);
                JOptionPane.showMessageDialog(null, "Role has been Created");
            }

            roleModel.populateRoleTable(rolePanel.getRoleTable());

            rolePanel.setRoleTextField("");
        });
    }
}
