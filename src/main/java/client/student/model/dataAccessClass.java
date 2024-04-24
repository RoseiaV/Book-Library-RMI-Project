package client.student.model;

import common.*;
import shared.ClientInterface;

import javax.swing.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class dataAccessClass extends UnicastRemoteObject implements Serializable {
    private static ClientInterface clientInterface;
    public dataAccessClass(ClientInterface clientInterface) throws RemoteException {
        dataAccessClass.clientInterface = clientInterface;
    }
    public void switchPanel (JPanel mainPanel, JPanel switchedPanel){
        mainPanel.removeAll();
        mainPanel.add(switchedPanel);
        mainPanel.repaint();
        mainPanel.revalidate();
    }

    //Returns a List of Categories
    public static List<Category> getCategoryList() {
        try {
            return clientInterface.getCategoryData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //Returns a List of Languages
    public static List<Language> getLanguageList() {
        try {
            return clientInterface.getLanguageData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //Returns an List of Books
    public static List<Books> getBookList() {
        try {
            return clientInterface.getBooksData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<BooksRequestee> getBooksRequesteeList(){
        try {
            return clientInterface.getBooksRequesteeData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<BooksBorrower> getBooksBorrowerList() {
        try {
            return clientInterface.getBooksBorrowerDate();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Accounts> getAccountList() {
        try {
            return clientInterface.getAccountData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Bookmarks> getBookmarkList() {
        try {
            return clientInterface.getBookmarksData();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean ifBorrowerContains(String id){
        boolean flag = true;
        for (BooksBorrower bb : getBooksBorrowerList()){
            if (bb.getAccountId().equals(id)) {
                flag = true;
            } else {
                flag = false;
            }
        }
        return flag;
    }

    public String getDateToday(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        return localDateTime.format(dateTimeFormatter);
    }

    //Logs out the Account of the Student
    public void logOut(String username, String id){
        Accounts accounts = new Accounts(id, username,null);
        try {
            clientInterface.logOut(accounts);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    //send object to the server
    //This method lets the server create request for the admin to accept
    public void requestBook(BooksRequestee booksRequestee){
        try {
            // Send BooksRequestee object to server using RMI call
            clientInterface.addBookRequest(booksRequestee);
            System.out.println("Request sent successfully!");
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("Error sending request to server!");
        }
    }

    //Send Bookmark object to the server
    public void bookmark(Bookmarks bookmarks) {
        try {
            clientInterface.clientBookMark(bookmarks);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeBookmark(Bookmarks bookmarks){
        try {
            clientInterface.removeClientBookmark(bookmarks);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static Integer[] getDaysInMonth(int month, int year) {
        int daysInMonth;
        if (month == 2) {
            if (Year.isLeap(year)) {
                daysInMonth = 29;
            } else {
                daysInMonth = 28;
            }
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            daysInMonth = 30;
        } else {
            daysInMonth = 31;
        }

        Integer[] days = new Integer[daysInMonth];
        for (int i = 0; i < daysInMonth; i++) {
            days[i] = i + 1;
        }
        return days;
    }

    public static Integer[] getMonths() {
        Integer[] months = new Integer[12];
        for (int i = 0; i < 12; i++) {
            months[i] = i + 1;
        }
        return months;
    }

    public static Integer[] getYears() {
        int currentYear = LocalDate.now().getYear();
        Integer[] years = new Integer[5];
        for (int i = 0; i < 5; i++) {
            years[i] = currentYear + i;
        }
        return years;
    }

    public LocalDate getLocalDate() {
        return LocalDate.now();
    }

    public static byte[] getBookImage(String FileName){
        try {
            return clientInterface.getBookImage(FileName);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that populated the JComboBox of the LibraryClass
     * @param comboBox
     */
    public void populateCategoryComboBox(JComboBox<String> comboBox){
        DefaultComboBoxModel<String> comboBoxModel = (DefaultComboBoxModel<String>) comboBox.getModel();
        comboBoxModel.removeAllElements();
        comboBoxModel.addElement("All");
        for (int i = 0; i < getCategoryList().size(); i++){
            String item = getCategoryList().get(i).getCategory();
            comboBoxModel.addElement(item);
        }
    }

    /**
     * Method that populated the JComboBox of the LibraryClass
     * @param comboBox
     */
    public void populateLanguageComboBox(JComboBox<String> comboBox){
        DefaultComboBoxModel<String> comboBoxModel = (DefaultComboBoxModel<String> ) comboBox.getModel();
        comboBoxModel.removeAllElements();
        comboBoxModel.addElement("All");
        for(int i = 0; i < getLanguageList().size(); i++){
            String item = getLanguageList().get(i).getLanguage();
            comboBoxModel.addElement(item);
        }
    }
}
