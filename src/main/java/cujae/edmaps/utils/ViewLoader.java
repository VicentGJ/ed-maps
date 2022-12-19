package cujae.edmaps.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ViewLoader {

    private static Stage stage;

    public static Object newWindow(URL loc, String title, Stage parentStage) throws IOException {
        Object controller = null;
        FXMLLoader loader = new FXMLLoader(loc);
        stage = Objects.requireNonNullElseGet(parentStage, () -> new Stage(StageStyle.DECORATED));
        stage.setTitle(title);
        stage.setScene(new Scene(loader.load()));
        stage.show();
        controller = loader.getController();
        return controller;
    }

    public static void newWindow(Stage stage) {
        stage.show();
    }

    public static Object thisWindow(URL fxmlResource, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(fxmlResource);
        Parent next_page_parent = loader.load();
        Object controller = loader.getController();
        Scene next_page_scene = new Scene(next_page_parent);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(next_page_scene);
        stage.show();
        return controller;
    }

    public static void closeWindow(ActionEvent event) {
        Stage thisStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        thisStage.close();
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        ViewLoader.stage = stage;
    }

}
