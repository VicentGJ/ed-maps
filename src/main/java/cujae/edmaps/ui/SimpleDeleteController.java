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
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SimpleDeleteController implements Initializable {

    private cujae.edmaps.ui.AddType type = cujae.edmaps.ui.AddType.STOP;

    @FXML
    private ComboBox<String> comboBox;

    private ObservableList<String> itemList = FXCollections.observableArrayList();
    private City city = MapsManager.getInstance().getActualCity();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Stage stage = ViewLoader.getStage();
        switch (stage.getTitle()) {
            case "Delete Bus" -> type = AddType.BUS;
            case "Delete Stop" -> type = AddType.STOP;
        }
        switch (type) {
            case STOP -> {
                List<BusStop> list = city.getBusStopList();
                for (BusStop stop: list) {
                    itemList.add(stop.getName());
                }
            }
            case BUS -> {
                List<Bus> list = city.getBusList();
                for (Bus bus: list) {
                    itemList.add(bus.getName());
                }
            }
        }
        comboBox.setItems(itemList);
        comboBox.setValue(itemList.get(0));
    }

    @FXML
    private void onOkButton(ActionEvent event) {
        switch (type) {
            case STOP -> {
                city.removeBusStop(comboBox.getValue());
                Drawer drawer = Drawer.getInstance();
                MainController.setGraphContainer(drawer.draw(null, null));
            }
            case BUS -> {
                city.removeBus(comboBox.getValue());
                Drawer drawer = Drawer.getInstance();
                MainController.setGraphContainer(drawer.draw(null, null));
            }
        }
        ViewLoader.closeWindow(event);
    }

    @FXML
    private void onCancelButton(ActionEvent event) {
        ViewLoader.closeWindow(event);
    }
}
