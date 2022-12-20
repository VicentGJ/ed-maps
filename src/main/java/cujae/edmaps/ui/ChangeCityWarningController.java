package cujae.edmaps.ui;

import cujae.edmaps.core.MapsManager;
import cujae.edmaps.utils.Drawer;
import cujae.edmaps.utils.ViewLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChangeCityWarningController implements Initializable {
    private Type type = Type.NEW;
    private String cityName;

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @FXML
    private void onSave(ActionEvent event) throws IOException {
        MapsManager.getInstance().saveCity();
        switch (type) {
            case NEW -> {
                ViewLoader.newWindow(getClass().getResource("add-form.fxml"), "Add City", null);
            }
            case LOAD -> {
                MapsManager.getInstance().setActualCity(cityName);
                MainController.setGraphContainer(Drawer.getInstance().draw(null, null));
            }
        }
        ViewLoader.closeWindow(event);
    }

    @FXML
    private void onDiscard(ActionEvent event) throws IOException {
        switch (type) {
            case NEW -> {
                ViewLoader.newWindow(getClass().getResource("add-form.fxml"), "Add City", null);
            }
            case LOAD -> {
                MapsManager.getInstance().setActualCity(cityName);
                MainController.setGraphContainer(Drawer.getInstance().draw(null, null));            }
        }
        ViewLoader.closeWindow(event);
    }

    @FXML
    private void onCancel(ActionEvent event) {
        ViewLoader.closeWindow(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Stage stage = ViewLoader.getStage();
        switch (stage.getTitle()) {
            case "New City Warning" -> type = Type.NEW;
            case "Load City Warning" -> type = Type.LOAD;
        }
    }

    private enum Type {
        NEW, LOAD
    }
}
