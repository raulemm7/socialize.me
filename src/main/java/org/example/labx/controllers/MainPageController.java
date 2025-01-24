package org.example.labx.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.util.Collection;

public class MainPageController extends Controller{
    private Collection<String> currentUserNots;
    private volatile boolean listeningActive = true;

    @FXML
    public Label usernameLabel;

    @FXML
    public Button userButton;
    @FXML
    public Button homeButton;
    @FXML
    public Button searchButton;
    @FXML
    public Button friendsButton;
    @FXML
    public Button messageButton;

    @FXML
    private HBox hboxNotification;
    @FXML
    private Label labelNotification;
    @FXML
    private Button seeNotificationButton;
    @FXML
    private Button closeNotificationButton;

    @FXML
    public void initialize() {
        this.startNotificationsListener();

        this.setNotificationInvisible();

        this.usernameLabel.setText(this.getCurrentUser().getFirstName());
        this.seeNotificationButton.setOnAction(actionEvent -> {
            ControllerFactory.getInstance().runPage(ControllerType.FRIENDSPAGE, this.seeNotificationButton);
            // setez acea notificare/friendrequest ca falsa
            setFriendRequestsFalse();
        });
        this.closeNotificationButton.setOnAction(actionEvent -> {
            this.setNotificationInvisible();
            // setez la fel
            setFriendRequestsFalse();
        });

        userButton.setOnAction(actionEvent -> handleUserButton());
        homeButton.setOnAction(actionEvent -> handleHomeButton());
        searchButton.setOnAction(actionEvent -> handleSearchButton());
        friendsButton.setOnAction(actionEvent -> handleFriendsButton());
        messageButton.setOnAction(actionEvent -> handleMessageButton());
    }

    private void setNotificationInvisible() {
        this.hboxNotification.setVisible(false);
        this.hboxNotification.setManaged(false);
        this.labelNotification.setVisible(false);
        this.labelNotification.setManaged(false);
        this.seeNotificationButton.setVisible(false);
        this.seeNotificationButton.setManaged(false);
        this.closeNotificationButton.setVisible(false);
        this.closeNotificationButton.setManaged(false);
    }

    private void setNotificationVisible() {
        this.hboxNotification.setVisible(true);
        this.hboxNotification.setManaged(true);
        this.labelNotification.setVisible(true);
        this.labelNotification.setManaged(true);
        this.seeNotificationButton.setVisible(true);
        this.seeNotificationButton.setManaged(true);
        this.closeNotificationButton.setVisible(true);
        this.closeNotificationButton.setManaged(true);
    }

    private void handleHomeButton() {
        // TODO: nothing to do, eventually a refresh
        System.out.println("Home button clicked");
    }

    private void handleSearchButton() {
        ControllerFactory.getInstance().runPage(ControllerType.SEARCHPAGE, this.searchButton);

    }

    private void handleFriendsButton() {
        ControllerFactory.getInstance().runPage(ControllerType.FRIENDSPAGE, this.friendsButton);
    }

    private void handleMessageButton() {
        ControllerFactory.getInstance().runPage(ControllerType.MESSAGEPAGE, this.messageButton);
    }

    private void handleUserButton() {
        ControllerFactory.getInstance().runPage(ControllerType.USERPAGE, this.userButton);
    }

    private void startNotificationsListener() {
        new Thread(() -> {
            while(listeningActive) {
                currentUserNots = getFriendRequestsService().getNots(getCurrentUser().getId());

                if(!currentUserNots.isEmpty()) {
                    System.out.println("Found notifications at: " + LocalDateTime.now());

                    Platform.runLater(() -> {
                        if(currentUserNots.size() == 1) {
                            showNotification("You have a new friend request!");
                        }
                        if(currentUserNots.size() >= 2) {
                            showNotification("You have new friend requests!");
                        }

                    });
                    listeningActive = false;
                }

                try {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e) {
                    System.out.println("Sleep interrupted");
                }
                System.out.println("Still running on user: " + getCurrentUser().getFirstName());
            }
        }).start();
    }

    private void showNotification(String message) {
        this.labelNotification.setText(message);
        this.setNotificationVisible();
    }

    private void setFriendRequestsFalse() {
        for(var not : currentUserNots) {
            Long senderID = Long.valueOf(not.split(",")[0]);

            System.out.print("setez pe false: ");
            System.out.println(getFriendRequestsService().setFriendRequestFalse(senderID, getCurrentUser().getId()));
        }

        this.listeningActive = true;
    }
}
