package com.example.mymovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/** Implements Parcelable so that movies can be serialized
 * and passed through savedInstanceState bundles and through Intent extras
 */
public class Trailer {
    private final String key;
    private final String name;
    private final String type;

    /* Constructor that creates new movie from movieData */
    public Trailer(String key, String name, String type) {

        this.key = key;
        this.name = name;
        this.type = type;
    }

    public String getKey() { return key; }

    public String getName() { return name; }

    public String getType() { return type; }

}
