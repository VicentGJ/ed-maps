package cujae.edmaps.core;

public class Route {
    private Bus bus;
    private Float distance;

    public Route(Bus bus, Float distance) {
        setBus(bus);
        setDistance(distance);
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

    public boolean setDistance(Float distance) {
        if (distance < 0) return false;
        else this.distance = distance;
        return true;
    }
}
