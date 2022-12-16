package cujae.edmaps.core;

import cu.edu.cujae.ceis.graph.vertex.Vertex;
import cujae.edmaps.core.dijkstra.CompletePath;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * FileManager class is a singleton class for this project files management.
 * it uses the project's relative path for file saving and loading.
 * <p>
 * When the first instance is created it automatically creates the files/ directory and inside a cities/ and consults/ directories with their corresponding .gitignore to avoid any conflicts.
 * All these directories and files are only created if they don't already exist
 */
public class FileManager {
    private static final String FILES_DIRECTORY = "./src/main/java/cujae/edmaps/files/";
    private static final String CITIES_DIRECTORY = FILES_DIRECTORY + "cities/";
    private static final String CONSULTS_DIRECTORY = FILES_DIRECTORY + "consults/";

    public static void createFiles() {
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
        throw new InvalidParameterException("cityName: " + cityName);
    }
    public static File saveCity(City city) {
        File file = loadCityFile(city.getName());
        try {
            file.delete();
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
        getCityConsultsDirectory(city.getName());
        return file;
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
    public void deleteCity(String cityName, boolean deleteConsultsToo) {
        loadCityFile(cityName).delete();
        if (deleteConsultsToo) {
            File consultsDirectory = getCityConsultsDirectory(cityName);
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
     * @param cityName the City's name of the consult you want to get
     * @param consultName the name of the specific consult you want to get
     * @return the file of the city's consults or null if file is not found
     */
    public static File loadConsultFile(String cityName, String consultName) {
        File cityFolder = new File(CONSULTS_DIRECTORY + cityName);
        if (!cityFolder.exists()) throw new InvalidParameterException("cityName: " +cityName);
        File consult = new File(cityFolder, consultName);
        if(!consult.exists()) throw new InvalidParameterException("consultName: " + consultName);
        return consult;
    }
    /**
     *
     * @param cityName the City's name of the consult you want to get
     * @param consultName the name of the specific consult you want to get
     * @return the path save on consult file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static CompletePath loadConsult(String cityName, String consultName) throws IOException, ClassNotFoundException {
        File consult = loadConsultFile(cityName, consultName);
        RandomAccessFile raf = new RandomAccessFile(consult, "r");
        int length = raf.readInt();
        byte[] pathByte = new byte[length];
        raf.read(pathByte);
        return (CompletePath) Convert.toObject(pathByte);
    }

    /**
     *
     * @param cityName the City's name of the consult you want to get
     * @param path the path of the consult you want to save
     * @return the file of the city consult
     */
    public static File saveConsult(String cityName, CompletePath path) {
        File ccd = getCityConsultsDirectory(cityName);
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
            byte[] pathByte = Convert.toBytes(path);
            raf.write(pathByte.length);
            raf.write(pathByte);
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return consult;
    }
}
