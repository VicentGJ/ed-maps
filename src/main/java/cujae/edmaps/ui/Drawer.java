package cujae.edmaps.ui;

import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.City;
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
    private HashMap<Vertex, ImageView> nodes;
    private ArrayList<Route> addedEdges;
    private HashMap<WeightedEdge, Segment> edges;
    private final Stage stage;


    public Drawer(Stage stage) {
        this.stage = stage;
    }

    public Group draw() {
        Group graph = new Group();
        nodes = new HashMap<>();
        addedEdges = new ArrayList<>();
        edges = new HashMap<>();
        LinkedList<ImageView> locations = addLocations();
        LinkedList<Text> labels = addLabelToLocations();
        LinkedList<Path> connections = addConnections();
        LinkedList<Text> weights = addWeightToEdges();
        Text cityName = addCityName();
        graph.getChildren().addAll(connections);
        graph.getChildren().addAll(weights);
        graph.getChildren().addAll(locations);
        graph.getChildren().addAll(labels);
        graph.getChildren().add(cityName);
        return graph;
    }

    private LinkedList<ImageView> addLocations() {
        LinkedList<ImageView> locations = new LinkedList<>();
        LinkedList<Vertex> vertices = City.getInstance().getRouteGraph().getVerticesList();
        final int NODES = vertices.size();
        final double RADIUS = stage.getHeight() / 2 - 70d;
        final double CENTER_X = stage.getWidth() / 2;
        final double CENTER_Y = stage.getHeight() / 2;
        Iterator<Vertex> iterator = vertices.descendingIterator();
        double i = 0;
        Vertex current = null;
        if (vertices.size() == 1) {
            ImageView location = new ImageView(new Image(ASSETS_LOCATION, 25d, 25d, false, true));
            location.setX(CENTER_X);
            location.setY(CENTER_Y - 30d);
            locations.add(location);
            nodes.put(vertices.get(0), location);
        } else while (iterator.hasNext()) {
            current = iterator.next();
            double angle = Math.toRadians((++i / NODES) * 360d);
            double centerX = (Math.cos(angle) * RADIUS) + CENTER_X;
            double centerY = (Math.sin(angle) * RADIUS) + CENTER_Y;
            ImageView location = new ImageView(new Image(ASSETS_LOCATION, 25d, 25d, false, true));
            location.setX(centerX);
            location.setY(centerY - 30d);
            locations.add(location);
            nodes.put(current, location);
        }
        return locations;
    }

    private LinkedList<Text> addLabelToLocations() {
        LinkedList<Text> labels = new LinkedList<>();
        for (Map.Entry<Vertex, ImageView> entry : nodes.entrySet()) {
            double posX = entry.getValue().getX();
            double posY = entry.getValue().getY() - 5d;
            String stopName = ((BusStop) entry.getKey().getInfo()).getName();
            Text text = new Text(posX, posY, stopName);
            text.setFont(Font.font("Ubuntu", FontWeight.BOLD, 14));
            labels.add(text);
        }
        return labels;
    }

    private LinkedList<Path> addConnections() {
        Vertex vertex = null;
        ImageView iView = null;
        LinkedList<Path> paths = new LinkedList<>();
        ArrayList<Edge> added = new ArrayList<>();
        for (Map.Entry<Vertex, ImageView> entry : nodes.entrySet()) {
            Path path = new Path();
            vertex = entry.getKey();
            iView = entry.getValue();
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
                ImageView ivHead = nodes.get(headVertex);
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
        if (weight.getBus() != null) busName = weight.getBus().getName();
        Text wText = new Text(middle.getX() - 20d, middle.getY(), busName + " [" + weight.getDistance() + "]");
        wText.setFont(Font.font("Ubuntu", FontWeight.BOLD, 13d));
        return wText;
    }

    private Text addCityName() {
        Text city = new Text(50, 50, City.getInstance().getName());
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

    public record Segment(Point2D tail, Point2D head) {
    }
}
