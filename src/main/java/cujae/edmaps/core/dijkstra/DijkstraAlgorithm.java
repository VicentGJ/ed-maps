package cujae.edmaps.core.dijkstra;

import cu.edu.cujae.ceis.graph.vertex.Vertex;

import java.util.Map;

public interface DijkstraAlgorithm {
    /**
     * Apply dijkstra algorithm starting on the initial point given and returning
     * a map with all accessible vertices and the way to arrive them
     *
     * @param initialStop Vertex that represent the initial stop where the algorithm will start
     * @return a map with the stops and the way to arrive to them
     */
    Map<Vertex, WayToArrive> getStopMap(Vertex initialStop);
}
