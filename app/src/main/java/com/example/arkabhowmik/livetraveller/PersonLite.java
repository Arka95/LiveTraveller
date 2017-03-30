package com.example.arkabhowmik.livetraveller;

/**
 * Created by Arka Bhowmik on 2/18/2017.
 */
public class PersonLite {
    private String id;
    private String name;
    private Double locLat, locLong;
    private String dp;
    private int isOnline;
    private String country, city, state;

    //note that here we are getting the src for the image and in the imageloader we are using that src only
    public PersonLite() {
    }

    public PersonLite(String id, String name, int isonline, double Lat, double Long, String dp) {
        this.name = name;
        this.id = id;
        this.isOnline = isonline;
        this.dp = dp;
        this.locLat = Lat;
        this.locLong = Long;

    }

    public String getCountry() {
        return this.country;
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }


    public String getName() {
        return name;
    }

    public void setName(String nam) {
        name = nam;
    }

    public String getId() {
        return id;
    }

    public int getIsOnline() {
        return this.isOnline;
    }

    public void setIsOnline(int val) {
        isOnline = val;
    }

    public Double getLat() {
        return locLat;
    }

    public String getDp() {
        return dp;
    }

    public Double getLong() {
        return locLong;
    }

}

