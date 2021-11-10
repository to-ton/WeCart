package com.lobomarket.wecart.Models;

public class IndividualReport {
    String sale_inventory;
    String order_fulfilled;
    String daily_visitors;

    public IndividualReport(){}

    public String getSale_inventory() {
        return sale_inventory;
    }

    public void setSale_inventory(String sale_inventory) {
        this.sale_inventory = sale_inventory;
    }

    public String getOrder_fulfilled() {
        return order_fulfilled;
    }

    public void setOrder_fulfilled(String order_fulfilled) {
        this.order_fulfilled = order_fulfilled;
    }

    public String getDaily_visitors() {
        return daily_visitors;
    }

    public void setDaily_visitors(String daily_visitors) {
        this.daily_visitors = daily_visitors;
    }
}
