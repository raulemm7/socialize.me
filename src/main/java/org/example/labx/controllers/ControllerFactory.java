package org.example.labx.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.labx.domain.Utilizator;
import org.example.labx.service.FriendRequestsService;
import org.example.labx.service.FriendshipService;
import org.example.labx.service.MessageService;
import org.example.labx.service.UserService;

public class ControllerFactory {
    private Utilizator currentUser;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestsService friendRequestsService;
    private MessageService messageService;

    private String boxMessage;
    private static ControllerFactory factory = null;

    boolean stageSet = false;
    private Stage firstStage;

    private ControllerFactory() {}

    public static ControllerFactory getInstance() {
        if (factory == null) {
            factory = new ControllerFactory();
        }
        return factory;
    }

    public void setFirstStage(Stage stage) {
        this.firstStage = stage;
        this.stageSet = true;
    }

    public void runPage(ControllerType pageType, Button buttonTrigger) {
        if(this.stageSet) {
            runLoginPage();
            return;
        }

        switch (pageType) {
            case INFOBOX -> runInfoBox(buttonTrigger);
            case HOMEPAGE -> runHomePage();
            case SEARCHPAGE -> runSearchPage();
            case FRIENDSPAGE -> runFriendsPage();
            case MESSAGEPAGE -> runMessagePage();
            case USERPAGE -> runUserPage();
            case LOGINPAGE -> runLoginPage();
        }

        if(pageType != ControllerType.INFOBOX) {
            Stage currentStage = (Stage) buttonTrigger.getScene().getWindow();
            currentStage.close();
        }
    }

    public void setFactoryServices(UserService userService, FriendshipService friendshipService,
                                   FriendRequestsService friendRequestsService, MessageService messageService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestsService = friendRequestsService;
        this.messageService = messageService;
    }

    public void setInfoBoxMessage(String boxMessage) {
        this.boxMessage = boxMessage;
    }

    public void setCurrentUser(Utilizator currentUser) {
        this.currentUser = currentUser;
    }

    private void runGenericPage(FXMLLoader loader) {
        try {
            Parent newWindowRoot = loader.load();

            // Configurăm noua scenă
            Stage newStage = new Stage();
            Scene newScene = new Scene(newWindowRoot);
            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.setScene(newScene);
            newStage.show();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void runInfoBox(Button buttonTrigger) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/labx/infoBox_view.fxml"));

            Parent root = loader.load();
            InfoBoxController controller = loader.getController();

            controller.setTextToInfo(this.boxMessage);

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setTitle("Info");
            stage.setScene(scene);

            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(buttonTrigger.getScene().getWindow());
            stage.show();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void runHomePage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/labx/mainPage_view.fxml"));

        MainPageController controller = new MainPageController();
        controller.setCurrentUser(this.currentUser);
        controller.setServices(this.userService, this.friendshipService, this.friendRequestsService, this.messageService);
        loader.setController(controller);

        this.runGenericPage(loader);
    }

    private void runSearchPage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/labx/searchPage_view.fxml"));

        SearchViewController searchViewController = new SearchViewController();
        searchViewController.setCurrentUser(currentUser);
        searchViewController.setServices(this.userService, this.friendshipService, this.friendRequestsService, this.messageService);
        loader.setController(searchViewController);

        this.runGenericPage(loader);
    }

    private void runFriendsPage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/labx/friendsPage_view.fxml"));

        FriendsPageController controller = new FriendsPageController();
        controller.setCurrentUser(this.currentUser);
        controller.setServices(this.userService, this.friendshipService, this.friendRequestsService, this.messageService);
        loader.setController(controller);

        this.runGenericPage(loader);
    }

    private void runMessagePage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/labx/messagePage_view.fxml"));

        MessagePageController controller = new MessagePageController();
        controller.setCurrentUser(this.currentUser);
        controller.setServices(this.userService, this.friendshipService, this.friendRequestsService, this.messageService);
        loader.setController(controller);

        this.runGenericPage(loader);
    }

    private void runUserPage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/labx/userPage_view.fxml"));

        UserPageController userController = new UserPageController();
        userController.setCurrentUser(currentUser);
        userController.setServices(this.userService, this.friendshipService, this.friendRequestsService, this.messageService);
        loader.setController(userController);

        this.runGenericPage(loader);
    }

    private void runLoginPage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/labx/login_view.fxml"));

        LoginViewController loginViewController = new LoginViewController();
        loginViewController.setServices(this.userService, this.friendshipService, this.friendRequestsService, this.messageService);
        loader.setController(loginViewController);

        if(this.stageSet) {
            try {
                Scene scene = new Scene(loader.load(), 600, 400);
                this.firstStage.initStyle(StageStyle.UNDECORATED);
                this.firstStage.setScene(scene);
                this.firstStage.show();
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
            }
            this.stageSet = false;
        }
        else
            this.runGenericPage(loader);
    }
}
