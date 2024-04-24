package client.librarian.controller;

import client.librarian.model.AdminFrameModel;
import client.librarian.model.AdminLogInModel;
import client.librarian.model.ManageAuthorModel;
import client.librarian.view.ManageAuthorPanel;
import common.Author;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

public class ManageAuthorController {
    protected ManageAuthorPanel authorPanel;
    protected ManageAuthorModel authorModel;
    public ManageAuthorController(ManageAuthorPanel authorPanel, ManageAuthorModel authorModel){
        this.authorPanel = authorPanel;
        this.authorModel = authorModel;
        manageAuthorController();
        addMouseListener(); //added this
    }
    private void addMouseListener() {
        authorPanel.getAuthorTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedRow = authorPanel.getAuthorTable().rowAtPoint(e.getPoint());
                    if (selectedRow!= -1) {
                        String authorName = authorPanel.getAuthorTable().getModel().getValueAt(selectedRow, 1).toString();
                        authorPanel.setAuthorTextField(authorName);
                    }
                }
            }
        });
    }
    public void manageAuthorController(){
        this.authorPanel.setAuthorUpdateButton(e -> {
            int selectedRow = authorPanel.getAuthorTable().getSelectedRow();
            if (selectedRow != -1) {
                String authorName = authorPanel.getAuthorTable().getModel().getValueAt(selectedRow, 1).toString();

                // Get the original author information from the model
                Author originalAuthor = authorModel.getAuthor(authorName);
                if (originalAuthor != null) {
                    // Get the updated author information from the view
                    String updatedAuthorName = authorPanel.getAuthorTextField();
                    boolean getStatus = authorPanel.getStatusComboBox().getSelectedItem().equals("Active");

                    // Preserve the original dateCreated value
                    String dateCreated = originalAuthor.getDateCreated();

                    // Create the updated author object
                    Author updatedAuthor = new Author(
                            updatedAuthorName,
                            dateCreated, // Preserve the original dateCreated value
                            AdminFrameModel.getDateToday(), // Update the dateModified field
                            getStatus
                    );

// Update the author's information in the model
                    try {
                        authorModel.updateAuthor(authorName, updatedAuthor);
                        JOptionPane.showMessageDialog(null, "Author has been Updated");
                        authorModel.populateAuthorTable(authorPanel.getAuthorTable());
                        authorPanel.setAuthorTextField("");
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Author not found");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select an author to update");
            }

            authorModel.populateAuthorTable(authorPanel.getAuthorTable());

            authorPanel.setAuthorTextField("");
        });

        this.authorPanel.setAuthorDeleteButton(e -> { //added this - Don
            int selectedRow = authorPanel.getAuthorTable().getSelectedRow();
            if (selectedRow != -1) {
                String authorName = authorPanel.getAuthorTable().getModel().getValueAt(selectedRow, 1).toString();
                int confirmDialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this category?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirmDialogResult == JOptionPane.YES_OPTION)
                    authorModel.deleteAuthor(authorName);
                JOptionPane.showMessageDialog(null, "Author has been Deleted");
                authorModel.populateAuthorTable(authorPanel.getAuthorTable());
                authorPanel.setAuthorTextField("");
            } else {
                JOptionPane.showMessageDialog(null, "Please select an author to delete");
            }
            authorPanel.setAuthorTextField("");
        });
        this.authorPanel.setAuthorCreateButton( e -> {

            boolean getStatus = authorPanel.getStatusComboBox().getSelectedItem().equals("Active");
            if (authorPanel.getAuthorTextField().isEmpty()){
                JOptionPane.showMessageDialog(null, "Please Fill the Author Field before you Add it");
            } else {
                Author author = new Author(
                        authorPanel.getAuthorTextField(),
                        AdminFrameModel.getDateToday(),
                        "",
                        getStatus
                );

                authorModel.addAuthor(author);
                JOptionPane.showMessageDialog(null, "Author has been Added");
            }

            authorModel.populateAuthorTable(authorPanel.getAuthorTable());

            authorPanel.setAuthorTextField("");
        });
    }
}
