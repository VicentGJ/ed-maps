package cujae.edmaps.core.dijkstra;

import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.Bus;
import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.Route;

import java.security.InvalidParameterException;
import java.util.*;

public class DijkstraShortestPath {
    private final Vertex start;
    private final Map<Vertex, WayToArrive> nodes;

    /**
     * WayToArrive class represent how you came to a vertex having the previous vertex,
     * the bus used to, the distance from the beginning, and the distance of the edge.
     */
    private static class WayToArrive {
        private Vertex previous;
        private Bus bus;
        private Float distanceUpToHere;
        private Float edgeDistance;

        public WayToArrive(Vertex previous, Bus bus, Float distanceUpToHere, Float edgeDistance) {
            this.previous = previous;
            this.bus = bus;
            this.distanceUpToHere = distanceUpToHere;
            this.edgeDistance = edgeDistance;
        }
    }

    public DijkstraShortestPath(Vertex start) {
        this.start = start;
        nodes = new HashMap<>();
        Map<Vertex, WayToArrive> toUnlock = new HashMap<>();
        WayToArrive wayToArrive = new WayToArrive(null, null, 0.0f, 0.0f);
        nodes.put(start, wayToArrive);
        toUnlock.put(start, wayToArrive);
        dijkstraAlgorithm(new HashSet<>(), toUnlock);
    }

    /**
     *Use a modification of dijkstra shortest path algorithm to find the best way to cross a city and save it
     * on a map to be used later
     *
     * @param alreadyUnlocked vertex that were unlocked by the algorithm
     * @param toUnlock vertex that are waiting to be unlocked should start with the start vertex
     */
    private void dijkstraAlgorithm(Set<Vertex> alreadyUnlocked, Map<Vertex, WayToArrive> toUnlock) {
        Vertex vertex = getShortest(toUnlock);
        while (vertex != null) {
            alreadyUnlocked.add(vertex);
            toUnlock.remove(vertex);
            Float distance = nodes.get(vertex).distanceUpToHere;
            for (Edge e : vertex.getEdgeList()) {
                Route weight = (Route) ((WeightedEdge) e).getWeight();
                Vertex target = e.getVertex();
                if(!alreadyUnlocked.contains(target)) {
                    Float actualDistance = distance + weight.getDistance();
                    WayToArrive wayToArrive;
                    if (nodes.containsKey(target)) {
                        wayToArrive = nodes.get(target);
                        if (wayToArrive.distanceUpToHere > actualDistance) {
                            wayToArrive.distanceUpToHere = actualDistance;
                            wayToArrive.bus = weight.getBus();
                            wayToArrive.previous = vertex;
                            wayToArrive.edgeDistance = weight.getDistance();
                        }
                    } else {
                        wayToArrive = new WayToArrive(vertex, weight.getBus(), actualDistance, weight.getDistance());
                        nodes.put(target, wayToArrive);
                    }
                    toUnlock.put(target, wayToArrive);
                }
            }
            vertex = getShortest(toUnlock);
        }
    }

    /**
     * Using the map made by dijkstraAlgorithm find the best way to go from the start vertex set before to the
     * destination vertex
     *
     * @param destination the vertex you want to go
     * @return A CompletePath with the list of vertex you have to go through
     * @throws InvalidParameterException if there is no way to go to the destination vertex
     */
    public CompletePath getShortestPathTo(Vertex destination) {
        CompletePath result = new CompletePath();
        while (destination != null) {
            if (nodes.containsKey(destination)) {
                WayToArrive wayToArrive = nodes.get(destination);
                result.addPath(destination, wayToArrive.bus, wayToArrive.edgeDistance);
                destination = wayToArrive.previous;
            } else throw new InvalidParameterException("destination: " + ((BusStop)destination.getInfo()).getName());

        }
        return result;
    }

    /**
     * Find the shortest route up to that moment
     *
     * @param map vertex with they WayToArrive
     * @return vertex who has the shortest distance
     */
    private Vertex getShortest(Map<Vertex, WayToArrive> map) {
        Vertex result = null;
        Iterator<Map.Entry<Vertex, WayToArrive>> it = map.entrySet().iterator();
        Float min = 0.0f;
        if (it.hasNext()) {
            Map.Entry<Vertex, WayToArrive> entry = it.next();
            min = entry.getValue().distanceUpToHere;
            result = entry.getKey();
        }
        while (it.hasNext()) {
            Map.Entry<Vertex, WayToArrive> entry = it.next();
            if (min > entry.getValue().distanceUpToHere) {
                min = entry.getValue().distanceUpToHere;
                result = entry.getKey();
            }
        }
        return result;
    }

    public Vertex getStart() {
        return start;
    }
}
