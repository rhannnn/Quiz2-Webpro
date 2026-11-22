package com.prestige.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking {

    public static final java.util.List<String> STATUSES =
            java.util.List.of("pending", "approved", "completed", "cancelled");

    private long id;
    private long userId;
    private long carId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private String status;
    private String userName;
    private String userEmail;
    private String carName;
    private String carBrand;
    private String carImage;
    private BigDecimal carPricePerDay;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public BigDecimal getCarPricePerDay() {
        return carPricePerDay;
    }

    public void setCarPricePerDay(BigDecimal carPricePerDay) {
        this.carPricePerDay = carPricePerDay;
    }

    public boolean isPending() {
        return "pending".equals(status);
    }

    public long getDays() {
        if (startDate == null || endDate == null) {
            return 0L;
        }
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    public String getInvoiceNo() {
        return String.format("INV-%06d", id);
    }
}
