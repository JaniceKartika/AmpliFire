package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketDao {
    public boolean isActivated;
    public Map<String, MarketItemDao> items;
    public int point;
    public String validationCode;

    public boolean isActivated() {
        return isActivated;
    }

    public Map<String, MarketItemDao> getItems() {
        return items;
    }

    public int getPoint() {
        return point;
    }

    public String getValidationCode() {
        return validationCode;
    }
}
