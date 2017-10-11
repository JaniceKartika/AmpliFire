package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationDao {
    public String address;
    public double latitude;
    public double longitude;
    public String name;
    public Map<String, Boolean> quest;
    public int radius;

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public int getRadius() {
        return radius;
    }

    public Map<String, Boolean> getQuest() {
        return quest;
    }
}


