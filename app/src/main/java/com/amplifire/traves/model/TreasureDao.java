package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TreasureDao {
    public String key;
    public String barcode;
    public String desc;
    public int point;
    public int status; //0 blm, 1 sudah


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPoint(int point) {
        this.point = point;
    }

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
