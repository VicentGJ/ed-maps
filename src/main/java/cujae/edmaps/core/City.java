package cujae.edmaps.core;

import cu.edu.cujae.ceis.graph.*;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.interfaces.ILinkedWeightedEdgeDirectedGraph;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.dijkstra.CompletePath;
import cujae.edmaps.core.dijkstra.DijsktraShortestPath;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class City {
    private String name;
    private LinkedGraph routeGraph;
    private List<Bus> busList;
    private DijsktraShortestPath dijsktraShortestPath;

    public City(String name) {
        setName(name);
        this.routeGraph = new LinkedGraph();
        this.busList = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ILinkedWeightedEdgeDirectedGraph getRouteGraph() {
        return routeGraph;
    }

    public List<Bus> getBusList() {
        return busList;
    }

    public void addBus(Bus bus) {
        this.busList.add(bus);
    }

    public void addBusStop(String name) {
        if (!existName(name)) {
            routeGraph.insertVertex(new BusStop(name));
        }
    }

    public void addRoute(BusStop stop1, BusStop stop2, Bus bus, Double distance) {
        Vertex v1 = getVertex(stop1);
        if (v1 != null) {
            Vertex v2 = getVertex(stop2);
            if (v2 != null) {
                Route route = new Route(bus, distance);
                routeGraph.insertWEdgeDG(routeGraph.getVertexIndex(v1), routeGraph.getVertexIndex(v2), route);
            }
        }
    }

    public void addNearbyStop(BusStop stop1, BusStop stop2, Double distance) {
        Vertex v1 = getVertex(stop1);
        if (v1 != null) {
            Vertex v2 = getVertex(stop2);
            if (v2 != null) {
                Route route = new Route(null, distance);
                routeGraph.insertWEdgeDG(routeGraph.getVertexIndex(v1), routeGraph.getVertexIndex(v2), route);
            }
        }
    }

    public boolean existName(String name) {
        Iterator<Vertex> i = routeGraph.getVerticesList().iterator();
        boolean found = false;
        while (i.hasNext() && !found)
            found = ((BusStop) i.next().getInfo()).getName().equals(name);
        return found;
    }

    public Vertex getVertex(BusStop busStop) {
        Vertex result = null;
        Iterator<Vertex> it = routeGraph.getVerticesList().iterator();
        boolean found = false;
        while (it.hasNext() && !found) {
            Vertex aux = it.next();
            if (aux.getInfo().equals(busStop)) {
                found = true;
                result = aux;
            }
        }
        return result;
    }

    public CompletePath getPathBetween(Vertex start, Vertex goal) throws Exception {
        if (dijsktraShortestPath == null || !dijsktraShortestPath.getStart().equals(start)) {
            dijsktraShortestPath = new DijsktraShortestPath(start);
        }
        return dijsktraShortestPath.getShortestPathTo(goal);
    }

    public int getBusStopIndex(BusStop busStop) {
        int index = -1;
        int count = 0;
        for (Iterator<Vertex> i = this.routeGraph.getVerticesList().iterator(); index == -1 && i.hasNext(); ++count)
            if (((BusStop) i.next().getInfo()).equals(busStop)) index = count;
        return index;
    }

    public boolean insertRoute(Route route, BusStop tail, BusStop head) {
        boolean success = false;
        int indexTail = getBusStopIndex(tail);
        if (indexTail != -1) {
            int indexHead = getBusStopIndex(head);
            if (indexHead != -1) {
                this.routeGraph.insertWEdgeDG(indexTail, indexHead, route);
                success = true;
            }
        }
        return success;
    }

    public boolean removeRoute(BusStop tail, BusStop head) {
        boolean success = false;
        int indexTail = getBusStopIndex(tail);
        if (indexTail != -1) {
            int indexHead = getBusStopIndex(head);
            if (indexHead != -1) {
                this.routeGraph.deleteEdgeD(indexTail, indexHead);
                success = true;
            }
        }
        return success;
    }
}
