package cujae.edmaps.ui;

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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class ConnectionAddController implements Initializable {

    @FXML
    ComboBox<String> stop1ComboBox, stop2ComboBox, busComboBox;
    @FXML
    Spinner<Integer> distanceSpinner;

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
        }
        catch (Exception e) {
            PopupController controller = (PopupController) ViewLoader.newWindow(getClass().getResource("popup.fxml"), "Error", null);
            controller.setText("Error", e.getMessage());
            controller.setPrevious("add-connection-form.fxml", ((Stage) okButton.getScene().getWindow()).getTitle());
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
        busComboBox.getItems().add("Walking");
        busComboBox.setValue("Walking");

        stop1ComboBox.setOnAction(event -> {
            LinkedList<String> busList = new LinkedList<>();
            if (stop2ComboBox.getValue() != null) {
                if (stop2ComboBox.getValue().equalsIgnoreCase(stop1ComboBox.getValue())) {
                    busComboBox.setValue("Walking");
                    okButton.setDisable(true);
                } else {
                    for (Bus b : city.busFilter(stop1ComboBox.getValue(), stop2ComboBox.getValue())) {
                        busList.add(b.getName());
                    }
                    busComboBox.getItems().clear();
                    busComboBox.getItems().add("Walking");
                    busComboBox.setValue("Walking");
                    busComboBox.getItems().addAll(busList);
                    okButton.setDisable(false);
                }
            } else {
                busComboBox.setValue("Walking");
                okButton.setDisable(true);
            }
            busComboBox.setDisable(busList.isEmpty());
        });
        stop2ComboBox.setOnAction(event -> {
            LinkedList<String> busList = new LinkedList<>();
            if (stop1ComboBox.getValue() != null) {
                if (stop2ComboBox.getValue().equalsIgnoreCase(stop1ComboBox.getValue())) {
                    busComboBox.setValue("Walking");
                    okButton.setDisable(true);
                } else {
                    for (Bus b : city.busFilter(stop2ComboBox.getValue(), stop1ComboBox.getValue())) {
                        busList.add(b.getName());
                    }
                    busComboBox.getItems().clear();
                    busComboBox.getItems().add("Walking");
                    busComboBox.setValue("Walking");
                    busComboBox.getItems().addAll(busList);
                    okButton.setDisable(false);
                }
            } else {
                busComboBox.setValue("Walking");
                okButton.setDisable(true);
            }
            busComboBox.setDisable(busList.isEmpty());
        });
    }
}
