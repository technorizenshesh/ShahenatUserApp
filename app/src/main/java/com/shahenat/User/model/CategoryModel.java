package com.shahenat.User.model;

public class CategoryModel {

    String name;
    int Img;

    public CategoryModel(String name, int img) {
        this.name = name;
        Img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return Img;
    }

    public void setImg(int img) {
        Img = img;
    }
}
