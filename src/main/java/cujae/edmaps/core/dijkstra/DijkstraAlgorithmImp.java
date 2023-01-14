package cujae.edmaps.core.dijkstra;

import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.Route;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public class DijkstraAlgorithmImp implements DijkstraAlgorithm{
    private Map<Vertex, WayToArrive> stopMap;
    private Set<Vertex> alreadyUnlocked;
    private PriorityQueue<Map.Entry<Vertex, WayToArrive>> toUnlock;

    /**
     * Apply dijkstra algorithm starting on the initial point given and returning
     * a map with all accessible vertices and the way to arrive them
     *
     * @param initialStop Vertex that represent the initial stop where the algorithm will start
     * @return a map with the stops and the way to arrive to them
     */
    @Override
    public Map<Vertex, WayToArrive> getStopMap(@NotNull Vertex initialStop){
        initializeParameters(initialStop);
        executeAlgorithm();
        return stopMap;
    }

    private void initializeParameters(Vertex initialStop){
        stopMap = new HashMap<>();
        alreadyUnlocked = new HashSet<>();
        WayToArrive wayToArrive = new WayToArrive(null, null, 0f, 0f);
        stopMap.put(initialStop, wayToArrive);
        toUnlock = new PriorityQueue<>(
                (e1, e2) -> Float.compare(e1.getValue().getDistanceUpToHere(), e2.getValue().getDistanceUpToHere())
        );
        toUnlock.add(Map.entry(initialStop, wayToArrive));
    }
    private void executeAlgorithm() {
        while(!toUnlock.isEmpty()){
            Vertex stop = toUnlock.poll().getKey();
            unlockStop(stop);
        }
    }
    private void unlockStop(Vertex stop){
        alreadyUnlocked.add(stop);
        addAdjacentStopsToToUnlockMap(stop);
    }
    private void addAdjacentStopsToToUnlockMap(Vertex stop){
        for (Edge edge : stop.getEdgeList()) {
            Vertex target = edge.getVertex();
            if(isUnlock(target))
                continue;
            Route route = (Route) ((WeightedEdge) edge).getWeight();
            Float distanceUpToHere = stopMap.get(stop).getDistanceUpToHere();
            float actualDistance = distanceUpToHere + route.getDistance();
            setWayToArrive(target, stop, actualDistance, route);
        }
    }
    private void setWayToArrive(Vertex target, Vertex previousStop, float actualDistance, Route route){
        if(stopMap.containsKey(target)) {
            WayToArrive wayToArrive = stopMap.get(target);
            if (wayToArrive.getDistanceUpToHere() > actualDistance) {
                wayToArrive.setDistanceUpToHere(actualDistance);
                wayToArrive.setBus(route.getBus());
                wayToArrive.setPreviousStop(previousStop);
                wayToArrive.setEdgeDistance(route.getDistance());
            }
        }else{
            WayToArrive wayToArrive = new WayToArrive(previousStop, route.getBus(), actualDistance, route.getDistance());
            stopMap.put(target, wayToArrive);
            toUnlock.offer(Map.entry(target, wayToArrive));
        }
    }
    private boolean isUnlock(Vertex stop){
        return alreadyUnlocked.contains(stop);
    }
}
