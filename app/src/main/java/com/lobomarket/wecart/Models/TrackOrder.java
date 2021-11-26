package com.lobomarket.wecart.Models;

public class TrackOrder {
    private String trackingId;
    private String deliveryStatus;
    private String finalTotal;
    private String delivery_fee;
    private String add_fee;


    public TrackOrder() {
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(String finalTotal) {
        this.finalTotal = finalTotal;
    }

    public String getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(String delivery_fee) {
        this.delivery_fee = delivery_fee;
    }

    public String getAdd_fee() {
        return add_fee;
    }

    public void setAdd_fee(String add_fee) {
        this.add_fee = add_fee;
    }
}
