package com.example.budgeting;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class ExpenseCategory extends AppCompatActivity {
    private static final String TAG = "ExpenseCategory";
    private PieChart expenseChart;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expensecategory);
        expenseChart = findViewById(R.id.expenseChart);
        // Fetch and display income categories
        fetchDataFromFirestore();
    }

    private void fetchDataFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            fetchExpenseData(userId);
        }
    }

    private void fetchExpenseData(String userId) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        db.collection("UserExpenseData")
                .document(userId)
                .collection("ExpenseData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    String category = document.getId();
                                    float amount = document.getDouble("expense").floatValue();
                                    String dateString = document.getString("date"); // Assuming "date" is the field containing the date as a string
                                    Date date = parseDate(dateString);

                                    // Get the document name
                                    String documentName = document.getId();

                                    pieEntries.add(new CustomPieEntry(amount, category, documentName));
                                    pieEntries.get(pieEntries.size() - 1).setData(date); // Set date as data
                                }


                                // Sort pieEntries based on date
                                Collections.sort(pieEntries, new Comparator<PieEntry>() {
                                    @Override
                                    public int compare(PieEntry entry1, PieEntry entry2) {
                                        Date date1 = (Date) entry1.getData();
                                        Date date2 = (Date) entry2.getData();

                                        // Add null checks
                                        if (date1 == null && date2 == null) {
                                            return 0; // Both dates are null, consider them equal
                                        } else if (date1 == null) {
                                            return 1; // date1 is null, consider it greater than date2
                                        } else if (date2 == null) {
                                            return -1; // date2 is null, consider it greater than date1
                                        } else {
                                            return date1.compareTo(date2);
                                        }
                                    }
                                });

                                ArrayList<String> dateAndDocumentLabels = new ArrayList<>();
                                for (PieEntry entry : pieEntries) {
                                    Date date = (Date) entry.getData();
                                    if (date != null) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                        // Get the category (document name) and expense amount for the current entry
                                        String category = ((CustomPieEntry) entry).getLabel(); // Get category from CustomPieEntry
                                        String documentName = ((CustomPieEntry) entry).getDocumentName(); // Get document name from CustomPieEntry
                                        float expense = entry.getValue(); // Get expense amount from PieEntry
                                        // Format the legend label with category, expense amount, and date
                                        String legendLabel = category + " - " + expense + " - " + sdf.format(date);
                                        dateAndDocumentLabels.add(legendLabel);
                                    } else {
                                        dateAndDocumentLabels.add("N/A");
                                    }
                                }

                                TextView legendDatesTextView = findViewById(R.id.legendDates);
                                legendDatesTextView.setText(TextUtils.join("\n", dateAndDocumentLabels));




                                // After the loop, you can update your PieChart
                                PieDataSet pieDataSet = new PieDataSet(pieEntries, "Expense Categories");
                                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                                PieData pieData = new PieData(pieDataSet);
                                expenseChart.setData(pieData);
                                expenseChart.getDescription().setEnabled(false);
                                expenseChart.setCenterText("Expense Categories");
                                expenseChart.animateY(1000);
                                expenseChart.invalidate();

                            } else {
                                Log.w(TAG, "QuerySnapshot is null");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }

                });
    }

    private Date parseDate(String dateString) {
        try {
            if (dateString != null && !dateString.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                return sdf.parse(dateString);
            } else {
                // Handle the case where dateString is null or empty
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Error parsing date: " + dateString, e); // Add this line for additional logging
            return null;
        }
    }


}
