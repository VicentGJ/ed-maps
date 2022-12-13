package cujae.edmaps.ui;

import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.BusStop;
import cujae.edmaps.core.City;
import cujae.edmaps.core.Route;
import cujae.edmaps.utils.ViewLoader;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.*;

public class Drawer {
    private final double NODE_RADIUS = 10d;
    private HashMap<Vertex, Circle> nodes;
    private Group root;
    private ArrayList<Route> addedEdges;

    public Drawer() {
        root = new Group();
        nodes = new HashMap<>();
        addedEdges = new ArrayList<>();
    }

    public Group draw(Stage stage) {
        addNodes(stage);
        addLabelToNodes();
        addConnections();
        addConnectionWeights();
        return this.root;
    }

    private void addNodes(Stage stage) {
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
            nodes.put(current, c);
            root.getChildren().add(c);
        }
    }

    private void addLabelToNodes() {
        for (Map.Entry<Vertex, Circle> entry : nodes.entrySet()) {
            double posX = entry.getValue().getCenterX();
            double posY = entry.getValue().getCenterY() - NODE_RADIUS - 10;
            String stopName = ((BusStop) entry.getKey().getInfo()).getName();
            Text text = new Text(posX, posY, stopName);
            root.getChildren().add(text);
        }
    }

    private void addConnections() {
        Vertex v = null;
        Circle c = null;
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
                addWeightToEdge((WeightedEdge) e, tail, head);
            }
            root.getChildren().add(path);
            path.setStroke(Paint.valueOf("GREY"));
        }
    }

    private void addConnectionWeights() {
    }

    private Circle getNodeOfVertex(Vertex v) {
        Circle result = null;
        for (Map.Entry<Vertex, Circle> entry : nodes.entrySet()) {
            if (((BusStop) entry.getKey().getInfo()).getName().equalsIgnoreCase(((BusStop) v.getInfo()).getName()))
                result = entry.getValue();
        }
        return result;
    }

    private void addWeightToEdge(WeightedEdge edge, Point2D tail, Point2D head) {
        Point2D middle = new Point2D((tail.getX() + head.getX()) / 2, (tail.getY() + head.getY()) / 2);
        Route weight = (Route) edge.getWeight();
        String busName = "Walking";
        if (weight.getBus() != null) busName = weight.getBus().getName();
        Text text = new Text(middle.getX()-20d, middle.getY(), busName + " [" + weight.getDistance() + "]");
        while (textOverlaps(text)) {
            text.setY(text.getY() - 15d);
        }
        addedEdges.add(weight);
        root.getChildren().add(text);
    }

    private boolean textOverlaps(Text text) {
        ObservableList<Node> nodes = root.getChildren();
        for (Node node : nodes) {
            if (node instanceof Text) {
                if (((Text) node).getX() == text.getX() && ((Text) node).getY() == text.getY()) {
                    return true;
                }
            }
        }
        return false;
    }
}
