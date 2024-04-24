package server.controller;

import server.model.ServerMainGUIModel;
import server.view.ServerMainGUI;

public class ServerMainGUIController {

    private ServerMainGUI view;
    private ServerMainGUIModel model;
    public ServerMainGUIController(ServerMainGUI view, ServerMainGUIModel model){
        this.view = view;
        this.model = model;

        buttonsActions();
    }

    public void buttonsActions(){
        this.view.setStartServiceButton( e -> {
            System.out.println("Start Service");
            model.startService();
        });
        this.view.setStopServiceButton( e -> {
            System.out.println("Stop Service");
        });
        this.view.setLogOutButton( e -> {
            System.out.println("Log out");
            model.disposeFrame(view);
        });
    }

}
