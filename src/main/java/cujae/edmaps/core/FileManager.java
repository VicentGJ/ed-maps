package cujae.edmaps.core;

import cu.edu.cujae.ceis.graph.vertex.Vertex;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * FileManager class is a singleton class for this project files management.
 * it uses the project's relative path for file saving and loading.
 * <p>
 * When the first instance is created it automatically creates the files/ directory and inside a cities/ and consults/ directories with their corresponding .gitignore to avoid any conflicts.
 * All this directories and files are only created if they don't already exist
 */
public class FileManager {
    private final String FILES_DIRECTORY = "./src/main/java/cujae/edmaps/files/";
    private final String CITIES_DIRECTORY = FILES_DIRECTORY + "cities/";
    private final String CONSULTS_DIRECTORY = FILES_DIRECTORY + "consults/";

    private static FileManager instance;

    public FileManager() {
        new File(FILES_DIRECTORY).mkdir();
        File cities = new File(CITIES_DIRECTORY);
        File consults = new File(CONSULTS_DIRECTORY);
        consults.mkdir();
        cities.mkdir();
        File citiesIgnore = new File(cities, ".gitignore");
        File consultsIgnore = new File(consults, ".gitignore");
        try {
            FileWriter fw = new FileWriter(citiesIgnore);
            fw.write("*\n!.gitignore");
            fw.close();
            fw = new FileWriter(consultsIgnore);
            fw.write("*\n!.gitignore");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileManager getInstance() {
        if (FileManager.instance == null)
            FileManager.instance = new FileManager();
        return instance;
    }

//CITIES

    /**
     * @return all the files in the cities/ directory
     */
    public File[] getAllCityFiles() {
        return new File(CITIES_DIRECTORY).listFiles();
    }

    /**
     * Get a single city file
     *
     * @param cityName the name of the city to get
     * @return the File of the wanted city or null if the file is not found
     */
    public File loadCityFile(String cityName) {
        File[] cities = new File(CITIES_DIRECTORY).listFiles();
        File city = null;
        if (cities != null) {
            for (File f : cities) {
                if (f.getName().equalsIgnoreCase(cityName)) {
                    city = f;
                    break;
                }
            }
        }
        return city;
    }

    //TODO
    public File saveCity(City city) throws Exception {
        File file = loadCityFile(city.getName());
        if(file != null){
            if (file.exists())
                file.delete();
        }
        else
            file = new File(CITIES_DIRECTORY + city.getName() + ".csv");
        if (!file.createNewFile())
            throw new Exception();
        FileWriter fileWriter = new FileWriter(file);
        List<Vertex> verticesList = city.getRouteGraph().getVerticesList();
        for (Vertex vertex : verticesList) {
            BusStop stop = (BusStop) vertex.getInfo();
            fileWriter.write(stop.getName() + ",");
        }
        fileWriter.write("\n");
        for (Vertex vertex : verticesList) {
            for (Vertex vertex2 : verticesList) {
                if (vertex.isAdjacent(vertex2)) {
                    fileWriter.write(1);
                } else fileWriter.write(0);
                fileWriter.write(",");
            }
            fileWriter.write("\n");
        }
        return file;
    }

    /**
     * Deletes the city file corresponding to the given city
     *
     * @param cityName the City's name which file is going to be deleted
     * @return true if and only if the file or directory is successfully deleted; false otherwise
     */
    public boolean deleteCityFile(String cityName) {
        return loadCityFile(cityName).delete();
    }
//CONSULTS

    /**
     * @return all the Files in the consults/ directory
     */
    public File[] getAllConsultFiles() {
        return new File(CONSULTS_DIRECTORY).listFiles();
    }

    /**
     * Get a single consult file
     *
     * @param cityName the name of the city which consults are needed
     * @return the file of the city's consults or null if file is not found
     */
    public File loadConsultFile(String cityName) {
        File[] cities = new File(CONSULTS_DIRECTORY).listFiles();
        File consults = null;
        if (cities != null) {
            for (File f : cities) {
                if (f.getName().equalsIgnoreCase(cityName)) {
                    consults = f;
                    break;
                }
            }
        }
        return consults;
    }

    //TODO
    public File saveConsult() {
        throw new RuntimeException();
    }

    /**
     * Deletes the consult file corresponding to the given city
     *
     * @param cityName the City's name with consults file is going to be deleted
     * @return true if and only if the file is successfully deleted; false otherwise
     */
    public boolean deleteConsultFile(String cityName) {
        return loadConsultFile(cityName).delete();
    }
}
