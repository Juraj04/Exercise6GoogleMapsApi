package com.example.janik.exercise6googlemapsapi.Classes;

/**
 * Created by janik on 03.10.2017.
 */

public class Point {
    private String title;
    private String address;
    private String phone;
    private String email;
    private String web;

    public Point(String title, String address, String phone, String email, String web) {
        this.title = title;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.web = web;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getWeb() {
        return web;
    }
}
