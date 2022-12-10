package cujae.edmaps.core;

import java.io.*;

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

    /**
     * @return all the files in the cities/ directory
     */
    public File[] getAllCityFiles() {
        return new File(CITIES_DIRECTORY).listFiles();
    }

    /**
     * Get a single city file
     * @param cityName the name of the city to get
     * @return the File of the wanted city or null if the file is not found
     */
    public File loadCity(String cityName) {
        File[] cities = new File(CITIES_DIRECTORY).listFiles();
        File city = null;
        for (File f : cities) {
            if (f.getName().equalsIgnoreCase(cityName)) {
                city = f;
                break;
            }
        }
        return city;
    }

    //TODO
    public File saveCity(City city) {
        throw new RuntimeException();
    }

    /**
     *
     * @return all the Files in the consults/ directory
     */
    public File[] getAllConsultFiles() {
        return new File(CONSULTS_DIRECTORY).listFiles();
    }

    /**
     * Get a single consult file
     * @param cityName the name of the city which consults are needed
     * @return the file of the city's consults or null if file is not found
     */
    public File loadConsult(String cityName) {
        File[] cities = new File(CONSULTS_DIRECTORY).listFiles();
        File consults = null;
        for (File f : cities) {
            if (f.getName().equalsIgnoreCase(cityName)) {
                consults = f;
                break;
            }
        }
        return consults;
    }

    //TODO
    public File saveConsult() {
        throw new RuntimeException();
    }
}
