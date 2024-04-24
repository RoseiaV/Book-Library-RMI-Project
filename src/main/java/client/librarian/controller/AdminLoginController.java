package client.librarian.controller;

import client.librarian.model.AdminLogInModel;
import client.librarian.view.AdminFrame;
import client.librarian.view.AdminLogIn;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class AdminLoginController {
    protected  AdminLogIn adminLogIn;
    protected AdminFrame adminFrame;
    protected AdminLogInModel adminLogInModel;
    public AdminLoginController(AdminLogIn adminLogIn, AdminFrame adminFrame, AdminLogInModel adminLogInModel){
        this.adminLogIn = adminLogIn;
        this.adminFrame = adminFrame;
        this.adminLogInModel = adminLogInModel;

        adminLogInController();
    }
    public void adminLogInController(){
        this.adminLogIn.setLogInButtonListener( e -> {
            if (adminLogIn.getUsernameField().isEmpty() || adminLogIn.getPasswordField().isEmpty()){
                JOptionPane.showMessageDialog(null, "Please fill out the Fields");
            } else {
                try {
                    adminLogInModel.logInAuth(adminLogIn, adminFrame, adminLogIn.getUsernameField(), adminLogIn.getPasswordField(), adminLogIn.getServerIpTextField());
                } catch (RemoteException | NotBoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
            //Clears the Field
            adminLogIn.setUsernameField("");
            adminLogIn.setPasswordField("");
            adminLogIn.setServerIpTextField("");
        });
    }
}
