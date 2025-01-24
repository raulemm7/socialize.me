package org.example.labx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class InfoBoxController {
    @FXML
    private Button closeButton;
    @FXML
    private Label textLabel;

    @FXML
    public void initialize() {
        closeButton.setOnAction(actionEvent -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });
    }

    public void setTextToInfo(String text) {
        textLabel.setText(text);
    }
}
