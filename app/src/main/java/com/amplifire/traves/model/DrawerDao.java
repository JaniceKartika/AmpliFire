package com.amplifire.traves.model;

public class DrawerDao {
    public String title;
    public boolean selected;
    public boolean divider;

    public DrawerDao(String title, boolean selected, boolean divider) {
        this.title = title;
        this.selected = selected;
        this.divider = divider;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isDivider() {
        return divider;
    }

    public void setDivider(boolean divider) {
        this.divider = divider;
    }
}
