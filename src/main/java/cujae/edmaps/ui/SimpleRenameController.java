package cujae.edmaps.ui;

import cujae.edmaps.core.*;
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
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SimpleRenameController implements Initializable {
    @FXML
    Button okButton;
    @FXML
    ComboBox<String> comboBox;
    @FXML
    TextField nameField;
    @FXML
    Label label;
    private Type type = Type.CITY;

    @FXML
    private void onOkButton(ActionEvent event) {
        String newName = nameField.getText();
        String oldName = comboBox.getValue();
        switch (type) {
            case CITY -> {
                City c = FileManager.getCity(oldName);
                c.setName(newName);
                FileManager.renameCityFile(oldName, newName);
                if (oldName.equalsIgnoreCase(MapsManager.getInstance().getActualCity().getName())) {
                    MapsManager.getInstance().setActualCity(newName);
                }
            }
            case BUS -> {
                MapsManager.getInstance().getActualCity().renameBus(oldName,newName);
            }
            case STOP -> {
                MapsManager.getInstance().getActualCity().renameBusStop(oldName,newName);
            }
        }
        MainController.setGraphContainer(Drawer.getInstance().draw(null, null));
        ViewLoader.closeWindow(event);
    }

    @FXML
    private void onCancelButton(ActionEvent event) {
        ViewLoader.closeWindow(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Stage stage = ViewLoader.getStage();
        switch (stage.getTitle()) {
            case "Rename Bus" -> type = Type.BUS;
            case "Rename Stop" -> type = Type.STOP;
            case "Rename City" -> type = Type.CITY;
        }
        ObservableList<String> existent = FXCollections.observableArrayList();
        switch (type) {
            case CITY -> {
                label.setText("City");
                File[] cities = FileManager.getAllConsultDirectories();
                for (File f : cities) {
                    existent.add(f.getName());
                }
            }
            case BUS -> {
                label.setText("Bus");
                List<Bus> busList = MapsManager.getInstance().getActualCity().getBusList();
                for (Bus b : busList) {
                    existent.add(b.getName());
                }
            }
            case STOP -> {
                label.setText("Stop");
                List<BusStop> busStopList = MapsManager.getInstance().getActualCity().getBusStopList();
                for (BusStop bs : busStopList) {
                    existent.add(bs.getName());
                }
            }
        }

        comboBox.setItems(existent);
        comboBox.setValue(existent.get(0));
        okButton.setDisable(true);
        nameField.setOnKeyTyped(keyEvent -> {
            boolean exists = false;

            if (!nameField.getText().isBlank()) {
                for (String s : existent) {
                    if (s.equalsIgnoreCase(nameField.getText())) {
                        exists = true;
                        break;
                    }
                }
            }
            okButton.setDisable(exists);
        });
    }

    private enum Type {
        CITY, BUS, STOP
    }
}