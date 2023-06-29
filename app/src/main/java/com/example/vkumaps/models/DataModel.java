package com.example.vkumaps.models;

import java.util.List;

public class DataModel implements Comparable<DataModel> {

    private List<String> nestedList;
    private String itemText;
    private String iconUrl;
    private boolean isExpandable;

    public DataModel(List<String> itemList, String itemText, String iconUrl) {
        this.nestedList = itemList;
        this.itemText = itemText;
        this.iconUrl = iconUrl;
        isExpandable = false;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public List<String> getNestedList() {
        return nestedList;
    }

    public String getItemText() {
        return itemText;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    @Override
    public int compareTo(DataModel dataModel) {
        return this.itemText.compareTo(dataModel.itemText);
    }
}