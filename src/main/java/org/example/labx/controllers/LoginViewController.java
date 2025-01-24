package org.example.labx.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.labx.domain.Utilizator;
import org.example.labx.domain.validators.EmailException;
import org.example.labx.domain.validators.FirstNameException;
import org.example.labx.domain.validators.LastNameException;
import org.example.labx.domain.validators.PasswordException;

public class LoginViewController extends Controller{
    private Boolean onSignInContext = true;

    @FXML
    private Label labelText;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button signUpButton;
    @FXML
    private Button signInButton;
    @FXML
    private Button exitButton;
    @FXML
    private HBox hboxFields;
    @FXML
    private TextField firstnameField;
    @FXML
    private TextField lastnameField;
    @FXML
    private Label labelErrorFirstname;
    @FXML
    private Label labelErrorLastname;
    @FXML
    private Label labelErrorEmail;
    @FXML
    private Label labelErrorPassword;

    @FXML
    public void initialize() {
        setFieldsInvisible();

        addTextFieldListeners();

        signInButton.setOnAction(actionEvent -> {
            if(onSignInContext) {
                this.handleSignIn();
            }
            else
                this.handleCreateAccount();
        });
        signUpButton.setOnAction(actionEvent -> {
            if(onSignInContext) {
                this.handleSignUp();
            }
            else {
                this.handleBackToSignIn();
            }
        });
        exitButton.setOnAction(actionEvent -> {
            Stage currentStage = (Stage) this.exitButton.getScene().getWindow();
            currentStage.close();
        });
    }

    private void setFieldsInvisible() {
        for(Node node : hboxFields.getChildren()) {
            node.setVisible(false);
            node.setManaged(false);
        }
        this.hboxFields.setVisible(false);
        this.hboxFields.setManaged(false);
    }

    private void setFieldsVisible() {
        for(Node node : hboxFields.getChildren()) {
            node.setVisible(true);
            node.setManaged(true);
        }
        this.hboxFields.setVisible(true);
        this.hboxFields.setManaged(true);
    }

    private void handleSignIn() {
        var email = emailField.getText();
        var password = passwordField.getText();

        var user = this.getUserService().authenticateUser(email, password);
        if(user.isPresent()) {
            System.out.println("Logat cu succes!");

            this.openHomePage(user.get());
        }
        else {
            labelErrorEmail.setText("Wrong email or password");
            labelErrorEmail.setVisible(true);
            emailField.setText("");

            labelErrorPassword.setText("Wrong email or password");
            labelErrorPassword.setVisible(true);
            passwordField.setText("");
        }
    }

    private void handleCreateAccount() {
        var firstname = this.firstnameField.getText();
        var lastname = this.lastnameField.getText();
        var email = this.emailField.getText();
        var password = this.passwordField.getText();
        this.clearAllTextFields();

        try {
            var message = this.getUserService().addUser(firstname, lastname, email, password);
            this.showInfoBox(message);
        } catch (FirstNameException e) {
            this.labelErrorFirstname.setText(e.getMessage());
            this.labelErrorFirstname.setVisible(true);
        } catch (LastNameException e) {
            this.labelErrorLastname.setText(e.getMessage());
            this.labelErrorLastname.setVisible(true);
        } catch (EmailException e) {
            this.labelErrorEmail.setText(e.getMessage());
            this.labelErrorEmail.setVisible(true);
        } catch (PasswordException e) {
            this.labelErrorPassword.setText(e.getMessage());
            this.labelErrorPassword.setVisible(true);
        }
    }

    private void handleBackToSignIn() {
        clearAllTextFields();

        onSignInContext = true;
        this.setFieldsInvisible();

        this.labelText.setText("Welcome back! Log in to your account or create a new one");
        this.signInButton.setText("Sign In");
        this.signUpButton.setText("Sign Up");
    }

    private void handleSignUp() {
        clearAllTextFields();

        onSignInContext = false;
        this.setFieldsVisible();

        this.labelText.setText("Enter your data in order to create your account");
        this.signInButton.setText("Create");
        this.signUpButton.setText("Back");
    }

    private void addTextFieldListeners() {
        emailField.setOnKeyTyped(event -> labelErrorEmail.setVisible(false));
        passwordField.setOnKeyTyped(event -> labelErrorPassword.setVisible(false));
        firstnameField.setOnKeyTyped(event -> labelErrorFirstname.setVisible(false));
        lastnameField.setOnKeyTyped(event -> labelErrorLastname.setVisible(false));
    }

    private void clearAllTextFields() {
        emailField.clear();
        passwordField.clear();
        firstnameField.clear();
        lastnameField.clear();

        labelErrorFirstname.setText("");
        labelErrorLastname.setText("");
        labelErrorPassword.setText("");
        labelErrorEmail.setText("");
    }

    private void showInfoBox(String message) {
        ControllerFactory.getInstance().setInfoBoxMessage(message);
        ControllerFactory.getInstance().runPage(ControllerType.INFOBOX, this.signInButton);
    }

    private void openHomePage(Utilizator user) {
        ControllerFactory.getInstance().setCurrentUser(user);
        ControllerFactory.getInstance().runPage(ControllerType.HOMEPAGE, this.signInButton);
    }
}
