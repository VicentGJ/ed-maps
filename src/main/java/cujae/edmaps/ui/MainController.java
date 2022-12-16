package cujae.edmaps.ui;

import cujae.edmaps.core.FileManager;
import cujae.edmaps.utils.ViewLoader;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
    private void onRefresh() {
        Group graph = new Drawer(stage).draw(null);
        if (graphContainer.getChildren().size() > 0)
            this.graphContainer.getChildren().remove(0);
        this.graphContainer.getChildren().add(graph);
    }

    @FXML
    private void onSaveCity() {
        FileManager.getInstance().saveCity();
    }

    @FXML
    private void onShowingConsultsMenu() {
        loadCityConsultFileMenu.getItems().clear();
        FileManager fm = FileManager.getInstance();
        File[] cities = fm.getAllConsultDirectories();
        Arrays.sort(cities);
        for (File city : cities) {
            String[] consults = city.list();
            if (consults != null) {
                Arrays.sort(consults);
                Menu submenu = new Menu(city.getName());
                for (String consult : consults) {
                    MenuItem c = new MenuItem(consult.split("\\.")[0]);
                    c.setOnAction(event1 -> {
                        //TODO: trigger load consult
                    });
                    submenu.getItems().add(c);
                }
                loadCityConsultFileMenu.getItems().add(submenu);
            }
        }
    }

    @FXML
    private void onShowingFileMenu() {
        loadCityFileMenu.getItems().clear();
        FileManager fm = FileManager.getInstance();
        File[] cities = fm.getAllCityFiles();
        if (cities != null) {
            Arrays.sort(cities);
            for (File city : cities) {
                MenuItem c = new MenuItem(city.getName().split("\\.")[0]);
                c.setOnAction(event -> {
                    //TODO trigger load city
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