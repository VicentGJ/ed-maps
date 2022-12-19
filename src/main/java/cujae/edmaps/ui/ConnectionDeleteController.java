package cujae.edmaps.ui;

import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.*;
import cujae.edmaps.utils.Drawer;
import cujae.edmaps.utils.ViewLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class ConnectionDeleteController implements Initializable {

    @FXML
    ComboBox<String> stop1ComboBox, stop2ComboBox, busComboBox;

    @FXML
    Button okButton;

    ObservableList<String> stopList = FXCollections.observableArrayList();
    private final City city = MapsManager.getInstance().getActualCity();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (BusStop stop : city.getBusStopList()) {
            stopList.add(stop.getName());
        }
        stop1ComboBox.setItems(stopList);
        stop2ComboBox.setItems(stopList);
        busComboBox.setDisable(true);
        okButton.setDisable(true);
        stop1ComboBox.setOnAction(event -> {
            LinkedList<String> busList = new LinkedList<>();
            if (stop2ComboBox.getValue() != null) {
                for (Edge e : city.getConnectingEdges(stop1ComboBox.getValue(), stop2ComboBox.getValue())) {
                    Bus bus = ((Route) ((WeightedEdge) e).getWeight()).getBus();
                    if (bus != null) busList.add(bus.getName());
                    else busList.add("Walking");
                }
                busComboBox.setDisable(busList.isEmpty());
                if(!busList.isEmpty()) busComboBox.setValue(busList.getFirst());
                busComboBox.getItems().clear();
                busComboBox.getItems().addAll(busList);
            } else {
                busComboBox.setDisable(true);
            }
            okButton.setDisable(busComboBox.isDisable());
        });
        stop2ComboBox.setOnAction(event -> {
            LinkedList<String> busList = new LinkedList<>();
            if (stop1ComboBox.getValue() != null) {
                for (Edge e : city.getConnectingEdges(stop1ComboBox.getValue(), stop2ComboBox.getValue())) {
                    Bus bus = ((Route) ((WeightedEdge) e).getWeight()).getBus();
                    if (bus != null) busList.add(bus.getName());
                    else busList.add("Walking");
                }
                busComboBox.setDisable(busList.isEmpty());
                if(!busList.isEmpty()) busComboBox.setValue(busList.getFirst());
                busComboBox.getItems().clear();
                busComboBox.getItems().addAll(busList);
            } else {
                busComboBox.setDisable(true);
            }
            okButton.setDisable(busComboBox.isDisable());

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
