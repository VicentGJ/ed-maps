package cujae.edmaps.core.dijkstra;

import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.Route;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;

public class DijkstraAlgorithmImp implements DijkstraAlgorithm{
    private Vertex initialStop;
    private Map<Vertex, WayToArrive> stopMap;
    private Map<Vertex, WayToArrive> toUnlock;
    private Set<Vertex> alreadyUnlocked;

    /**
     * Apply dijkstra algorithm starting on the initial point given and returning
     * a map with all accessible vertices and the way to arrive them
     *
     * @param initialStop Vertex that represent the initial stop where the algorithm will start
     * @return a map with the stops and the way to arrive to them
     */
    @Override
    public Map<Vertex, WayToArrive> getStopMap(Vertex initialStop){
        initializeParameters(initialStop);
        executeAlgorithm();
        return stopMap;
    }

    private void initializeParameters(Vertex initialStop){
        setInitialStop(initialStop);
        stopMap = new HashMap<>();
        toUnlock = new HashMap<>();
        alreadyUnlocked = new HashSet<>();
        stopMap.put(initialStop, new WayToArrive(null, null, 0f, 0f));
    }
    private void executeAlgorithm() {
        Vertex stop = initialStop;
        while(stop != null){
            unlockStop(stop);
            stop = getShortest();
        }
    }
    private void unlockStop(Vertex stop){
        alreadyUnlocked.add(stop);
        toUnlock.remove(stop);
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
            toUnlock.put(target, wayToArrive);
        }
    }
    private boolean isUnlock(Vertex stop){
        return alreadyUnlocked.contains(stop);
    }
    /**
     * Find the shortest route up to that moment
     *
     * @return vertex who has the shortest distance
     */
    @Nullable
    private Vertex getShortest() {
        Vertex result = null;
        Iterator<Map.Entry<Vertex, WayToArrive>> it = toUnlock.entrySet().iterator();
        Float min = 0.0f;
        if (it.hasNext()) {
            Map.Entry<Vertex, WayToArrive> entry = it.next();
            min = entry.getValue().getDistanceUpToHere();
            result = entry.getKey();
        }
        while (it.hasNext()) {
            Map.Entry<Vertex, WayToArrive> entry = it.next();
            if (min > entry.getValue().getDistanceUpToHere()) {
                min = entry.getValue().getDistanceUpToHere();
                result = entry.getKey();
            }
        }
        return result;
    }

    private void setInitialStop(@NotNull Vertex initialStop) {
        this.initialStop = initialStop;
    }
}
