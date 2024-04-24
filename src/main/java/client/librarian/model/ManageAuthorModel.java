package client.librarian.model;

import common.Author;
import shared.ClientInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.RemoteException;
import java.util.List;

public class ManageAuthorModel {
    protected static ClientInterface serverInterface;
    public ManageAuthorModel(ClientInterface serverInterface){
        ManageAuthorModel.serverInterface = serverInterface;
    }

    public void addAuthor(Author author){
        try {
            serverInterface.addAuthor(author);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //update
    public void updateAuthor(String authorName, Author updatedAuthor) throws RemoteException {
        try {
            serverInterface.updateAuthor(authorName, updatedAuthor);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void deleteAuthor(String authorName){//added this - Don
        try {
            serverInterface.deleteAuthor(authorName);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void populateAuthorTable(JTable table){
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        String [] col = {"#", "Author", "Date Created", "Date Updated", "Status"};
        for (String colm : col){
            tableModel.addColumn(colm);
        }
        String status = "";
        int number = 0;
        for (Author author : getListOfAuthor()){
            if (author.getStatus()){
                status = "Active";
            } else if (!author.getStatus()) {
                status = "Inactive";
            }
            number++;
            tableModel.addRow(new Object[] {number, author.getAuthor(), author.getDateCreated(), author.getDateUpdated(), status});
        }
    }

    public void setStatusActivityComboBox(JComboBox<String> comboBox){
        DefaultComboBoxModel<String> defaultComboBoxModel = (DefaultComboBoxModel<String>) comboBox.getModel();
        defaultComboBoxModel.addElement("Active");
        defaultComboBoxModel.addElement("Inactive");
    }

    public static List<Author> getListOfAuthor(){
        try {
            return serverInterface.getAuthorData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //the method to not overwrite the date
    public Author getAuthor(String authorName) {
        for (Author author : getListOfAuthor()) {
            if (author.getAuthor().equals(authorName)) {
                return author;
            }
        }
        return null; // Author not found
    }
}
