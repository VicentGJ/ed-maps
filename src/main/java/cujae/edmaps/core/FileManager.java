package cujae.edmaps.core;

import cu.edu.cujae.ceis.graph.LinkedGraph;
import cu.edu.cujae.ceis.graph.edge.Edge;
import cu.edu.cujae.ceis.graph.edge.WeightedEdge;
import cu.edu.cujae.ceis.graph.interfaces.ILinkedWeightedEdgeNotDirectedGraph;
import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.dijkstra.Path;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * FileManager class is a singleton class for this project files management.
 * it uses the project's relative path for file saving and loading.
 * <p>
 * When the first instance is created it automatically creates the files/ directory and inside a cities/ and consults/ directories with their corresponding .gitignore to avoid any conflicts.
 * All these directories and files are only created if they don't already exist
 */
public class FileManager {
    private static final String FILES_DIRECTORY = "./src/main/resources/files/";
    private static final String CITIES_DIRECTORY = FILES_DIRECTORY + "cities/";
    private static final String CONSULTS_DIRECTORY = FILES_DIRECTORY + "consults/";

    public static void createFiles() {
        File filesDirectory = new File(FILES_DIRECTORY);
        filesDirectory.mkdir();
        File cities = new File(CITIES_DIRECTORY);
        cities.mkdir();
        File consults = new File(CONSULTS_DIRECTORY);
        consults.mkdir();
    }

//CITIES

    /**
     * @return all the files in the cities/ directory
     */
    public static File[] getAllCityFiles() {
        return new File(CITIES_DIRECTORY).listFiles();
    }

    /**
     * Get a single city file
     *
     * @param cityName the name of the city to get
     * @return the File of the wanted city
     * @throws InvalidParameterException if it can not find the file
     */
    public static File loadCityFile(String cityName) {
        File city = new File(CITIES_DIRECTORY + cityName + ".csv");
        if (city.exists()) return city;
        return null;
    }

