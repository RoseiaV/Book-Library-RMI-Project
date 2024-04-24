package client.student.controller;

import client.student.model.dataAccessClass;
import client.student.view.*;
import client.student.view.booksUtil.*;
import common.Books;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class libraryController {

    private dataAccessClass model;
    private application application;
    private libraryPanel libraryPanel;
    private viewBook viewBook;
    private homeBooks homeBooks;
    public libraryController(){}

    public libraryController(application application, libraryPanel libraryPanel,viewBook viewBook, dataAccessClass model){
        this.application = application;
        this.libraryPanel = libraryPanel;
        this.viewBook = viewBook;
        this.model = model;

        model.populateCategoryComboBox(libraryPanel.getCategoryComboBox());
        model.populateLanguageComboBox(libraryPanel.getLanguageComboBox());
        populateBooks();
        setLibraryPanelController();
    }

    public void setLibraryPanelController(){
        //JComboBox Listener
        this.libraryPanel.setCategoryComboBoxListener( e -> {
            //System.out.println("Combo Box Category");
            String category = libraryPanel.getCategoryComboBox().getSelectedItem().toString();
            if (category.equals("All")){
                defaultFilter();
            } else {
                filterByCategory(category);
            }
        });
        this.libraryPanel.setLanguageComboBoxListener( e -> {
            //System.out.println("Combo Box Language");
            String language = libraryPanel.getLanguageComboBox().getSelectedItem().toString();
            if (language.equals("All")){
                defaultFilter();
            } else {
                filterByLanguage(language);
            }
        });
        this.libraryPanel.getSearchField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                String search = libraryPanel.getSearchField().getText();
                searchField(search);
            }
        });
        this.libraryPanel.setClearButtonListener(e -> {
            libraryPanel.getSearchField().setText("");
        });
    }

    public void populateBooks(){
        libraryPanel.getBooksPanel().removeAll();
        for (Books books : dataAccessClass.getBookList()){
            homeBooks = new homeBooks();
            homeBooks.getBooknameLabel().setText(books.getBookName());
            homeBooks.getAuthorLabel().setText(books.getAuthor());
            homeBooks.getCategoryLabel().setText(books.getCategory());
            homeBooks.getViewButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String Status = "";
                    if (books.getStatus()){
                        Status = "Available";
                        viewBook.getSendRequestButton().setEnabled(true);
                    } else {
                        Status = "Not Available";
                        viewBook.getSendRequestButton().setEnabled(false);
                    }

                    //Switch the Panel
                    model.switchPanel(application.getBodyPanel(), viewBook);

                    try {
                        ByteArrayInputStream bis = new ByteArrayInputStream(model.getBookImage(books.getFileName()));
                        BufferedImage originalImage = ImageIO.read(bis);
                        Image resizedImage = originalImage.getScaledInstance(viewBook.getBookImage().getWidth(), viewBook.getBookImage().getHeight(), Image.SCALE_SMOOTH);
                        viewBook.getBookImage().setIcon(new ImageIcon(resizedImage));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    viewBook.getBookISBN().setText(books.getISBN());
                    viewBook.getBookTitle().setText(books.getBookName());
                    viewBook.getAuthorName().setText(books.getAuthor());
                    viewBook.getBookLanguage().setText(books.getLanguage());
                    viewBook.getBookCategory().setText(books.getCategory());
                    viewBook.getBookPublishedDate().setText(books.getPublishedDate());
                    viewBook.getBookDescrption().setText(books.getDescription());
                    viewBook.getBookStatus().setText(Status);
                }
            });
            libraryPanel.getBooksPanel().add(homeBooks);
            libraryPanel.getBooksPanel().repaint();
            libraryPanel.getBooksPanel().revalidate();
        }
    }

    //Method to search
    public void searchField(String search){
        libraryPanel.getBooksPanel().removeAll();
        for (Books books : dataAccessClass.getBookList()){
            if (books.getBookName().contains(search) || books.getAuthor().contains(search)){
                populateBooksByOne(books);
            }
            libraryPanel.getBooksPanel().repaint();
            libraryPanel.getBooksPanel().revalidate();
        }
    }

    //Method to Filter Language
    public void filterByLanguage(String language){
        libraryPanel.getBooksPanel().removeAll();
        for (Books books : dataAccessClass.getBookList()){
            if (books.getLanguage().equals(language)){
                populateBooksByOne(books);
            }
            libraryPanel.getBooksPanel().repaint();
            libraryPanel.getBooksPanel().revalidate();
        }
    }

    //Method to Filter Category
    public void filterByCategory(String category){
        libraryPanel.getBooksPanel().removeAll();
        for (Books books : dataAccessClass.getBookList()){
            if (books.getCategory().equals(category)){
                populateBooksByOne(books);
            }
            libraryPanel.getBooksPanel().repaint();
            libraryPanel.getBooksPanel().revalidate();
        }
    }

    //Sets the Filter into Default
    public void defaultFilter(){
        libraryPanel.getBooksPanel().removeAll();
        for (Books books : dataAccessClass.getBookList()) {
            populateBooksByOne(books);
        }
        libraryPanel.getBooksPanel().repaint();
        libraryPanel.getBooksPanel().revalidate();
    }

    public void populateBooksByOne(Books books){
        homeBooks = new homeBooks();
        homeBooks.getBooknameLabel().setText(books.getBookName());
        homeBooks.getAuthorLabel().setText(books.getAuthor());
        homeBooks.getCategoryLabel().setText(books.getCategory());
        homeBooks.getViewButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Status = "";
                if (books.getStatus()){
                    Status = "Available";
                    viewBook.getBookStatus().setForeground(Color.RED);
                    viewBook.getSendRequestButton().setEnabled(true);
                } else {
                    Status = "Not Available";
                    viewBook.getSendRequestButton().setEnabled(false);
                }

                //Switch the Panel
                model.switchPanel(application.getBodyPanel(), viewBook);

                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(model.getBookImage(books.getFileName()));
                    BufferedImage originalImage = ImageIO.read(bis);
                    Image resizedImage = originalImage.getScaledInstance(viewBook.getBookImage().getWidth(), viewBook.getBookImage().getHeight(), Image.SCALE_SMOOTH);
                    viewBook.getBookImage().setIcon(new ImageIcon(resizedImage));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                viewBook.getBookISBN().setText(books.getISBN());
                viewBook.getBookTitle().setText(books.getBookName());
                viewBook.getAuthorName().setText(books.getAuthor());
                viewBook.getBookLanguage().setText(books.getLanguage());
                viewBook.getBookCategory().setText(books.getCategory());
                viewBook.getBookPublishedDate().setText(books.getPublishedDate());
                viewBook.getBookDescrption().setText(books.getDescription());
                viewBook.getBookStatus().setText(Status);
            }
        });
        libraryPanel.getBooksPanel().add(homeBooks);
        libraryPanel.getBooksPanel().repaint();
        libraryPanel.getBooksPanel().revalidate();
    }
}
