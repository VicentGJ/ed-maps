package cujae.edmaps.core.dijkstra;

import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.BusStop;

import java.security.InvalidParameterException;
import java.util.*;

public class ShortestPath {
    private Vertex initialStop;
    private Map<Vertex, WayToArrive> stopMap;
    /**
     * Find the best way to go from initial vertex set before to destination vertex
     *
     * @param destination the vertex you want to go
     * @return A CompletePath with the list of vertices you have to go through
     * @throws InvalidParameterException if there is no way to go to the destination vertex
     */
    public CompletePath getShortestPathTo(Vertex destination) {
        CompletePath result = new CompletePath();
        while (destination != null) {
            if (stopMap.containsKey(destination)) {
                WayToArrive wayToArrive = stopMap.get(destination);
                result.addPath(destination, wayToArrive.getBus(), wayToArrive.getEdgeDistance());
                destination = wayToArrive.getPreviousStop();
            } else throw new InvalidParameterException("destination: " + ((BusStop)destination.getInfo()).getName());
        }
        return result;
    }
    public void setInitialStop(Vertex initialStop) {
        DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithmImp();
        this.stopMap = dijkstraAlgorithm.getStopMap(initialStop);
        this.initialStop = initialStop;
    }
    public Vertex getInitialStop() {
        return initialStop;
    }
}
