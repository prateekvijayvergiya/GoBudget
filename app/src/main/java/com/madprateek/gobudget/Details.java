package com.madprateek.gobudget;

/**
 * Created by hp on 21-02-2018.
 */

public class Details {

    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String name;
    private String date;
    public Details(String image,String name,String date) {

        this.image = image;
        this.image= name;
        this.date = date;
    }
}
