package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketItemDao {
    public boolean isActivated;
    public String name;
    public int point;
    public int quantity;
    public int checked;

    public boolean isActivated() {
        return isActivated;
    }

    public String getName() {
        return name;
    }

    public int getPoint() {
        return point;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public int getChecked() {
        return checked;
    }
}
