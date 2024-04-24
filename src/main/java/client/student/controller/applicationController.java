package client.student.controller;

import client.student.model.applicationModel;
import client.student.model.dataAccessClass;
import client.student.view.*;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class applicationController {
    private application application;
    private homePanel homePanel;
    private libraryPanel libraryPanel;
    private bookShelfPanel bookShelfPanel;
    private transcriptPanel transcriptPanel;
    private dataAccessClass model;
    private applicationModel applicationModel;
    public applicationController(application application, homePanel homePanel, libraryPanel libraryPanel, bookShelfPanel bookShelfPanel,
                                 transcriptPanel transcriptPanel, dataAccessClass model, applicationModel applicationModel){
        this.application = application;
        this.homePanel = homePanel;
        this.libraryPanel = libraryPanel;
        this.bookShelfPanel = bookShelfPanel;
        this.transcriptPanel = transcriptPanel;
        this.model= model;
        this.applicationModel = applicationModel;

        homePanel.getWelcomingLabel().setText("WELCOME " + application.getUserMailLabel().getText());
        setApplicationController();
    }

    public void setApplicationController(){
        this.application.setHomeButtonListener( e -> {
            System.out.println("Home Button");
            homePanel.getWelcomingLabel().setText("WELCOME TO README.");
            applicationModel.switchPanel(application.getBodyPanel(), homePanel);
        });
        this.application.setLibraryButtonListener( e -> {
            System.out.println("Library Button");
            applicationModel.switchPanel(application.getBodyPanel(), libraryPanel);
        });
        this.application.setBookShelfButtonListener( e -> {
            System.out.println("Book Shelf Button");
            applicationModel.switchPanel(application.getBodyPanel(), bookShelfPanel);
        });
        this.application.setTranscriptButtonListener( e -> {
            System.out.println("Transcript Button");
            applicationModel.switchPanel(application.getBodyPanel(), transcriptPanel);
        });

        this.application.setLogOutButtonListener( e -> {
            System.out.println("Log Out Button");
            application.dispose();
            String username = application.getUserMailLabel().getText();
            String id = application.getUserIdLabel().getText();
            model.logOut(username, id);
        });

        this.application.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?\n" +
                        "You will be logged out.", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION){
                    //Log out the User
                    application.dispose();
                    String username = application.getUserMailLabel().getText();
                    String id = application.getUserIdLabel().getText();

                    model.logOut(username, id);
                }
            }
        });
    }
}
