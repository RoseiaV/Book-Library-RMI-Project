package client.librarian.controller;

import client.librarian.model.AdminFrameModel;
import client.librarian.model.ManageCategoryModel;
import client.librarian.view.ManageCategoryPanel;
import common.Author;
import common.Category;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

public class ManageCategoryController {
    protected ManageCategoryPanel categoryPanel;
    protected ManageCategoryModel categoryModel;
    public ManageCategoryController(ManageCategoryPanel categoryPanel, ManageCategoryModel categoryModel){
        this.categoryPanel = categoryPanel;
        this.categoryModel = categoryModel;
        manageCategoryController();
        addMouseListener(); //added this
    }

    private void addMouseListener() {
        categoryPanel.getCategoryTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedRow = categoryPanel.getCategoryTable().rowAtPoint(e.getPoint());
                    if (selectedRow!= -1) {
                        String categoryName = categoryPanel.getCategoryTable().getModel().getValueAt(selectedRow, 1).toString();
                        categoryPanel.setCategoryTextField(categoryName);
                    }
                }
            }
        });
    }

    public void manageCategoryController(){
        this.categoryPanel.setCategoryUpdateButtonListener(e -> {
            int selectedRow = categoryPanel.getCategoryTable().getSelectedRow();
            if (selectedRow != -1) {
                String categoryName = categoryPanel.getCategoryTable().getModel().getValueAt(selectedRow, 1).toString();

                // Get the original category information from the model
                Category originalCategory = categoryModel.getCategory(categoryName);
                if (originalCategory != null) {
                    // Get the updated category information from the view
                    String updatedCategoryName = categoryPanel.getCategoryTextField();
                    boolean getStatus = categoryPanel.getStatusComboBox().getSelectedItem().equals("Active");

                    // Preserve the original dateCreated value
                    String dateCreated = originalCategory.getDateCreated();

                    // Create the updated category object
                    Category updatedCategory = new Category(
                            updatedCategoryName,
                            dateCreated, // Preserve the original dateCreated value
                            AdminFrameModel.getDateToday(), // Update the dateModified field
                            getStatus
                    );

                    // Update the category's information in the model
                    try {
                        categoryModel.updateCategory(categoryName, updatedCategory);
                        JOptionPane.showMessageDialog(null, "Category has been Updated");
                        categoryModel.populateCategoryTable(categoryPanel.getCategoryTable());
                        categoryPanel.setCategoryTextField("");
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Category not found");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a category to update");
            }

            categoryModel.populateCategoryTable(categoryPanel.getCategoryTable());

            categoryPanel.setCategoryTextField("");
        });


        this.categoryPanel.setCategoryDeleteButtonListener(e -> {
            String categoryName = categoryPanel.getCategoryTextField();
            if (categoryName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select a category to delete.");
            } else {
                // Confirm deletion with the user
                int confirmDialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this category?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirmDialogResult == JOptionPane.YES_OPTION) {
                    // Delete the category
                    categoryModel.deleteCategory(categoryName);

                    // Update the table view
                    categoryModel.populateCategoryTable(categoryPanel.getCategoryTable());

                    // Clear the text field
                    categoryPanel.setCategoryTextField("");
                    JOptionPane.showMessageDialog(null, "Category has been deleted successfully.");
                }
            }
        });

        this.categoryPanel.setCategoryCreateButtonListener( e -> {

            boolean getCategoryStatus = categoryPanel.getStatusComboBox().getSelectedItem().equals("Active");
            if(categoryPanel.getCategoryTextField().isEmpty()){
                JOptionPane.showMessageDialog(null, "You cannot add a Category with an Empty Field");
            } else {
                Category category = new Category(
                        categoryPanel.getCategoryTextField(),
                        AdminFrameModel.getDateToday(),
                        "",
                        getCategoryStatus
                );

                JOptionPane.showMessageDialog(null, "Category has been Added");
                categoryModel.addCategory(category);
            }
            //Update the ComboBox
            categoryModel.populateCategoryTable(categoryPanel.getCategoryTable());

            //Clears the textField
            categoryPanel.setCategoryTextField("");
        });
    }
}