    public static File saveCity(City city) {
        File file = loadCityFile(city.getName());
        try {
            if (file != null) file.delete();
            file = new File(CITIES_DIRECTORY + city.getName() + ".csv");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            List<Vertex> verticesList = city.getRouteGraph().getVerticesList();
            for (Vertex vertex : verticesList) {
                BusStop stop = (BusStop) vertex.getInfo();
                fileWriter.write(stop.getName() + ",");
            }
            fileWriter.write("\n");
            for (Vertex vertex : verticesList) {
                for (Vertex vertex2 : verticesList) {
                    boolean isAdjacent = false;
                    LinkedList<Edge> edgesOfAdjacent = city.getConnectingEdges(vertex, vertex2);
                    int counter = edgesOfAdjacent.size();
                    for (Edge e : edgesOfAdjacent) {
                        WeightedEdge wE = (WeightedEdge) e;
                        Route route = (Route) wE.getWeight();
                        if (route.getBus() != null)
                            fileWriter.write(route.getBus().getName());
                        else
                            fileWriter.write("null");
                        fileWriter.write(";");
                        fileWriter.write(route.getDistance().toString());
                        isAdjacent = true;
                        if (counter-- > 1)
                            fileWriter.write("|");
                    }
                    if (!isAdjacent) fileWriter.write("0");
                    fileWriter.write(",");
                }
                fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getCityConsultsDirectory(city.getName());
        return file;
    }

    /**
     * @param cityName the name of the city to get
     * @return the city
     * @throws InvalidParameterException if it can not find the city file
     */
    public static City getCity(String cityName) throws InvalidParameterException {
        File cityFile = loadCityFile(cityName);
        Scanner sc = null;
        try {
            sc = new Scanner(cityFile);
        } catch (FileNotFoundException e) {
            throw new InvalidParameterException("cityName: " + cityName);
        }
        City city = new City(cityName);
        if (sc.hasNextLine()) {
            String[] vertices = sc.nextLine().split(",");
            if (!vertices[0].isBlank()) {
                for (String s : vertices) {
                    city.addBusStop(s);
                }
                int i = 0;
                while (sc.hasNextLine()) {
                    String[] connections = sc.nextLine().split(",");
                    for (int j = i + 1; j < connections.length; j++) {
                        if (!connections[j].equals("0")) {
                            String[] routes = connections[j].split("\\|");
                            for (String route : routes) {
                                String[] r = route.split(";");
                                String busName = r[0];
                                Float distance = Float.parseFloat(r[1]);
                                if (!busName.equals("null"))
                                    city.addBus(busName);
                                city.getRouteGraph().insertWEdgeNDG(i, j, new Route(city.getBus(busName), distance));
                            }
                        }
                    }
                    i++;
                }
            }
        }
        sc.close();
        return city;
    }

    /**
     * If the current city's consults directory doesn't exist, create it and return the File instance, otherwise just return the File instance
     *
     * @param cityName the name of the consult city
     * @return the File instance of the current city's consults directory
     */
    private static File getCityConsultsDirectory(String cityName) {
        File cityConsultsDirectory = new File(CONSULTS_DIRECTORY + "/" + cityName + "/");
        cityConsultsDirectory.mkdir();
        return cityConsultsDirectory;
    }


    /**
     * Deletes the city file corresponding to the given city, and it's corresponding consults directory
     *
     * @param cityName          the City's name which file is going to be deleted
     * @param deleteConsultsToo true to also delete the city's consults directory, false otherwise
     */
    public static void deleteCity(String cityName, boolean deleteConsultsToo) {
        File cityfile = loadCityFile(cityName);
        if (cityfile != null) {
            cityfile.delete();
            File consultsDirectory = getCityConsultsDirectory(cityName);
            if (deleteConsultsToo) {
                File[] consults = consultsDirectory.listFiles();
                if (consults != null)
                    for (File consult : consults)
                        consult.delete();
                consultsDirectory.delete();
            } else {
                int newID = 1;
                File[] cities = FileManager.getAllConsultDirectories();
                for (File f : cities) {
                    if (f.getName().startsWith("[deleted]-") && f.getName().endsWith(cityName)) newID++;
                }
                consultsDirectory.renameTo(new File(CONSULTS_DIRECTORY + "[deleted]-" + newID + "-" + cityName));
            }
        } else throw new InvalidParameterException("city name: " + cityName);
    }
//CONSULTS

    /**
     * @return all the Directories in the consults/ directory
     */
    public static File[] getAllConsultDirectories() {
        return new File(CONSULTS_DIRECTORY).listFiles();
    }

    /**
     * Get the consult files of the current city
     *
     * @param cityName the City's name of the consults you want to get
     * @return an array of File instances of the consult files corresponding to the current city
     */
    public static File[] getAllCityConsultFiles(String cityName) {
        return getCityConsultsDirectory(cityName).listFiles();
    }


    /**
     * Get a single consult file
     *
     * @param cityName    the City's name of the consult you want to get
     * @param consultName the name of the specific consult you want to get
     * @return the file of the city's consults or null if file is not found
     */
    public static File loadConsultFile(String cityName, String consultName) {
        File cityFolder = new File(CONSULTS_DIRECTORY + cityName);
        if (!cityFolder.exists()) throw new InvalidParameterException("cityName: " + cityName);
        File consult = new File(cityFolder, consultName);
        if (!consult.exists()) throw new InvalidParameterException("consultName: " + consultName);
        return consult;
    }

    /**
     * @param cityName    the City's name of the consult you want to get
     * @param consultName the name of the specific consult you want to get
     * @return the path save on consult file
     */
    public static LinkedList<Vertex> loadConsult(String cityName, String consultName) {
        try {
            File consult = loadConsultFile(cityName, consultName);
            Scanner sc = new Scanner(consult);
            ILinkedWeightedEdgeNotDirectedGraph subgraph = new LinkedGraph();
            Vertex previous = null;
            Vertex current = null;
            int counter = 0;
            sc.nextLine();//skip headers
            do {
                String[] pathString = sc.nextLine().split(",");
                String busStopString = pathString[0];
                String busString = pathString[1];
                String distanceString = pathString[2];
                subgraph.insertVertex(new BusStop(busStopString));
                current = subgraph.getVerticesList().get(counter);
                if (previous != null) {
                    Route weight = new Route(new Bus(busString), Float.parseFloat(distanceString));
                    subgraph.insertWEdgeNDG(counter - 1, counter, weight);
                }
                previous = current;
                counter++;
            } while (sc.hasNextLine());
            return subgraph.getVerticesList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LinkedList<Vertex> loadLastConsult(String cityName) {
        File[] consults = FileManager.getAllCityConsultFiles(cityName);
        int lastId = consults.length;
        String name = "";
        for (File f : consults) {
            if (f.getName().startsWith("[" + lastId + "]")) {
                name = f.getName();
                break;
            }
        }
        return loadConsult(cityName, name);
    }

    /**
     * @param cityName the City's name of the consult you want to get
     * @param paths    the path of the consult you want to save
     * @return the file of the city consult
     */
    public static File saveConsult(String cityName, LinkedList<Path> paths) {
        File ccd = getCityConsultsDirectory(cityName);
        File consult = null;
        boolean consultFileExists;
        int id = ccd.listFiles().length;
        String[] headers = {"Bus Stop", "Bus", "Distance"};
        try {
            do {
                String newConsultName = "[" + ++id + "] " + ((BusStop) paths.getFirst().getStop().getInfo()).getName() + "-" + ((BusStop) paths.getLast().getStop().getInfo()).getName() + ".csv";
                consult = new File(ccd.getPath(), newConsultName);
                consultFileExists = !consult.createNewFile();
            } while (consultFileExists);
            FileWriter fw = new FileWriter(consult);
            Path first = paths.getFirst();
            for (String header : headers) {
                fw.write(header);
                fw.write(",");
            }
            fw.write("\n");
            for (Path path : paths) {
                String busName = "Walking";
                if (path.equals(first)) busName = "Start";
                else if (path.getBus() != null) busName = path.getBus().getName();
                Float distance = path.getDistance();
                String stopName = ((BusStop) path.getStop().getInfo()).getName();
                fw.write(stopName);
                fw.write(",");
                fw.write(busName);
                fw.write(",");
                fw.write(String.valueOf(distance));
                fw.write("\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return consult;
    }

    public static void renameCityFile(String oldName, String newName) {
        File[] cities = getAllCityFiles();
        File city = null;
        for (File f : cities) {
            if (f.getName().split("\\.")[0].equalsIgnoreCase(oldName)) {
                city = f;
                break;
            }
        }
        File consultsDirectory = getCityConsultsDirectory(oldName);
        city.renameTo(new File(CITIES_DIRECTORY + newName + ".csv"));
        consultsDirectory.renameTo(new File(CONSULTS_DIRECTORY + newName));
    }
}
