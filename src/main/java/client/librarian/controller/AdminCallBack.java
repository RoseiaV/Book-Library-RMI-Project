package client.librarian.controller;

import client.librarian.model.AdminFrameModel;
import client.librarian.model.IssueBookModel;
import client.librarian.view.IssueBookPanel;
import common.Author;
import common.BooksRequestee;
import shared.AdminCallBackInterface;

import javax.swing.table.DefaultTableModel;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AdminCallBack extends UnicastRemoteObject implements AdminCallBackInterface {

    private IssueBookPanel issueBookPanel;
    private IssueBookModel issueBookModel;
    public AdminCallBack(IssueBookPanel issueBookPanel, IssueBookModel issueBookModel) throws RemoteException{
        this.issueBookModel = issueBookModel;
        this.issueBookPanel = issueBookPanel;
    };

    @Override
    public void addClientRequest(BooksRequestee booksRequestee) throws RemoteException {
        DefaultTableModel tableModel = (DefaultTableModel) issueBookPanel.getIssueBookTable().getModel();

        String status = "";
        if (booksRequestee.getStatus()){
            status = "Active";
        } else if (!booksRequestee.getStatus()) {
            status = "Inactive";
        }

        tableModel.addRow(new Object[]{
                IssueBookModel.getNumberOfBookRequestee(),
                booksRequestee.getAccountId(),
                booksRequestee.getBookISBN(),
                booksRequestee.getDateWasRequested(),
                booksRequestee.getRequestDate(),
                status,
                booksRequestee.getReason()
        });
    }
}
