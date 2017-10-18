package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketItemDao {
    public int min;
    public String name;

    public int getMin() {
        return min;
    }

    public String getName() {
        return name;
    }
}
