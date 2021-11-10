package com.lobomarket.wecart.Models;

public class TransactionHistory {
    private String username;
    private String storeName;
    private String finalTotal;
    private String date;

    public TransactionHistory() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(String finalTotal) {
        this.finalTotal = finalTotal;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
