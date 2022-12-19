package cujae.edmaps.ui;

import cujae.edmaps.core.City;
import cujae.edmaps.core.FileManager;
import cujae.edmaps.core.MapsManager;
import cujae.edmaps.utils.Drawer;
import cujae.edmaps.utils.ViewLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RenameCityController implements Initializable {
    @FXML
    Button okButton;
    @FXML
    ComboBox<String> comboBox;
    @FXML
    TextField nameField;


    @FXML
    private void onOkButton(ActionEvent event) throws IOException {
        String newCityName = nameField.getText();
        String oldCityName = comboBox.getValue();
        City c = FileManager.getCity(oldCityName);
        c.setName(newCityName);
        FileManager.renameCityFile(oldCityName, newCityName);
        if (oldCityName.equalsIgnoreCase(MapsManager.getInstance().getActualCity().getName())) {
            MapsManager.getInstance().setActualCity(newCityName);
            MainController.setGraphContainer(Drawer.getInstance().draw(null, null));
        }
        System.out.println(MapsManager.getInstance().getActualCity().getName());
        ViewLoader.closeWindow(event);
    }

    @FXML
    private void onCancelButton(ActionEvent event) throws IOException {
        ViewLoader.closeWindow(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File[] cities = FileManager.getAllConsultDirectories();
        ObservableList<String> cityList = FXCollections.observableArrayList();
        for (File f : cities) {
            cityList.add(f.getName());
        }
        comboBox.setItems(cityList);
        comboBox.setValue(cityList.get(0));
        okButton.setDisable(true);
        nameField.setOnKeyTyped(keyEvent -> {
            boolean exists = false;
            if (!nameField.getText().isBlank()) {
                for (String s : cityList) {
                    if (s.equalsIgnoreCase(nameField.getText())) {
                        exists = true;
                        break;
                    }
                }
            }
            okButton.setDisable(exists);
        });
    }
}