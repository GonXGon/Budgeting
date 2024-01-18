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

public class Expense extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstancesState) {

        super.onCreate(savedInstancesState);
        setContentView(R.layout.expenses);
        Button addButton = findViewById(R.id.buttonAddExpense);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText expenseEdit = findViewById(R.id.editExpense);
                int expense = Integer.parseInt(expenseEdit.getText().toString());

                saveExpenseToFirestore(expense);
            }
        });

    }

    private void saveExpenseToFirestore(int expense) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null){
            String userId = currentUser.getUid();

            db.collection("ExpenseData")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()){
                                    updateExpense(document.getId(), expense);
                                }else {
                                    addNewExpense(userId, expense);
                                }
                            }else{
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void addNewExpense(String userId, int expense) {
        ExpenseData newExpesnedata = new ExpenseData(userId, expense);
        db.collection("ExpenseData")
                .document(userId)
                .set(new IncomeData(userId,expense))
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

    private void updateExpense(String userId, int newExpense) {
        db.collection("ExpenseData").document(userId)
                .update("expense", newExpense)
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
