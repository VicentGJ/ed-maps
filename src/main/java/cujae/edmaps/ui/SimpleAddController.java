package cujae.edmaps.ui;

import cujae.edmaps.utils.ViewLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SimpleAddController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Stage stage = ViewLoader.getStage();
        System.out.println(stage.getTitle());
    }
}
