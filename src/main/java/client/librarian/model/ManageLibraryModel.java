package client.librarian.model;

import client.librarian.view.ManageLibraryPanel;
import common.*;
import shared.ClientInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.rmi.RemoteException;
import java.util.List;

public class ManageLibraryModel {
    protected static ClientInterface serverInterface;
    public ManageLibraryModel(ClientInterface serverInterface){
        ManageLibraryModel.serverInterface = serverInterface;
    }

    public static byte[] getImage(String imageName){
        try {
            return serverInterface.getBookImage(imageName);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    public void addBook(Books books, byte [] imageData){
        try {
            serverInterface.addBook(books, imageData);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteBook(String isbn) {
        try {
            serverInterface.deleteBook(isbn);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateBook(String isbn, String updatedBookName, String updatedAuthor, String updatedCategory, String updatedLanguage, String updatedDescription) {
        try {
            serverInterface.updateBook(isbn, updatedBookName,updatedAuthor, updatedCategory, updatedLanguage, updatedDescription);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    public void populateLibraryTable(JTable table){
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        String [] col = {"Isbn", "Title", "Author", "Category", "Language", "Date Published", "Description", "Status"};
        for (String column : col){
            tableModel.addColumn(column);
        }

        String status = "";
        for (Books books : getListOfBooks()){
            if (books.getStatus()){
                status = "Available";
            } else if (!books.getStatus()) {
                status = "Not Available";
            }

            tableModel.addRow(new Object[] {
                    books.getISBN(),
                    books.getBookName(),
                    books.getAuthor(),
                    books.getCategory(),
                    books.getLanguage(),
                    books.getPublishedDate(),
                    books.getDescription(),
                    status
            });
        }
    }
    public void populateAuthorComboBox(JComboBox<String> comboBox){
        DefaultComboBoxModel<String> comboBoxModel = (DefaultComboBoxModel<String>) comboBox.getModel();
        comboBoxModel.removeAllElements();
        for (int i = 0; i < getListOFAuthor().size(); i++){
            String item = getListOFAuthor().get(i).getAuthor();
            comboBoxModel.addElement(item);
        }
    }
    public void populateCategoryComboBox(JComboBox<String> comboBox){
        DefaultComboBoxModel<String> comboBoxModel = (DefaultComboBoxModel<String>) comboBox.getModel();
        comboBoxModel.removeAllElements();
        for (int i = 0; i < getListOfCategory().size(); i++){
            String item = getListOfCategory().get(i).getCategory();
            comboBoxModel.addElement(item);
        }
    }
    public void populateLanguageComboBox(JComboBox<String> comboBox){
        DefaultComboBoxModel<String> comboBoxModel = (DefaultComboBoxModel<String>) comboBox.getModel();
        comboBoxModel.removeAllElements();
        for (int i = 0; i < getListOfLanguage().size(); i++){
            String item = getListOfLanguage().get(i).getLanguage();
            comboBoxModel.addElement(item);
        }
    }

    public static List<Books> getListOfBooks(){
        try {
            return serverInterface.getJsonData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Author> getListOFAuthor(){
        try {
            return serverInterface.getAuthorData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Category> getListOfCategory(){
        try {
            return serverInterface.getCategoryData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Language> getListOfLanguage(){
        try {
            return serverInterface.getLanguageData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    public void setUpdateImage(File file) {
        ManageLibraryPanel.ImagePanel imagePanel = new ManageLibraryPanel.ImagePanel();
        imagePanel.updateImage(file); // Call updateImage method of ImagePanel
    }
}
