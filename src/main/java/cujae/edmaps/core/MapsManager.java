package cujae.edmaps.core;

import cujae.edmaps.core.dijkstra.CompletePath;

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
    public void saveCity(){
        FileManager.saveCity(actualCity);
    }
    public void saveConsult(CompletePath path){
        FileManager.saveConsult(actualCity.getName(), path.getPaths());
    }
    public void setActualCity(String cityName){
        actualCity = FileManager.getCity(cityName);
    }
    

}
