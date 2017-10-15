package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PictureDao {
    public boolean isActivated;
    public int min;
    public int point;
    public boolean validation;

    public boolean isActivated() {
        return isActivated;
    }

    public int getMin() {
        return min;
    }

    public int getPoint() {
        return point;
    }

    public boolean isValid() {
        return validation;
    }
}
