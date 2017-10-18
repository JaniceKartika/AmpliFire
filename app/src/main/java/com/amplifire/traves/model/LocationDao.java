package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationDao {
    public String key;
    public String address;
    public String imageUrl;
    public double latitude;
    public double longitude;
    public String name;
    public Map<String, Boolean> quest;
    public double radius;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getAddress() {
        return address;
    }

    public String getImageUrl() {
        return imageUrl;
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

    public double getRadius() {
        return radius;
    }

    public Map<String, Boolean> getQuest() {
        return quest;
    }
}
