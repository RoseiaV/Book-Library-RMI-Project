package client.student.controller;

import client.student.view.booksUtil.bookmark;
import client.student.view.booksUtil.borrowedBook;
import client.student.view.booksUtil.homeBooks;
import client.student.view.booksUtil.requestedBook;
import client.student.view.*;
import common.Books;
import common.BooksBorrower;
import common.BooksRequestee;
import shared.ClientCallBackInterface;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CallbackImplementation extends UnicastRemoteObject implements ClientCallBackInterface {
    private bookmark bookmark;
    private borrowedBook borrowedBook;
    private homeBooks homeBooks;
    private requestedBook requestedBook;

    private libraryPanel libraryPanel;
    private transcriptPanel transcriptPanel;
    private homePanel homePanel;

    public CallbackImplementation(homePanel homePanel, libraryPanel libraryPanel, transcriptPanel transcriptPanel) throws RemoteException {
        this.homePanel = homePanel;
        this.libraryPanel = libraryPanel;
        this.transcriptPanel = transcriptPanel;
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
}
