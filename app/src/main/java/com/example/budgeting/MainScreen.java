package com.example.budgeting;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private PieChart incomeExpenseChart;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        incomeExpenseChart = findViewById(R.id.incomeExpenseChart);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the action bar
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu); // You can set your own menu icon
        }

        // Set up the navigation view item listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here
                if (item.getItemId() == android.R.id.home) {
                    drawerLayout.openDrawer(GravityCompat.START);
                    return true;
                } else if (item.getItemId() == R.id.menu_income) {
                    Intent intent = new Intent(MainScreen.this, Income.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_expense) {
                    Intent intent = new Intent(MainScreen.this, Expense.class);
                    startActivity(intent);
                    finish();
                    return true;
                }

                // Close the drawer after handling the item click
                drawerLayout.closeDrawers();

                return true;
            }
        });

        fetchDataFromFirestore();
    }

    private void fetchDataFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("IncomeData")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Assuming the field name in your IncomeData class is "income"
                                    Double income = document.getDouble("income");
                                    if (income != null) {
                                        fetchExpenseData(income.floatValue());
                                    } else {
                                        // Handle null value for income
                                        populatePieChart(0f, 0f);
                                    }
                                } else {
                                    // No income document found for the user
                                    populatePieChart(0f, 0f);
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }

    private void fetchExpenseData(float totalIncome) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("ExpenseData")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Assuming the field name in your ExpenseData class is "expense"
                                    Double expense = document.getDouble("expense");
                                    if (expense != null) {
                                        populatePieChart(totalIncome, expense.floatValue());
                                    } else {
                                        // Handle null value for expense
                                        populatePieChart(totalIncome, 0f);
                                    }
                                } else {
                                    // No expense document found for the user
                                    populatePieChart(totalIncome, 0f);
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }

    private void populatePieChart(float incomeVal,float expenseVal){
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(incomeVal,"Income"));
        entries.add(new PieEntry(expenseVal, "Expense"));

        PieDataSet dataSet = new PieDataSet(entries, "Income vs Expense");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        incomeExpenseChart.setData(data);

        incomeExpenseChart.getDescription().setEnabled(false);
        incomeExpenseChart.setCenterText("Budget Overview");
        incomeExpenseChart.animateY(1000);

        incomeExpenseChart.invalidate(); // Refresh the chart
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


