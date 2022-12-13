package cujae.edmaps.ui;

import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.City;
import cujae.edmaps.core.Route;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public class Drawer {
    private final double NODE_RADIUS = 10d;
    private HashMap<Vertex, Circle> nodes;
    private Group root;
    private ArrayList<Route> addedEdges;
    private HashMap<WeightedEdge, Segment> edges;
    private Stage stage;

    public Drawer(Stage stage) {
        root = new Group();
        nodes = new HashMap<>();
        addedEdges = new ArrayList<>();
        edges = new HashMap<>();
        this.stage = stage;
    }

    public Group draw() {
        LinkedList<Circle> circles = addNodes();
        LinkedList<Text> labels = addLabelToNodes();
        LinkedList<Path> connections = addConnections();
        LinkedList<Text> weights = addWeightToEdges();
        root.getChildren().addAll(connections);
        root.getChildren().addAll(weights);
        root.getChildren().addAll(circles);
        root.getChildren().addAll(labels);
        return this.root;
    }

    private LinkedList<Circle> addNodes() {
        LinkedList<Circle> circles = new LinkedList<>();
        Text city = new Text(50, 50, City.getInstance().getName());
        city.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        root.getChildren().add(city);
        LinkedList<Vertex> vertices = City.getInstance().getRouteGraph().getVerticesList();
        final int NODES = vertices.size();
        final double RADIUS = stage.getHeight() / 2 - 50;
        final double CENTER_X = stage.getWidth() / 2;
        final double CENTER_Y = stage.getHeight() / 2 - 20f;
        Iterator<Vertex> iterator = vertices.iterator();
        int i = 0;
        Vertex current = null;
        while (iterator.hasNext()) {
            current = iterator.next();
            double angle = Math.toRadians(((double) i++ / NODES) * 360d);
            double centerX = (Math.cos(angle) * RADIUS) + CENTER_X;
            double centerY = (Math.sin(angle) * RADIUS) + CENTER_Y;
            Circle c = new Circle(centerX, centerY, NODE_RADIUS);
            circles.add(c);
            nodes.put(current, c);
        }
        return circles;
    }

    private LinkedList<Text> addLabelToNodes() {
        LinkedList<Text> labels = new LinkedList<>();
        for (Map.Entry<Vertex, Circle> entry : nodes.entrySet()) {
            double posX = entry.getValue().getCenterX();
            double posY = entry.getValue().getCenterY() - NODE_RADIUS - 10;
            String stopName = ((BusStop) entry.getKey().getInfo()).getName();
            Text text = new Text(posX, posY, stopName);
            labels.add(text);
        }
        return labels;
    }

    private LinkedList<Path> addConnections() {
        Vertex v = null;
        Circle c = null;
        LinkedList<Path> paths = new LinkedList<>();
        ArrayList<Edge> added = new ArrayList<>();
        for (Map.Entry<Vertex, Circle> entry : nodes.entrySet()) {
            Path path = new Path();
            v = entry.getKey();
            c = entry.getValue();
            Point2D tail = new Point2D(c.getCenterX(), c.getCenterY());
            MoveTo mt = new MoveTo(tail.getX(), tail.getY());
            path.getElements().add(mt);
            LinkedList<Edge> edges = v.getEdgeList();
            Point2D head = null;
            WeightedEdge wEdge = null;
            for (Edge e : edges) {
                wEdge = (WeightedEdge) e;
                if (addedEdges.contains((Route) wEdge.getWeight())) continue;
                Vertex headVertex = e.getVertex();
                Circle circleHead = getNodeOfVertex(headVertex);
                head = new Point2D(circleHead.getCenterX(), circleHead.getCenterY());
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
            Text weight = addWeightToEdge(entry.getKey(), entry.getValue().getTail(), entry.getValue().getHead());
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
        return new Text(middle.getX() - 20d, middle.getY(), busName + " [" + weight.getDistance() + "]");
    }

    private Circle getNodeOfVertex(Vertex v) {
        Circle result = null;
        for (Map.Entry<Vertex, Circle> entry : nodes.entrySet()) {
            if (((BusStop) entry.getKey().getInfo()).getName().equalsIgnoreCase(((BusStop) v.getInfo()).getName()))
                result = entry.getValue();
        }
        return result;
    }

    private boolean textOverlaps(LinkedList<Text> texts, Text text) {
        for (Text node : texts) {
            if (node.getX() == text.getX() && node.getY() == text.getY()) {
                return true;
            }
        }
        return false;
    }

    private class Segment {
        private Point2D head;
        private Point2D tail;

        public Segment(Point2D tail, Point2D head) {
            this.tail = tail;
            this.head = head;
        }

        public Point2D getTail() {
            return tail;
        }

        public Point2D getHead() {
            return head;
        }
    }
}
