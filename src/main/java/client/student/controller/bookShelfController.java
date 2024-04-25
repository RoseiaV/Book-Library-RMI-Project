package client.student.controller;

import client.student.model.dataAccessClass;
import client.student.view.application;
import client.student.view.bookShelfPanel;
import client.student.view.booksUtil.borrowedBook;
import client.student.view.booksUtil.requestedBook;
import common.Books;
import common.BooksBorrower;
import common.BooksRequestee;

import java.awt.*;

public class bookShelfController {
    private application applicationFrame;
    private bookShelfPanel bookShelfPanel;
    private dataAccessClass model;
    private borrowedBook borrowed;
    private requestedBook requested;
    public bookShelfController(application applicationFrame, bookShelfPanel bookShelfPanel, dataAccessClass model){
        this.applicationFrame = applicationFrame;
        this.bookShelfPanel = bookShelfPanel;
        this.model = model;

        setBorrowedBooksController();
        setRequestedBooksController();
    }

    public void setBorrowedBooksController(){
        int w = bookShelfPanel.getBorrowedBookPanel().getWidth();
        if (model.ifBorrowerContains(applicationFrame.getUserIdLabel().getText())){
            for (BooksBorrower booksBorrower : dataAccessClass.getBooksBorrowerList()){
                if (booksBorrower.getAccountId().equals(applicationFrame.getUserIdLabel().getText())){

                    borrowed = new borrowedBook();
                    borrowed.getReturnDateLabel().setText(booksBorrower.getReturnDate());
                    bookShelfPanel.getBorrowedBookPanel().add(borrowed);
                    System.out.println(booksBorrower);
                }

                for (Books books : dataAccessClass.getBookList()){
                    if (booksBorrower.getBookISBN().equals(books.getISBN())){
                        borrowed.getBooknameLabel().setText(books.getBookName());
                        borrowed.getAuthorLabel().setText(books.getAuthor());
                    }
                }

                bookShelfPanel.getBorrowedBookPanel().revalidate();
                bookShelfPanel.getBorrowedBookPanel().repaint();
            }
        } else {
            System.out.println("No Records");
        }

    }

    public void setRequestedBooksController(){
        int w = bookShelfPanel.getRequestedBookPanel().getWidth();

        for (BooksRequestee booksRequestee : dataAccessClass.getBooksRequesteeList()){
            if (booksRequestee.getAccountId().equals(applicationFrame.getUserIdLabel().getText())) {
                for (int i = 0; i < 3; i++) {
                    bookShelfPanel.getRequestedBookPanel().setPreferredSize(new Dimension(w, 195));
                    w = w + 690;
                }

                requested = new requestedBook();
                requested.getReturnDateLabel().setText(booksRequestee.getRequestDate());
                bookShelfPanel.getRequestedBookPanel().add(requested);


                System.out.println(booksRequestee.toString());

                for (Books books : dataAccessClass.getBookList()){
                    if (books.getISBN().equals(booksRequestee.getBookISBN())){
                        requested.getBooknameLabel().setText(books.getBookName());
                        requested.getAuthorLabel().setText(books.getAuthor());
                    }
                }
            }

            bookShelfPanel.getRequestedBookPanel().revalidate();
            bookShelfPanel.getRequestedBookPanel().repaint();
        }
    }
}
