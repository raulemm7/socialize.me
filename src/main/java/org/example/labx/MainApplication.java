package org.example.labx;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.labx.controllers.ControllerFactory;
import org.example.labx.controllers.ControllerType;
import org.example.labx.domain.validators.FriendshipValidator;
import org.example.labx.domain.validators.UtilizatorValidator;
import org.example.labx.repository.FriendRequestsDBRepository;
import org.example.labx.repository.FriendshipDBRepository;
import org.example.labx.repository.MessagesDBRepository;
import org.example.labx.repository.UserDBRepository;
import org.example.labx.service.FriendRequestsService;
import org.example.labx.service.FriendshipService;
import org.example.labx.service.MessageService;
import org.example.labx.service.UserService;

import java.io.IOException;
import java.sql.*;

public class MainApplication extends Application {
    private UserDBRepository userDBRepository;
    private FriendshipDBRepository friendshipDBRepository;
    private FriendRequestsDBRepository friendRequestsDBRepository;
    private MessagesDBRepository messagesDBRepository;

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestsService friendRequestsService;
    private MessageService messageService;

    @Override
    public void start(Stage stage) throws Exception {
        checkConnectionToDataBase();
        initializeData();
        initializeLoginPage(stage);
    }

    private void initializeData() {
        String DATABASE_URL = DatabaseConfig.getDBURL();
        String DATABASE_USER = DatabaseConfig.getDBUser();
        String DATABASE_PASSWORD = DatabaseConfig.getDBPassword();

        this.userDBRepository = new UserDBRepository(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD, new UtilizatorValidator());
        this.friendshipDBRepository = new FriendshipDBRepository(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD, new FriendshipValidator(), this.userDBRepository);
        this.friendRequestsDBRepository = new FriendRequestsDBRepository(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD, this.userDBRepository);
        this.messagesDBRepository = new MessagesDBRepository(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD, this.userDBRepository);

        this.userService = new UserService(this.userDBRepository, this.friendshipDBRepository);
        this.friendshipService = new FriendshipService(this.friendshipDBRepository);
        this.friendRequestsService =  new FriendRequestsService(this.friendRequestsDBRepository);
        this.messageService = new MessageService(this.messagesDBRepository);
    }

    private void initializeLoginPage(Stage stage) throws IOException {
        ControllerFactory.getInstance().setFactoryServices(this.userService, this.friendshipService, this.friendRequestsService, this.messageService);
        ControllerFactory.getInstance().setFirstStage(stage);
        ControllerFactory.getInstance().runPage(ControllerType.LOGINPAGE, null);
    }

    public void checkConnectionToDataBase() {
        try{
            DatabaseConfig.getConnection();
            System.out.println("Connected to database");
        }
        catch(SQLException e){
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}