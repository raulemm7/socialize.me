package org.example.labx.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.labx.domain.Message;
import org.example.labx.domain.Utilizator;
import org.example.labx.utils.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class MessagePageController extends Controller{
    private ObservableList<Utilizator> usersOnListView;
    private Utilizator selectedUserForMessage = null;
    private LocalDate lastMessageDate;
    private final Long messagesPerQuery = 10L;
    private Long pageNumber = 1L;
    private Boolean isAlreadyLoading = false;
    private Long totalNrOfMessages = 0L;
    private Message oldestMessage;


    @FXML
    private Button homeButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button friendsButton;
    @FXML
    private Button userButton;
    @FXML
    private Label usernameLabel;

    @FXML
    private ListView<String> conversationsList;

    @FXML
    private VBox messageZone;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private Label loadingLabel;

    @FXML
    private TextField messageField;
    @FXML
    private Button sendMessageButton;

    @FXML
    public void initialize() {
        this.loadingLabel.setVisible(false);
        this.loadingLabel.setManaged(false);

        this.usernameLabel.setText(getCurrentUser().getFirstName());
        this.chatScrollPane.setStyle("-fx-border-color: transparent");

        this.homeButton.setOnAction(actionEvent -> ControllerFactory.getInstance().runPage(ControllerType.HOMEPAGE, this.homeButton));
        this.searchButton.setOnAction(actionEvent -> ControllerFactory.getInstance().runPage(ControllerType.SEARCHPAGE, this.searchButton));
        this.friendsButton.setOnAction(actionEvent -> ControllerFactory.getInstance().runPage(ControllerType.FRIENDSPAGE, this.friendsButton));
        this.userButton.setOnAction(actionEvent -> ControllerFactory.getInstance().runPage(ControllerType.USERPAGE, this.userButton));

        fillUsersWithConv();
        conversationsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                for(var user : usersOnListView) {
                    if(newValue.equals(user.getNames())) {
                        System.out.println(user.getNames() + "\n" + "acum dau load la new user");
                        selectedUserForMessage = user;
                        System.out.println(selectedUserForMessage.getNames());
                        totalNrOfMessages = 0L;
                        pageNumber = 1L;
                        uploadConversation();
                    }
                }
            }
        });

        chatScrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.doubleValue() == 0.0) {
                System.out.println("am atins coltul sus");
                loadMoreMessages();
            }
        });

        this.sendMessageButton.setOnAction(actionEvent -> handleMessageSend());
        this.messageField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleMessageSend();
            }
        });
    }

    private void fillUsersWithConv() {
        this.usersOnListView = FXCollections.observableArrayList(
                this.getMessageService().getConversations(getCurrentUser()).values()
        );

        ObservableList<String> items = FXCollections.observableArrayList(
                usersOnListView.stream()
                        .map(Utilizator::getNames)
                        .toList()
        );

        this.conversationsList.setItems(items);
    }

    private void uploadConversation() {
        this.messageZone.getChildren().clear();

        ObservableList<Message> messagesWithUser = FXCollections.observableArrayList(
                this.getMessageService().getMessagesForUsers(getCurrentUser(), this.selectedUserForMessage, new Pageable(messagesPerQuery, pageNumber))
                        .getEntitiesOnPage()
        );
        pageNumber += 1;         // setez urmatoarea pagina
        totalNrOfMessages += messagesWithUser.size();
        oldestMessage = messagesWithUser.get(0);

        lastMessageDate = messagesWithUser.get(0).getSendAt().toLocalDate();
        setMessagesDateOnScreen(messageZone.getChildren().size(), messagesWithUser.get(0).getSendAt().toLocalDate());

        for(var message : messagesWithUser) {
            if (!message.getSendAt().toLocalDate().equals(lastMessageDate)) {
                setMessagesDateOnScreen(messageZone.getChildren().size(), message.getSendAt().toLocalDate());
                lastMessageDate = message.getSendAt().toLocalDate();
            }

            if(message.getSender().equals(getCurrentUser())) {
                uploadMessageOnScreen(message, Pos.CENTER_RIGHT, messageZone.getChildren().size());
            }
            else {
                uploadMessageOnScreen(message, Pos.CENTER_LEFT, messageZone.getChildren().size());
            }

        }
    }

    private void setMessagesDateOnScreen(int whereTo, LocalDate messageDate) {
        String s;
        if(LocalDateTime.now().toLocalDate().equals(messageDate)) {
            s = "Today";
        }
        else if (LocalDateTime.now().toLocalDate().minusDays(1).isEqual(messageDate)) {
            s = "Yesterday";
        }
        else {
            s = messageDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("en")) + ", " +
                    messageDate.format(DateTimeFormatter.ofPattern("dd")) + " " +
                    messageDate.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("en"));
        }

        Label dateLabel = new Label(s);

        dateLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: gray; -fx-padding: 10px;");
        dateLabel.setAlignment(Pos.CENTER);

        HBox dateBox = new HBox(dateLabel);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.setPadding(new Insets(10, 0, 10, 0));

        messageZone.getChildren().add(whereTo, dateBox);
    }

    private void uploadMessageOnScreen(Message message, Pos alignment, int whereTo) {
        if (message != null && !message.isEmpty()) {
            HBox messageBox = new HBox();
            messageBox.setAlignment(alignment);

            Label messageLabel = new Label(message.getMessage());
            messageLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 45px; -fx-background-color: #e6e0f1;");
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(230);

            Label timestampLabel = new Label(message.getSendAt().format(DateTimeFormatter.ofPattern("HH:mm")));
            timestampLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

            VBox messageContainer = new VBox();
            messageContainer.getChildren().addAll(messageLabel, timestampLabel);
            messageContainer.setSpacing(1);
            messageContainer.setAlignment(alignment);
            messageContainer.setMaxHeight(240);

            if (alignment == Pos.CENTER_RIGHT) {
                VBox.setMargin(timestampLabel, new Insets(0, 10, 0, 0));
                HBox.setMargin(messageContainer, new Insets(2, 30, 2, 0));
            } else {
                VBox.setMargin(timestampLabel, new Insets(0));
                HBox.setMargin(messageContainer, new Insets(2, 0, 2, 30));
            }

            messageBox.getChildren().add(messageContainer);
            messageZone.getChildren().add(whereTo, messageBox);

            if(whereTo != 0)
                Platform.runLater(this::scrollToBottom);
        }
    }

    private void loadMoreMessages() {
        this.loadingLabel.setVisible(true);
        this.loadingLabel.setManaged(true);

        if(isAlreadyLoading) return;
        isAlreadyLoading = true;

        if(totalNrOfMessages >= getMessageService().getNrOfMessageB2Users(getCurrentUser(), selectedUserForMessage)) {
            isAlreadyLoading = false;
            this.loadingLabel.setVisible(false);
            this.loadingLabel.setManaged(false);
            return;
        }

        ObservableList<Message> messagesWithUser = FXCollections.observableArrayList(
                this.getMessageService().getMessagesForUsers(getCurrentUser(), this.selectedUserForMessage, new Pageable(messagesPerQuery, pageNumber))
                        .getEntitiesOnPage()
        );
        totalNrOfMessages += messagesWithUser.size();
        pageNumber += 1L;

        // dau delete la data
        this.messageZone.getChildren().remove(0);

        for(var i = messagesWithUser.size() - 1; i >= 0; i--) {
            var message = messagesWithUser.get(i);

            if(!message.getSendAt().toLocalDate().equals(oldestMessage.getSendAt().toLocalDate()))
                setMessagesDateOnScreen(0, oldestMessage.getSendAt().toLocalDate());
            oldestMessage = message;

            if(message.getSender().getId().equals(getCurrentUser().getId()))
                uploadMessageOnScreen(message, Pos.CENTER_RIGHT, 0);
            else
                uploadMessageOnScreen(message, Pos.CENTER_LEFT, 0);
        }

        // mai adaug o data
        setMessagesDateOnScreen(0, oldestMessage.getSendAt().toLocalDate());

        isAlreadyLoading = false;
        this.chatScrollPane.setVvalue((double) 1/Math.pow(2, pageNumber - 2) + 0.60 / (pageNumber - 2));
        this.loadingLabel.setVisible(false);
        this.loadingLabel.setManaged(false);
    }

    private void scrollToBottom() {
        chatScrollPane.layout();
        chatScrollPane.setVvalue(1.0);
    }

    private void handleMessageSend() {
        String mesg = messageField.getText();
        messageField.clear();

        if("".equals(mesg))
            return;
        if(selectedUserForMessage == null)
            return;

        try {
            var message = this.getMessageService().sendMessage(getCurrentUser(), selectedUserForMessage, mesg);

            if (!message.getSendAt().toLocalDate().equals(lastMessageDate)) {
                setMessagesDateOnScreen(messageZone.getChildren().size(), message.getSendAt().toLocalDate());
                lastMessageDate = message.getSendAt().toLocalDate();
            }

            uploadMessageOnScreen(message, Pos.CENTER_RIGHT, messageZone.getChildren().size());
        }
        catch(Exception e) {
            ControllerFactory.getInstance().setInfoBoxMessage(e.getMessage());
            ControllerFactory.getInstance().runPage(ControllerType.INFOBOX, this.homeButton);
        }
    }
}
