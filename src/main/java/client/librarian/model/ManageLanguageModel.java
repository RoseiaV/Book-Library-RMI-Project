package client.librarian.model;

import common.Language;
import shared.ClientInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ManageLanguageModel {
    protected static ClientInterface serverInterface;
    protected List<Language> languageList = new ArrayList<>();
    public ManageLanguageModel(ClientInterface serverInterface){
        ManageLanguageModel.serverInterface = serverInterface;
    }

    public void addLanguage(Language language) {
        try {
            serverInterface.addLanguage(language);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    //update
    public void updateLanguage(String languageName, Language updatedLanguage) throws RemoteException {
        try {
            serverInterface.updateLanguage(languageName, updatedLanguage);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //delete
    public void deleteLanguage(String languageName){//added this - Don
        try {
            serverInterface.deleteLanguage(languageName);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    public void populateLanguageTable(JTable table){
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        String [] col = {"#", "Language", "Date Created", "Date Updated", "Status"};
        for (String colm : col){
            tableModel.addColumn(colm);
        }

        String status = "";
        int number = 0;
        for (Language language : getLanguageList()){
            if (language.getStatus()){
                status = "Active";
            } else if (!language.getStatus()) {
                status = "Inactive";
            }
            number++;
            tableModel.addRow(new Object[] {number, language.getLanguage(), language.getDateCreated(), language.getDateUpdated(), status});
        }
    }
    public void setStatusActivityComboBox(JComboBox<String> comboBox){
        DefaultComboBoxModel<String> defaultComboBoxModel = (DefaultComboBoxModel<String>) comboBox.getModel();
        defaultComboBoxModel.addElement("Active");
        defaultComboBoxModel.addElement("Inactive");
    }

    public static List<Language> getLanguageList(){
        try {
            return serverInterface.getLanguageData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    // The method to not overwrite the date
    public Language getLanguage(String languageName) {
        for (Language language : getLanguageList()) {
            if (language.getLanguage().equals(languageName)) {
                return language;
            }
        }
        return null; // Language not found
    }

}
