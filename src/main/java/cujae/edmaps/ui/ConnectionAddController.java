package cujae.edmaps.ui;

import cujae.edmaps.core.Bus;
import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.City;
import cujae.edmaps.utils.ViewLoader;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

import java.net.URL;
import java.util.ResourceBundle;

public class ConnectionAddController implements Initializable {

    @FXML
    ComboBox<String> stop1ComboBox, stop2ComboBox, busComboBox;
    @FXML
    Spinner<Float> distanceSpinner;

    ObservableList<String> stopList = FXCollections.observableArrayList();
    ObservableList<String> busList = FXCollections.observableArrayList();
    private final City city = City.getInstance();

    @FXML
    public void onOkButton() {
        city.insertRoute(stop1ComboBox.getValue(), stop2ComboBox.getValue(), busComboBox.getValue(), distanceSpinner.getValue());
        ViewLoader.getStage().close();
    }

    @FXML
    public void onCancelButton() {
        ViewLoader.getStage().close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (BusStop stop: city.getBusStopList()) {
            stopList.add(stop.getName());
        }
        for (Bus bus: city.getBusList()) {
            busList.add(bus.getName());
        }
        stop1ComboBox.setItems(stopList);
        stop2ComboBox.setItems(stopList);
        busComboBox.setItems(busList);
    }
}