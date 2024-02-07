package com.example.budgeting;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class Income extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinnerCategories;
    private Spinner timeFrameCategories;

    @Override
    protected void onCreate(Bundle savedInstancesState) {

        super.onCreate(savedInstancesState);
        setContentView(R.layout.income);
        Button addButton = findViewById(R.id.buttonAddIncome);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText incomeEdit = findViewById(R.id.editIncomeText);
                int income = Integer.parseInt(incomeEdit.getText().toString());

                String selectedCategory = spinnerCategories.getSelectedItem().toString();
                String selectedtimeFrame = timeFrameCategories.getSelectedItem().toString();

                saveIncomeToFirestore(income, selectedCategory, selectedtimeFrame);
            }
        });

        spinnerCategories = findViewById(R.id.screen);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.income_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(adapter);

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

        timeFrameCategories = findViewById(R.id.timeFrame);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.time_frames, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeFrameCategories.setAdapter(adapter1);

        // Set a listener for the spinner item selection
        timeFrameCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void saveIncomeToFirestore(int income, String category, String timeFrame) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            CollectionReference userCollection = db.collection("UserIncomeData").document(userId).collection("IncomeData");
            DocumentReference documentReference = userCollection.document(timeFrame);  // Use timeFrame instead of category

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Document for the user and category exists, update the income
                            updateIncome(userId, income, timeFrame, documentReference);
                        } else {
                            addNewIncome(userId, income, timeFrame, category);
                        }
                    } else {
                        Log.d(TAG, "Error getting document: " + task.getException());
                    }
                }
            });
        }
    }


    private void addNewIncome(String userId, int income, String timeFrame, String selectedCategory) {
        // Add a new document with the user ID and income

        Map<String, Object> incomeData = new HashMap<>();
        incomeData.put("income", income);
        incomeData.put("category", selectedCategory);

        DocumentReference timeFrameDocRef = db.collection("UserIncomeData").document(userId)
                .collection("IncomeData").document(timeFrame);

        // Set the income data for the time frame
        timeFrameDocRef.set(incomeData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Document for the time frame added successfully
                        Toast.makeText(Income.this, "Income Data saved to Firestore: " + userId, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Income.this, "Error saving data to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void updateIncome(String userId, int newIncome, String category, DocumentReference documentReference) {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("income", newIncome);

        documentReference.update(updatedData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Income.this, "Income Data updated in Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Income.this, "Error updating data in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }






}
