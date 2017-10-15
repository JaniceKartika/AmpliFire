package com.amplifire.traves.model;

public class DrawerDao {
    public String title;
    public String subtitle;
    public boolean divider;

    public DrawerDao(String title, String subtitle, boolean divider) {
        this.title = title;
        this.subtitle = subtitle;
        this.divider = divider;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isDivider() {
        return divider;
    }
}


