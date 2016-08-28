package com.andrewrobertclifton.pokesniper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 8/28/16.
 */
public class Pokemon {
    private static final String ID = "pokemon_id";
    private static final String NAME = "pokemon_name";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String EXPIRES = "expires";

    private int id;
    private String name;
    private double lat;
    private double lon;
    private long expireTime;

    public static Pokemon fromJSONObject(JSONObject jsonObject) throws JSONException {
        return new Pokemon(jsonObject.getInt(ID), jsonObject.getString(NAME), jsonObject.getDouble(LATITUDE), jsonObject.getDouble(LONGITUDE), jsonObject.getLong(EXPIRES));
    }

    public Pokemon(int id, String name, double lat, double lon, long expireTime) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.expireTime = expireTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
}
