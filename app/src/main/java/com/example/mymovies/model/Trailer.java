package com.example.mymovies.model;

public class Trailer {
    private final String key;
    private final String name;
    private final String type;

    public Trailer(String key, String name, String type) {

        this.key = key;
        this.name = name;
        this.type = type;
    }

    public String getKey() { return key; }

    public String getName() { return name; }

    public String getType() { return type; }
}
