package com.bidmaster.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Item {
    private int itemId;
    private String title;
    private String description;
    private BigDecimal startingPrice;
    private BigDecimal reservePrice;
    private BigDecimal currentPrice;
    private String imageUrl;
    private int categoryId;
    private int sellerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Timestamp createdAt;
    private String sellerUsername;
    // Default constructor
    public Item() {
    }
    
    // Parameterized constructor
    public Item(String title, String description, BigDecimal startingPrice, BigDecimal reservePrice, 
                String imageUrl, int categoryId, int sellerId, LocalDateTime startTime, LocalDateTime endTime) {
        this.title = title;
        this.description = description;
        this.startingPrice = startingPrice;
        this.reservePrice = reservePrice;
        this.currentPrice = startingPrice;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.sellerId = sellerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "pending";
    }
    
    // Constructor with itemId
    public Item(int itemId, String title, String description, BigDecimal startingPrice, BigDecimal reservePrice, 
                BigDecimal currentPrice, String imageUrl, int categoryId, int sellerId, LocalDateTime startTime, 
                LocalDateTime endTime, String status, Timestamp createdAt) {
        this.itemId = itemId;
        this.title = title;
        this.description = description;
        this.startingPrice = startingPrice;
        this.reservePrice = reservePrice;
        this.currentPrice = currentPrice;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.sellerId = sellerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getItemId() {
        return itemId;
    }
    
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getStartingPrice() {
        return startingPrice;
    }
    
    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }
    
    public BigDecimal getReservePrice() {
        return reservePrice;
    }
    
    public void setReservePrice(BigDecimal reservePrice) {
        this.reservePrice = reservePrice;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public int getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    // Business logic methods
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status.equals("active") && now.isAfter(startTime) && now.isBefore(endTime);
    }
    
    public boolean isEnded() {
        return LocalDateTime.now().isAfter(endTime);
    }
    
    public boolean isReserveMet() {
        return reservePrice == null || currentPrice.compareTo(reservePrice) >= 0;
    }
    public String getSellerUsername() {
        return sellerUsername;
    }
    
    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }
    
    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", title='" + title + '\'' +
                ", currentPrice=" + currentPrice +
                ", status='" + status + '\'' +
                ", endTime=" + endTime +
                '}';
    }
}
