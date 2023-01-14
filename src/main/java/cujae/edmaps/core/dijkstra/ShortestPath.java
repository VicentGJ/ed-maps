package cujae.edmaps.core.dijkstra;

import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.*;

import java.util.LinkedList;

public class ShortestPath {
    private final LinkedList<Path> paths;

    public ShortestPath() {
        paths = new LinkedList<>();
    }

    public void addPath(Vertex vertex, Bus bus, Float distance) {
        paths.addFirst(new Path(vertex, bus, distance));
    }

    public LinkedList<Path> getPaths() {
        return paths;
    }

    public void save() {
        MapsManager.getInstance().saveConsult(paths);
    }

}