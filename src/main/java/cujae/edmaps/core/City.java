package cujae.edmaps.core;

import cu.edu.cujae.ceis.graph.*;
import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.interfaces.ILinkedWeightedEdgeNotDirectedGraph;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.dijkstra.CompletePath;
import cujae.edmaps.core.dijkstra.DijkstraShortestPath;

import java.security.InvalidParameterException;
import java.util.*;

public class City {
    private String name;
    private ILinkedWeightedEdgeNotDirectedGraph routeGraph;
    private List<Bus> busList;
    private DijkstraShortestPath dijsktraShortestPath;

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

    public ILinkedWeightedEdgeNotDirectedGraph getRouteGraph() {
        return routeGraph;
    }

    //***************************************************************
    //------------------------------BUS------------------------------
    //***************************************************************

    public List<Bus> getBusList() {
        return busList;
    }

    public void addBus(String name) {
        if (!existBus(name))
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
            if (b.getName().equalsIgnoreCase(busName)) bus = b;
        return bus;
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
            if (bus.getName().equalsIgnoreCase(name)) {
                found = true;
                break;
            }
        return found;
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
            if (current.getName().equalsIgnoreCase(name)) {
                removeConnections(current);
                i.remove();
                removed = true;
            }
        }
        if (!removed) throw new InvalidParameterException("name not found: " + name);
    }

    public void renameBus(String oldName, String newName) {
        Bus bus = getBus(oldName);
        if (bus != null) {
            if (!existBus(newName)) {
                bus.setName(newName);
            } else throw new InvalidParameterException("newName already exist: " + newName);
        } else throw new InvalidParameterException("oldName not found: " + oldName);
    }
    //********************************************************************
    //------------------------------BUS STOP------------------------------
    //********************************************************************
    public void addBusStop(String name) {
        if (!existBusStop(name)) {
            routeGraph.insertVertex(new BusStop(name));
        }
    }

    public List<BusStop> getBusStopList() {
        List<BusStop> busStopList = new ArrayList<>();
        List<Vertex> vertices = routeGraph.getVerticesList();
        for (Vertex v : vertices) {
            busStopList.add((BusStop) v.getInfo());
        }
        return busStopList;
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
        while (i.hasNext() && !found) found = ((BusStop) i.next().getInfo()).getName().equalsIgnoreCase(name);
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
            if (((BusStop) aux.getInfo()).getName().equalsIgnoreCase(busStopName)) {
                found = true;
                result = aux;
            }
        }
        return result;
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
            if (((BusStop) i.next().getInfo()).getName().equalsIgnoreCase(busStopName)) index = count;
        return index;
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

    public void removeBusStop(String name) {
        Vertex busStopVertex = getVertex(name);
        if (busStopVertex == null) throw new InvalidParameterException("name not found: " + name);
        Vertex currentVertex = null;
        Iterator<Vertex> vertexIterator = this.routeGraph.getVerticesList().iterator();
        while (vertexIterator.hasNext()) {
            currentVertex = vertexIterator.next();
            Edge currentEdge = null;
            if (currentVertex.equals(busStopVertex)) {
                currentVertex.getEdgeList().clear();
                vertexIterator.remove();
            } else {
                Iterator<Edge> edgeIterator = currentVertex.getEdgeList().iterator();
                while (edgeIterator.hasNext()) {
                    currentEdge = edgeIterator.next();
                    if (currentEdge.getVertex().equals(busStopVertex)) {
                        edgeIterator.remove();
                    }
                }
            }
        }
    }
    //******************************************************************
    //------------------------------ROUTES------------------------------
    //******************************************************************

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
                        WeightedEdge existentEdge = getEdge(busName, busStopTail, busStopHead);
                        if (existentEdge != null) {
                            ((Route) existentEdge.getWeight()).setDistance(distance);
                        } else if (canInsertRoute(tailIndex, headIndex, bus)) {
                            Route route = new Route(bus, distance);
                            this.routeGraph.insertWEdgeNDG(tailIndex, headIndex, route);
                        } else throw new InvalidParameterException("busName: " + busName);
                    } else throw new InvalidParameterException("busStopHead not found: " + busStopHead);
                } else throw new InvalidParameterException("busStopTail not found: " + busStopTail);
            } else throw new InvalidParameterException("distance(walking route): " + distance);
        } else throw new InvalidParameterException("distance: " + distance);
    }

    /**
     * @param stop1Index the BusStop's index from the graph
     * @param stop2Index the BusStop's index from the graph
     * @param bus        the route you want to check
     * @return true if the route can be inserted or false if not
     */
    public boolean canInsertRoute(int stop1Index, int stop2Index, Bus bus) {
        if (bus != null) {
            boolean insertable = false;
            Vertex tail = routeGraph.getVerticesList().get(stop1Index);
            Vertex head = routeGraph.getVerticesList().get(stop2Index);
            Iterator<Vertex> it = routeGraph.getVerticesList().iterator();
            int stops = 0;
            while (it.hasNext() && !insertable) {
                Vertex aux = it.next();
                Iterator<Edge> it2 = aux.getEdgeList().iterator();
                while (it2.hasNext() && !insertable) {
                    WeightedEdge edge = (WeightedEdge) it2.next();
                    Route route = (Route) edge.getWeight();
                    if (Objects.equals(route.getBus(), bus)) {
                        if (aux.equals(tail) || aux.equals(head) ||
                                edge.getVertex().equals(tail) || edge.getVertex().equals(head)) {
                            insertable = true;
                        } else stops++;
                    }
                }
            }
            if (!insertable && stops == 0)
                insertable = true;
            return insertable;
        }
        return true;
    }

    /**
     * Removes the <strong>weighted edge</strong> that points from tail to head
     *
     * @param tail the BusStop's name that represents the tail of the edge
     * @param head the BusStop's name that represents the head of the edge
     * @throws InvalidParameterException if tail or head doesn't exist
     */
    public void removeRoute(String tail, String head, String busName) {
        Vertex tailVertex = getVertex(tail);
        if (tailVertex == null) throw new InvalidParameterException("tail not found: " + tail);
        Vertex headVertex = getVertex(head);
        if (headVertex == null) throw new InvalidParameterException("head not found: " + head);
        WeightedEdge wEdge = getEdge(busName, tail, head);
        Iterator<Edge> edgeIterator = tailVertex.getEdgeList().iterator();
        boolean removedFromTail = false;
        boolean removedFromHead = false;
        WeightedEdge currentWEdge = null;
        while (!removedFromTail && edgeIterator.hasNext()) {
            currentWEdge = (WeightedEdge) edgeIterator.next();
            if (currentWEdge.getVertex().equals(headVertex) && wEdge == currentWEdge) {
                edgeIterator.remove();
                removedFromTail = true;
                edgeIterator = headVertex.getEdgeList().iterator();
                wEdge = getEdge(busName, head, tail);
                while (!removedFromHead && edgeIterator.hasNext()) {
                    currentWEdge = (WeightedEdge) edgeIterator.next();
                    if (currentWEdge.getVertex().equals(tailVertex) && wEdge == currentWEdge) {
                        edgeIterator.remove();
                        removedFromHead = true;
                    }
                }
            }
        }
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
     * Modify the distance of a route between two busStops
     *
     * @param tail        the BusStop's name that represents the departure stop of the route
     * @param head        the BusStop's name that represents the arrival stop of the route
     * @param bus         the name of the route you want to modify
     * @param newDistance the new route's distance
     * @throws InvalidParameterException if tail, head or bus don't exist
     */
    public void modifyDistanceBetween(String tail, String head, String bus, Float newDistance) {
        if (newDistance <= 0) throw new InvalidParameterException("newDistance: " + newDistance);
        Vertex stop1 = getVertex(tail);
        if (stop1 != null) {
            Vertex stop2 = getVertex(head);
            if (stop2 != null) {
                WeightedEdge edge = getEdge(bus, tail, head);
                if (edge != null) {
                    ((Route) edge.getWeight()).setDistance(newDistance);
                } else
                    throw new InvalidParameterException("no route from tail to head with that bus: tail:" + tail + " head:" + head + " bus: " + bus);
            } else throw new InvalidParameterException("head:" + head);
        } else throw new InvalidParameterException("tail:" + tail);


    }

    /**
     *Change the route bus for another that exists
     *
     * @param tail the BusStop's name that represents the departure stop of the route
     * @param head the BusStop's name that represents the arrival stop of the route
     * @param oldBusName the name of the bus it's going to be change
     * @param newBusName the new bus name
     */
    public void changeRouteBus(String tail, String head, String oldBusName, String newBusName){
        WeightedEdge edge = getEdge(oldBusName, tail, head);
        if(edge == null) throw new InvalidParameterException("oldBusName: " + oldBusName);
        Route route = (Route) edge.getWeight();
        Bus bus = getBus(newBusName);
        if(bus == null) throw new InvalidParameterException("newBusName: " + newBusName);
        //if aux == null means that there is not another route with the same bus between these vertex
        WeightedEdge aux = getEdge(newBusName, tail, head);
        if(aux == null && canInsertRoute(getBusStopIndex(tail), getBusStopIndex(head), bus))
            route.setBus(bus);
    }

    /**
     * Get the WeightedEdge instance that goes from tail to head, using the given bus
     *
     * @param busName the name of the bus that represents the route
     * @param tail    a bus stop name
     * @return The WeightedEdge that connects tail and head through the bus, null if it doesn't exist
     */
    //TODO getVertex can return null
    public WeightedEdge getEdge(String busName, String tail, String head) {
        Vertex t = getVertex(tail);
        Vertex h = getVertex(head);
        WeightedEdge found = null;
        WeightedEdge current = null;
        Route currentRoute = null;
        Bus b = getBus(busName);
        for (Edge e : t.getEdgeList()) {
            current = (WeightedEdge) e;
            currentRoute = (Route) current.getWeight();
            if (e.getVertex().equals(h)) {
                if (b == null) {
                    if (currentRoute.getBus() == null) {
                        found = current;
                    }
                } else if (currentRoute.getBus() != null) {
                    if (currentRoute.getBus().getName().equalsIgnoreCase(b.getName()))
                        found = current;
                }
            }
        }
        return found;
    }

    /**
     * Get all the edges that directly connect v1 with v2
     *
     * @param tail tail vertex
     * @param head head vertex
     * @return the list of edges that connect both vertices
     */
    public LinkedList<Edge> getConnectingEdges(Vertex tail, Vertex head) {
        LinkedList<Edge> edges = new LinkedList<>();
        for (Edge e : tail.getEdgeList()) {
            if (e.getVertex().equals(head)) edges.add(e);
        }
        return edges;
    }

    public LinkedList<Edge> getConnectingEdges(String tail, String head) {
        Vertex headVertex = getVertex(head);
        Vertex tailVertex = getVertex(tail);
        LinkedList<Edge> edges = new LinkedList<>();
        for (Edge e : tailVertex.getEdgeList()) {
            if (e.getVertex().equals(headVertex)) edges.add(e);
        }
        return edges;
    }
    //*************************************************************************
    //------------------------------SHORTEST PATH------------------------------
    //*************************************************************************
    /**
     * Finds the shortest path between the given busStops
     *
     * @param start the BusStop's name of the starting point
     * @param goal  the BusStop's name of the goal point
     * @return a CompletePath instance with the path from start to goal
     */
    public CompletePath getPathBetween(String start, String goal) {
        Vertex tail = getVertex(start);
        Vertex head = getVertex(goal);
        if (dijsktraShortestPath == null || !((BusStop) dijsktraShortestPath.getStart().getInfo()).getName().equalsIgnoreCase(start)) {
            dijsktraShortestPath = new DijkstraShortestPath(tail);
        }
        CompletePath consult = dijsktraShortestPath.getShortestPathTo(head);
        consult.save();
        return consult;
    }

    public void restartDijkstra() {
        this.dijsktraShortestPath = null;
    }

    //Bus Filter
    public List<Bus> busFilter(String tail, String head){
        List<Bus> buses = new LinkedList<>();
        for(Bus b : busList){
            if(canInsertRoute(getBusStopIndex(tail), getBusStopIndex(head), b))
                buses.add(b);
        }
        return buses;
    }


}
