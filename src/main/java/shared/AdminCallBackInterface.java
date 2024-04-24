package shared;

import common.Books;
import common.BooksRequestee;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AdminCallBackInterface extends Remote {
    void addClientRequest(BooksRequestee booksRequestee) throws RemoteException;
}
