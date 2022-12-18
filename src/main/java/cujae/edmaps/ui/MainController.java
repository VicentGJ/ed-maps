package cujae.edmaps.ui;

import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.FileManager;
import cujae.edmaps.core.MapsManager;
import cujae.edmaps.utils.Drawer;
import cujae.edmaps.utils.ViewLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private AnchorPane graphContainer;
    @FXML
    private Menu loadCityConsultFileMenu;
    @FXML
    private Menu loadCityFileMenu;

    private Stage stage;

    @FXML
    private void onAddBus() throws IOException {
        ViewLoader.newWindow(getClass().getResource("add-form.fxml"), "Add Bus", null);
    }
    @FXML
    private void onCreateCity() throws IOException {
        ViewLoader.newWindow(getClass().getResource("add-form.fxml"), "Add City", null);
    }

    @FXML
    private void onAddStop() throws IOException {
        ViewLoader.newWindow(getClass().getResource("add-form.fxml"), "Add Stop", null);
    }

    @FXML
    private void onAddConnection() throws IOException {
        ViewLoader.newWindow(getClass().getResource("add-connection-form.fxml"), "Add Connection", null);
    }

    @FXML
    private void onMakeConsult() throws IOException {
        ViewLoader.newWindow(getClass().getResource("consult-shortest-path.fxml"), "Consult Shortest Path", null);
    }

    @FXML
    public void onRefresh() {
        Group graph = new Drawer(stage).draw(null, null);
        if (graphContainer.getChildren().size() > 0)
            this.graphContainer.getChildren().remove(0);
        this.graphContainer.getChildren().add(graph);
    }

    @FXML
    private void onSaveCity() {
        MapsManager.getInstance().saveCity();
    }

    @FXML
    private void onShowingConsultsMenu() {
        loadCityConsultFileMenu.getItems().clear();
        File[] cities = FileManager.getAllConsultDirectories();
        Arrays.sort(cities);
        for (File city : cities) {
            String[] consults = city.list();
            if (consults != null) {
                Arrays.sort(consults);
                Menu submenu = new Menu(city.getName());
                for (String consult : consults) {
                    MenuItem c = new MenuItem(consult.split("\\.")[0]);
                    c.setOnAction(event1 -> {
                        //TODO: trigger load consult show subgraph
                        LinkedList<Vertex> vertices = FileManager.loadConsult(city.getName(), consult);
                        onRefresh(vertices, city.getName());
                    });
                    submenu.getItems().add(c);
                }
                loadCityConsultFileMenu.getItems().add(submenu);
            }
        }
    }

    public void onRefresh(LinkedList<Vertex> vertices, String cityName) {
        Group graph = new Drawer(stage).draw(vertices,cityName);
        if (graphContainer.getChildren().size() > 0)
            this.graphContainer.getChildren().remove(0);
        this.graphContainer.getChildren().add(graph);
    }

    @FXML
    private void onShowingFileMenu() {
        loadCityFileMenu.getItems().clear();
        File[] cities = FileManager.getAllCityFiles();
        if (cities != null) {
            Arrays.sort(cities);
            for (File city : cities) {
                MenuItem c = new MenuItem(city.getName().split("\\.")[0]);
                c.setOnAction(event -> {
                    MapsManager.getInstance().setActualCity(city.getName().split("\\.")[0]);
                    onRefresh();
                });
                loadCityFileMenu.getItems().add(c);
            }
        }
    }

    @FXML
    private void onQuit() {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stage = ViewLoader.getStage();
    }
}