package client.librarian.model;

import common.BooksBorrower;
import common.BooksRequestee;
import shared.ClientInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.RemoteException;
import java.util.List;

public class IssueBookModel {

    protected static ClientInterface clientInterface;

    public IssueBookModel(ClientInterface serverInterface){
        IssueBookModel.clientInterface = serverInterface;
    }

    public void populateIssueBookTable(JTable table){
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        String [] column = {"#", "Student", "ISBN", "Request Date", "Return Date", "Status", "Reason"};
        for (String colm : column){
            tableModel.addColumn(colm);
        }

        int x = 0;
        for (BooksRequestee booksRequestee : getBooksRequesteeList()){
            x++;
            String status = "";
            if (booksRequestee.getStatus()){
                status = "Active";
            } else if (!booksRequestee.getStatus()) {
                status = "Inactive";
            }
            tableModel.addRow(new Object[]{
                    x,
                    booksRequestee.getAccountId(),
                    booksRequestee.getBookISBN(),
                    booksRequestee.getDateWasRequested(),
                    booksRequestee.getRequestDate(),
                    status,
                    booksRequestee.getReason()
            });
        }
    }

    //List of BooksRequesteee from the Server
    public static List<BooksRequestee> getBooksRequesteeList(){
        try {
            return clientInterface.getBooksRequesteeData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getNumberOfBookRequestee(){
        int number = 0;
        for (BooksRequestee booksRequestee : getBooksRequesteeList()){
            number++;
        }
        return number;
    }

    public void updateBorrowedBooks(BooksBorrower booksBorrower) {
        try {
            clientInterface.updateBorrowedBooks(booksBorrower);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeBorrowedBook(BooksBorrower booksBorrower) {
        try {
            clientInterface.removeBorrowedBook(booksBorrower);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
