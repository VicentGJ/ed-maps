package cujae.edmaps.ui;

import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.Bus;
import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.City;
import cujae.edmaps.core.MapsManager;
import cujae.edmaps.utils.Drawer;
import cujae.edmaps.utils.ViewLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ConnectionDeleteController implements Initializable {

    @FXML
    ComboBox<String> stop1ComboBox, stop2ComboBox, busComboBox;

    ObservableList<String> stopList = FXCollections.observableArrayList();
    ObservableList<String> busList = FXCollections.observableArrayList();
    private final City city = MapsManager.getInstance().getActualCity();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (BusStop stop : city.getBusStopList()) {
            stopList.add(stop.getName());
        }
        stop1ComboBox.setItems(stopList);
        stop2ComboBox.setItems(stopList);
        busComboBox.setDisable(true);
        busComboBox.getItems().add("Walking");
        busComboBox.setValue("Walking");

        stop1ComboBox.setOnAction(event -> {
            if (stop2ComboBox.getValue() != null) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                for (Bus b : city.busFilter(stop1ComboBox.getValue(), stop2ComboBox.getValue())) {
                    busList.add(b.getName());
                }
                busComboBox.setDisable(false);
                busComboBox.getItems().clear();
                busComboBox.getItems().add("Walking");
                busComboBox.setValue("Walking");
                busComboBox.getItems().addAll(busList);
            } else {
                busComboBox.setDisable(true);
                busComboBox.setValue("Walking");
            }
        });
        stop2ComboBox.setOnAction(event -> {
            if (stop1ComboBox.getValue() != null) {
                System.out.println("2!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                for (Bus b : city.busFilter(stop1ComboBox.getValue(), stop2ComboBox.getValue())) {
                    busList.add(b.getName());
                }
                busComboBox.setDisable(false);
                busComboBox.getItems().clear();
                busComboBox.getItems().add("Walking");
                busComboBox.setValue("Walking");
                busComboBox.getItems().addAll(busList);
            } else {
                busComboBox.setDisable(true);
                busComboBox.setValue("Walking");
            }
        });
    }

    @FXML
    private void onOkButton(ActionEvent event) {
        city.removeRoute(stop1ComboBox.getValue(), stop2ComboBox.getValue(), busComboBox.getValue());
        Drawer drawer = Drawer.getInstance();
        MainController.setGraphContainer(drawer.draw(null, null));
        ViewLoader.closeWindow(event);
    }

    @FXML
    private void onCancelButton(ActionEvent event) {
        ViewLoader.closeWindow(event);
    }
}
