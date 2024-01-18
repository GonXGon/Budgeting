package com.example.budgeting;

public class ExpenseData {

    private int expense;
    private String userId;

    public ExpenseData(String userId,int expense) {
        this.userId = userId;
        this.expense = expense;
    }

    public int getExpense() {
        return expense;
    }

    public String getUserId() {
        return userId;
    }

    public void setExpense(int expense) {
        this.expense = expense;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }
}
