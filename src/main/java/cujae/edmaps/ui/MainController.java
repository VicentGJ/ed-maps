package cujae.edmaps.ui;

import cujae.edmaps.utils.ViewLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private AnchorPane graphContainer;

    private Stage stage;

    @FXML
    private void onAddBus(ActionEvent event) throws IOException {
        ViewLoader.newWindow(getClass().getResource("add-form.fxml"), "Add Bus", null);
    }

    @FXML
    private void onAddStop(ActionEvent event) throws IOException {
        ViewLoader.newWindow(getClass().getResource("add-form.fxml"), "Add Stop", null);
    }

    @FXML
    private void onAddConnection(ActionEvent event) throws IOException {
        ViewLoader.newWindow(getClass().getResource("add-connection-form.fxml"), "Add Connection", null);
    }

    @FXML
    private void onMakeConsult() throws IOException {
        ViewLoader.newWindow(getClass().getResource("consult-shortest-path.fxml"), "Consult Shortest Path", null);
    }

    @FXML
    private void onRefresh(ActionEvent event) {
        Group graph = new Drawer().draw(stage);
        if (graphContainer.getChildren().size() > 0)
            this.graphContainer.getChildren().remove(0);
        this.graphContainer.getChildren().add(graph);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stage = ViewLoader.getStage();
    }
}