package client.librarian.controller;

import client.librarian.model.AdminFrameModel;
import client.librarian.model.ManageLanguageModel;
import client.librarian.view.ManageLanguagePanel;
import common.Author;
import common.Language;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

public class ManageLanguageController {
    protected ManageLanguagePanel languagePanel;
    protected ManageLanguageModel languageModel;
    public ManageLanguageController(ManageLanguagePanel languagePanel, ManageLanguageModel languageModel){
        this.languagePanel = languagePanel;
        this.languageModel = languageModel;
        manageLanguageController();
        addMouseListener(); //added this
    }

    private void addMouseListener() {
        languagePanel.getLanguageTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedRow = languagePanel.getLanguageTable().rowAtPoint(e.getPoint());
                    if (selectedRow!= -1) {
                        String languageName = languagePanel.getLanguageTable().getModel().getValueAt(selectedRow, 1).toString();
                        languagePanel.setLanguageTextField(languageName);
                    }
                }
            }
        });
    }

    public void manageLanguageController(){
        this.languagePanel.setLanguageUpdateButton( e -> {
            int selectedRow = languagePanel.getLanguageTable().getSelectedRow();
            if (selectedRow != -1) {
                String languageName = languagePanel.getLanguageTable().getModel().getValueAt(selectedRow, 1).toString();

                // Get the original language information from the model
                Language originalLanguage = languageModel.getLanguage(languageName);
                if (originalLanguage != null) {
                    // Get the updated language information from the view
                    String updatedLanguageName = languagePanel.getLanguageTextField();
                    boolean getStatus = languagePanel.getStatusComboBox().getSelectedItem().equals("Active");

                    // Preserve the original dateCreated value
                    String dateCreated = originalLanguage.getDateCreated();

                    // Create the updated language object
                    Language updatedLanguage = new Language(
                            updatedLanguageName,
                            dateCreated, // Preserve the original dateCreated value
                            AdminFrameModel.getDateToday(), // Update the dateModified field
                            getStatus
                    );

                    // Update the language's information in the model
                    try {
                        languageModel.updateLanguage(languageName, updatedLanguage);
                        JOptionPane.showMessageDialog(null, "Language has been Updated");
                        languageModel.populateLanguageTable(languagePanel.getLanguageTable());
                        languagePanel.setLanguageTextField("");
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Language not found");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a language to update");
            }

            languageModel.populateLanguageTable(languagePanel.getLanguageTable());

            languagePanel.setLanguageTextField("");
        });

        this.languagePanel.setLanguageDeleteButton(e -> {
            int selectedRow = languagePanel.getLanguageTable().getSelectedRow();
            if (selectedRow != -1) {
                String languageName = languagePanel.getLanguageTable().getModel().getValueAt(selectedRow, 1).toString();
                int confirmDialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this language?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirmDialogResult == JOptionPane.YES_OPTION) {
                    languageModel.deleteLanguage(languageName);
                    JOptionPane.showMessageDialog(null, "Language has been Deleted");
                    languageModel.populateLanguageTable(languagePanel.getLanguageTable());
                    languagePanel.setLanguageTextField("");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a language to delete");
            }
            languagePanel.setLanguageTextField("");
        });


        this.languagePanel.setCreateCreateButton( e -> {
            boolean getLanguageStatus = languagePanel.getStatusComboBox().getSelectedItem().equals("Active");
            if(languagePanel.getLanguageTextField().isEmpty()){
                JOptionPane.showMessageDialog(null, "You cannot add A Language with an Empty Field");
            } else {
                Language language = new Language(
                        languagePanel.getLanguageTextField(),
                        AdminFrameModel.getDateToday(),
                        "",
                        getLanguageStatus
                );
                JOptionPane.showMessageDialog(null, "Language has been Added");
                languageModel.addLanguage(language);
            }

            languageModel.populateLanguageTable(languagePanel.getLanguageTable());

            //Clears the textField
            languagePanel.setLanguageTextField("");
        });
    }
}
