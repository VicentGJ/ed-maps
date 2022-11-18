package cujae.edmaps.core;

import cu.edu.cujae.ceis.graph.*;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.vertex.Vertex;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class City {
    private String name;
    private LinkedGraph graph;
    private List<Bus> buses;

    public void addBusStop(String name){
        if(!existName(name)){
            graph.insertVertex(new BusStop(name));
        }
    }
    public void addRoute(BusStop stop1, BusStop stop2, Bus bus, Double distance){
        Vertex v1 = getVertex(stop1);
        if(v1 != null){
            Vertex v2 = getVertex(stop2);
            if(v2 != null){
                Route route = new Route(bus, distance);
                graph.insertWEdgeDG(graph.getVertexIndex(v1), graph.getVertexIndex(v2), route);
            }
        }
    }
    public void addNearbyStop(BusStop stop1, BusStop stop2, Double distance){
        Vertex v1 = getVertex(stop1);
        if(v1 != null){
            Vertex v2 = getVertex(stop2);
            if(v2 != null){
                Route route = new Route(null, distance);
                graph.insertWEdgeDG(graph.getVertexIndex(v1), graph.getVertexIndex(v2), route);
            }
        }
    }
    public boolean existName(String name){
        return false;
    }
    public Vertex getVertex(BusStop busStop){
        Vertex result = null;
        Iterator<Vertex> it = graph.getVerticesList().iterator();
        boolean found = false;
        while(it.hasNext() && !found){
            Vertex aux = it.next();
            if(aux.getInfo().equals(busStop)){
                found = true;
                result = aux;
            }
        }
        return result;
    }

}
