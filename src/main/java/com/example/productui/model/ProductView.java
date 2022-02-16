package com.example.productui.model;

public class ProductView {
    private String name;
    private int stock;
    private String brand;
    private String category;

    public ProductView() {
    }

    public ProductView(String name, int stock, String brand, String category) {
        this.name = name;
        this.stock = stock;
        this.brand = brand;
        this.category = category;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return name;
    }
}
