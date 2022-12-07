package cujae.edmaps.core;

import java.util.LinkedList;
import java.util.List;

public class Country {
    List<City> cities;
    private String name;
    private static Country country;

    public static Country getInstance() {
        if (country == null)
            country = new Country("Cuba");
        return country;
    }

    private Country(String name) {
        setName(name);
        this.cities = new LinkedList<>();
    }

    public List<City> getCities() {
        return cities;
    }

    public void addCity(City city) {
        cities.add(city);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
