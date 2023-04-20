package com.kynl.myassistant.model;

public class MenuElement {
    int iconId;
    String fragmentClassName;

    public MenuElement(int iconId, String fragmentClassName) {
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
}
