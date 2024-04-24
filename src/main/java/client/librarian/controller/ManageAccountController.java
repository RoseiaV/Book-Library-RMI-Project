package client.librarian.controller;

import client.librarian.model.ManageAccountModel;
import client.librarian.view.ManageAccountPanel;
import common.Accounts;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ManageAccountController {
    protected ManageAccountPanel accountPanel;
    protected ManageAccountModel accountModel;

    public ManageAccountController(ManageAccountPanel accountPanel, ManageAccountModel accountModel) {
        this.accountPanel = accountPanel;
        this.accountModel = accountModel;
        manageAccountController();
        addMouseListener();
    }

    private void addMouseListener() {
        accountPanel.getAccountTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedRow = accountPanel.getAccountTable().rowAtPoint(e.getPoint());
                    if (selectedRow != -1) {
                        String username = accountPanel.getAccountTable().getModel().getValueAt(selectedRow,1).toString();
                        // Set values to text fields
                        accountPanel.setUsernameField(username);
                    }
                }
            }
        });
    }



    public void manageAccountController(){
        this.accountPanel.setUpdateButtonListener(e -> {
            int selectedRow = accountPanel.getAccountTable().getSelectedRow();
            if (selectedRow != -1) {
                String id = accountPanel.getIdField();
                String name = accountPanel.getNameField();
                String username = accountPanel.getUsernameField();
                String password = accountPanel.getPasswordField();
                String role = (String) accountPanel.getRoleComboBox().getSelectedItem();
                boolean updatedStatus = accountPanel.getStatusComboBox().getSelectedItem().equals("Active");

                // Update account in the model
                accountModel.updateAccount(id, username, password, role, updatedStatus);
                JOptionPane.showMessageDialog(null, "Account has been updated");

                // Update table
                accountModel.populateAccountTable(accountPanel.getAccountTable());

            } else {
                JOptionPane.showMessageDialog(null, "Please select an account to update");
            }
        });


        this.accountPanel.setDeleteButtonListener( e -> {
            int selectedRow = accountPanel.getAccountTable().getSelectedRow();
            if (selectedRow != -1) {
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this account?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    // Delete account from the model
                    accountModel.deleteAccount(selectedRow);
                    JOptionPane.showMessageDialog(null, "Account has been deleted");

                    // Update table
                    accountModel.populateAccountTable(accountPanel.getAccountTable());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select an account to delete");
            }
        });

        this.accountPanel.setCreateButtonListener( e -> {
            boolean selectedStatus = accountPanel.getStatusComboBox().getSelectedItem().equals("Active");

            if (accountPanel.getIdField().isEmpty() || accountPanel.getNameField().isEmpty() || accountPanel.getUsernameField().isEmpty() || accountPanel.getPasswordField().isEmpty()){
                JOptionPane.showMessageDialog(null, "You cannot create an account with missing information on it.");
            } else {
                // Create Accounts object
                Accounts accounts = new Accounts(
                        "defaultPicture",
                        accountPanel.getIdField(),
                        accountPanel.getNameField(),
                        accountPanel.getUsernameField(),
                        accountPanel.getPasswordField(),
                        "",
                        "",
                        (String) accountPanel.getRoleComboBox().getSelectedItem(),
                        selectedStatus
                );
                // Add account to model
                accountModel.addAccount(accounts);
                JOptionPane.showMessageDialog(null, "Account has been Created");
            }

            //Updates the table
            accountModel.populateAccountTable(accountPanel.getAccountTable());

            //Clears the Field
            accountPanel.setIdField("");
            accountPanel.setNameField("");
            accountPanel.setUsernameField("");
            accountPanel.setPasswordField("");
        });
        this.accountPanel.setCreateButtonListener( e -> {

            boolean selectedStatus = accountPanel.getStatusComboBox().getSelectedItem().equals("Active");

            if (accountPanel.getIdField().isEmpty() || accountPanel.getNameField().isEmpty() || accountPanel.getUsernameField().isEmpty() || accountPanel.getPasswordField().isEmpty()){
                JOptionPane.showMessageDialog(null, "You cannot create an account with missing information on it.");
            } else {

                Accounts accounts = new Accounts(
                        "defaultPicture",
                        accountPanel.getIdField(),
                        accountPanel.getName(),
                        accountPanel.getUsernameField(),
                        accountPanel.getPasswordField(),
                        "",
                        "",
                        (String) accountPanel.getRoleComboBox().getSelectedItem(),
                        selectedStatus
                );

                accountModel.addAccount(accounts);
                JOptionPane.showMessageDialog(null, "Account has been Created");
            }

            //Updates the table
            accountModel.populateAccountTable(accountPanel.getAccountTable());

            //Clears the Field
            accountPanel.setIdField("");
            accountPanel.setNameField("");
            accountPanel.setUsernameField("");
            accountPanel.setPasswordField("");
        });
    }

}
