package com.prestige.model;

import java.math.BigDecimal;
import java.util.List;

public class Car {

    public static final List<String> CATEGORIES = List.of("Supercar", "Luxury Sedan", "Motorsport", "SUV");
    public static final List<String> STATUSES = List.of("available", "rented");

    private long id;
    private String name;
    private String brand;
    private String category;
    private BigDecimal pricePerDay;
    private int hp;
    private int topSpeed;
    private String status;
    private String image;
    private boolean favorite;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getTopSpeed() {
        return topSpeed;
    }

    public void setTopSpeed(int topSpeed) {
        this.topSpeed = topSpeed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isAvailable() {
        return "available".equals(status);
    }

    public long getPriceInt() {
        return pricePerDay == null ? 0L : pricePerDay.longValue();
    }

    public String getImagePath() {
        return image == null || image.isBlank() ? "/assets/placeholder-car.svg" : "/media/" + image;
    }
}
