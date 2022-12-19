package cujae.edmaps.ui;

import cujae.edmaps.core.*;
import cujae.edmaps.utils.ViewLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CityDeleteController implements Initializable {

    @FXML
    ComboBox<String> cityCombobox;
    @FXML
    CheckBox checkBox;
    @FXML
    Button okButton;
    ObservableList<String> cityList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File[] cities = FileManager.getAllCityFiles();
        for (File f : cities) {
            cityList.add(f.getName().split("\\.")[0]);
        }
        okButton.setDisable(cityList.isEmpty());
        if (!cityList.isEmpty()) {
            cityCombobox.setItems(cityList);
            cityCombobox.setValue(cityList.get(0));
        }
    }

    @FXML
    private void onOkButton(ActionEvent event) throws IOException {
        FileManager.deleteCity(cityCombobox.getValue(), checkBox.isSelected());
        PopupController controller = (PopupController) ViewLoader.newWindow(
                getClass().getResource("popup.fxml"), "Info", null);
        controller.setText("Success", "City deleted successfully");
        ViewLoader.closeWindow(event);
    }

    @FXML
    private void onCancelButton(ActionEvent event) {
        ViewLoader.closeWindow(event);
    }
}
