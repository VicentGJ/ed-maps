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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final AnchorPane graphContainer = new AnchorPane();
    @FXML
    private VBox root;
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
//        ViewLoader.newWindow(getClass().getResource("add-form.fxml"), "Add City", null);
        ViewLoader.newWindow(getClass().getResource("change-city-warning.fxml"), "New City Warning", null);
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
    private void onDeleteBus() throws IOException {
        ViewLoader.newWindow(getClass().getResource("delete-bus-and-stop-form.fxml"), "Delete Bus", null);
    }

    @FXML
    private void onDeleteStop() throws IOException {
        ViewLoader.newWindow(getClass().getResource("delete-bus-and-stop-form.fxml"), "Delete Stop", null);
    }

    @FXML
    private void onDeleteConnection() throws IOException {
        ViewLoader.newWindow(getClass().getResource("delete-connection-form.fxml"), "Delete Connection", null);
    }

    @FXML
    public void onRefresh() {
        Drawer drawer = Drawer.getInstance();
        drawer.setStage(stage);
        Group graph = drawer.draw(null, null);
        if (graphContainer.getChildren().size() > 0)
            graphContainer.getChildren().remove(0);
        graphContainer.getChildren().add(graph);
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
            Menu submenu = new Menu(city.getName());
            if (consults != null && consults.length > 0) {
                Arrays.sort(consults);
                for (String consult : consults) {
                    MenuItem c = new MenuItem(consult.split("\\.")[0]);
                    c.setOnAction(event1 -> {
                        LinkedList<Vertex> vertices = FileManager.loadConsult(city.getName(), consult);
                        onRefresh(vertices, city.getName());
                    });
                    submenu.getItems().add(c);
                }
            } else {
                MenuItem c = new MenuItem("No Consults");
                c.setDisable(true);
                submenu.getItems().add(c);
            }
            loadCityConsultFileMenu.getItems().add(submenu);
        }
    }

    public void onRefresh(LinkedList<Vertex> vertices, String cityName) {
        Drawer drawer = Drawer.getInstance();
        drawer.setStage(stage);
        Group graph = drawer.draw(vertices, cityName);
        if (graphContainer.getChildren().size() > 0)
            graphContainer.getChildren().remove(0);
        graphContainer.getChildren().add(graph);
    }

    public static void setGraphContainer(Group graph) {
        if (graphContainer.getChildren().size() > 0)
            graphContainer.getChildren().remove(0);
        graphContainer.getChildren().add(graph);
    }

    @FXML
    private void onShowingFileMenu() {
        loadCityFileMenu.getItems().clear();
        File[] cities = FileManager.getAllCityFiles();
        if (cities.length != 0) {
            Arrays.sort(cities);
            for (File city : cities) {
                MenuItem c = new MenuItem(city.getName().split("\\.")[0]);
                c.setOnAction(event -> {
                    try {
                        ChangeCityWarningController controller = (ChangeCityWarningController) ViewLoader.newWindow(getClass().getResource("change-city-warning.fxml"), "Load City Warning", null);
                        controller.setCityName(city.getName().split("\\.")[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                loadCityFileMenu.getItems().add(c);
            }
        } else {
            MenuItem c = new MenuItem("No data");
            c.setDisable(true);
            loadCityFileMenu.getItems().add(c);
        }
    }

    @FXML
    private void onQuit() {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stage = ViewLoader.getStage();
        root.getChildren().add(graphContainer);
    }

    @FXML
    public void onDeleteCity() throws IOException {
        ViewLoader.newWindow(getClass().getResource("delete-city-form.fxml"), "Delete City", null);
    }

    @FXML
    public void onRenameCity() throws IOException {
        ViewLoader.newWindow(getClass().getResource("rename-form.fxml"), "Rename City", null);
    }

    @FXML
    public void onRenameBus() throws IOException {
        ViewLoader.newWindow(getClass().getResource("rename-form.fxml"), "Rename Bus", null);
    }

    @FXML
    public void onRenameBusStop() throws IOException {
        ViewLoader.newWindow(getClass().getResource("rename-form.fxml"), "Rename Stop", null);
    }

    @FXML
    public void onModifyConnection() throws IOException {
        ViewLoader.newWindow(getClass().getResource("modify-connection-form.fxml"), "Modify Connection", null);

    }
}