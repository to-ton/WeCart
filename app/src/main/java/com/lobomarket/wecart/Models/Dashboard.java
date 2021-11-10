package com.lobomarket.wecart.Models;

public class Dashboard {
    String sellerCount;
    String userCount;
    String agentCount;

    public Dashboard(){}

    public String getSellerCount() {
        return sellerCount;
    }

    public void setSellerCount(String sellerCount) {
        this.sellerCount = sellerCount;
    }

    public String getUserCount() {
        return userCount;
    }

    public void setUserCount(String userCount) {
        this.userCount = userCount;
    }

    public String getAgentCount() {
        return agentCount;
    }

    public void setAgentCount(String agentCount) {
        this.agentCount = agentCount;
    }
}
