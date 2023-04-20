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

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getFragmentClassName() {
        return fragmentClassName;
    }

    public void setFragmentClassName(String fragmentClassName) {
        this.fragmentClassName = fragmentClassName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
