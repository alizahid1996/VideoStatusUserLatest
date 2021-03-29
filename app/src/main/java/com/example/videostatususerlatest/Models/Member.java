package com.example.videostatususerlatest.Models;

public class Member {
    private String name;
    private String Videourl;
    private String search;
    private String status;
    private String userId;

    public Member() {
    }

    public Member(String name, String videourl, String search, String status, String userId) {
        this.name = name;
        Videourl = videourl;
        this.search = search;
        this.status = status;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideourl() {
        return Videourl;
    }

    public void setVideourl(String videourl) {
        Videourl = videourl;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
