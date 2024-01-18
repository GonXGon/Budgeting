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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Total extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = findViewById(R.id.button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText nameEdit = findViewById(R.id.editTextText2);
                EditText incomeEdit = findViewById(R.id.editTextNumber3);
                EditText expensesEdit = findViewById(R.id.editTextNumber4);

                String name = nameEdit.getText().toString();
                int income = Integer.parseInt(incomeEdit.getText().toString());
                int expenses = Integer.parseInt(expensesEdit.getText().toString());

                int budget = income - expenses;


                saveDataToFirestore(name, income, expenses, budget);

                Toast.makeText(Total.this, "Budget Calculated: "+ budget, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDataToFirestore(String name, int income, int expenses, int budget) {
        // Create a collection reference
        db.collection("budgetData")
                .add(new BudgetData(name, income, expenses, budget))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>(){
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(Total.this, "Data saved to Firestore" + documentReference.getId(), Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Total.this, "Error saving data to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
    }
}
