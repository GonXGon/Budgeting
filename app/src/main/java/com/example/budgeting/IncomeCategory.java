package com.example.budgeting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class IncomeCategory extends AppCompatActivity {

    private static final String TAG = "IncomeCategory";
    private PieChart incomeChart;
    private HorizontalBarChart incomeBarChart;
    private DrawerLayout drawerLayout;
    private String selectedTimeFrame = "Annually";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incomecategory);

        incomeChart = findViewById(R.id.incomeChart);
        incomeBarChart = findViewById(R.id.incomeBarChart);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        Toolbar toolbar = findViewById(R.id.toolbarcatincome);
        setSupportActionBar(toolbar);

        // Set up the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        // Set up the navigation view item listener
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == android.R.id.home) {
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            } else if (item.getItemId() == R.id.annually) {
                selectedTimeFrame = "Annually";
                fetchDataFromFirestore();
                return true;
            } else if (item.getItemId() == R.id.monthly) {
                selectedTimeFrame = "Monthly";
                fetchDataFromFirestore();
                return true;
            } else if (item.getItemId() == R.id.weekly) {
                selectedTimeFrame = "Weekly";
                fetchDataFromFirestore();
                return true;
            } else if (item.getItemId() == R.id.daily) {
                selectedTimeFrame = "Daily";
                fetchDataFromFirestore();
                return true;
            }

            // Close the drawer after handling the item click
            drawerLayout.closeDrawers();
            return true;
        });

        // Fetch and display income categories
        fetchDataFromFirestore();
    }

    private void fetchDataFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            fetchIncomeData(userId, selectedTimeFrame);
        }
    }

    private void fetchIncomeData(String userId, String timeFrame) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        db.collection("UserIncomeData")
                .document(userId)
                .collection("IncomeData")
                .document(timeFrame)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Retrieve the result (document) from the task
                        DocumentSnapshot documentSnapshot = task.getResult();

                        // Check if the result is not null
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            // Assuming categories are stored directly within the document
                            Map<String, Object> categories = documentSnapshot.getData();

                            // Iterate through the categories
                            for (Map.Entry<String, Object> entry : categories.entrySet()) {
                                String category = entry.getKey();
                                float amount = convertToFloat(entry.getValue());
                                pieEntries.add(new PieEntry(amount, category));
                                barEntries.add(new BarEntry(pieEntries.size() - 1, amount));
                            }

                            // Update PieChart
                            PieDataSet pieDataSet = new PieDataSet(pieEntries, "Income Categories");
                            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                            PieData pieData = new PieData(pieDataSet);

                            incomeChart.setData(pieData);
                            incomeChart.getDescription().setEnabled(false);
                            incomeChart.setCenterText("Income Categories");
                            incomeChart.animateY(1000);
                            incomeChart.invalidate();

                            // Update HorizontalBarChart
                            BarDataSet barDataSet = new BarDataSet(barEntries, "Income Categories");
                            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                            BarData barData = new BarData(barDataSet);
                            incomeBarChart.setData(barData);
                            incomeBarChart.getDescription().setEnabled(false);
                            incomeBarChart.animateY(1000);
                            incomeBarChart.invalidate();
                        } else {
                            Log.w(TAG, "DocumentSnapshot is null or does not exist");
                        }
                    } else {
                        Log.w(TAG, "Error getting document.", task.getException());
                    }
                });
    }

    private float convertToFloat(Object value) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else {
            Log.w(TAG, "Unsupported income type: " + value.getClass().getSimpleName());
            return 0.0f; // or handle it according to your needs
        }
    }



}
