package com.example.budgeting;
import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Expense extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinnerCategories;
    private EditText expenseEdit;
    private Button buttonShowDatePicker;
    private Button addButton;

    // Calendar instance to store the selected date
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.expenses);

        addButton = findViewById(R.id.buttonAddExpense);
        buttonShowDatePicker = findViewById(R.id.buttonShowDatePicker);
        expenseEdit = findViewById(R.id.editExpense);
        spinnerCategories = findViewById(R.id.screen);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int expense = Integer.parseInt(expenseEdit.getText().toString());
                String selectedCategory = spinnerCategories.getSelectedItem().toString();
                saveExpenseToFirestore(expense, selectedCategory);
            }
        });

        buttonShowDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Set a listener for the spinner item selection
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selection if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private void showDatePicker() {
        // Create a new DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set the selected date to the Calendar instance
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, monthOfYear);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Optionally, you can format the date and display it to the user
                        // String formattedDate = DateFormat.getDateInstance().format(selectedDate.getTime());
                        // Toast.makeText(Expense.this, "Selected Date: " + formattedDate, Toast.LENGTH_SHORT).show();
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void saveExpenseToFirestore(int expense, String category) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            CollectionReference userCollection = db.collection("UserExpenseData").document(userId).collection("ExpenseData");

            DocumentReference documentReference = userCollection.document(category);

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            updateExpense(document.getId(), expense, category, documentReference);
                        } else {
                            addNewExpense(userId, expense, category);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    private void addNewExpense(String userId, int expense, String category) {
        Map<String, Object> expenseData = new HashMap<>();
        expenseData.put("expense", expense);

        long dateTimestamp = selectedDate.getTimeInMillis();

        Date date = new Date(dateTimestamp);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(date);

        expenseData.put("date", formattedDate); // Save the selected date as a timestamp

        db.collection("UserExpenseData").document(userId).collection("ExpenseData").document(category)
                .set(expenseData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Expense.this, "Expense Data saved to Firestore: " + userId, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Expense.this, "Error saving data to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateExpense(String userId, int newExpense, String category, DocumentReference documentReference) {
        Map<String, Object> expenseData = new HashMap<>();
        expenseData.put("expense", newExpense);

        long dateTimestamp = selectedDate.getTimeInMillis();

        Date date = new Date(dateTimestamp);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(date);

        expenseData.put("date", formattedDate); // Save the selected date as a timestamp

        documentReference.update(expenseData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Expense.this, "Expense Data updated in Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Expense.this, "Error updating data in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
