package cujae.edmaps;

import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.City;
import cujae.edmaps.core.dijkstra.CompletePath;
import cujae.edmaps.core.dijkstra.Path;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Tocas!");
        stage.setScene(scene);
        stage.show();
        stage.close();
    }

    public static void testCLI() {

        City havana = new City("Havana");
        havana.addBus("Route-1");
        havana.addBus("Route-2");
        havana.addBus("Route-3");
        havana.addBusStop("Airport");
        havana.addBusStop("Train Station");
        havana.addBusStop("Residential town");
        havana.addBusStop("Old town");
        havana.addBusStop("City Hall");
        //Route-1
        havana.insertRoute("Airport", "Train Station", "Route-1", 1.0f);
        havana.insertRoute("Train Station", "City Hall", "Route-1", 1.0f);
        havana.insertRoute("City Hall", "Old town", "Route-1", 1.0f);
        havana.insertRoute("Old town", "Residential town", "Route-1", 2.0f);
        //Route-2
        havana.insertRoute("Airport", "Residential town", "Route-2", 2.0f);
        //Route-3
        havana.insertRoute("Airport", "Train Station", "Route-3", 1.0f);
        havana.insertRoute("Train Station", "Old town", "Route-3", 1.0f);
        try {
            CompletePath path = havana.getPathBetween("Airport", "Old town");
            for(Path p : path.getPaths()){
                System.out.println(p.getBus()!=null?p.getBus().getName():null);
                System.out.println(p.getDistance());
                System.out.println(((BusStop)p.getStop().getInfo()).getName());
                System.out.println(":::::::::::::::::");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        testCLI();
        launch();
    }
}