package cujae.edmaps.core;

import cujae.edmaps.core.dijkstra.Path;

import java.util.LinkedList;

public class MapsManager {
    private City actualCity;
    private static MapsManager instance;
    public static MapsManager getInstance(){
        if(instance == null)
            instance = new MapsManager();
        return instance;
    }

    public City getActualCity() {
        return actualCity;
    }
    public void saveCity(){
        FileManager.saveCity(actualCity);
    }
    public void saveConsult(LinkedList<Path> paths){
        FileManager.saveConsult(actualCity.getName(), paths);
    }
    public void setActualCity(String cityName){
        actualCity = FileManager.getCity(cityName);
    }
    public void createCity(String cityName){
        actualCity = new City(cityName);
        FileManager.saveCity(actualCity);
    }
    

}
