package org.example.labx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserPageController extends Controller{
    @FXML
    public Label usernameLabel;
    @FXML
    public Label labelName;

    @FXML
    public Button logoutButton;
    @FXML
    public Button exitButton;
    @FXML
    public Button updateButton;

    @FXML
    public TextField inputFirstname;
    @FXML
    public TextField inputLastname;

    @FXML
    public Button homeButton;
    @FXML
    public Button searchButton;
    @FXML
    public Button friendsButton;
    @FXML
    public Button messageButton;

    @FXML
    public void initialize() {
        usernameLabel.setText(getCurrentUser().getFirstName());
        labelName.setText(getCurrentUser().getFirstName() + " " + getCurrentUser().getLastName());

        logoutButton.setOnAction(actionEvent -> handleLogOut());
        exitButton.setOnAction(actionEvent -> handleExit());
        updateButton.setOnAction(actionEvent -> handleUpdate());

        homeButton.setOnAction(actionEvent -> handleHome());
        searchButton.setOnAction(actionEvent -> handleSearch());
        friendsButton.setOnAction(actionEvent -> handleFriends());
        messageButton.setOnAction(actionEvent -> handleMessage());
    }

    private void handleLogOut() {
        ControllerFactory.getInstance().runPage(ControllerType.LOGINPAGE, this.logoutButton);
    }

    private void handleExit() {
        Stage currentStage = (Stage) this.exitButton.getScene().getWindow();
        currentStage.close();
    }

    private void handleUpdate() {
        //TODO: cand user ul introduce date si apasa pe update, se modifica numele si prenumele in baza de date
        System.out.println("Still working on it...");
    }

    private void handleHome() {
        ControllerFactory.getInstance().runPage(ControllerType.HOMEPAGE, this.homeButton);
    }

    private void handleSearch() {
        ControllerFactory.getInstance().runPage(ControllerType.SEARCHPAGE, this.searchButton);
    }

    private void handleFriends() {
        ControllerFactory.getInstance().runPage(ControllerType.FRIENDSPAGE, this.friendsButton);
    }

    private void handleMessage() {
        ControllerFactory.getInstance().runPage(ControllerType.MESSAGEPAGE, this.messageButton);
    }
}
