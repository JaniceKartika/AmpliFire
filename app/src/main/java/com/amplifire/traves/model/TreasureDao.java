package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TreasureDao {
    public String barcode;
    public String desc;
    public int point;

    public String getBarcode() {
        return barcode;
    }

    public String getDesc() {
        return desc;
    }

    public int getPoint() {
        return point;
    }
}
