package client.student.controller;

import client.student.view.*;
import client.student.model.*;
import common.BooksRequestee;

import javax.swing.*;

public class viewBookController {

    private application application;
    private viewBook viewBook;
    private libraryPanel libraryPanel;
    private dataAccessClass model;
    public viewBookController(application application,libraryPanel libraryPanel, viewBook viewBook, dataAccessClass model){
        this.application = application;
        this.libraryPanel = libraryPanel;
        this.viewBook = viewBook;
        this.model = model;

        viewBook.getDayComboBox().setModel(new DefaultComboBoxModel<>(dataAccessClass.getDaysInMonth(model.getLocalDate().getMonthValue(), model.getLocalDate().getYear())));
        viewBook.getMonthComboBox().setModel(new DefaultComboBoxModel<>(dataAccessClass.getMonths()));
        viewBook.getYearComboBox().setModel(new DefaultComboBoxModel<>(dataAccessClass.getYears()));
        setViewBookController();
    }

    //Method for the ActionListeners of the Buttons of this Class
    public void setViewBookController(){
        this.viewBook.setGoBackButtonListener(e -> {
            System.out.println("Go Back Button");
            model.switchPanel(application.getBodyPanel(), libraryPanel);
            viewBook.getBookTitle().setText("");
            viewBook.getAuthorName().setText("");
            viewBook.getBookLanguage().setText("");
            viewBook.getBookCategory().setText("");
            viewBook.getBookPublishedDate().setText("");
            viewBook.getBookPublishedDate().setText("");
            viewBook.getBookDescrption().setText("");
            viewBook.getBookStatus().setText("");
        });
        this.viewBook.setSendRequestButtonListener( e -> {
            String studentID = application.getUserIdLabel().getText(); //Get the stored id from the ApplicationFrame.class
            String bookId = viewBook.getBookISBN().getText();
            int day = Integer.parseInt(viewBook.getDayComboBox().getSelectedItem().toString());
            int month = Integer.parseInt(viewBook.getMonthComboBox().getSelectedItem().toString());
            int year = Integer.parseInt(viewBook.getYearComboBox().getSelectedItem().toString());
            String compiledDate = year + "-" + month + "-" + day;
            String reason = viewBook.getReasonTextPane().getText();

            BooksRequestee booksRequestee = new BooksRequestee(studentID, bookId, model.getDateToday(), reason, compiledDate, false);
            model.requestBook(booksRequestee);
            JOptionPane.showMessageDialog(null, "Request sent!");
        });

        this.viewBook.setMonthComboBoxItemListener( e -> {updateDayComboBox();});
        this.viewBook.setYearComboBoxItemListener( e -> { updateDayComboBox();});

        //BookMark ItemListener
        this.viewBook.setBookmarkButtonListener( e -> {
            if (viewBook.getBookmarkButton().isSelected()){
                System.out.println("Bookmarked");
                //TODO
                //Add the current LoggedIn accountId of user to the bookmark.json in the server
            } else {
                System.out.println("Un-Bookmarked");
                //TODO
                //Remove the current LoggedIn accountId of user to the bookmark.json in the server
            }
        });

    }

    public void updateDayComboBox() {
        int selectedDay = (int) viewBook.getDayComboBox().getSelectedItem();
        int selectedMonth = (int) viewBook.getMonthComboBox().getSelectedItem();
        int selectedYear = (int) viewBook.getYearComboBox().getSelectedItem();
        Integer[] daysInMonth =  dataAccessClass.getDaysInMonth(selectedMonth, selectedYear);

        viewBook.getDayComboBox().setModel(new DefaultComboBoxModel<>(daysInMonth));
        if (selectedDay <= daysInMonth.length) {
            viewBook.getDayComboBox().setSelectedItem(selectedDay);
        }
    }
}
