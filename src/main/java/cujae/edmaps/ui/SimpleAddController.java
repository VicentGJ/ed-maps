package cujae.edmaps.ui;

import cujae.edmaps.core.City;
import cujae.edmaps.utils.ViewLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SimpleAddController implements Initializable {

    @FXML
    private TextField nameField;
    private boolean isBus = false;
    private City city = City.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Stage stage = ViewLoader.getStage();
        if (stage.getTitle().equals("Add Bus")) {
            isBus = true;
        }
        System.out.println(stage.getTitle());
    }

    @FXML
    public void onOkButton() {
        if (isBus) {
            city.addBus(nameField.getText());
        }
        else {
            city.addBusStop(nameField.getText());
        }
        ViewLoader.getStage().close();
    }

    @FXML
    public  void onCancelButton() {
        ViewLoader.getStage().close();
    }
}
