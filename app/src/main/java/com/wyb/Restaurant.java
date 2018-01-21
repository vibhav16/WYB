package com.wyb;

/**
 * Created by VIBHAV on 22-09-2017.
 */

public class Restaurant {

    private int id;
    private String restaurantName;
    private String imageLink;
    private String location;

    public Restaurant(int id, String restaurantName, String imageLink, String location) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.imageLink = imageLink;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
