package shared;

import common.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

public interface ClientInterface extends Remote {

    Boolean logInAuthenticate(Accounts accounts) throws RemoteException;
    void logOut(Accounts accounts) throws RemoteException;

    void registerCallBack(ClientCallBackInterface callBackInterface) throws RemoteException;
    void registerAdminCallBack(AdminCallBackInterface adminCallBackInterface) throws RemoteException;

    List<Accounts> getAccountsData() throws RemoteException;
    List<Language> getLanguageData() throws RemoteException;
    List<Category> getCategoryData() throws RemoteException;
    List<Books> getBooksData() throws RemoteException;
    List<BooksRequestee> getBooksRequesteeData() throws RemoteException;
    List<BooksBorrower> getBooksBorrowerDate() throws RemoteException;

    byte[] getBookImage(String imageName) throws RemoteException;
    List<Pictures> getPictureData() throws RemoteException;

    Boolean adminLogInAuth(Admin admin) throws RemoteException;
    void adminLogOut(Admin admin) throws RemoteException;

    void addBook(Books books, byte [] imageData) throws RemoteException;
    void addAuthor(Author author) throws RemoteException;
    void addCategory(Category category) throws RemoteException;
    void addRole(Role role) throws RemoteException;
    void addAccount(Accounts accounts) throws  RemoteException;
    void addLanguage(Language language) throws RemoteException;
    List<Books> getJsonData() throws RemoteException;
    List<Role> getRoleData() throws RemoteException;
    List<Author> getAuthorData() throws RemoteException;
    List<Bookmarks> getBookmarksData() throws RemoteException;

    List<Admin> getAdminData() throws RemoteException;
    List<Accounts> getAccountData() throws RemoteException;

    void deleteBook(String isbn)throws RemoteException;
    void updateBook(String isbn, String updatedBookName, String updatedAuthor, String updatedCategory, String updatedLanguage, String updatedDescription) throws RemoteException;

    void updateAuthor(String authorName, Author updatedAuthor) throws RemoteException;
    void deleteAuthor(String authorName) throws RemoteException; //added this - Don

    void updateCategory(String categoryName, Category updatedCategory) throws RemoteException;
    void deleteCategory(String categoryName) throws RemoteException;

    void updateLanguage(String languageName, Language updatedLanguage) throws RemoteException;
    void deleteLanguage(String languageName) throws RemoteException;

    void updateRole(String roleName, Role updatedRole) throws RemoteException;
    void deleteRole(String roleName) throws RemoteException;

    void updateAccount(String id, String username, String password, String role, boolean updatedStatus) throws RemoteException;
    void deleteAccount(int selectedRow) throws RemoteException;

    void updateAccount(int selectedRow, Accounts updatedAccount) throws RemoteException;

    void addBookRequest(BooksRequestee booksRequestee) throws RemoteException;
    void clientBookMark(Bookmarks bookmarks) throws RemoteException;

    void updateBorrowedBooks(BooksBorrower booksBorrower) throws RemoteException;
    void removeBorrowedBook(BooksBorrower booksBorrower) throws RemoteException;

}
