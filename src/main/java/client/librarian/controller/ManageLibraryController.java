package client.librarian.controller;

import client.librarian.model.AdminFrameModel;
import client.librarian.model.ManageLibraryModel;
import client.librarian.view.ManageLibraryPanel;
import common.Author;
import common.Books;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.rmi.RemoteException;

public class ManageLibraryController {
    protected ManageLibraryPanel libraryPanel;
    protected ManageLibraryModel libraryModel;
    protected String path = "";
    protected String file = "";

    public ManageLibraryController(ManageLibraryPanel libraryPanel, ManageLibraryModel libraryModel) {
        this.libraryPanel = libraryPanel;
        this.libraryModel = libraryModel;
        manageLibraryController();
        addMouseListener();
    }

        private void addMouseListener() {
        libraryPanel.getBookTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedRow = libraryPanel.getBookTable().rowAtPoint(e.getPoint());
                    if (selectedRow != -1) {
                        String isbn = libraryPanel.getBookTable().getModel().getValueAt(selectedRow, 0).toString();
                        String bookName = libraryPanel.getBookTable().getModel().getValueAt(selectedRow, 1).toString();
                        String author = libraryPanel.getBookTable().getModel().getValueAt(selectedRow, 2).toString();
                        String category = libraryPanel.getBookTable().getModel().getValueAt(selectedRow, 3).toString();
                        String language = libraryPanel.getBookTable().getModel().getValueAt(selectedRow, 4).toString();
                        String description = libraryPanel.getBookTable().getModel().getValueAt(selectedRow, 6).toString();
//                        boolean status = (boolean) libraryPanel.getBookTable().getModel().getValueAt(selectedRow, 7);

                        // Set values to text fields
                        libraryPanel.setIsbnTextfield(isbn);
                        libraryPanel.setBookNameTextField(bookName);
                        libraryPanel.setDescriptionTextArea(description);

                        // Set selected values to combo boxes
                        setComboBoxSelectedItem(libraryPanel.getAuthorComboBox(), author);
                        setComboBoxSelectedItem(libraryPanel.getCategoryComboBox(), category);
                        setComboBoxSelectedItem(libraryPanel.getLanguageComboBox(), language);
//                        setStatusComboBoxSelectedItem(libraryPanel.getStatusComboBox(), status);


                        String fileName = "";
                       // Iterator<Books> booksIterator = ManageLibraryModel.getListOfBooks().listIterator();
                        for (Books book : ManageLibraryModel.getListOfBooks()){
                            if (book.getISBN().equals(isbn) && book.getBookName().equals(bookName)){
                                fileName = book.getFileName();
                            }
                        }

                        try {
                            ByteArrayInputStream bis = new ByteArrayInputStream(ManageLibraryModel.getImage(fileName));
                            BufferedImage originalImage = ImageIO.read(bis);
                            Image resizedImage = originalImage.getScaledInstance(libraryPanel.getImageLabel().getWidth(), libraryPanel.getImageLabel().getHeight(), Image.SCALE_SMOOTH);
                            libraryPanel.getImageLabel().setIcon(new ImageIcon(resizedImage));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });
    }

