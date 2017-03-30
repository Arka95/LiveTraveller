package com.example.arkabhowmik.livetraveller;

/**
 * Created by Arka Bhowmik on 7/16/2016.
 */
class Person extends PersonLite {
    private String id;
    private String name;
    private Double locLat, locLong;
    private String dp;
    private String dob;
    private int isOnline;
    private int isFriend;
    private String country, city, state;
//note that here we are getting the src for the image and in the imageloader we are using that src only

    Person() {
    }

    public Person(PersonLite p) {
        this.name = p.getName();
        this.id = p.getId();
        this.isOnline = p.getIsOnline();
        this.dp = p.getDp();
        this.locLat = p.getLat();
        this.locLong = p.getLong();

    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getisFriend() {
        return this.isFriend;
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

    public void setIsFriend(int f) {
        this.isFriend = f;
    }

    public Double getLat() {
        return locLat;
    }

    public void setLat(Double lat) {
        this.locLat = lat;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public Double getLong() {
        return locLong;
    }

    public void setLong(Double longi) {
        this.locLong = longi;
    }

    public String getDOB() {
        return this.dob;
    }

    public void setDOB(String dob) {
        this.dob = dob;
    }


}