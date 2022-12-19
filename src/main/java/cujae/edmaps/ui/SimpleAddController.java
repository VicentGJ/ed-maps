package cujae.edmaps.ui;

import cujae.edmaps.core.City;
import cujae.edmaps.core.FileManager;
import cujae.edmaps.core.MapsManager;
import cujae.edmaps.utils.Drawer;
import cujae.edmaps.utils.ViewLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SimpleAddController implements Initializable {

    @FXML
    private TextField nameField;
    private AddType type = AddType.STOP;
    private final City city = MapsManager.getInstance().getActualCity();
    @FXML
    Button okButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Stage stage = ViewLoader.getStage();
        switch (stage.getTitle()) {
            case "Add Bus" -> type = AddType.BUS;
            case "Add Stop" -> type = AddType.STOP;
            case "Add City" -> type = AddType.CITY;
        }
        okButton.setDisable(true);
        nameField.setOnKeyTyped(event -> {
            boolean exists = false;
            switch (type) {
                case CITY -> {
                    if (!nameField.getText().isBlank()) {
                        File[] cities = FileManager.getAllConsultDirectories();
                        for (File f : cities) {
                            if (f.getName().equalsIgnoreCase(nameField.getText())) {
                                exists = true;
                                break;
                            }
                        }
                    }
                }
                case BUS -> {
                    if (!nameField.getText().isBlank()) {
                        exists = MapsManager.getInstance().getActualCity().existBus(nameField.getText()) || nameField.getText().equalsIgnoreCase("walking");
                    }
                }
                case STOP -> {
                    if (!nameField.getText().isBlank()) {
                        exists = MapsManager.getInstance().getActualCity().existBusStop(nameField.getText());
                    }
                }
            }
            okButton.setDisable(exists);
        });
    }

    @FXML
    public void onOkButton(ActionEvent event) throws IOException {
        try {
            switch (type) {
                case BUS -> city.addBus(nameField.getText());
                case STOP -> {
                    city.addBusStop(nameField.getText());
                    Drawer drawer = Drawer.getInstance();
                    MainController.setGraphContainer(drawer.draw(null, null));
                }
                case CITY -> {
                    MapsManager.getInstance().createCity(nameField.getText());
                    Drawer drawer = Drawer.getInstance();
                    MainController.setGraphContainer(drawer.draw(null, null));
                }
            }
            ViewLoader.getStage().close();
        } catch (Exception e) {
            PopupController controller = (PopupController) ViewLoader.newWindow(getClass().getResource("popup.fxml"), "Error", null);
            controller.setText("Error", e.getMessage());
            controller.setPrevious("add-form.fxml", ((Stage) nameField.getScene().getWindow()).getTitle());
            ViewLoader.closeWindow(event);
        }
    }

    @FXML
    public void onCancelButton() {
        ViewLoader.getStage().close();
    }
}

enum AddType {
    CITY, BUS, STOP
}