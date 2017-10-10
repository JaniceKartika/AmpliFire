package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusDao {
    public Map<String, String> status;

    public Map<String, String> getStatus() {
        return status;
    }
}


