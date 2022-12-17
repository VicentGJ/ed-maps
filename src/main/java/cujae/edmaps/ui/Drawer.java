package cujae.edmaps.ui;

import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.MapsManager;
import cujae.edmaps.core.Route;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public class Drawer {
    private final String ASSETS_LOCATION = "file:./src/main/resources/cujae/edmaps/ui/assets/location.png";
    private HashMap<Integer, Location> nodes;
    private ArrayList<Route> addedEdges;
    private HashMap<WeightedEdge, Segment> edges;
    private final Stage stage;
    private Float totalDistance;


    public Drawer(Stage stage) {
        this.stage = stage;
    }

    public Group draw(LinkedList<Vertex> subgraph, String name) {
        Group graph = new Group();
        totalDistance = 0f;
        nodes = new HashMap<>();
        addedEdges = new ArrayList<>();
        edges = new HashMap<>();
        LinkedList<ImageView> locations = addLocations(subgraph);
        LinkedList<Text> labels = addLabelToLocations(subgraph);
        LinkedList<Path> connections = addConnections();
        LinkedList<Text> weights = addWeightToEdges();
        Text cityName = addCityName(name);
        graph.getChildren().addAll(connections);
        graph.getChildren().addAll(weights);
        graph.getChildren().addAll(locations);
        graph.getChildren().addAll(labels);
        graph.getChildren().add(cityName);
        if (subgraph != null) {
            Text distance = addTotalDistance();
            Text fromTo = addFromTo(subgraph);
            graph.getChildren().add(distance);
            graph.getChildren().add(fromTo);
        }
        return graph;
    }

    private LinkedList<ImageView> addLocations(LinkedList<Vertex> vertexList) {
        LinkedList<ImageView> locations = new LinkedList<>();
        LinkedList<Vertex> vertices = vertexList == null ? MapsManager.getInstance().getActualCity().getRouteGraph().getVerticesList() : vertexList;
        final int NODES = vertices.size();
        final double RADIUS = stage.getHeight() / 2 - 70d;
        final double CENTER_X = stage.getWidth() / 2;
        final double CENTER_Y = stage.getHeight() / 2 - 50d;
        Iterator<Vertex> iterator= vertices.descendingIterator();
        double i = 0;
        Vertex current = null;
        int pos = NODES;
        if (vertices.size() == 1) {
            ImageView location = new ImageView(new Image(ASSETS_LOCATION, 25d, 25d, false, true));
            location.setX(CENTER_X);
            location.setY(CENTER_Y);
            locations.add(location);
            nodes.put(1, new Location(vertices.getFirst(), location));
        } else while (iterator.hasNext()) {
            current = iterator.next();
            System.out.println(((BusStop)current.getInfo()).getName());
            double angle = Math.toRadians((++i / NODES) * 360d);
            double centerX = (Math.cos(angle) * RADIUS) + CENTER_X;
            double centerY = (Math.sin(angle) * RADIUS) + CENTER_Y;
            ImageView location = new ImageView(new Image(ASSETS_LOCATION, 25d, 25d, false, true));
            location.setX(centerX);
            location.setY(centerY);
            locations.add(location);
            nodes.put(pos--, new Location(current, location));
        }
        return locations;
    }

    private LinkedList<Text> addLabelToLocations(LinkedList<Vertex> subgraph) {
        LinkedList<Text> labels = new LinkedList<>();
        for (Map.Entry<Integer, Location> entry : nodes.entrySet()) {
            double posX = entry.getValue().location.getX();
            double posY = entry.getValue().location.getY();
            String stopName = ((BusStop) entry.getValue().vertex.getInfo()).getName();
            Text text = new Text(posX, posY, stopName + (subgraph == null ? "" : " [" + entry.getKey() + "]"));
            text.setFont(Font.font("Ubuntu", FontWeight.BOLD, 14));
            labels.add(text);
        }
        return labels;
    }

    private LinkedList<Path> addConnections() {
        Vertex vertex = null;
        ImageView iView = null;
        LinkedList<Path> paths = new LinkedList<>();
        for (Map.Entry<Integer, Location> entry : nodes.entrySet()) {
            Path path = new Path();
            vertex = entry.getValue().vertex();
            iView = entry.getValue().location();
            Point2D tail = new Point2D(iView.getX() + iView.getFitWidth() + 13.5d, iView.getY() + iView.getFitHeight() + 24d);
            MoveTo mt = new MoveTo(tail.getX(), tail.getY());
            path.getElements().add(mt);
            LinkedList<Edge> edges = vertex.getEdgeList();
            Point2D head = null;
            WeightedEdge wEdge = null;
            for (Edge e : edges) {
                wEdge = (WeightedEdge) e;
                if (addedEdges.contains((Route) wEdge.getWeight())) continue;
                Vertex headVertex = e.getVertex();
                ImageView ivHead = getLocationOfVertex(headVertex);
                head = new Point2D(ivHead.getX() + ivHead.getFitWidth() + 13.5d, ivHead.getY() + ivHead.getFitHeight() + 24d);
                LineTo line = new LineTo(head.getX(), head.getY());
                path.getElements().add(line);
                path.getElements().add(mt);
                this.edges.put(wEdge, new Segment(tail, head));
                addedEdges.add((Route) wEdge.getWeight());
            }
            path.setStroke(Paint.valueOf("GREY"));
            paths.add(path);
        }
        return paths;
    }

    private ImageView getLocationOfVertex(Vertex v) {
        ImageView result = null;
        for (Map.Entry<Integer, Location> entry : nodes.entrySet()) {
            if (entry.getValue().vertex().equals(v)) {
                result = entry.getValue().location();
                break;
            }
        }
        return result;
    }

    private LinkedList<Text> addWeightToEdges() {
        LinkedList<Text> weights = new LinkedList<>();
        for (Map.Entry<WeightedEdge, Segment> entry : edges.entrySet()) {
            Text weight = addWeightToEdge(entry.getKey(), entry.getValue().tail(), entry.getValue().head());
            while (textOverlaps(weights, weight)) {
                weight.setY(weight.getY() - 15d);
            }
            weights.add(weight);
        }
        return weights;
    }

    private Text addWeightToEdge(WeightedEdge edge, Point2D tail, Point2D head) {
        Point2D middle = new Point2D((tail.getX() + head.getX()) / 2, (tail.getY() + head.getY()) / 2);
        Route weight = (Route) edge.getWeight();
        String busName = "Walking";
        this.totalDistance += weight.getDistance();
        if (weight.getBus() != null) busName = weight.getBus().getName();
        Text wText = new Text(middle.getX() - 30d, middle.getY() - 5d, busName + "[" + weight.getDistance() + "]");
        wText.setFont(Font.font("Ubuntu", FontWeight.BOLD, 13d));
        return wText;
    }

    private Text addCityName(String cityName) {
        String name = cityName == null ? MapsManager.getInstance().getActualCity().getName() : cityName;
        Text city = new Text(stage.getWidth() / 15, stage.getHeight() / 10, name);
        city.setFont(Font.font("Ubuntu", FontWeight.BOLD, 30d));
        return city;
    }

    private boolean textOverlaps(LinkedList<Text> texts, Text text) {
        for (Text node : texts) {
            if (node.getX() == text.getX() && node.getY() == text.getY()) {
                return true;
            }
        }
        return false;
    }

    private Text addTotalDistance() {
        Text distance = new Text(40d, stage.getHeight() - stage.getHeight() / 3, "Total distance: " + this.totalDistance + "m");
        distance.setFont(Font.font("Ubuntu", FontWeight.BOLD, 18));
        return distance;
    }

    private Text addFromTo(LinkedList<Vertex> vertices) {
        String text = "From: ";

        text += ((BusStop) vertices.getFirst().getInfo()).getName() + "\nTo: ";
        text += ((BusStop) vertices.getLast().getInfo()).getName();
        Text fromTo = new Text(40d, stage.getHeight() - stage.getHeight() / 4, text);
        fromTo.setFont(Font.font("Ubuntu", FontWeight.BOLD, 18));
        return fromTo;
    }

    private record Segment(Point2D tail, Point2D head) {
    }

    private record Location(Vertex vertex, ImageView location) {
    }
}
