package cujae.edmaps.core;

public class MapsManager {
    private City actualCity;
    private FileManager fileManager;
    private static MapsManager instance;
    private MapsManager(){
        fileManager = new FileManager();
    }
    public static MapsManager getInstance(){
        if(instance == null)
            instance = new MapsManager();
        return instance;
    }
    

}
