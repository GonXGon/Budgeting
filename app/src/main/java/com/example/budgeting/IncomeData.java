package com.example.budgeting;

public class IncomeData {
    private int income;
    private String userId;

    public IncomeData(String userId, int income) {
        this.userId = userId;
        this.income = income;
    }

    public int getIncome() {
        return income;
    }

    public String getUserId() {
        return userId;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }
}
