package com.kynl.myassistant.model;

public class Device {
    private int id;
    private Type type;
    private String name;
    private State status;

    public enum Type {
        LIGHT,
        TEMPERATURE,
        FAN,
        PUMP,
        MEDIA
    }

    public enum State {
        ON,
        OFF,
        INVALID
    }

    public Device(int id, Type type, String name, State status) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getStatus() {
        return status;
    }

    public void setStatus(State status) {
        this.status = status;
    }
}
