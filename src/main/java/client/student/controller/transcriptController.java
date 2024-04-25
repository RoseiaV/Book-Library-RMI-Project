package client.student.controller;

import client.student.model.dataAccessClass;
import client.student.view.*;
import client.student.view.booksUtil.bookmark;
import common.Accounts;
import common.Bookmarks;
import common.Books;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class transcriptController {
    private application application;
    private viewBook viewBook;
    private dataAccessClass model;
    private transcriptPanel transcriptPanel;
    private bookmark bookmark;

    public transcriptController(application application, viewBook viewBook, transcriptPanel transcriptPanel, dataAccessClass model){
        this.application = application;
        this.viewBook = viewBook;
        this.transcriptPanel = transcriptPanel;
        this.model = model;

        populateAccountPanel();
        setTranscriptPanelController();
        populateBookmarks();
    }

    public void populateAccountPanel(){
        for (Accounts accounts : dataAccessClass.getAccountList()){
            if (accounts.getId().equals(application.getUserIdLabel().getText()) && accounts.getUsername().equals(application.getUserMailLabel().getText())){
                transcriptPanel.getStudentEmail().setText(accounts.getUsername());
                transcriptPanel.getStudentId().setText(accounts.getId());
                transcriptPanel.getStudentCourse().setText(accounts.getCourse());
                transcriptPanel.getRole().setText(accounts.getRole());
            }
        }
    }

    public void populateBookmarks(){
        for (Bookmarks bookmarks : model.getBookmarkList()){
            List<String> accID = bookmarks.getAccountId();
            for (String thisAcc : accID){
                System.out.println(thisAcc);
                if (thisAcc.equals(application.getUserIdLabel().getText())){
                    bookmark = new bookmark();
                    for (Books books : dataAccessClass.getBookList()){
                        if (bookmarks.getBookId().equals(books.getISBN())){
                            bookmark.getBookISBN().setText(books.getISBN());
                            bookmark.getBooknameLabel().setText(books.getBookName());
                            bookmark.getAuthorLabel().setText(books.getAuthor());
                            bookmark.getCategoryLabel().setText(books.getCategory());

                            //View Book Action Listener
                            bookmark.getViewButton().addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String Status = "";
                                    if (books.getStatus()){
                                        Status = "Available";
                                        viewBook.getSendRequestButton().setEnabled(true);
                                    } else {
                                        Status = "Not Available";
                                        viewBook.getSendRequestButton().setEnabled(false);
                                    }

                                    //Switch the Panel
                                    model.switchPanel(application.getBodyPanel(), viewBook);

                                    try {
                                        ByteArrayInputStream bis = new ByteArrayInputStream(model.getBookImage(books.getFileName()));
                                        BufferedImage originalImage = ImageIO.read(bis);
                                        Image resizedImage = originalImage.getScaledInstance(viewBook.getBookImage().getWidth(), viewBook.getBookImage().getHeight(), Image.SCALE_SMOOTH);
                                        viewBook.getBookImage().setIcon(new ImageIcon(resizedImage));
                                    } catch (IOException ex) {
                                        throw new RuntimeException(ex);
                                    }

                                    viewBook.getBookISBN().setText(books.getISBN());
                                    viewBook.getBookTitle().setText(books.getBookName());
                                    viewBook.getAuthorName().setText(books.getAuthor());
                                    viewBook.getBookLanguage().setText(books.getLanguage());
                                    viewBook.getBookCategory().setText(books.getCategory());
                                    viewBook.getBookPublishedDate().setText(books.getPublishedDate());
                                    viewBook.getBookDescrption().setText(books.getDescription());
                                    viewBook.getBookStatus().setText(Status);

                                    viewBook.getBookmarkButton().addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            if (viewBook.getBookmarkButton().isSelected()){
                                                System.out.println("Bookmarked");

                                                //Add the current LoggedIn accountId of user to the bookmark.json in the server
                                                model.bookmark(new Bookmarks(viewBook.getBookISBN().getText(), application.getUserIdLabel().getText()));
                                            } else {
                                                System.out.println("Un-Bookmarked");

                                                //Remove the current LoggedIn accountId of user to the bookmark.json in the server
                                                model.removeBookmark(new Bookmarks(viewBook.getBookISBN().getText(), application.getUserIdLabel().getText()));
                                            }
                                        }
                                    });
                                }
                            });

                            bookmark.getRemoveButton().addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    //Remove the accountId of the user on the bookmark json file
                                    int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this Bookmark?","Exit Confirmation", JOptionPane.YES_NO_OPTION);
                                    if (answer == JOptionPane.YES_OPTION){
                                        model.removeBookmark(new Bookmarks(bookmark.getBookISBN().getText(), application.getUserIdLabel().getText()));
                                    }
                                }
                            });
                        }
                    }
                    transcriptPanel.getBookmarkPanel().add(bookmark);
                    transcriptPanel.getBookmarkPanel().repaint();
                    transcriptPanel.getBookmarkPanel().revalidate();
                }
            }
        }
    }

    public void setTranscriptPanelController(){
        this.transcriptPanel.setFileButtonListener(e -> {
            System.out.println("File Button clicked");
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setCurrentDirectory(new File("./src/main/java/client/student/res/pictures"));
            int response = jFileChooser.showOpenDialog(null);

            if (response == JFileChooser.APPROVE_OPTION){
                File filePath = new File(jFileChooser.getSelectedFile().getAbsolutePath()); //Get the path of that file
                File pictureName = new File(jFileChooser.getSelectedFile().getName()); //Get the name of the File

                System.out.println("File Path -> "+filePath);
                System.out.println("Picture Name -> "+pictureName);

                ImageIcon imageIcon = new ImageIcon(new ImageIcon(String.valueOf(filePath)).getImage().getScaledInstance(
                        transcriptPanel.getAccountImage().getWidth(), transcriptPanel.getAccountImage().getHeight(), Image.SCALE_SMOOTH
                ));

                transcriptPanel.getAccountImage().setIcon(imageIcon);
            }
        });
        this.transcriptPanel.setRemoveButtonListener( e -> {
            transcriptPanel.getAccountImage().setIcon(new ImageIcon(""));
        });
    }
}
