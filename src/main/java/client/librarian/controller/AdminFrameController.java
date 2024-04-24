package client.librarian.controller;

import client.librarian.model.*;
import client.librarian.view.*;
import common.Accounts;
import common.Admin;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AdminFrameController {

    protected AdminFrame view;
    protected ManageLibraryPanel libraryPanel;
    protected ManageAuthorPanel authorPanel;
    protected ManageCategoryPanel categoryPanel;
    protected ManageLanguagePanel languagePanel;
    protected ManageAccountPanel accountPanel;
    protected ManageRolePanel rolePanel;
    protected IssueBookPanel issueBookPanel;
    protected AdminFrameModel model;
    protected ManageLibraryModel libraryModel;
    protected ManageAuthorModel authorModel;
    protected ManageCategoryModel categoryModel;
    protected ManageLanguageModel languageModel;
    protected ManageAccountModel accountModel;
    protected ManageRoleModel roleModel;
    protected IssueBookModel issueBookModel;

    public AdminFrameController(AdminFrame view, ManageLibraryPanel libraryPanel, ManageAuthorPanel authorPanel, ManageCategoryPanel categoryPanel,
                                ManageLanguagePanel languagePanel, ManageAccountPanel accountPanel, ManageRolePanel rolePanel, IssueBookPanel issueBookPanel,
                                AdminFrameModel model, ManageLibraryModel libraryModel, ManageAuthorModel authorModel, ManageCategoryModel categoryModel,
                                ManageLanguageModel languageModel, ManageAccountModel accountModel, ManageRoleModel roleModel, IssueBookModel issueBookModel){
        this.view = view;
        this.libraryPanel = libraryPanel;
        this.authorPanel = authorPanel;
        this.categoryPanel = categoryPanel;
        this.languagePanel = languagePanel;
        this.accountPanel = accountPanel;
        this.rolePanel = rolePanel;
        this.issueBookPanel = issueBookPanel;
        this.model = model;
        this.libraryModel = libraryModel;
        this.authorModel = authorModel;
        this.categoryModel = categoryModel;
        this.languageModel = languageModel;
        this.accountModel = accountModel;
        this.roleModel = roleModel;
        this.issueBookModel = issueBookModel;

        adminFrameController();
    }
    public void adminFrameController(){
        view.setManageLibraryButtonListener( e -> {
            model.switchPanel(view.getBodyPanel(), libraryPanel);
            libraryModel.populateLibraryTable(libraryPanel.getBookTable());
            libraryModel.populateAuthorComboBox(libraryPanel.getAuthorComboBox());
            libraryModel.populateCategoryComboBox(libraryPanel.getCategoryComboBox());
            libraryModel.populateLanguageComboBox(libraryPanel.getLanguageComboBox());
            model.setAvailabilityComboBox(libraryPanel.getStatusComboBox());
        });
        view.setManageAuthorButtonListener( e -> {
            model.switchPanel(view.getBodyPanel(), authorPanel);
            authorModel.populateAuthorTable(authorPanel.getAuthorTable());
            authorModel.setStatusActivityComboBox(authorPanel.getStatusComboBox());
        });
        view.setManageCategoryButtonListener( e -> {
            model.switchPanel(view.getBodyPanel(), categoryPanel);
            categoryModel.populateCategoryTable(categoryPanel.getCategoryTable());
            categoryModel.setStatusActivityComboBox(categoryPanel.getStatusComboBox());
        });
        view.setManageLanguageButtonListener( e -> {
            model.switchPanel(view.getBodyPanel(), languagePanel);
            languageModel.populateLanguageTable(languagePanel.getLanguageTable());
            languageModel.setStatusActivityComboBox(languagePanel.getStatusComboBox());
        });
        view.setManageAccountsButtonListener( e -> {
            model.switchPanel(view.getBodyPanel(), accountPanel);
            accountModel.populateAccountTable(accountPanel.getAccountTable());
            accountModel.setStatusActivityComboBox(accountPanel.getStatusComboBox());
            accountModel.populateRoleComboBox(accountPanel.getRoleComboBox());
        });
        view.setManageRoleButtonListener( e -> {
            model.switchPanel(view.getBodyPanel(), rolePanel);
            roleModel.populateRoleTable(rolePanel.getRoleTable());
            roleModel.setStatusActivityComboBox(rolePanel.getStatusComboBox());
        });
        view.setIssueBookButtonListener( e -> {
            model.switchPanel(view.getBodyPanel(), issueBookPanel);
        });
        view.setLogOutButtonListener( e -> {
            view.dispose();
            String username = view.getUserNameLabel();
            String password = view.getUserPassWordLabel();

            Admin admin = new Admin(username, password);
            model.logOut(admin);
        });

        //For the Exit button on the Frame
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?\n" +
                        "You will be logged out.", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION){
                    //Log out the User
                    view.dispose();
                    String username = view.getUserNameLabel();
                    String password = view.getUserPassWordLabel();

                    Admin admin = new Admin(username, password);
                    model.logOut(admin);
                }
            }
        });
    }
}
