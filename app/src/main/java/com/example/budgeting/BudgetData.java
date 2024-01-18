package com.example.budgeting;

public class BudgetData {

    private String name;
    private int income;
    private int expenses;
    private int budget;

    // Default constructor required for Firestore
    public BudgetData() {
    }

    public BudgetData(String name, int income, int expenses, int budget) {
        this.name = name;
        this.income = income;
        this.expenses = expenses;
        this.budget = budget;
    }

    // Add getters and setters for each field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getExpenses() {
        return expenses;
    }

    public void setExpenses(int expenses) {
        this.expenses = expenses;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }
}
