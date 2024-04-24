package server.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import common.*;
import server.view.ServerMainGUI;
import shared.AdminCallBackInterface;
import shared.ClientCallBackInterface;
import shared.ClientInterface;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AdminImplication extends UnicastRemoteObject implements ClientInterface, Serializable {
    protected List<Books> booksList = new ArrayList<>();
    protected List<Author> authorList = new ArrayList<>();
    protected List<Category> categoryList = new ArrayList<>();
    protected List<Role> roleList = new ArrayList<>();
    protected List<Accounts> accountsList = new ArrayList<>();
    protected List<Language> languageList = new ArrayList<>();
    protected List<BooksRequestee> booksRequesteeList = new ArrayList<>();
    protected List<BooksBorrower> booksBorrowerList = new ArrayList<>();
    protected List<Bookmarks> bookmarksList = new ArrayList<>();

    protected Registry registry;
    private final String path = "src/main/java/server/database/pictures/";

    public static Map<String, String> logAccounts = new ConcurrentHashMap<>();
    public static Map<String, String> adminLog = new ConcurrentHashMap<>();
    protected static List<ClientCallBackInterface> callbacks;
    protected static List<AdminCallBackInterface> adminCallBackInterfaces;


    public AdminImplication() throws RemoteException {
        super();
        callbacks = new ArrayList<>();
        adminCallBackInterfaces = new ArrayList<>();
    }

    public void startServer(){
        try{
            System.out.println("Starting Server....");
            ClientInterface stub = new AdminImplication();
            registry = LocateRegistry.createRegistry(10000);

            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            registry.rebind("SERVER", stub);


            ServerMainGUI.statusTextField.setText("Server IP: " + ipAddress);
            System.out.println(ipAddress);
            System.out.println("Server Started");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }


    //Method to add A Book
    @Override
    public void addBook(Books books, byte[] imageData) throws RemoteException{
        File file = new File("src/main/java/Server/database/json/books.json"); //File
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); //Sets the JSON file to readable
        if (file.exists()){ //Checks if the File Exist on the Database Folder
            try (Reader reader = new FileReader(file)){
                booksList = gson.fromJson(reader, new TypeToken<List<Books>>(){}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        booksList.add(books);
        try (Writer writer = new FileWriter(file)){
            gson.toJson(booksList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Write the Image to the designated Path of database in the server
        try (FileOutputStream fileOutputStream = new FileOutputStream(path + books.getFileName())){
            fileOutputStream.write(imageData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Notify Call back
        notifyCallbacks(books);
    }

    /**
     * Method that create a JSON file out of the Requested Book in the Client Side
     * @param booksRequestee
     * @throws RemoteException
     */
    public void addBookRequest(BooksRequestee booksRequestee) throws RemoteException {
        Map<String, String> map1 = new HashMap<>();
        map1.put(booksRequestee.getAccountId(), booksRequestee.getBookISBN()); // put the accountID and the BookId that, that account borrowed
        //Iterate
        for (BooksRequestee br : getBooksRequesteeData()){
            Map<String, String> map2 = new HashMap<>();
            map2.put(br.getAccountId(), br.getBookISBN()); //Put the parsed json file on the map2

            //Checks if the Requested book of that user is being requested again
            //Through by comparing the actual entry from the entries on the json file
            if (map1.equals(map2)){
                notifyRequestRepetition();
            } else {
                File file = new File("src/main/java/server/database/json/requests.json");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                if (file.exists()){ //Checks if the File Exist on the Database Folder
                    try (Reader reader = new FileReader(file)){
                        booksRequesteeList = gson.fromJson(reader, new TypeToken<List<BooksRequestee>>(){}.getType());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                booksRequesteeList.add(booksRequestee);
                try (Writer writer = new FileWriter(file)){
                    gson.toJson(booksRequesteeList, writer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //Notify Client on its Request
                notifyRequestCallbacks(booksRequestee);
            }
        }
        //Notify the Admin that Request has been Made
        updateAdminIssueBookTable(booksRequestee);
        System.out.println("Book request received for ISBN: " + booksRequestee.getAccountId() + " : " + booksRequestee.getBookISBN());
    }


    //Method to create or add user to the bookmark of that certain book
    @Override
    public void clientBookMark(Bookmarks bookmarks) throws RemoteException {
        File file = new File("src/main/java/Server/database/json/bookmark.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()){
            try (Reader reader = new FileReader(file)){
                bookmarksList = gson.fromJson(reader,new TypeToken<List<Bookmarks>>() {}.getType());

                boolean flag = false;

                for (Bookmarks bkm : bookmarksList){
                    if (bkm.getBookId().equals(bookmarks.getBookId())){ //Check if the BookISBN of account bookmarked is the same

                        //Adds the Account from that Object
                        bkm.getAccountId().add(bookmarks.getAccId()); //Add the Account from the List of Accounts
                        flag  = true;
                        break;
                    }
                }

                if (!flag){
                    List<String> newAccountIds = new ArrayList<>();
                    newAccountIds.add(bookmarks.getAccId());
                    Bookmarks newBookmark = new Bookmarks(bookmarks.getBookId(), newAccountIds);
                    bookmarksList.add(newBookmark);
                }

                try (Writer writer = new FileWriter(file)){
                    gson.toJson(bookmarksList, writer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Method that removes the AccountId from the BookISBN Object
    @Override
    public void removeClientBookmark(Bookmarks bookmarks) throws RemoteException {
        File file = new File("src/main/java/Server/database/json/bookmark.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Reader reader = new FileReader(file)){
            bookmarksList = gson.fromJson(reader, new TypeToken<List<Bookmarks>>() {}.getType());

            Bookmarks removeBookmark = null;
            for (Bookmarks bookmark : bookmarksList){
                if (bookmark.getBookId().equals(bookmarks.getBookId())){
                    bookmark.getAccountId().remove(bookmarks.getAccId()); //Remove the AccountId from the List of AccountId's

                    //Checks if there is no accountId left bookmarked on that book
                    if (bookmark.getAccountId().isEmpty()){
                        removeBookmark = bookmark;
                    }
                    break;
                }
            }

            if (removeBookmark != null){
                bookmarksList.remove(removeBookmark); //Removes the Bookmark from the List of Bookmarks
            }

            try (Writer writer = new FileWriter(file)){
                gson.toJson(bookmarksList, writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //Parse the JSON File request
    @Override
    public List<BooksRequestee> getBooksRequesteeData() throws RemoteException {
        try (FileReader reader = new FileReader("src/main/java/server/database/json/requests.json")){
            Gson gson = new Gson();
            return gson.fromJson(reader,  new TypeToken<List<BooksRequestee>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BooksBorrower> getBooksBorrowerDate() throws RemoteException {
        try (FileReader reader = new FileReader("src/main/java/server/database/json/borrowedBooks.json")){
            Gson gson = new Gson();
            return gson.fromJson(reader,  new TypeToken<List<BooksBorrower>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void notifyRequestRepetition(){
        for (ClientCallBackInterface callBackInterface : callbacks){
            try {
                callBackInterface.bookRequestIsRepeated();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateAdminIssueBookTable(BooksRequestee booksRequestee){
        for (AdminCallBackInterface adminCallBackInterface : adminCallBackInterfaces){
            try {
                adminCallBackInterface.addClientRequest(booksRequestee);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Add a Author on a Json File
    @Override
    public void addAuthor(Author author) throws RemoteException {
        File file = new File("src/main/java/Server/database/json/author.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()){ //Checks if the File Exist on the Database Folder
            try (Reader reader = new FileReader(file)){
                authorList = gson.fromJson(reader, new TypeToken<List<Author>>(){}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        authorList.add(author);
        try (Writer writer = new FileWriter(file)){
            gson.toJson(authorList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Adds a Category on a Json File
    @Override
    public void addCategory(Category category) throws RemoteException {
        File file = new File("src/main/java/Server/database/json/category.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()){ //Checks if the File Exist on the Database Folder
            try (Reader reader = new FileReader(file)){
                categoryList = gson.fromJson(reader, new TypeToken<List<Category>>(){}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        categoryList.add(category);
        try (Writer writer = new FileWriter(file)){
            gson.toJson(categoryList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addRole(Role role) throws RemoteException {
        File file = new File("src/main/java/Server/database/json/role.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()){ //Checks if the File Exist on the Database Folder
            try (Reader reader = new FileReader(file)){
                roleList = gson.fromJson(reader, new TypeToken<List<Role>>(){}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        roleList.add(role);
        try (Writer writer = new FileWriter(file)){
            gson.toJson(roleList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addAccount(Accounts accounts) throws RemoteException {
        File file = new File("src/main/java/Server/database/json/accounts.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()){ //Checks if the File Exist on the Database Folder
            try (Reader reader = new FileReader(file)){
                accountsList = gson.fromJson(reader, new TypeToken<List<Accounts>>(){}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        accountsList.add(accounts);
        try (Writer writer = new FileWriter(file)){
            gson.toJson(accountsList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addLanguage(Language language) throws RemoteException {
        File file = new File("src/main/java/Server/database/json/language.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()){ //Checks if the File Exist on the Database Folder
            try (Reader reader = new FileReader(file)){
                languageList = gson.fromJson(reader, new TypeToken<List<Language>>(){}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        languageList.add(language);
        try (Writer writer = new FileWriter(file)){
            gson.toJson(languageList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Method to Return A Books Information to the Admin
    @Override
    public List<Books> getJsonData() throws RemoteException {
        try (FileReader reader = new FileReader("src/main/java/Server/database/json/books.json")) {
            Gson gson = new Gson();
            return gson.fromJson(reader, new TypeToken<List<Books>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Method to Return A Role Value to the Admin
    @Override
    public List<Role> getRoleData() throws RemoteException {
        try (FileReader reader = new FileReader("src/main/java/server/database/json/role.json")){
            Gson gson = new Gson();
            return gson.fromJson(reader,  new TypeToken<List<Role>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Method to Return a Author Value to the Admin
    @Override
    public List<Author> getAuthorData() throws RemoteException {
        try (FileReader reader = new FileReader("src/main/java/server/database/json/author.json")){
            Gson gson = new Gson();
            return gson.fromJson(reader,  new TypeToken<List<Author>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Method to return Parsed JSON of BookMarks
    @Override
    public List<Bookmarks> getBookmarksData() throws RemoteException {
        try (FileReader reader = new FileReader("src/main/java/server/database/json/bookmark.json")){
            Gson gson = new Gson();
            return gson.fromJson(reader,  new TypeToken<List<Bookmarks>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Method to Return a Category List to the Admin
    @Override
    public List<Category> getCategoryData() throws RemoteException {
        try (FileReader reader = new FileReader("src/main/java/server/database/json/category.json")){
            Gson gson = new Gson();
            return gson.fromJson(reader,  new TypeToken<List<Category>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Books> getBooksData() throws RemoteException {
        try (FileReader reader = new FileReader("src/main/java/Server/database/json/books.json")) {
            Gson gson = new Gson();
            return gson.fromJson(reader, new TypeToken<List<Books>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    @Override
    public byte[] getBookImage(String imageName) throws RemoteException {
        try {
            // Read the image
            BufferedImage image = ImageIO.read(new File(path+imageName));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return List<Pictures>
     * @throws RemoteException
     */
    @Override
    public List<Pictures> getPictureData() throws RemoteException {
        List<Pictures> picturesList = new ArrayList<>();
        booksList = getBooksData();

        for (Books books : booksList){
            picturesList.add(populatePicture(path, books.getFileName()));
        }
        return picturesList;
    }

    /**
     *
     * @param admin
     * @return
     * @throws RemoteException
     */
    @Override
    public Boolean adminLogInAuth(Admin admin) throws RemoteException {
        boolean flag = false;
        String username = admin.getUsername();
        String password = admin.getPassword();

        if (adminLog.containsValue(username)){
            System.out.println("Contains Admin Username");
            flag = false;
        } else {
            for (Admin a : getAdminData()){
                if (a.getUsername().equals(username) && a.getPassword().equals(password)){
                    adminLog.put(username, password);
                    System.out.println("HashMap");
                    System.out.println("Admin Log Put : " + adminLog);
                    flag = true;
                } else {
                    flag = false;
                }
            }
        }
        return flag;
    }

    /**
     *
     * @param admin
     * @throws RemoteException
     */
    @Override
    public void adminLogOut(Admin admin) throws RemoteException {
        Iterator<String> iteratorAdminLog = adminLog.keySet().iterator();
        while (iteratorAdminLog.hasNext()){
            if (adminLog.containsKey(admin.getUsername())){
                adminLog.remove(admin.getUsername());
                System.out.println("Key Removed");
                System.out.println("Current Admin stored in HashTable : " + adminLog);
            }
        }
    }

    /**
     *
     * @param directoryPath
     * @param pictureName
     * @return Pictures
     */
    private static Pictures populatePicture(String directoryPath, String pictureName) {
        File pictureFile = new File(directoryPath, pictureName);
        byte[] pictureData = null;
        try {
            // Read the bytes of the picture file
            pictureData = Files.readAllBytes(pictureFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pictures(pictureName, pictureData);
    }

    /**
     *
     * @param clientCallBackInterface
     * @throws RemoteException
     */
    @Override
    public void registerCallBack(ClientCallBackInterface clientCallBackInterface) throws RemoteException {
        callbacks.add(clientCallBackInterface);
    }

    /**
     *
     * @param adminCallBackInterface
     * @throws RemoteException
     */
    @Override
    public void registerAdminCallBack(AdminCallBackInterface adminCallBackInterface) throws RemoteException {
        adminCallBackInterfaces.add(adminCallBackInterface);
    }

    /**
     *
     * @param accounts
     * @return
     * @throws RemoteException
     */
    @Override
    public Boolean logInAuthenticate(Accounts accounts) throws RemoteException {
        boolean flag = false;

        String id = accounts.getId();
        String username = accounts.getUsername();
        String password = accounts.getPassword();
        if (logAccounts.containsValue(username)){ //Check if the account is being logged in again
            System.out.println("Contains username");
            flag = false;
        } else {
            accountsList = getAccountsData();
            for(Accounts acc : accountsList){
                if (username.equals(acc.getUsername()) && password.equals(acc.getPassword())){
                    logAccounts.put(acc.getId(), username); //Put the
                    System.out.println("HashMap ");
                    System.out.println(logAccounts);
                    flag = true;
                }
            }
        }
        return flag;
    }

    /**
     *
     * @param accounts
     * @throws RemoteException
     */
    @Override
    public void logOut(Accounts accounts) throws RemoteException {
        Iterator<String> iteratedLogAcc = logAccounts.keySet().iterator();
        while (iteratedLogAcc.hasNext()){
            if (logAccounts.containsKey(accounts.getId())){
                logAccounts.remove(accounts.getId());
                System.out.println("Key Removed");
                System.out.println("Current User stored in HashTable : " + logAccounts);
            }
        }
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public List<Accounts> getAccountsData() throws RemoteException {
        try (FileReader reader = new FileReader("src/main/java/server/database/json/accounts.json")){
            Gson gson = new Gson();
            return gson.fromJson(reader,  new TypeToken<List<Accounts>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public List<Language> getLanguageData() throws RemoteException {
        try (FileReader reader = new FileReader("src/main/java/server/database/json/language.json")){
            Gson gson = new Gson();
            return gson.fromJson(reader,  new TypeToken<List<Language>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public List<Admin> getAdminData() throws RemoteException {
        try (FileReader reader = new FileReader("src/main/java/server/database/json/admin.json")){
            Gson gson = new Gson();
            return gson.fromJson(reader,  new TypeToken<List<Admin>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public List<Accounts> getAccountData() throws RemoteException {
        try (FileReader reader = new FileReader("src/main/java/server/database/json/accounts.json")){
            Gson gson = new Gson();
            return gson.fromJson(reader,  new TypeToken<List<Accounts>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param isbn
     * @throws RemoteException
     */
    @Override
    public void deleteBook(String isbn) throws RemoteException {
        File file = new File("src/main/java/server/database/json/books.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                booksList = gson.fromJson(reader, new TypeToken<List<Books>>() {}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Find and remove the book with the specified ISBN
        Iterator<Books> iterator = booksList.iterator();
        while (iterator.hasNext()) {
            Books book = iterator.next();
            if (book.getISBN().equals(isbn)) {
                iterator.remove();
                break; // Exit loop once the book is found and removed
            }
        }

        // Write the updated list back to the file
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(booksList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param isbn
     * @param updatedBookName
     * @param updatedAuthor
     * @param updatedCategory
     * @param updatedLanguage
     * @param updatedDescription
     * @throws RemoteException
     */
    @Override
    public void updateBook(String isbn, String updatedBookName, String updatedAuthor, String updatedCategory, String updatedLanguage, String updatedDescription) throws RemoteException {
        File file = new File("src/main/java/server/database/json/books.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                booksList = gson.fromJson(reader, new TypeToken<List<Books>>() {}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Find and update the book with the specified ISBN
        for (Books book : booksList) {
            if (book.getISBN().equals(isbn)) {
                book.setBookName(updatedBookName);
                book.setAuthor(updatedAuthor);
                book.setCategory(updatedCategory);
                book.setLanguage(updatedLanguage);
                book.setDescription(updatedDescription);

                // Toggle the status
                boolean updatedStatus = !book.getStatus(); // Toggling the status
                book.setStatus(updatedStatus);

                // Convert boolean status to string for display
                String statusString = updatedStatus ? "Available" : "Not Available";
                book.setStatusString(statusString);

                break; // Exit loop once the book is found and updated
            }
        }

        // Write the updated list back to the file
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(booksList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateAuthor(String authorName, Author updatedAuthor) throws RemoteException {
        File file = new File("src/main/java/server/database/json/author.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()) { //Checks if the File Exist on the Database Folder
            try (Reader reader = new FileReader(file)) {
                authorList = gson.fromJson(reader, new TypeToken<List<Author>>() {}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Find and update the author with the specified name
        for (int i = 0; i < authorList.size(); i++) {
            Author author = authorList.get(i);
            if (author.getAuthor().equals(authorName)) {
                authorList.set(i, updatedAuthor); // Update author information
                break; // Exit loop once the author is found and updated
            }
        }

        // Write the updated list back to the file
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(authorList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAuthor(String authorName) throws RemoteException { ////added this - Don
        File file = new File("src/main/java/server/database/json/author.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()) { //Checks if the File Exist on the Database Folder
            try (Reader reader = new FileReader(file)) {
                authorList = gson.fromJson(reader, new TypeToken<List<Author>>() {}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Find and remove the author with the specified name
        Iterator<Author> iterator = authorList.iterator();
        while (iterator.hasNext()) {
            Author author = iterator.next();
            if (author.getAuthor().equals(authorName)) {
                iterator.remove();
                break; // Exit loop once the author is found and removed
            }
        }

        // Write the updated list back to the file
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(authorList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCategory(String categoryName, Category updatedCategory) throws RemoteException {
        File file = new File("src/main/java/server/database/json/category.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()) { // Checks if the File Exist on the Database Folder
            try (Reader reader = new FileReader(file)) {
                categoryList = gson.fromJson(reader, new TypeToken<List<Category>>() {
                }.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        boolean categoryFound = false;
        // Find and update the category with the specified name
        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);
            if (category.getCategory().equals(categoryName)) {
                categoryList.set(i, updatedCategory); // Update category information
                categoryFound = true;
                break; // Exit loop once the category is found and updated
            }
        }

        if (!categoryFound) {
            throw new RemoteException("Category not found: " + categoryName);
        }

        // Write the updated list back to the file
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(categoryList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void deleteCategory(String categoryName) throws RemoteException {
        File file = new File("src/main/java/server/database/json/category.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()) { //Checks if the File Exist on the Database Folder
            try (Reader reader = new FileReader(file)) {
                categoryList = gson.fromJson(reader, new TypeToken<List<Category>>() {}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Find and remove the category with the specified name
        Iterator<Category> iterator = categoryList.iterator();
        while (iterator.hasNext()) {
            Category existingCategory = iterator.next();
            if (existingCategory.getCategory().equals(categoryName)) {
                iterator.remove();
                break; // Exit loop once the category is found and removed
            }
        }

        // Write the updated list back to the file
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(categoryList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateLanguage(String languageName, Language updatedLanguage) throws RemoteException {
        File file = new File("src/main/java/server/database/json/language.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()) { //Checks if the File Exist on the Database Folder
            try (Reader reader = new FileReader(file)) {
                languageList = gson.fromJson(reader, new TypeToken<List<Language>>() {}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Find and update the author with the specified name
        for (int i = 0; i < languageList.size(); i++) {
            Language language  = languageList.get(i);
            if (language.getLanguage().equals(languageName)) {
                languageList.set(i, updatedLanguage); // Update author information
                break; // Exit loop once the author is found and updated
            }
        }

        // Write the updated list back to the file
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(languageList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteLanguage(String languageName) throws RemoteException {
        File file = new File("src/main/java/server/database/json/language.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()) { //Checks if the File Exist on the Database Folder
            try (Reader reader = new FileReader(file)) {
                languageList = gson.fromJson(reader, new TypeToken<List<Language>>() {}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Find and remove the language with the specified name
        Iterator<Language> iterator = languageList.iterator();
        while (iterator.hasNext()) {
            Language language = iterator.next();
            if (language.getLanguage().equals(languageName)) {
                iterator.remove();
                break; // Exit loop once the language is found and removed
            }
        }

        // Write the updated list back to the file
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(languageList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRole(String roleName, Role updatedRole) throws RemoteException{
        File file = new File("src/main/java/server/database/json/role.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()) { //Checks if the File Exist on the Database Folder
            try (Reader reader = new FileReader(file)) {
                roleList = gson.fromJson(reader, new TypeToken<List<Role>>() {}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Find and update the author with the specified name
        for (int i = 0; i < roleList.size(); i++) {
            Role role  = roleList.get(i);
            if (role.getRole().equals(roleName)) {
                roleList.set(i, updatedRole); // Update author information
                break; // Exit loop once the author is found and updated
            }
        }

        // Write the updated list back to the file
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(roleList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteRole(String roleName) throws RemoteException { ////added this - Don
        File file = new File("src/main/java/server/database/json/role.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()) { //Checks if the File Exist on the Database Folder
            try (Reader reader = new FileReader(file)) {
                roleList = gson.fromJson(reader, new TypeToken<List<Role>>() {}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Find and remove the role with the specified name
        Iterator<Role> iterator = roleList.iterator();
        while (iterator.hasNext()) {
            Role role = iterator.next();
            if (role.getRole().equals(roleName)) {
                iterator.remove();
                break; // Exit loop once the role is found and removed
            }
        }

        // Write the updated list back to the file
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(roleList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateAccount(String id, String username, String password, String role, boolean updatedStatus) throws RemoteException {
        File file = new File("src/main/java/server/database/json/accounts.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                accountsList = gson.fromJson(reader, new TypeToken<List<Accounts>>() {}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (Accounts accounts: accountsList) {
            accounts.setId(id);
            accounts.setUsername(username);
            accounts.setPassword(password);
            accounts.setRole(role);
            updatedStatus = !accounts.getStatus(); // Toggling the status
            accounts.setStatus(updatedStatus);

            // Convert boolean status to string for display
            String statusString = updatedStatus ? "Available" : "Not Available";
            accounts.setStatusString(statusString);
            break;
        }

        // Write the updated list back to the file
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(accountsList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void deleteAccount(int selectedRow) throws RemoteException {
        File file = new File("src/main/java/server/database/json/accounts.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                accountsList = gson.fromJson(reader, new TypeToken<List<Accounts>>() {}.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Remove the account at the specified index
        accountsList.remove(selectedRow);

        // Write the updated list back to the file
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(accountsList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateAccount(int selectedRow, Accounts updatedAccount) throws RemoteException {

    }

    private void notifyCallbacks(Books books) {
        for (ClientCallBackInterface callback : callbacks) {
            try {
                callback.updateBook(books);
                callback.updateHomeBook(books);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    private void notifyRequestCallbacks(BooksRequestee booksRequestee){
        for (ClientCallBackInterface callback : callbacks) {
            try {
                callback.updateRequestedBook(booksRequestee);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyBorrowerCallbacks(BooksBorrower booksBorrower){
        for (ClientCallBackInterface callback : callbacks) {
            try {
                callback.updateBorrowedBooks(booksBorrower);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //Method  update the request
    @Override
    public void updateBorrowedBooks(BooksBorrower booksBorrower) throws RemoteException {
        List<BooksBorrower> borrowedBooks = getBooksBorrowerDate();
        borrowedBooks.add(booksBorrower);
        try (FileWriter writer = new FileWriter("src/main/java/server/database/json/borrowedBooks.json")){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(borrowedBooks, writer);
        } catch (IOException e) {
            System.out.println(e);
        }

        updateRequest(booksBorrower, true);
        notifyBorrowerCallbacks(booksBorrower);
    }

    //Method to update the request status into true
    public void updateRequest(BooksBorrower booksBorrower, boolean status) throws RemoteException{
        // Read the JSON file
        try (Reader reader = new FileReader("src/main/java/server/database/json/requests.json")) {
            // Parse the JSON file into an array of Book objects
            Gson gson = new Gson();
            BooksRequestee[] booksRequestees = gson.fromJson(reader, BooksRequestee[].class);

            // Update the data
            for (BooksRequestee booksRequestee : booksRequestees) {
                if (booksRequestee.getAccountId().equals(booksBorrower.getAccountId()) && booksRequestee.getBookISBN().equals(booksBorrower.getBookISBN())) {
                    booksRequestee.setStatus(status);
                    break;
                }
            }

            // Convert the updated array of Book objects to JSON
            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            String updatedJson = prettyGson.toJson(booksRequestees);

            // Write the updated JSON back to the file
            try (FileWriter writer = new FileWriter("src/main/java/server/database/json/requests.json")) {
                writer.write(updatedJson);
                System.out.println("Data updated successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //Method to remove reuqest
    public void removeRequest(BooksBorrower booksBorrower) throws RemoteException {
        BooksRequestee temp = null;
        booksRequesteeList = getBooksRequesteeData();
        for (BooksRequestee booksRequestee : booksRequesteeList){
            if (booksRequestee.getAccountId().equals(booksBorrower.getAccountId()) && booksRequestee.getBookISBN().equals(booksBorrower.getBookISBN())
            && booksRequestee.getStatus().equals(false)){
                temp = booksRequestee;
                break;
            }
        }

        if (temp != null) {
            booksRequesteeList.remove(temp);
            try (FileWriter writer = new FileWriter("src/main/java/server/database/json/requests.json")){
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(booksRequesteeList, writer);

            } catch (IOException e) {
                System.out.println("Error updating borrowedBooks.json: " + e.getMessage());
            }
        } else {
            System.out.println("Book not found in borrowedBooks.json!");
        }
        System.out.println("Callback refresh");
    }

    private void notifyRefreshRequest() throws RemoteException {
        for (ClientCallBackInterface callback : callbacks) {
            try {
                callback.refreshRequestedBooks();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeBorrowedBook(BooksBorrower booksBorrower) throws RemoteException {
        List<BooksBorrower> borrowedBooks = getBooksBorrowerDate();
        BooksBorrower booksToRemove = null;
        for (BooksBorrower book : getBooksBorrowerDate()) {
            if (book.getAccountId().equals(booksBorrower.getAccountId()) && book.getBookISBN().equals(booksBorrower.getBookISBN())) {
                booksToRemove = book;
                break;
            }
        }

        if (booksToRemove != null) {
            borrowedBooks.remove(booksToRemove);
            try (FileWriter writer = new FileWriter("src/main/java/server/database/json/borrowedBooks.json")){
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(borrowedBooks, writer);
                System.out.println("Borrowed book information updated successfully!");
            } catch (IOException e) {
                System.out.println("Error updating borrowedBooks.json: " + e.getMessage());
            }
        } else {
            System.out.println("Book not found in borrowedBooks.json!");
        }

        //Refreshes the Borrowed Books
        System.out.println("Send Callback");
        notifyReturnedBook();
    }

    private void notifyReturnedBook() {
        for (ClientCallBackInterface callback : callbacks) {
            try {
                callback.returnedBook();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }



}
