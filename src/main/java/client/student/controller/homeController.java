package client.student.controller;

import client.student.model.dataAccessClass;
import client.student.view.booksUtil.homeBooks;
import client.student.view.*;
import client.student.view.viewBook;
import common.Bookmarks;
import common.Books;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class homeController {
    private dataAccessClass model;
    private application application;
    private homePanel view;
    private viewBook viewBook;
    private homeBooks homeBooks;
    public homeController(application application, homePanel view, viewBook viewBook, dataAccessClass model){
        this.application = application;
        this.view = view;
        this.viewBook = viewBook;
        this.model = model;

        setHomeBooksController();
    }
    
    public void setHomeBooksController(){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Books books : dataAccessClass.getBookList()){
            LocalDate publishedDate = LocalDate.parse(books.getPublishedDate(), formatter); //Format the date form the json into a Date
            if (currentDate.minusDays(4).isBefore(publishedDate) && currentDate.minusDays(3).isBefore(currentDate)) {
                homeBooks = new homeBooks();
                homeBooks.getBooknameLabel().setText(books.getBookName());
                homeBooks.getAuthorLabel().setText(books.getAuthor());
                homeBooks.getCategoryLabel().setText(books.getCategory());

                homeBooks.getViewButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Button is Pressed");

                        String Status = "";
                        if (books.getStatus()){
                            Status = "Available";
                            viewBook.getBookStatus().setForeground(Color.RED);
                            viewBook.getSendRequestButton().setEnabled(true);
                        } else {
                            Status = "Not Available";
                            viewBook.getSendRequestButton().setEnabled(false);
                        }
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

                        viewBook.getBookmarkButton().addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (viewBook.getBookmarkButton().isSelected()){
                                    System.out.println("Bookmarked");

                                    //Add the current LoggedIn accountId of user to the bookmark.json in the server
                                    model.bookmark(new Bookmarks(viewBook.getBookISBN().getText(), application.getUserIdLabel().getText()));
                                } else {
                                    System.out.println("Un-Bookmarked");

                                    //Remove the current LoggedIn accountId of user to the bookmark.json in the server
                                    model.removeBookmark(new Bookmarks(viewBook.getBookISBN().getText(), application.getUserIdLabel().getText()));
                                }
                            }
                        });
                    }
                });
                view.getNewBooksPanel().add(homeBooks);
            }
            view.getNewBooksPanel().repaint();
            view.getNewBooksPanel().revalidate();
        }
    }
}
