package cujae.edmaps.core.dijkstra;

import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.Bus;
import cujae.edmaps.core.Route;

import java.util.*;

public class DijkstraShortestPath {
    private Vertex start;
    private Map<Vertex, WayToArrive> nodes;

    private class WayToArrive {
        private Vertex previous;
        private Float distance;

        private Bus bus;

        public WayToArrive(Vertex previous, Float distance, Bus bus) {
            this.previous = previous;
            this.distance = distance;
            this.bus = bus;
        }
    }

    public DijkstraShortestPath(Vertex start) {
        this.start = start;
        nodes = new HashMap<>();
        Map<Vertex, WayToArrive> toUnlock = new HashMap<>();
        WayToArrive wayToArrive = new WayToArrive(null, 0.0f, null);
        nodes.put(start, wayToArrive);
        toUnlock.put(start, wayToArrive);
        dijkstraAlgorithm(new HashSet<>(), toUnlock);
    }

    public void dijkstraAlgorithm(Set<Vertex> alreadyUnlocked, Map<Vertex, WayToArrive> toUnlock) {
        Vertex vertex = getShortest(toUnlock);
        while (vertex != null) {
            Float distance = nodes.get(vertex).distance;
            for (Edge e : vertex.getEdgeList()) {
                Route weight = (Route) ((WeightedEdge) e).getWeight();
                Vertex target = e.getVertex();
                Float actualDistance = distance + weight.getDistance();
                WayToArrive wayToArrive = null;
                if (nodes.containsKey(target)) {
                    wayToArrive = nodes.get(target);
                    if (wayToArrive.distance > actualDistance) {
                        wayToArrive.distance = actualDistance;
                        wayToArrive.bus = weight.getBus();
                    }
                } else {
                    wayToArrive = new WayToArrive(vertex, actualDistance, weight.getBus());
                    nodes.put(target, wayToArrive);
                }
                if (!alreadyUnlocked.contains(target)) {
                    alreadyUnlocked.add(target);
                    toUnlock.put(target, wayToArrive);
                }
            }
            toUnlock.remove(vertex);
            vertex = getShortest(toUnlock);
        }

    }

    public CompletePath getShortestPathTo(Vertex goal) throws Exception {
        CompletePath result = new CompletePath();
        while (goal != null) {
            if (nodes.containsKey(goal)) {
                WayToArrive wayToArrive = nodes.get(goal);
                result.addPath(goal, wayToArrive.bus, wayToArrive.distance);
                goal = wayToArrive.previous;
            } else throw new Exception();
        }
        return result;
    }

    private Vertex getShortest(Map<Vertex, WayToArrive> map) {
        Vertex result = null;
        Iterator<Map.Entry<Vertex, WayToArrive>> it = map.entrySet().iterator();
        Float min = 0.0f;
        if (it.hasNext()) {
            Map.Entry<Vertex, WayToArrive> entry = it.next();
            min = entry.getValue().distance;
            result = entry.getKey();
        }
        while (it.hasNext()) {
            Map.Entry<Vertex, WayToArrive> entry = it.next();
            if (min > entry.getValue().distance) {
                min = entry.getValue().distance;
                result = entry.getKey();
            }
        }
        return result;
    }

    public Vertex getStart() {
        return start;
    }
}
