package cujae.edmaps.core.dijkstra;

import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.Bus;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CompletePath {
    private LinkedList<Path> paths;

    public CompletePath() {
        paths = new LinkedList<>();
    }
    public void addPath(Vertex vertex, Bus bus, Double distance){
        paths.addFirst(new Path(vertex, bus, distance));
    }
}
