package cujae.edmaps;

import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.*;
import cujae.edmaps.core.dijkstra.CompletePath;
import cujae.edmaps.core.dijkstra.Path;
import cujae.edmaps.ui.MainController;
import cujae.edmaps.utils.ViewLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MainController controller = (MainController) ViewLoader.newWindow(
                HelloApplication.class.getResource("ui/main.fxml"), "ED MAPS", null);
        ViewLoader.getStage().setMaximized(true);
        controller.onRefresh();
    }

    public static void userHistoryCityCreation() {
        //user story 1: create city and get the shortest path between 2 bus stops
        System.out.println("User story 1: shortest path");
        MapsManager.getInstance().createCity("Test City");
        City madrid = MapsManager.getInstance().getActualCity();
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
            System.out.println("consult file created...");
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
        WeightedEdge wEdge = madrid.getEdge("Route-1", "Airport", "Train Station");
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

        File madridFile = FileManager.saveCity(madrid);
        System.out.println(madrid.getName() + " city saved at: " + madridFile.getAbsolutePath());

//        FileManager.renameCityFile("Madrid","ABC");
//        madrid.setName("ABC");
    }

    public static void main(String[] args) throws Exception {
        FileManager.createFiles();
        userHistoryCityCreation();
        launch();
    }
}