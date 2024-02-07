package com.example.budgeting;

public class ExpenseData {

    private int expense;
    private String userId;
    private String category;

    public ExpenseData(String userId,int expense, String category) {
        this.userId = userId;
        this.expense = expense;
        this.category = category;
    }

    public int getExpense() {
        return expense;
    }

    public String getUserId() {
        return userId;
    }
    public String getCategory() {
        return category;
    }

    public void setExpense(int expense) {
        this.expense = expense;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }
    public void setCategory(String category){
        this.category = category;
    }
}
