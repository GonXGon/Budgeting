package com.example.budgeting;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.Objects;

import com.example.budgeting.IncomeCategory;



public class MainScreen extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private PieChart incomeExpenseChart;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        incomeExpenseChart = findViewById(R.id.incomeExpenseChart);

        // Inside the onCreate method
        Button btnIncome = findViewById(R.id.btnIncome);
        Button btnExpense = findViewById(R.id.btnExpense);

        btnIncome.setFocusable(true);
        btnIncome.setFocusableInTouchMode(true);

        btnExpense.setFocusable(true);
        btnExpense.setFocusableInTouchMode(true);

        btnIncome.setOnClickListener(v -> {
            // Handle the click on the "Income" button
            Intent intent = new Intent(MainScreen.this, IncomeCategory.class);
            startActivity(intent);

        });

        btnExpense.setOnClickListener(v -> {
            // Handle the click on the "Income" button
            Intent intent = new Intent(MainScreen.this, ExpenseCategory.class);
            startActivity(intent);

        });


        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        // Set up the navigation view item listener
        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle navigation view item clicks here
            if (item.getItemId() == android.R.id.home) {
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            } else if (item.getItemId() == R.id.menu_income) {
                Intent intent = new Intent(getApplicationContext(),Income.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.menu_expense) {
                Intent intent = new Intent(getApplicationContext(),Expense.class);
                startActivity(intent);
                return true;
            }  else if (item.getItemId() == R.id.menu_user) {
                Intent intent = new Intent(getApplicationContext(),UserProfile.class);
                startActivity(intent);
                return true;
            }
            // Close the drawer after handling the item click
            drawerLayout.closeDrawers();

            return true;
        });

        fetchDataFromFirestore();
    }

    private void fetchDataFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            fetchIncomeData(userId);
        }
    }

    private void fetchIncomeData(String userId) {
        db.collection("UserIncomeData")
                .document(userId)
                .collection("IncomeData")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float totalIncome = 0f;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            float amount = Objects.requireNonNull(document.getDouble("income")).floatValue();

                            // Calculate total income
                            totalIncome += amount;
                        }

                        // Fetch expense data after fetching income data
                        fetchExpenseData(userId, totalIncome);

                    } else {
                        Log.w(TAG, "Error getting income documents.", task.getException());
                    }
                });
    }

    private void fetchExpenseData(String userId, float totalIncome) {
        db.collection("UserExpenseData")
                .document(userId)
                .collection("ExpenseData")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float totalExpense = 0f;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            float amount = Objects.requireNonNull(document.getDouble("expense")).floatValue();

                            // Calculate total expense
                            totalExpense += amount;
                        }

                        populateCombinedPieChart(totalIncome, totalExpense);

                    } else {
                        Log.w(TAG, "Error getting expense documents.", task.getException());
                    }
                });
    }


    private void populateCombinedPieChart(float totalIncome, float totalExpense) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(totalIncome, "Total Income"));
        entries.add(new PieEntry(totalExpense, "Total Expense"));

        // Add total income entry
        PieDataSet dataSet = new PieDataSet(entries, "Income vs Expense");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);

        // Inside the populateCombinedPieChart method
        incomeExpenseChart.setTouchEnabled(true);
        incomeExpenseChart.setHighlightPerTapEnabled(true);

        runOnUiThread(() -> {
            incomeExpenseChart.setData(data);
            incomeExpenseChart.getDescription().setEnabled(false);
            incomeExpenseChart.setCenterText("Budget Overview");
            incomeExpenseChart.animateY(1000);
            incomeExpenseChart.invalidate();

            // Set legend text color
            Legend legend = incomeExpenseChart.getLegend();
            legend.setTextColor(Color.WHITE);
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Open the drawer when the menu icon is selected in the action bar
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
