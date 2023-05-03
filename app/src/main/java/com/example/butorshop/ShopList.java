package com.example.butorshop;

public class ShopList {
    private String name;
    private String info;
    private String price;
    private int imgres;


    public ShopList(String name, String info, String price, int imgres) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.imgres = imgres;
    }

    public ShopList() {
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getPrice() {
        return price;
    }

    public int getImgres() {
        return imgres;
    }
}
