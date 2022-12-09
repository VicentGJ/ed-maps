package cujae.edmaps.core;

import cu.edu.cujae.ceis.graph.*;
import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.interfaces.ILinkedWeightedEdgeNotDirectedGraph;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.dijkstra.CompletePath;
import cujae.edmaps.core.dijkstra.DijkstraShortestPath;

import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class City {
    private String name;
    private ILinkedWeightedEdgeNotDirectedGraph routeGraph;
    private List<Bus> busList;
    private DijkstraShortestPath dijsktraShortestPath;

    private static City city;

    public static City getInstance() {
        if (city == null) {
            city = new City("Alacant");
        }
        return city;
    }

    private City(String name) {
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

    public ILinkedWeightedEdgeNotDirectedGraph getRouteGraph() {
        return routeGraph;
    }

    public List<Bus> getBusList() {
        return busList;
    }

    public void addBus(String name) {
        this.busList.add(new Bus(name));
    }

    /**
     * Find the bus in the busList by a given name
     *
     * @param busName the name of the Bus to get
     * @return the Bus or null if is not found
     */
    public Bus getBus(String busName) {
        Bus bus = null;
        for (Bus b : busList)
            if (b.getName().equals(busName)) bus = b;
        return bus;
    }

    public void addBusStop(String name) {
        if (!existBusStop(name)) {
            routeGraph.insertVertex(new BusStop(name));
        }
    }

    /**
     * Search for the BusStop's name in the graph
     *
     * @param name the name of the BusStop to find
     * @return true if the BusStop already exists, false otherwise
     */
    public boolean existBusStop(String name) {
        Iterator<Vertex> i = routeGraph.getVerticesList().iterator();
        boolean found = false;
        while (i.hasNext() && !found) found = ((BusStop) i.next().getInfo()).getName().equals(name);
        return found;
    }

    /**
     * Search for the Bus's name in BusList
     *
     * @param name the name of the Bus to find
     * @return true if the Bus already exists, false otherwise
     */
    public boolean existBus(String name) {
        boolean found = false;
        for (Bus bus : busList)
            if (bus.getName().equals(name)) {
                found = true;
                break;
            }
        return found;
    }

    /**
     * Get the BusStop's Vertex from the graph, by searching the BusStop name
     *
     * @param busStopName the name of the busStop to find
     * @return the Vertex corresponding to the given BusStop's name or null if it doesn't exist
     */
    public Vertex getVertex(String busStopName) {
        Vertex result = null;
        Iterator<Vertex> it = routeGraph.getVerticesList().iterator();
        boolean found = false;
        while (it.hasNext() && !found) {
            Vertex aux = it.next();
            if (((BusStop) aux.getInfo()).getName().equals(busStopName)) {
                found = true;
                result = aux;
            }
        }
        return result;
    }

    /**
     * Finds the shortest path between the given busStops
     *
     * @param start the BusStop's name of the starting point
     * @param goal  the BusStop's name of the goal point
     * @return a CompletePath instance with the path from start to goal
     * @throws Exception
     */
    //TODO: document when is the Exception thrown
    public CompletePath getPathBetween(String start, String goal) throws Exception {
        Vertex tail = getVertex(start);
        Vertex head = getVertex(goal);
        if (dijsktraShortestPath == null || !((BusStop) dijsktraShortestPath.getStart().getInfo()).getName().equals(start)) {
            dijsktraShortestPath = new DijkstraShortestPath(tail);
        }
        return dijsktraShortestPath.getShortestPathTo(head);
    }

    /**
     * Find the index of the given BusStop in the vertices list of the graph
     *
     * @param busStopName the busStop which index we want to find
     * @return the index in the graph or -1 if it doesn't exist
     */
    public int getBusStopIndex(String busStopName) {
        int index = -1;
        int count = 0;
        for (Iterator<Vertex> i = this.routeGraph.getVerticesList().iterator(); index == -1 && i.hasNext(); ++count)
            if (((BusStop) i.next().getInfo()).getName().equals(busStopName)) index = count;
        return index;
    }

    /**
     * Insert the <strong>weighted directed edge</strong> from tail to head
     *
     * @param busStopTail the BusStop's name from which the edge is going to point
     * @param busStopHead the BusStop's name to which the edge is going to point
     * @param busName     the Bus's name that will represent the Route, or <strong>null</strong> to set a walking route
     * @param distance    the distance to set from tail to head, has to be positive and if busName is <strong>null</strong>, it has to be <= 500
     * @throws InvalidParameterException if busStopTail or busStopHead doesn't exist, and if distance < 0 or if is a walking route and distance >500
     */
    public void insertRoute(String busStopTail, String busStopHead, String busName, Float distance) throws InvalidParameterException {
        if (distance > 0) {
            Bus bus = getBus(busName);
            if (bus != null || distance <= 500) {
                int tailIndex = getBusStopIndex(busStopTail);
                if (tailIndex != -1) {
                    int headIndex = getBusStopIndex(busStopHead);
                    if (headIndex != -1) {
                        Route route = new Route(bus, distance);
                        this.routeGraph.insertWEdgeNDG(tailIndex, headIndex, route);
                    } else throw new InvalidParameterException("busStopHead not found: " + busStopHead);
                } else throw new InvalidParameterException("busStopTail not found: " + busStopTail);
            } else throw new InvalidParameterException("distance(walking route): " + distance);
        } else throw new InvalidParameterException("distance: " + distance);
    }

    /**
     * Removes the <strong>weighted edge</strong> that points from tail to head
     *
     * @param tail the BusStop's name that represents the tail of the edge
     * @param head the BusStop's name that represents the head of the edge
     * @throws InvalidParameterException if tail or head doesn't exist
     */
    public void removeRoute(String tail, String head, String busName) {
        if (tail == null || head == null) return;
        Vertex tailVertex = getVertex(tail);
        if (tailVertex == null) throw new InvalidParameterException("tail not found: " + tail);
        Vertex headVertex = getVertex(head);
        if (headVertex == null) throw new InvalidParameterException("head not found: " + head);
        WeightedEdge wEdge = getEdge(tailVertex, busName);
        Iterator<Edge> edgeIterator = tailVertex.getEdgeList().iterator();
        boolean removed = false;
        WeightedEdge currentWEdge = null;
        while (edgeIterator.hasNext() && !removed) {
            currentWEdge = (WeightedEdge) edgeIterator.next();
            if (currentWEdge.getVertex().equals(headVertex) && wEdge == currentWEdge) {
                edgeIterator.remove();
                removed = true;
                removeRoute(head, tail, busName);
            }
        }
    }

    /**
     * Removes the Bus and all its routes in the graph
     *
     * @param name the Bus's name to remove
     * @throws InvalidParameterException if bus doesn't exist
     */
    public void removeBus(String name) {
        Iterator<Bus> i = busList.iterator();
        boolean removed = false;
        Bus current = null;
        while (i.hasNext() && !removed) {
            current = i.next();
            if (current.getName().equals(name)) {
                removeConnections(current);
                i.remove();
                removed = true;
            }
        }
        if (!removed) throw new InvalidParameterException("name not found: " + name);
    }

    /**
     * Remove all the connections of a given Bus
     *
     * @param bus the Bus which connections are going to be deleted
     */
    private void removeConnections(Bus bus) {
        LinkedList<Vertex> vertices = routeGraph.getVerticesList();
        Iterator<Vertex> i = vertices.iterator();
        Vertex currentVertex = null;
        while (i.hasNext()) {
            currentVertex = i.next();
            LinkedList<Edge> edges = currentVertex.getEdgeList();
            Iterator<Edge> j = edges.iterator();
            WeightedEdge currentWEdge = null;
            while (j.hasNext()) {
                currentWEdge = (WeightedEdge) j.next();
                Route route = (Route) currentWEdge.getWeight();
                if (route.getBus().equals(bus)) j.remove();
            }
        }
    }

    /**
     * Modify the name of the specified BusStop
     *
     * @param oldName The actual BusStop's name
     * @param newName the new BusStop's name
     * @throws InvalidParameterException if the oldName doesn't exist or if the newName is already taken
     */
    public void renameBusStop(String oldName, String newName) {
        Vertex vertex = getVertex(oldName);
        if (vertex != null) {
            if (!existBusStop(newName))
                ((BusStop) vertex.getInfo()).setName(newName);
            else throw new InvalidParameterException("newName already exist: " + newName);
        } else throw new InvalidParameterException("oldName not found: " + oldName);
    }

    /**
     * Modify the distance of a route between two busStops
     *
     * @param tail        the BusStop's name that represents the departure stop of the route
     * @param head        the BusStop's name that represents the arrival stop of the route
     * @param bus         the name of the route it's going to be deleted
     * @param newDistance the new route's distance
     * @throws InvalidParameterException if tail, head or bus don't exist
     */
    public void modifyDistanceBetween(String tail, String head, String bus, Float newDistance) {
        if (newDistance <= 0) throw new InvalidParameterException("newDistance: " + newDistance);
        Vertex stop1 = getVertex(tail);
        if (stop1 != null) {
            Vertex stop2 = getVertex(head);
            if (stop2 != null) {
                WeightedEdge edge = getEdge(stop1, bus);
                if (edge != null) {
                    ((Route) edge.getWeight()).setDistance(newDistance);
                    edge = getEdge(stop2, bus);
                    ((Route) edge.getWeight()).setDistance(newDistance);
                } else throw new InvalidParameterException("bus:" + bus);
            } else throw new InvalidParameterException("head:" + head);
        } else throw new InvalidParameterException("tail:" + tail);


    }

    /**
     * Gets the edge that represents the route
     *
     * @param stop1 Departure vertex
     * @param bus   Bus name
     * @return The WeightedEdge
     */
    public WeightedEdge getEdge(Vertex stop1, String bus) {
        WeightedEdge result = null;
        boolean found = false;
        Iterator<Edge> it = stop1.getEdgeList().iterator();
        while (it.hasNext() && !found) {
            WeightedEdge aux = (WeightedEdge) it.next();
            //FIXME: .getBus() can return null if is a walking route, throwing an exception when doing null.getName()
            if (((Route) aux.getWeight()).getBus().getName().equals(bus)) {
                result = aux;
                found = true;
            }
        }
        return result;
    }
}
