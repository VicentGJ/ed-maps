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

    public void dijkstraAlgorithm(Set<Vertex> alreadyUnlocked, Map<Vertex, WayToArrive> toUnlock) {
        Vertex vertex = getShortest(toUnlock);
        while (vertex != null) {
            alreadyUnlocked.add(vertex);
            toUnlock.remove(vertex);
            Float distance = nodes.get(vertex).distanceUpToHere;
            for (Edge e : vertex.getEdgeList()) {
                Route weight = (Route) ((WeightedEdge) e).getWeight();
                Vertex target = e.getVertex();
                Float actualDistance = distance + weight.getDistance();
                WayToArrive wayToArrive = null;
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
                if (!alreadyUnlocked.contains(target)) {
                    toUnlock.put(target, wayToArrive);
                }
            }
            vertex = getShortest(toUnlock);
        }

    }

    public CompletePath getShortestPathTo(Vertex goal) throws Exception {
        CompletePath result = new CompletePath();
        while (goal != null) {
            if (nodes.containsKey(goal)) {
                WayToArrive wayToArrive = nodes.get(goal);
                result.addPath(goal, wayToArrive.bus, wayToArrive.edgeDistance);
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
