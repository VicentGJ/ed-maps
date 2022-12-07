package cujae.edmaps;

import cujae.edmaps.core.City;
import cujae.edmaps.core.Country;
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
    }

    public static void testCLI() {

        City havana = new City("Havana");
//      Añadir una guagua (Ruta 1)
        havana.addBus("Route-1");
//      Añadir una parada (Aeropuerto)
        havana.addBusStop("Airport");
//      Añadir otra parada (Estación de trenes)
        havana.addBusStop("Train Station");
//      Conectar ambas paradas con la guagua (Ruta 1, distancia 1km)
        havana.insertRoute("Airport", "Train Station", "Route-1", 1.0f);
//      Añadir otra parada (Centro de la ciudad)
        havana.addBusStop("City Hall");
//      Conectar ambas paradas con la guagua (Ruta 1, distancia 1km)
        havana.insertRoute("Train Station", "City Hall", "Route-1", 1.0f);
//      Añadir otra parada (Zona Vieja)
        havana.addBusStop("Old town");
//      Conectar ambas paradas con la guagua (Ruta 1, distancia 1km)
        havana.insertRoute("City Hall", "Old town", "Route-1", 1.0f);
//      Añadir otra parada (Zona residencial)
        havana.addBusStop("Residential town");
//      Conectar ambas paradas con la guagua (Ruta 1, distancia 2km)
        havana.insertRoute("Old town", "Residential town", "Route-1", 2.0f);
    }

    public static void main(String[] args) {
        testCLI();
        launch();
    }
}