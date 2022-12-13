package cujae.edmaps.core.dijkstra;

import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.Bus;
import cujae.edmaps.core.FileManager;

import java.util.LinkedList;

public class CompletePath {
    private LinkedList<Path> paths;

    public CompletePath() {
        paths = new LinkedList<>();
    }

    public void addPath(Vertex vertex, Bus bus, Float distance) {
        paths.addFirst(new Path(vertex, bus, distance));
    }

    public LinkedList<Path> getPaths() {
        return paths;
    }

    public void save() {
        FileManager.getInstance().saveConsult(paths);
    }
}
