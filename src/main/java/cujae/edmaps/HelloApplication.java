package cujae.edmaps;

import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.City;
import cujae.edmaps.core.FileManager;
import cujae.edmaps.core.Route;
import cujae.edmaps.core.dijkstra.CompletePath;
import cujae.edmaps.core.dijkstra.Path;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("ED MAPS");
        stage.setScene(scene);
        stage.show();
    }

    public static void userHistoryCityCreation() throws Exception {
        FileManager fm = FileManager.getInstance();
        //user story 1: create city and get the shortest path between 2 bus stops
        System.out.println("User story 1: shortest path");
        City madrid = City.getInstance();
        madrid.addBus("Route-1");
        madrid.addBus("Route-2");
        madrid.addBus("Route-3");
        madrid.addBusStop("Airport");
        madrid.addBusStop("Train Station");
        madrid.addBusStop("Residential town");
        madrid.addBusStop("Old town");
        madrid.addBusStop("City Hall");
        //Route-1
        madrid.insertRoute("Airport", "Train Station", "Route-1", 1.0f);
        madrid.insertRoute("Train Station", "City Hall", "Route-1", 1.0f);
        madrid.insertRoute("City Hall", "Old town", "Route-1", 1.0f);
        madrid.insertRoute("Old town", "Residential town", "Route-1", 2.0f);
        //Route-2
        madrid.insertRoute("Airport", "Residential town", "Route-2", 2.0f);
        //Route-3
        madrid.insertRoute("Airport", "Train Station", "Route-3", 1.0f);
        madrid.insertRoute("Train Station", "Old town", "Route-3", 1.0f);
        try {
            CompletePath path = madrid.getPathBetween("Airport", "Old town");
            for (Path p : path.getPaths()) {
                System.out.println(p.getBus() != null ? p.getBus().getName() : null);
                System.out.println(p.getDistance());
                System.out.println(((BusStop) p.getStop().getInfo()).getName());
                System.out.println(":::::::::::::::::");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //user story 2: remove a bus and all its routes/edges
        System.out.println("user story 2: remove bus and its connections(no output)");
        madrid.removeBus("Route-3");

        //user story 3: rename bus stop
        System.out.println("user story 3: rename bus stop");
        Vertex historicCenter = madrid.getVertex("Old town");
        BusStop busstop = ((BusStop) historicCenter.getInfo());
        System.out.println(busstop.getName());
        madrid.renameBusStop("Old town", "Historic Center");
        System.out.println(busstop.getName());

        // Validation already tested, uncomment to try again
        /** try {
         havana.renameBusStop("Historic Center", "Train Station");
         } catch (InvalidParameterException e) {
         e.printStackTrace();
         } **/

        //user story 4: modify route distance
        System.out.println("user story 4: modify route distance");
        WeightedEdge wEdge = madrid.getEdge(madrid.getVertex("Airport"), "Route-1");
        Route route = (Route) wEdge.getWeight();
        System.out.println(route.getDistance());
        madrid.modifyDistanceBetween("Airport", "Train Station", "Route-1", 600.0f);
        System.out.println(route.getDistance());

        // Validation already tested, uncomment to try again
        /** try {
         havana.modifyDistanceBetween("Airport", "Train Station", "Route-1", 0f);
         System.out.println(route.getDistance());
         } catch (InvalidParameterException e) {
         e.printStackTrace();
         } **/

        //user story 5: set walking route
        System.out.println("user story 5: set a walking route(no output)");
        madrid.insertRoute("Airport", "Train Station", null, 450f);

        File madridFile = fm.saveCity(madrid);
        System.out.println(madrid.getName() + " city saved at: " + madridFile.getAbsolutePath());
    }


    public static void main(String[] args) throws Exception {
        userHistoryCityCreation();
        launch();
    }
}