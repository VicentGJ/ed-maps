package cujae.edmaps.ui;

import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.City;
import cujae.edmaps.core.dijkstra.CompletePath;
import cujae.edmaps.core.dijkstra.Path;
import cujae.edmaps.utils.ViewLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ConsultController implements Initializable {

    @FXML
    private ComboBox<String> startComboBox, destinationComboBox;

    private final ObservableList<String> stopList = FXCollections.observableArrayList();
    private final City city = City.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (BusStop stop: city.getBusStopList()) {
            stopList.add(stop.getName());
        }
        startComboBox.setItems(stopList);
        destinationComboBox.setItems(stopList);
    }

    @FXML
    public void onOkButton() {
        try {
            CompletePath path = city.getPathBetween(startComboBox.getValue(), destinationComboBox.getValue());
            for (Path p : path.getPaths()) {
                System.out.println(p.getBus() != null ? p.getBus().getName() : null);
                System.out.println(p.getDistance());
                System.out.println(((BusStop) p.getStop().getInfo()).getName());
                System.out.println(":::::::::::::::::");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onCancelButton() {
        ViewLoader.getStage().close();
    }
}
