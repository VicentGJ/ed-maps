package cujae.edmaps.ui;

import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConnectionModifyController implements Initializable {

    @FXML
    ComboBox<String> stop1ComboBox, stop2ComboBox, busComboBox;
    @FXML
    Spinner<Double> distanceSpinner;

    @FXML
    Button okButton;

    ObservableList<String> stopList = FXCollections.observableArrayList();
    // ObservableList<String> busList = FXCollections.observableArrayList();
    private final City city = MapsManager.getInstance().getActualCity();

    @FXML
    public void onOkButton(ActionEvent event) throws IOException {
        try {
            Float distance = Float.parseFloat(String.valueOf(distanceSpinner.getValueFactory().getValue()));
            String bus = null;
            if (!busComboBox.getValue().equalsIgnoreCase("Walking")) bus = busComboBox.getValue();
            city.insertRoute(stop1ComboBox.getValue(), stop2ComboBox.getValue(), bus, distance);
            Drawer drawer = Drawer.getInstance();
            MainController.setGraphContainer(drawer.draw(null, null));
            ViewLoader.closeWindow(event);
        } catch (Exception e) {
            PopupController controller = (PopupController) ViewLoader.newWindow(getClass().getResource("popup.fxml"), "Error", null);
            controller.setText("Error", e.getMessage());
            controller.setPrevious("modify-connection-form.fxml", ((Stage) okButton.getScene().getWindow()).getTitle());
            ViewLoader.closeWindow(event);
        }
    }

    @FXML
    public void onCancelButton() {
        ViewLoader.getStage().close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        City city = MapsManager.getInstance().getActualCity();
        for (BusStop stop : city.getBusStopList()) {
            stopList.add(stop.getName());
        }
        stop1ComboBox.setItems(stopList);
        stop2ComboBox.setItems(stopList);
        busComboBox.setDisable(true);
        okButton.setDisable(true);

        stop1ComboBox.setOnAction(event -> {
            if (stop2ComboBox.getValue() != null) {
                ObservableList<String> adjacent = FXCollections.observableArrayList(city.getAdjacentStops(stop1ComboBox.getValue()));
                ObservableList<String> busList = FXCollections.observableArrayList();
                WeightedEdge wEdge = null;

                for (Edge e : city.getConnectingEdges(stop1ComboBox.getValue(), stop2ComboBox.getValue())) {
                    wEdge = (WeightedEdge) e;
                    Bus bus = ((Route) wEdge.getWeight()).getBus();
                    busList.add(bus == null ? "Walking" : bus.getName());
                }
                busComboBox.setDisable(busList.isEmpty());
                okButton.setDisable(!adjacent.contains(stop2ComboBox.getValue()) || busList.isEmpty());
                if (!busList.isEmpty()) {
                    busComboBox.setItems(busList);
                    busComboBox.setValue(busList.get(0));
                }
                if(okButton.isDisabled()){
                    distanceSpinner.getValueFactory().setValue(0d);
                }
            }
        });
        stop2ComboBox.setOnAction(event -> {
            if (stop1ComboBox.getValue() != null) {
                ObservableList<String> adjacent = FXCollections.observableArrayList(city.getAdjacentStops(stop2ComboBox.getValue()));
                ObservableList<String> busList = FXCollections.observableArrayList();
                WeightedEdge wEdge = null;

                for (Edge e : city.getConnectingEdges(stop2ComboBox.getValue(), stop1ComboBox.getValue())) {
                    wEdge = (WeightedEdge) e;
                    Bus bus = ((Route) wEdge.getWeight()).getBus();
                    busList.add(bus == null ? "Walking" : bus.getName());
                }
                busComboBox.setDisable(busList.isEmpty());
                okButton.setDisable(!adjacent.contains(stop1ComboBox.getValue()) || busList.isEmpty());
                if (!busList.isEmpty()) {
                    busComboBox.setItems(busList);
                    busComboBox.setValue(busList.get(0));
                }
                if(okButton.isDisabled()){
                    distanceSpinner.getValueFactory().setValue(0d);
                }
            }
        });
        busComboBox.setOnAction(event -> {
            WeightedEdge wEdge = city.getEdge(busComboBox.getValue(),stop1ComboBox.getValue(),stop2ComboBox.getValue());
            Double distance = Double.valueOf(String.valueOf(((Route)wEdge.getWeight()).getDistance()));
            distanceSpinner.getValueFactory().setValue(distance);
        });
    }
}
