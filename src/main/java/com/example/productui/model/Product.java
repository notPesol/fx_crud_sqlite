package com.example.productui.model;

public class Product {
    private int _id;
    private String name;
    private int stock;
    private int brand;
    private int category;

    public Product() {
    }

    public Product(int _id, String name, int stock, int brand, int category) {
        this._id = _id;
        this.name = name;
        this.stock = stock;
        this.brand = brand;
        this.category = category;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getBrand() {
        return brand;
    }

    public void setBrand(int brand) {
        this.brand = brand;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
