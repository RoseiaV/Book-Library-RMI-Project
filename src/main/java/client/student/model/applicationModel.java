package client.student.model;

import javax.swing.*;

public class applicationModel {
    public void switchPanel (JPanel mainPanel, JPanel switchedPanel){
        mainPanel.removeAll();
        mainPanel.add(switchedPanel);
        mainPanel.repaint();
        mainPanel.revalidate();
    }
}
