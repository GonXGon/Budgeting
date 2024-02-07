package com.example.budgeting;

public class IncomeData {
    private int income;
    private String userId;
    private String category;

    public IncomeData(String userId, int income,String category) {
        this.userId = userId;
        this.income = income;
        this.category = category;
    }

    public int getIncome() {
        return income;
    }

    public String getUserId() {
        return userId;
    }
    public String getCategory() {
        return category;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }
    public void setCategory(String category){
        this.category = category;
    }
}
