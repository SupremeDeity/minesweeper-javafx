package com.uniproject.oop;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class StartScreenController {

    @FXML
    private void newGame() throws IOException {
        App.setRoot("game");
    }

    @FXML
    private void exitGame() throws IOException {
        Platform.exit();
    }

}
