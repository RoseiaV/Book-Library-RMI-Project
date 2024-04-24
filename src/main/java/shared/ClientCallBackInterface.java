package shared;

import common.Books;
import common.BooksBorrower;
import common.BooksRequestee;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientCallBackInterface extends Remote {
    void updateBook(Books books) throws RemoteException;
    void updateRequestedBook(BooksRequestee booksRequestee) throws RemoteException;
    void updateBorrowedBooks(BooksBorrower booksBorrower) throws RemoteException;
    void bookRequestIsRepeated() throws RemoteException;
    void returnedBook() throws RemoteException;
    void refreshRequestedBooks() throws RemoteException;
}
