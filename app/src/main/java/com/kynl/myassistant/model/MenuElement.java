package com.kynl.myassistant.model;

public class MenuElement {
    int iconId;
    String fragmentClassName;
    String itemName;

    public MenuElement(String itemName, int iconId, String fragmentClassName) {
        this.itemName = itemName;
        this.iconId = iconId;
        this.fragmentClassName = fragmentClassName;
    }

    public int getIconId() {
        return iconId;
    }

    public String getFragmentClassName() {
        return fragmentClassName;
    }

    public String getItemName() {
        return itemName;
    }
}
