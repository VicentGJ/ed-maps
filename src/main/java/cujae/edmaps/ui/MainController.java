package cujae.edmaps.ui;

import cujae.edmaps.utils.ViewLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class MainController {

    @FXML
    private void onAddBus(ActionEvent event) throws IOException {
        ViewLoader.newWindow(getClass().getResource("add-form.fxml"), "Add Bus", null);
    }

    @FXML
    private void onAddStop(ActionEvent event) throws IOException {
        ViewLoader.newWindow(getClass().getResource("add-form.fxml"), "Add Stop", null);
    }

    @FXML
    private void onAddConnection(ActionEvent event) throws IOException {
        ViewLoader.newWindow(getClass().getResource("add-connection-form.fxml"), "Add Connection", null);
    }
}