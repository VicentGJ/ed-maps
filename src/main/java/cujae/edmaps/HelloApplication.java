package cujae.edmaps;

import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.City;
import cujae.edmaps.core.Route;
import cujae.edmaps.core.dijkstra.CompletePath;
import cujae.edmaps.core.dijkstra.Path;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.InvalidParameterException;

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

    public static void userHistoryCityCreation() {
        //user story 1: create city and get the shortest path between 2 bus stops
        System.out.println("User story 1: shortest path");
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
        havana.removeBus("Route-3");

        //user story 3: rename bus stop
        System.out.println("user story 3: rename bus stop");
        Vertex historicCenter = havana.getVertex("Old town");
        BusStop busstop = ((BusStop) historicCenter.getInfo());
        System.out.println(busstop.getName());
        havana.renameBusStop("Old town", "Historic Center");
        System.out.println(busstop.getName());

        try {
            havana.renameBusStop("Historic Center", "Train Station");
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }

        //user story 4: modify route distance
        System.out.println("user story 4: modify route distance");
        WeightedEdge wEdge = havana.getEdge(havana.getVertex("Airport"), "Route-1");
        Route route = (Route) wEdge.getWeight();
        System.out.println(route.getDistance());
        havana.modifyDistanceBetween("Airport", "Train Station", "Route-1", 600.0f);
        System.out.println(route.getDistance());

        try {
            havana.modifyDistanceBetween("Airport", "Train Station", "Route-1", 0f);
            System.out.println(route.getDistance());
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }

        //user story 5: set walking route
        System.out.println("user story 5: wat a walking route(no output)");
        havana.insertRoute("Airport", "Train Station", null, 450f);
    }


    public static void main(String[] args) {
        userHistoryCityCreation();
        launch();
    }
}