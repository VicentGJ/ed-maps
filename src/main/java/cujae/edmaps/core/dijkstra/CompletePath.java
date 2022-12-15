package cujae.edmaps.core.dijkstra;

import cu.edu.cujae.ceis.graph.LinkedGraph;
import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.interfaces.ILinkedWeightedEdgeNotDirectedGraph;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.Bus;
import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.FileManager;
import cujae.edmaps.core.Route;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

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

    /**
     * Parses the list of paths into a linear graph, all weight and vertex instances in this new graph are
     * copies of the original values, so original graph won't get modified from modifying this
     *
     * @return a ILinkedWeightedEdgeNotDirectedGraph instance
     * @throws IllegalStateException if the list of paths is empty
     */
    public ILinkedWeightedEdgeNotDirectedGraph parseToGraph() {
        if (paths.isEmpty()) throw new IllegalStateException("Nothing to parse");
        ILinkedWeightedEdgeNotDirectedGraph result = new LinkedGraph();
        Iterator<Path> pathIterator = paths.iterator();
        Path currentPath = null;
        Path previousPath = null;
        int counter = 0;
        while (pathIterator.hasNext()) {
            currentPath = pathIterator.next();
            Vertex originalVertex = currentPath.getStop();
            Object busStopCopy = ((BusStop) originalVertex.getInfo()).clone();
            result.insertVertex(busStopCopy);
            if (previousPath != null) {
                Vertex previousVertex = previousPath.getStop();
                LinkedList<Edge> prevEdges = previousVertex.getEdgeList();
                Iterator<Edge> prevEdgesIterator = prevEdges.iterator();
                WeightedEdge currentWEdge = null;
                boolean inserted = false;
                while (prevEdgesIterator.hasNext() && !inserted) {
                    currentWEdge = (WeightedEdge) prevEdgesIterator.next();
                    if (currentWEdge.getVertex().equals(originalVertex)) {
                        Object weight = ((Route) currentWEdge.getWeight()).clone();
                        result.insertWEdgeNDG(counter, counter - 1, weight);
                        inserted = true;
                    }
                }
            }
            ++counter;
            previousPath = currentPath;
        }
        return result;
    }
}
