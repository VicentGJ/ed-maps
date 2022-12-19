package cujae.edmaps.core.dijkstra;

import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.Bus;

public class Path {
    private Vertex stop;
    private Bus bus;
    private Float distance;

    public Path(Vertex stop, Bus bus, Float distance) {
        this.stop = stop;
        this.bus = bus;
        this.distance = distance;
    }

    public Vertex getStop() {
        return stop;
    }

    public void setStop(Vertex stop) {
        this.stop = stop;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }
}
