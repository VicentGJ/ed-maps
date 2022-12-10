package cujae.edmaps.ui;

import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.City;
import cujae.edmaps.core.dijkstra.CompletePath;
import cujae.edmaps.core.dijkstra.Path;
import cujae.edmaps.utils.ViewLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ConsultController implements Initializable {

    @FXML
    private ComboBox<String> startComboBox, destinationComboBox;
    @FXML
    private Label totalDistanceDisplay;
    @FXML
    private TableView<PathHelper> tableView;
    @FXML
    private TableColumn stopColumn, busColumn;

    private final ObservableList<String> stopList = FXCollections.observableArrayList();
    private final City city = City.getInstance();

    public static class PathHelper {
         private final SimpleStringProperty stopName;
         private final SimpleStringProperty busName;

         private PathHelper(String stopName, String busName) {
             this.stopName = new SimpleStringProperty(stopName);
             this.busName = new SimpleStringProperty(busName);
         }

         public String getStopName() {
             return stopName.get();
         }

         public String getBusName() {
             return busName.get();
         }

     }

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
            ObservableList<PathHelper> pathList = FXCollections.observableArrayList();
            CompletePath path = city.getPathBetween(startComboBox.getValue(), destinationComboBox.getValue());
            for (Path p : path.getPaths()) {
                System.out.println(p.getBus() != null ? p.getBus().getName() : null);
                System.out.println(p.getDistance());
                System.out.println(((BusStop) p.getStop().getInfo()).getName());
                System.out.println(":::::::::::::::::");
                totalDistanceDisplay.setText(p.getDistance().toString());
                pathList.add(new PathHelper(((
                        BusStop) p.getStop().getInfo()).getName(),
                        p.getBus() != null ? p.getBus().getName() : "Start"
                ));
            }
            stopColumn.setCellValueFactory(
                    new PropertyValueFactory<PathHelper, String>("stopName"));
            busColumn.setCellValueFactory(
                    new PropertyValueFactory<PathHelper, String>("busName"));
            tableView.setItems(pathList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onCancelButton() {
        ViewLoader.getStage().close();
    }
}
