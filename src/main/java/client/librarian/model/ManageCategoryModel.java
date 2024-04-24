package client.librarian.model;

import common.Category;
import shared.ClientInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.RemoteException;
import java.util.List;

public class ManageCategoryModel {
    protected static ClientInterface serverInterface;
    public ManageCategoryModel(ClientInterface serverInterface){
        ManageCategoryModel.serverInterface = serverInterface;
    }

    public void addCategory(Category category){
        try {
            serverInterface.addCategory(category);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //update
    public void updateCategory(String categoryName, Category updatedCategory) throws RemoteException {
        try {
            serverInterface.updateCategory(categoryName, updatedCategory);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //delete
    public void deleteCategory(String categoryName){//added this - Don
        try {
            serverInterface.deleteCategory(categoryName);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void populateCategoryTable(JTable table){
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        String [] col = {"#", "Language", "Date Created", "Date Updated", "Status"};
        for (String colm : col){
            tableModel.addColumn(colm);
        }

        String status = "";
        int number = 0;
        for (Category category : getCategoryList()){
            if (category.getStatus()){
                status = "Active";
            } else if (!category.getStatus()) {
                status = "Inactive";
            }
            number++;
            tableModel.addRow(new Object[] {number, category.getCategory(), category.getDateCreated(), category.getDateUpdated(), status});
        }
    }

    public void setStatusActivityComboBox(JComboBox<String> comboBox){
        DefaultComboBoxModel<String> defaultComboBoxModel = (DefaultComboBoxModel<String>) comboBox.getModel();
        defaultComboBoxModel.addElement("Active");
        defaultComboBoxModel.addElement("Inactive");
    }

    public static List<Category> getCategoryList(){
        try {
            return serverInterface.getCategoryData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Category getCategory(String categoryName) {
        for (Category category : getCategoryList()) {
            if (category.getCategory().equals(categoryName)) {
                return category;
            }
        }
        return null; // Category not found
    }

}
