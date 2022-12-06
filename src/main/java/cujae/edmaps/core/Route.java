package cujae.edmaps.core;

public class Route {
    private Bus bus;
    private Double distance;

    public Route(Bus bus, Double distance) {
        setBus(bus);
        setDistance(distance);
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Double getDistance() {
        return distance;
    }

    public boolean setDistance(Double distance) {
        if (distance < 0) return false;
        else this.distance = distance;
        return true;
    }
}
