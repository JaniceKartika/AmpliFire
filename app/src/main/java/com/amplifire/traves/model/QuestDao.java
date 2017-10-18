package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestDao {
    public int activeUser;
    public String key;
    public String desc;
    public String imageUrl;
    public double latitude;
    public double longitude;
    public int maximumUser;
    public String title;
    public PictureDao picture;
    public MarketDao market;
    public QuizDao quiz;
    public int status;
    public Map<String, TreasureDao> treasure;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(int activeUser) {
        this.activeUser = activeUser;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getMaximumUser() {
        return maximumUser;
    }

    public String getTitle() {
        return title;
    }

    public PictureDao getPicture() {
        return picture;
    }

    public MarketDao getMarket() {
        return market;
    }

    public QuizDao getQuiz() {
        return quiz;
    }

    public Map<String, TreasureDao> getTreasure() {
        return treasure;
    }

    public int getStatus() {
        return status;
    }
}
