package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketDao {
    public Map<String, MarketItemDao> items;
    public String validationCode;

    public Map<String, MarketItemDao> getItems() {
        return items;
    }

    public String getValidationCode() {
        return validationCode;
    }
}
