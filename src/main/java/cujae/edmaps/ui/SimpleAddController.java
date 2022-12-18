package cujae.edmaps.ui;

import cujae.edmaps.core.City;
import cujae.edmaps.core.MapsManager;
import cujae.edmaps.utils.Drawer;
import cujae.edmaps.utils.ViewLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SimpleAddController implements Initializable {

    @FXML
    private TextField nameField;
    private AddType type = AddType.STOP;
    private final City city = MapsManager.getInstance().getActualCity();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Stage stage = ViewLoader.getStage();
        switch (stage.getTitle()) {
            case "Add Bus" -> type = AddType.BUS;
            case "Add Stop" -> type = AddType.STOP;
            case "Add City" -> type = AddType.CITY;
        }
    }

    @FXML
    public void onOkButton() {
        switch (type) {
            case BUS:
                city.addBus(nameField.getText());
                break;
            case STOP:
                city.addBusStop(nameField.getText());
                Drawer drawer = Drawer.getInstance();
                MainController.setGraphContainer(drawer.draw(null, null));
                break;
            case CITY:
                MapsManager.getInstance().createCity(nameField.getText());
                break;
        }
        ViewLoader.getStage().close();
    }

    @FXML
    public void onCancelButton() {
        ViewLoader.getStage().close();
    }
}

enum AddType {
    CITY, BUS, STOP
}