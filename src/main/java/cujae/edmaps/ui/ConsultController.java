package cujae.edmaps.ui;

import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.City;
import cujae.edmaps.core.MapsManager;
import cujae.edmaps.core.dijkstra.ShortestPath;
import cujae.edmaps.core.dijkstra.Path;
import cujae.edmaps.utils.ViewLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    private TableColumn stopColumn, busColumn, distanceColumn;

    @FXML
    Button okButton;

    private final ObservableList<String> stopList = FXCollections.observableArrayList();
    private final City city = MapsManager.getInstance().getActualCity();

    public static class PathHelper {
        private final SimpleStringProperty stopName;
        private final SimpleStringProperty busName;
        private final SimpleStringProperty distance;

        private PathHelper(String stopName, String busName, String distance) {
            this.stopName = new SimpleStringProperty(stopName);
            this.busName = new SimpleStringProperty(busName);
            this.distance = new SimpleStringProperty(distance);
        }

        public String getStopName() {
            return stopName.get();
        }

        public String getBusName() {
            return busName.get();
        }

        public String getDistance() {
            return distance.get();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (BusStop stop : city.getBusStopList()) {
            stopList.add(stop.getName());
        }
        okButton.setDisable(true);
        startComboBox.setItems(stopList);
        destinationComboBox.setItems(stopList);
        city.restartDijkstra();
        startComboBox.setOnAction(event -> {
            okButton.setDisable(destinationComboBox.getValue() == null);
        });
        destinationComboBox.setOnAction(event -> {
            okButton.setDisable(startComboBox.getValue() == null);
        });
    }

    @FXML
    public void onOkButton() {
        try {
            Float totalDistance = 0f;
            ObservableList<PathHelper> pathList = FXCollections.observableArrayList();
            ShortestPath path = city.getPathBetween(startComboBox.getValue(), destinationComboBox.getValue());
            Path first = path.getPaths().getFirst();
            for (Path p : path.getPaths()) {
//                System.out.println(p.getBus() != null ? p.getBus().getName() : null);
//                System.out.println(p.getDistance());
//                System.out.println(((BusStop) p.getStop().getInfo()).getName());
//                System.out.println(":::::::::::::::::");
                totalDistance += p.getDistance();
                String busStopName = ((BusStop) p.getStop().getInfo()).getName();
                String busName = "Walking";
                if (p.getBus() != null) busName = p.getBus().getName();
                else if (p.equals(first)) busName = "Start";
                String distance = String.valueOf(p.getDistance()) + "m";
                pathList.add(new PathHelper(busStopName, busName, distance));
            }
            stopColumn.setCellValueFactory(new PropertyValueFactory<PathHelper, String>("stopName"));
            busColumn.setCellValueFactory(new PropertyValueFactory<PathHelper, String>("busName"));
            distanceColumn.setCellValueFactory(new PropertyValueFactory<PathHelper, String>("distance"));
            tableView.setItems(pathList);
            totalDistanceDisplay.setText(String.valueOf(totalDistance) + "m");
//            LinkedList<Vertex> vertices = FileManager.loadLastConsult(city.getName());
//            MainController.setGraphContainer(Drawer.getInstance().draw(vertices, city.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onCancelButton() {
        ViewLoader.getStage().close();
    }
}
