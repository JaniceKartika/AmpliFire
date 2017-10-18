package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuizDao {
    public boolean isActivated;
    public Map<String, QuizItemDao> items;
    public int point;

    public boolean isActivated() {
        return isActivated;
    }

    public Map<String, QuizItemDao> getItems() {
        return items;
    }

    public int getPoint() {
        return point;
    }
}
