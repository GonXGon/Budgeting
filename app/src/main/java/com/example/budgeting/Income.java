package com.example.budgeting;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Income extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

                saveIncomeToFirestore(income);
            }
        });

    }

    private void saveIncomeToFirestore(int income) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Check if a document with the user's UID exists
            db.collection("IncomeData")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Document for the user already exists, update the income
                                    updateIncome(document.getId(), income);
                                } else {
                                    // No existing document found, add a new one
                                    addNewIncome(userId, income);
                                }
                            } else {
                                Log.d(TAG, "Error getting document: ", task.getException());
                            }
                        }
                    });
        }
    }




    private void addNewIncome(String userId, int income) {
        // Add a new document with the user ID and income
        IncomeData newIncomeData = new IncomeData(userId, income);
        db.collection("IncomeData")
                .document(userId)
                .set(new IncomeData(userId,income))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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


    private void updateIncome(String userId, int newIncome) {
        db.collection("IncomeData").document(userId)
                .update("income", newIncome)
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