    // Helper method to set selected item in combo box
    private void setComboBoxSelectedItem(JComboBox comboBox, String selectedItem) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).toString().equals(selectedItem)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void setStatusComboBoxSelectedItem(JComboBox comboBox, boolean status) {
        if (status) {
            comboBox.setSelectedItem("Available");
        } else {
            comboBox.setSelectedItem("Not Available");
        }
    }

    public void manageLibraryController() {
        this.libraryPanel.setCreateButtonListener(e -> {
            System.out.println("Create Button");
            boolean getBookStatus = libraryPanel.getStatusComboBox().getSelectedItem().equals("Available");
            if (libraryPanel.getIsbnTextfield().isEmpty() || libraryPanel.getBookNameTextField().isEmpty() || libraryPanel.getDescriptionTextArea().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please Fill out the Form");
            } else {
                Books books = new Books(
                        file,
                        libraryPanel.getIsbnTextfield(),
                        libraryPanel.getBookNameTextField(),
                        (String) libraryPanel.getAuthorComboBox().getSelectedItem(),
                        (String) libraryPanel.getCategoryComboBox().getSelectedItem(),
                        (String) libraryPanel.getLanguageComboBox().getSelectedItem(),
                        AdminFrameModel.getDateToday(),
                        libraryPanel.getDescriptionTextArea(),
                        getBookStatus
                );

                //Sends the Image
                File file = new File(path);
                byte[] imageData = new byte[(int) file.length()];
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                    fileInputStream.read(imageData);
                    fileInputStream.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                libraryModel.addBook(books, imageData);
                JOptionPane.showMessageDialog(null, "Book has been Added");
            }

            //PopulateTable
            libraryModel.populateLibraryTable(libraryPanel.getBookTable());

            //Clears the text from the Text Field
            libraryPanel.setIsbnTextfield("");
            libraryPanel.setBookNameTextField("");
            libraryPanel.setDescriptionTextArea("");
        });

        this.libraryPanel.setDeleteBookButtonListener(e -> {
            int selectedRow = libraryPanel.getBookTable().getSelectedRow();
            if (selectedRow != -1) {
                String isbn = libraryPanel.getBookTable().getModel().getValueAt(selectedRow, 0).toString();
                // Perform delete operation using isbn
                libraryModel.deleteBook(isbn);
                //PopulateTable
                libraryModel.populateLibraryTable(libraryPanel.getBookTable());
                JOptionPane.showMessageDialog(null, "Book has been Deleted");
            } else {
                JOptionPane.showMessageDialog(null, "Please select a book to delete");
            }
        });

        this.libraryPanel.setUpdateButtonListener(e -> {
            int selectedRow = libraryPanel.getBookTable().getSelectedRow();
            if (selectedRow != -1) {
                String isbn = libraryPanel.getBookTable().getModel().getValueAt(selectedRow, 0).toString();
                // You can retrieve the updated information from the text fields and combo boxes and perform the update operation
                // For example:
                String updatedBookName = libraryPanel.getBookNameTextField();
                String updatedAuthor = (String) libraryPanel.getAuthorComboBox().getSelectedItem(); // For Comboboxes
                String updatedDescription = libraryPanel.getDescriptionTextArea();
                String updatedCategory = (String) libraryPanel.getCategoryComboBox().getSelectedItem();
                String updatedLanguage = (String) libraryPanel.getLanguageComboBox().getSelectedItem();
                boolean updatedStatus = libraryPanel.getStatusComboBox().getSelectedItem().toString().equals("Available");
                // Retrieve other updated information as needed

                // Perform update operation using isbn and updated information
                libraryModel.updateBook(isbn, updatedBookName, updatedAuthor, updatedCategory, updatedLanguage, updatedDescription);
                //PopulateTable
                libraryModel.populateLibraryTable(libraryPanel.getBookTable());
                JOptionPane.showMessageDialog(null, "Book has been Updated");
            } else {
                JOptionPane.showMessageDialog(null, "Please select a book to update");
            }
        });

        this.libraryPanel.setFileButtonListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("src/main/java/admin/res"));
            int response = fileChooser.showOpenDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                System.out.println("Path : " + fileChooser.getSelectedFile().getAbsolutePath());
                System.out.println("File : " + fileChooser.getSelectedFile().getName());

                path = fileChooser.getSelectedFile().getAbsolutePath();
                file = fileChooser.getSelectedFile().getName();
                libraryPanel.setUpdateImage(new File(fileChooser.getSelectedFile().getAbsolutePath()));
                System.out.println("sent");
            }
        });
        this.libraryPanel.setDiselectButtonListener(e -> {

            libraryPanel.imagePanel.clearImage();

            path = "";
            file = "";

        });
    }
}
