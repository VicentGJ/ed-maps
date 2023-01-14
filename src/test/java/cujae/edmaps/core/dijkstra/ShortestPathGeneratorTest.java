package cujae.edmaps.core.dijkstra;

import cu.edu.cujae.ceis.graph.LinkedGraph;
import cu.edu.cujae.ceis.graph.interfaces.ILinkedWeightedEdgeNotDirectedGraph;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.Bus;
import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.Route;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShortestPathGeneratorTest {
    private static ShortestPathGenerator shortestPathGenerator;
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
    @BeforeAll
    static void initiateShortestPath(){
        shortestPathGenerator = new ShortestPathGenerator();
    }


    @Test
    void testPathFromAirportToTrainStation() {
        Vertex airport = graph.getVerticesList().get(0);
        Vertex trainStation = graph.getVerticesList().get(1);
        Vertex residentialTown = graph.getVerticesList().get(2);
        Vertex oldTown = graph.getVerticesList().get(3);
        Vertex cityHall = graph.getVerticesList().get(4);

        shortestPathGenerator.setInitialStop(airport);
        ShortestPath shortestPath = shortestPathGenerator.getShortestPathTo(trainStation);
        List<Path> pathList = shortestPath.getPaths();

        Path path;
        //Airport
        path = pathList.get(0);
        testPathForInitialStop(path, airport);
        //ResidentialTown
        path = pathList.get(1);
        testPath(path, residentialTown, "Route-2", 2);
        //OldTown
        path = pathList.get(2);
        testPath(path, oldTown, "Route-1", 2);
        //CityHall
        path = pathList.get(3);
        testPath(path, cityHall, "Route-1", 1);
        //TrainStation
        path = pathList.get(4);
        testPath(path, trainStation, "Route-1", 1);
    }
    private void testPath(Path path, Vertex expectedStop, String expectedBus, float expectedDistance){
        assertNotNull(path);
        assertEquals(expectedStop, path.getStop());
        assertEquals(expectedBus, path.getBus().getName());
        assertEquals(expectedDistance, path.getDistance());
    }
    private void testPathForInitialStop(Path path, Vertex expectedStop){
        assertNotNull(path);
        assertEquals(expectedStop, path.getStop());
        assertNull(path.getBus());
        assertEquals(0, path.getDistance());
    }
}