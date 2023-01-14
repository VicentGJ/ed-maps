package cujae.edmaps.core.dijkstra;

import cu.edu.cujae.ceis.graph.LinkedGraph;
import cu.edu.cujae.ceis.graph.interfaces.ILinkedWeightedEdgeNotDirectedGraph;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraAlgorithmTest {

    private DijkstraAlgorithm dijkstraAlgorithm;
    private static ILinkedWeightedEdgeNotDirectedGraph graph;
    @BeforeAll
    static void initiateGraph(){
        graph = new LinkedGraph();

        Vertex stop0 = new Vertex(new BusStop("Airport"));
        Vertex stop1 = new Vertex(new BusStop("Train Station"));
        Vertex stop2 = new Vertex(new BusStop("Residential town"));
        Vertex stop3 = new Vertex(new BusStop("Old town"));
        Vertex stop4 = new Vertex(new BusStop("City Hall"));

        graph.insertVertex(stop0);
        graph.insertVertex(stop1);
        graph.insertVertex(stop2);
        graph.insertVertex(stop3);
        graph.insertVertex(stop4);

        Bus bus1 = new Bus("Route-1");
        Bus bus2 = new Bus("Route-2");

        graph.insertWEdgeNDG(0, 1, new Route(bus1, 600f));
        graph.insertWEdgeNDG(1, 4, new Route(bus1, 1f));
        graph.insertWEdgeNDG(4, 3, new Route(bus1, 1f));
        graph.insertWEdgeNDG(3, 2, new Route(bus1, 2f));
        graph.insertWEdgeNDG(0, 2, new Route(bus2, 2f));
        graph.insertWEdgeNDG(0, 1, new Route(null, 450f));
    }

    @BeforeEach
    void setUp() {
        dijkstraAlgorithm = new DijkstraAlgorithmImp();
    }
    @Test
    void initialStopNullShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> {
            dijkstraAlgorithm.getStopMap(null);
                });
    }
    @Test
    void initialStopAirport(){
        Vertex airport = graph.getVerticesList().get(0);
        Map<Vertex, WayToArrive> stopMap = dijkstraAlgorithm.getStopMap(airport);
        assertFalse(stopMap.isEmpty());

        Vertex trainStation = graph.getVerticesList().get(1);
        Vertex residentialTown = graph.getVerticesList().get(2);
        Vertex oldTown = graph.getVerticesList().get(3);
        Vertex cityHall = graph.getVerticesList().get(4);
        WayToArrive wayToArrive;
        //Airport
        wayToArrive = stopMap.get(airport);
        testWayToArriveForInitialVertex(wayToArrive);
        //TrainStation
        wayToArrive = stopMap.get(trainStation);
        testWayToArrive(wayToArrive, cityHall, 1,
                "Route-1", 6);
        //ResidentialTown
        wayToArrive = stopMap.get(residentialTown);
        testWayToArrive(wayToArrive, airport, 2,
                "Route-2", 2);
        //OldTown
        wayToArrive = stopMap.get(oldTown);
        testWayToArrive(wayToArrive, residentialTown, 2,
                "Route-1", 4);
        //CityHall
        wayToArrive = stopMap.get(cityHall);
        testWayToArrive(wayToArrive, oldTown, 1,
                "Route-1", 5);
    }
    private void testWayToArrive(WayToArrive wayToArrive, Vertex expectedPreviousStop,
                                 float expectedEdgeDistance, String expectedBus,
                                 float expectedDistanceUpToHere){
        assertNotNull(wayToArrive);
        assertEquals(expectedPreviousStop, wayToArrive.getPreviousStop());
        assertEquals(expectedEdgeDistance, wayToArrive.getEdgeDistance());
        assertEquals(expectedBus, wayToArrive.getBus().getName());
        assertEquals(expectedDistanceUpToHere, wayToArrive.getDistanceUpToHere());
    }
    private void testWayToArriveForInitialVertex(WayToArrive wayToArrive){
        assertNotNull(wayToArrive);
        assertNull(wayToArrive.getPreviousStop());
        assertEquals(0, wayToArrive.getEdgeDistance());
        assertNull(wayToArrive.getBus());
        assertEquals(0, wayToArrive.getDistanceUpToHere());
    }
}