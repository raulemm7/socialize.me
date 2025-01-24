package org.example.labx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.labx.domain.Utilizator;

import java.io.IOException;

public class SearchViewController extends Controller{
    private Utilizator searchBarUserFound;

    @FXML
    public TextField searchBar;
    @FXML
    public HBox hboxUserFound;
    @FXML
    public Label labelUserFound;
    @FXML
    public Button buttonMessageUserFound;
    @FXML
    public Button buttonAddUserFound;
    @FXML
    public Label labelSugestion1;
    @FXML
    public Button buttonAddSugestion1;
    @FXML
    public Label labelSugestion2;
    @FXML
    public Button buttonAddSugestion2;
    @FXML
    public Button searchTextFieldButton;

    @FXML
    public Button homeButton;
    @FXML
    public Button userButton;
    @FXML
    public Button friendsButton;
    @FXML
    public Button messageButton;

    @FXML
    private Label usernameLabel;

    @FXML
    public void initialize() {
        this.usernameLabel.setText(getCurrentUser().getFirstName());

        setFoundInfoInvisible();
        randomizeSugestions();

        searchBar.setOnKeyTyped(keyEvent -> this.setFoundInfoInvisible());
        searchTextFieldButton.setOnAction(actionEvent -> handleSearch());
        buttonAddUserFound.setOnAction(actionEvent -> handleNewFriendRequest());
        buttonMessageUserFound.setOnAction(actionEvent -> handleMessageFoundUser());

        homeButton.setOnAction(actionEvent -> handleHome());
        userButton.setOnAction(actionEvent -> handleUser());
        friendsButton.setOnAction(actionEvent -> handleFriends());
        messageButton.setOnAction(actionEvent -> handleMessage());
    }

    private void setFoundInfoInvisible() {
        hboxUserFound.setVisible(false);
        hboxUserFound.setManaged(false);
        labelUserFound.setVisible(false);
        labelUserFound.setManaged(false);
        buttonMessageUserFound.setVisible(false);
        buttonMessageUserFound.setManaged(false);
        buttonAddUserFound.setVisible(false);
        buttonAddUserFound.setManaged(false);
    }

    private void handleSearch() {
        // TODO: continue with the search result
        String usernames = searchBar.getText();
        String[] names = usernames.split(" ");

        this.searchBar.setText("");
        try {
            Utilizator foundUser = this.getUserService().getUser(names[0], names[1]);

            this.searchBarUserFound = foundUser;
            this.labelUserFound.setText(names[0] + " " + names[1]);

            if(this.getFriendshipService().existFriendship(getCurrentUser(), foundUser)) {
                // afisez buttonul de message
                this.setDataVisible(this.buttonMessageUserFound);
            } else {
                // afisez buttonul de add
                this.setDataVisible(this.buttonAddUserFound);
            }
        } catch (RuntimeException e) {
            // TODO: afisez mesaj de eroare
            this.labelUserFound.setText("User not found!");
            this.setDataVisible(null);
        }

    }

    private void randomizeSugestions() {
        // TODO: give two users that are not friends with this one
    }

    private void handleHome() {
        ControllerFactory.getInstance().runPage(ControllerType.HOMEPAGE, this.homeButton);
    }

    private void handleUser() {
        ControllerFactory.getInstance().runPage(ControllerType.USERPAGE, this.userButton);
    }

    private void handleFriends() {
        ControllerFactory.getInstance().runPage(ControllerType.FRIENDSPAGE, this.friendsButton);
    }

    private void handleMessage() {
        ControllerFactory.getInstance().runPage(ControllerType.MESSAGEPAGE, this.messageButton);
    }

    private void setDataVisible(Button buttonChanged) {
        this.hboxUserFound.setVisible(true);
        this.hboxUserFound.setManaged(true);
        this.labelUserFound.setVisible(true);
        this.labelUserFound.setManaged(true);
        if(buttonChanged != null) {
            buttonChanged.setVisible(true);
            buttonChanged.setManaged(true);
        }
    }

    private void handleNewFriendRequest() {
        if(!this.hboxUserFound.isVisible()) {
            return;
        }

        var result = this.getFriendRequestsService().addFriendRequest(getCurrentUser(), searchBarUserFound);
        this.showInfoBox(result);
    }

    private void showInfoBox(String message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/labx/infoBox_view.fxml"));

            Parent root = loader.load();
            InfoBoxController controller = loader.getController();

            controller.setTextToInfo(message);

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setTitle("Info");
            stage.setScene(scene);

            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(this.homeButton.getScene().getWindow());
            stage.show();

            this.setFoundInfoInvisible();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleMessageFoundUser() {
        this.getMessageService().beginConversation(getCurrentUser(), searchBarUserFound);
        ControllerFactory.getInstance().runPage(ControllerType.MESSAGEPAGE, this.buttonMessageUserFound);
    }
}
