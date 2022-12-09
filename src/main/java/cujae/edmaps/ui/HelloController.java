package cujae.edmaps.ui;

import cujae.edmaps.utils.ViewLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick(ActionEvent event) throws IOException {
        ViewLoader.thisWindow(getClass().getResource("main.fxml"), event);
    }
}