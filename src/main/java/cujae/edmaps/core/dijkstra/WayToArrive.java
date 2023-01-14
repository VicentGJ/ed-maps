package cujae.edmaps.core.dijkstra;

import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.Bus;

/**
 * WayToArrive class represent how you came to a vertex having the previous vertex,
 * the bus used to, the distance from the beginning, and the distance of the edge.
 */
public class WayToArrive {
    private Vertex previousStop;
    private Bus bus;
    private Float distanceUpToHere;
    private Float edgeDistance;

    public WayToArrive(Vertex previousStop, Bus bus, Float distanceUpToHere, Float edgeDistance) {
        setPreviousStop(previousStop);
        setBus(bus);
        setDistanceUpToHere(distanceUpToHere);
        setEdgeDistance(edgeDistance);
    }

    public Vertex getPreviousStop() {
        return previousStop;
    }

    public void setPreviousStop(Vertex previousStop) {
        this.previousStop = previousStop;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Float getDistanceUpToHere() {
        return distanceUpToHere;
    }

    public void setDistanceUpToHere(Float distanceUpToHere) {
        this.distanceUpToHere = distanceUpToHere;
    }

    public Float getEdgeDistance() {
        return edgeDistance;
    }

    public void setEdgeDistance(Float edgeDistance) {
        this.edgeDistance = edgeDistance;
    }
}
