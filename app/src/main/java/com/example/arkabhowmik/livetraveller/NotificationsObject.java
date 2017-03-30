package com.example.arkabhowmik.livetraveller;

/**
 * Created by Arka Bhowmik on 2/17/2017.
 */
class NotificationsObject {
    private String to;
    private String from;
    private int type;
    private Integer not_id;

    NotificationsObject(Integer nid, String to, String from, int type) {
        this.not_id = nid;
        this.to = to;
        this.from = from;
        this.type = type;
    }

    public Integer getId() {
        return this.not_id;
    }

    public String getTo() {
        return this.to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setNot_id(Integer not_id) {
        this.not_id = not_id;
    }


}
