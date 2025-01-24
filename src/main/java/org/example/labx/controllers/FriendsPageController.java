package org.example.labx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.example.labx.domain.FriendRequest;
import org.example.labx.domain.Utilizator;

public class FriendsPageController extends Controller{
    private Utilizator selectedFriend;
    private ObservableList<Utilizator> friends;
    private Utilizator selectedUserOnRequest;
    private ObservableList<FriendRequest> friendsOnRequest;
    private String selectedObject;

    private Long currentPageNumber;
    private Long pageSize;
    private Long friendsNumber;

    @FXML
    public Label labelNrRequests;
    @FXML
    public Label labelNrFriends;

    @FXML
    public Button acceptRequestButton;
    @FXML
    public Button declineRequestButton;
    @FXML
    public Button deleteFriendButton;

    @FXML
    public Button homeButton;
    @FXML
    public Button searchButton;
    @FXML
    public Button messageButton;
    @FXML
    public Button userButton;

    @FXML
    public Label usernameLabel;

    @FXML
    public ListView<String> friendsList;
    @FXML ListView<String> requestsList;

    @FXML
    public Button backListButton;
    @FXML
    public Button forwardListButton;

    @FXML
    public void initialize() {
        this.backListButton.setVisible(false);
        this.backListButton.setManaged(false);

        this.backListButton.setOnAction(event -> goBackFriendsPage());
        this.forwardListButton.setOnAction(event -> goForwardFriendsPage());

        currentPageNumber = 1L;
        pageSize = 8L;

        this.usernameLabel.setText(getCurrentUser().getFirstName());

        fillFriendsList();
        friendsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                for(var fr : friends) {
                    if(newValue.equals(fr.getFirstName() + " " + fr.getLastName())) {
                        selectedFriend = fr;
                    }
                }
            }
        });

        fillRequestsList();
        requestsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                for(var fr : friendsOnRequest) {
                    if(newValue.equals(fr.userFromData())) {
                        selectedUserOnRequest = fr.getUserFrom();
                        selectedObject = fr.userFromData();
                    }
                }
            }
        });

        this.deleteFriendButton.setOnAction(actionEvent -> handleDeleteEvent());

        this.acceptRequestButton.setOnAction(actionEvent -> handleAcceptFriendRequest());
        this.declineRequestButton.setOnAction(actionEvent -> handleDeclineFriendRequest());

        this.homeButton.setOnAction(actionEvent -> ControllerFactory.getInstance().runPage(ControllerType.HOMEPAGE, this.homeButton));
        this.searchButton.setOnAction(actionEvent -> ControllerFactory.getInstance().runPage(ControllerType.SEARCHPAGE, this.searchButton));
        this.messageButton.setOnAction(actionEvent -> ControllerFactory.getInstance().runPage(ControllerType.MESSAGEPAGE, this.messageButton));
        this.userButton.setOnAction(actionEvent -> ControllerFactory.getInstance().runPage(ControllerType.USERPAGE, this.userButton));
    }

    private void goForwardFriendsPage() {
        this.currentPageNumber += 1L;
        this.friends.clear();

        fillFriendsList();

        this.backListButton.setVisible(true);
        this.backListButton.setManaged(true);
    }

    private void goBackFriendsPage() {
        this.currentPageNumber -= 1L;
        this.friends.clear();

        fillFriendsList();

        this.forwardListButton.setVisible(true);
        this.forwardListButton.setManaged(true);
    }

    private void fillFriendsList() {
        var page = this.getFriendshipService().getAllUsersFriends(getCurrentUser().getId(), currentPageNumber, pageSize);
        friends = FXCollections.observableArrayList(page.getEntitiesOnPage());

        ObservableList<String> items = FXCollections.observableArrayList(friends.stream()
                .map(Utilizator::getNames)
                .toList());

        friendsList.setItems(items);
        this.labelNrFriends.setText(String.valueOf(page.getTotalNumberOfElements()));
        friendsNumber = page.getTotalNumberOfElements();

        if(currentPageNumber == 1) {
            this.backListButton.setVisible(false);
            this.backListButton.setManaged(false);
        }
        if(currentPageNumber * pageSize >= page.getTotalNumberOfElements()) {
            this.forwardListButton.setVisible(false);
            this.forwardListButton.setManaged(false);
        }
    }

    private void fillRequestsList() {
        friendsOnRequest = FXCollections.observableArrayList(this.getFriendRequestsService()
                                                    .getFriendRequests(getCurrentUser()).values());

        ObservableList<String> items = FXCollections.observableArrayList(friendsOnRequest.stream()
                .map(FriendRequest::userFromData)
                .toList());

        requestsList.setItems(items);
        this.labelNrRequests.setText(String.valueOf(requestsList.getItems().size()));
    }

    private void handleDeleteEvent() {
        if (selectedFriend == null) {
            ControllerFactory.getInstance().setInfoBoxMessage("No friend selected!");
            ControllerFactory.getInstance().runPage(ControllerType.INFOBOX, this.deleteFriendButton);
            return;
        }

        var message = this.getFriendshipService().deleteFriendship(getCurrentUser(), selectedFriend);
        friends.remove(selectedFriend);
        friendsList.getItems().remove(selectedFriend.getNames());
        selectedFriend = null;

        fillFriendsList();

        ControllerFactory.getInstance().setInfoBoxMessage(message);
        ControllerFactory.getInstance().runPage(ControllerType.INFOBOX, this.deleteFriendButton);
    }

    private void deleteFriendRequestFromStorage() {
        FriendRequest fr = null;
        for(var frr : friendsOnRequest) {
            if(frr.getUserFrom().equals(selectedUserOnRequest)) {
                fr = frr;
            }
        }
        friendsOnRequest.remove(fr);
        this.getFriendRequestsService().removeFriendRequest(fr);
    }

    private void handleAcceptFriendRequest() {
        if(selectedUserOnRequest == null) {
            ControllerFactory.getInstance().setInfoBoxMessage("No user selected!");
            ControllerFactory.getInstance().runPage(ControllerType.INFOBOX, this.acceptRequestButton);
            return;
        }

        var message = this.getFriendshipService().addFriendships(getCurrentUser(), selectedUserOnRequest);
        if(message.equals("Congrats! Now you are friends!")){
            deleteFriendRequestFromStorage();

            requestsList.getItems().remove(selectedObject);
            this.labelNrRequests.setText(String.valueOf(requestsList.getItems().size()));

            selectedUserOnRequest = null;

            friendsList.getItems().clear();
            friends.clear();
            fillFriendsList();

            ControllerFactory.getInstance().setInfoBoxMessage(message);
            ControllerFactory.getInstance().runPage(ControllerType.INFOBOX, this.acceptRequestButton);
        }
    }

    private void handleDeclineFriendRequest() {
        if(selectedUserOnRequest == null) {
            ControllerFactory.getInstance().setInfoBoxMessage("No user selected!");
            ControllerFactory.getInstance().runPage(ControllerType.INFOBOX, this.acceptRequestButton);
            return;
        }

        deleteFriendRequestFromStorage();

        requestsList.getItems().remove(selectedObject);
        this.labelNrRequests.setText(String.valueOf(requestsList.getItems().size()));

        selectedUserOnRequest = null;

        ControllerFactory.getInstance().setInfoBoxMessage("Friend request deleted!");
        ControllerFactory.getInstance().runPage(ControllerType.INFOBOX, this.acceptRequestButton);
    }
}
