package client.student.controller;

import client.student.model.dataAccessClass;
import client.student.view.booksUtil.bookmark;
import client.student.view.booksUtil.borrowedBook;
import client.student.view.booksUtil.homeBooks;
import client.student.view.booksUtil.requestedBook;
import client.student.view.*;
import common.Bookmarks;
import common.Books;
import common.BooksBorrower;
import common.BooksRequestee;
import shared.ClientCallBackInterface;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class CallbackImplementation extends UnicastRemoteObject implements ClientCallBackInterface {
    private bookmark bookmark;
    private borrowedBook borrowedBook;
    private homeBooks homeBooks;
    private requestedBook requestedBook;

    private application application;
    private libraryPanel libraryPanel;
    private transcriptPanel transcriptPanel;
    private homePanel homePanel;
    private viewBook viewBook;
    private dataAccessClass model;
    private transcriptController transcriptController;

    public CallbackImplementation(transcriptController transcriptController, application application, homePanel homePanel, libraryPanel libraryPanel, transcriptPanel transcriptPanel, viewBook viewBook, dataAccessClass model) throws RemoteException {
        this.transcriptController = transcriptController;
        this.application = application;
        this.homePanel = homePanel;
        this.libraryPanel = libraryPanel;
        this.transcriptPanel = transcriptPanel;
        this.viewBook = viewBook;
        this.model = model;
    }

    //Callback that adds the book to the HomePanel
    @Override
    public void updateHomeBook(Books books) throws RemoteException {
        homeBooks = new homeBooks();
        homeBooks.getBooknameLabel().setText(books.getBookName());
        homeBooks.getAuthorLabel().setText(books.getAuthor());
        homeBooks.getCategoryLabel().setText(books.getCategory());
        homePanel.getNewBooksPanel().add(homeBooks);
        homePanel.getNewBooksPanel().repaint();
        homePanel.getNewBooksPanel().revalidate();
    }

    @Override
    public void updateBook(Books books) throws RemoteException {
        homeBooks = new homeBooks();
        homeBooks.getBooknameLabel().setText(books.getBookName());
        homeBooks.getAuthorLabel().setText(books.getAuthor());
        homeBooks.getCategoryLabel().setText(books.getCategory());
        libraryPanel.getBooksPanel().add(homeBooks);
        libraryPanel.getBooksPanel().repaint();
        libraryPanel.getBooksPanel().revalidate();
    }

    @Override
    public void updateRequestedBook(BooksRequestee booksRequestee) throws RemoteException {

    }

    @Override
    public void updateBorrowedBooks(BooksBorrower booksBorrower) throws RemoteException {

    }

    @Override
    public void bookRequestIsRepeated() throws RemoteException {
        JOptionPane.showMessageDialog(null, "You have already requested this Book!");
    }

    @Override
    public void returnedBook() throws RemoteException {

    }

    @Override
    public void refreshRequestedBooks() throws RemoteException {

    }

    @Override
    public void clientBookmark(Bookmarks bookmarks) throws RemoteException {
        bookmark = new bookmark();
        for (Books books : dataAccessClass.getBookList()){
            if (books.getISBN().equals(bookmarks.getBookId())){
                bookmark.getBooknameLabel().setText(books.getBookName());
                bookmark.getAuthorLabel().setText(books.getAuthor());
                bookmark.getCategoryLabel().setText(books.getCategory());
                transcriptPanel.getBookmarkPanel().add(bookmark);

                //View Book Action Listener
                bookmark.getViewButton().addActionListener(new ActionListener() {
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

                bookmark.getRemoveButton().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //Remove the accountId of the user on the bookmark json file
                        int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this Bookmark?","Exit Confirmation", JOptionPane.YES_NO_OPTION);
                        if (answer == JOptionPane.YES_OPTION){
                            model.removeBookmark(new Bookmarks(bookmark.getBookISBN().getText(), application.getUserIdLabel().getText()));
                        }
                    }
                });
            }
            transcriptPanel.getBookmarkPanel().repaint();
            transcriptPanel.getBookmarkPanel().revalidate();
        }
    }

    @Override
    public void refreshBookmark() throws RemoteException {
        transcriptController.populateBookmarks();
    }
}
