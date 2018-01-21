package com.wyb;

/**
 * Created by VIBHAV on 27-09-2017.
 */

public class LocPojo {
    Double lat,lon;
    String name;
    String addr;
    String location;

    public LocPojo(Double lat, Double lon, String name, String addr,String location) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.addr=addr;
        this.location=location;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
