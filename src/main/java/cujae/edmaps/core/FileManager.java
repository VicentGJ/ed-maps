package cujae.edmaps.core;

import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.dijkstra.Path;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

/**
 * FileManager class is a singleton class for this project files management.
 * it uses the project's relative path for file saving and loading.
 * <p>
 * When the first instance is created it automatically creates the files/ directory and inside a cities/ and consults/ directories with their corresponding .gitignore to avoid any conflicts.
 * All these directories and files are only created if they don't already exist
 */
public class FileManager {
    private final String FILES_DIRECTORY = "./src/main/java/cujae/edmaps/files/";
    private final String CITIES_DIRECTORY = FILES_DIRECTORY + "cities/";
    private final String CONSULTS_DIRECTORY = FILES_DIRECTORY + "consults/";
    private static FileManager instance;

    public FileManager() {
        File filesDirectory = new File(FILES_DIRECTORY);
        filesDirectory.mkdir();
        File cities = new File(CITIES_DIRECTORY);
        cities.mkdir();
        File consults = new File(CONSULTS_DIRECTORY);
        consults.mkdir();
        File readme = new File(consults, "README.txt");
        try {
            readme.createNewFile();
            FileWriter fw = new FileWriter(readme);
            fw.write("DO NOT DELETE ANY FILES FROM HERE MANUALLY!\nThank you.");
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
                if (f.getName().equalsIgnoreCase(cityName + ".csv")) {
                    city = f;
                    break;
                }
            }
        }
        return city;
    }

    /**
     * Get the current loaded city's file
     *
     * @return the File of the current city or null if the file is not found
     */
    public File loadCityFile() {
        String cityName = City.getInstance().getName();
        File[] cities = new File(CITIES_DIRECTORY).listFiles();
        File city = null;
        if (cities != null) {
            for (File f : cities) {
                if (f.getName().equalsIgnoreCase(cityName + ".csv")) {
                    city = f;
                    break;
                }
            }
        }
        return city;
    }


    public File saveCity() {
        City city = City.getInstance();
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
                    if (vertex.isAdjacent(vertex2)) {
                        fileWriter.write("1");
                    } else fileWriter.write("0");
                    fileWriter.write(",");
                }
                fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getCityConsultsDirectory();
        return file;
    }

    /**
     * If the current city's consults directory doesn't exist, create it and return the File instance, otherwise just return the File instance
     *
     * @return the File instance of the current city's consults directory
     */
    private File getCityConsultsDirectory() {
        File cityConsultsDirectory = new File(CONSULTS_DIRECTORY + "/" + City.getInstance().getName() + "/");
        cityConsultsDirectory.mkdir();
        return cityConsultsDirectory;
    }


    /**
     * Deletes the city file corresponding to the given city, and it's corresponding consults directory
     *
     * @param cityName          the City's name which file is going to be deleted
     * @param deleteConsultsToo true to also delete the city's consults directory, false otherwise
     */
    public void deleteCity(String cityName, boolean deleteConsultsToo) {
        loadCityFile(cityName).delete();
        if (deleteConsultsToo) getCityConsultsDirectory().delete();
    }

    /**
     * Deletes the city file corresponding to the current city, and it's corresponding consults directory
     *
     * @param deleteConsultsToo true to also delete the city's consults directory, false otherwise
     */
    public void deleteCity(boolean deleteConsultsToo) {
        loadCityFile(City.getInstance().getName()).delete();
        if (deleteConsultsToo) {
            File consultsDirectory = getCityConsultsDirectory();
            File[] consults = consultsDirectory.listFiles();
            if (consults != null)
                for (File consult : consults)
                    consult.delete();
            consultsDirectory.delete();
        }
    }


//CONSULTS

    /**
     * @return all the Directories in the consults/ directory
     */
    public File[] getAllConsultDirectories() {
        return new File(CONSULTS_DIRECTORY).listFiles();
    }

    /**
     * Get the consult files of the current city
     *
     * @return an array of File instances of the consult files corresponding to the current city
     */
    public File[] getAllCityConsultFiles() {
        return new File(getCityConsultsDirectory().getPath()).listFiles();
    }

    /**
     * Get the consult files of the given city
     *
     * @return an array of File instances of the consult files corresponding to the current city
     */
    public File[] getAllCityConsultFiles(String cityName) {
        File[] consultsDirectory = getAllConsultDirectories();
        File[] result = null;
        for (File file : consultsDirectory) {
            if (file.getName().equalsIgnoreCase(cityName)) {
                result = file.listFiles();
                break;
            }
        }
        return result;
    }

    /**
     * Get a single consult file
     *
     * @return the file of the city's consults or null if file is not found
     */
    public File loadConsultFile(int id) {
        File[] cities = new File(getCityConsultsDirectory().getPath()).listFiles();
        if (id > cities.length - 1) throw new InvalidParameterException("id not found: " + id);
        File consult = null;
        for (File f : cities) {
            if (f.getName().endsWith("-" + id + ".txt")) {
                consult = f;
                break;
            }
        }
        return consult;
    }

    public File saveConsult(LinkedList<Path> paths) {
        File ccd = getCityConsultsDirectory();
        File consult = null;
        boolean consultFileExists;
        int id = ccd.listFiles().length;
        try {
            do {
                String newConsultName = "consult-" + id++ + ".txt";
                consult = new File(ccd.getPath(), newConsultName);
                consultFileExists = !consult.createNewFile();
            } while (consultFileExists);
            RandomAccessFile raf = new RandomAccessFile(consult, "rw");
            int counter = 0;
            for (Path path : paths) {
                String busName = "Walking";
                if (counter++ == 0) busName = "Start";
                else if (path.getBus() != null) busName = path.getBus().getName();
                Float distance = path.getDistance();
                String stopName = ((BusStop) path.getStop().getInfo()).getName();
                byte[] busNameBytes = Convert.toBytes(busName);
                byte[] stopNameBytes = Convert.toBytes(stopName);
                raf.writeInt(busNameBytes.length);
                raf.write(busNameBytes);
                raf.writeInt(stopNameBytes.length);
                raf.write(stopNameBytes);
                raf.writeFloat(distance);
            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return consult;
    }
}
