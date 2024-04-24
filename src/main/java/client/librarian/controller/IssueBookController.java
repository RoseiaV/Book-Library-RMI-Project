package client.librarian.controller;

import client.librarian.model.IssueBookModel;
import client.librarian.view.IssueBookPanel;
import com.google.gson.Gson;
import common.BooksBorrower;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IssueBookController {
    protected IssueBookPanel issueBookPanel;
    protected IssueBookModel issueBookModel;
    public IssueBookController (IssueBookPanel issueBookPanel, IssueBookModel issueBookModel){
        this.issueBookPanel = issueBookPanel;
        this.issueBookModel = issueBookModel;

        fillers();
        addMouseListener();
        issueBookController();
    }

    public void issueBookController(){
        this.issueBookPanel.setAcceptButtonListener( e -> {
            int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to accept this request?", "Accept Request", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                String student = issueBookPanel.getStudentField();
                String isbn = issueBookPanel.getIsbnField();
                String dateBorrowed = issueBookPanel.getBdField();
                String returnDate = issueBookPanel.getrDField();
                boolean status = issueBookPanel.getStatusField().equals("Inactive");


                BooksBorrower booksBorrower = new BooksBorrower(returnDate, isbn, dateBorrowed, student, status);
                issueBookModel.updateBorrowedBooks(booksBorrower);
                issueBookModel.populateIssueBookTable(issueBookPanel.getIssueBookTable());
                JOptionPane.showMessageDialog(null, "Request accepted successfully!");
            }
        });
        this.issueBookPanel.setReturnedButtonListener( e -> {
            int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure the book has been returned?", "Return Book", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                String student = issueBookPanel.getStudentField();
                String isbn = issueBookPanel.getIsbnField();

                BooksBorrower booksBorrower = new BooksBorrower(student, isbn);
                issueBookModel.removeBorrowedBook(booksBorrower);
                issueBookModel.populateIssueBookTable(issueBookPanel.getIssueBookTable());
                JOptionPane.showMessageDialog(null, "Book marked as returned successfully!");
            }
        });
    }

    public void fillers(){
        issueBookModel.populateIssueBookTable(issueBookPanel.getIssueBookTable());
    }

    private void addMouseListener() {
        issueBookPanel.getIssueBookTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedRow = issueBookPanel.getIssueBookTable().rowAtPoint(e.getPoint());
                    if (selectedRow != -1) {
                        String isbn = issueBookPanel.getIssueBookTable().getModel().getValueAt(selectedRow, 2).toString();
                        String student = issueBookPanel.getIssueBookTable().getModel().getValueAt(selectedRow, 4).toString();
                        String borrowDate = issueBookPanel.getIssueBookTable().getModel().getValueAt(selectedRow, 3).toString();
                        String returnDate = issueBookPanel.getIssueBookTable().getModel().getValueAt(selectedRow, 1).toString();
                        String status = issueBookPanel.getIssueBookTable().getModel().getValueAt(selectedRow, 5).toString();
                        String reason = issueBookPanel.getIssueBookTable().getModel().getValueAt(selectedRow, 6).toString();
                        //update Text Fields in IssueBookPanel
                        issueBookPanel.setIsbnField(isbn);
                        issueBookPanel.setStudentField(student);
                        issueBookPanel.setBdField(borrowDate);
                        issueBookPanel.setrDField(returnDate);
                        issueBookPanel.setStatusField(status);
                        issueBookPanel.setReasonTextArea(reason);
                    }
                }
            }
        });
    }
}



