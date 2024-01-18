package com.example.budgeting;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signup extends AppCompatActivity {

    private TextInputEditText userNameText;
    private TextInputEditText passwordText;
    private TextInputEditText editTextConfirmPassword;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        userNameText = findViewById(R.id.editTextEmail);
        passwordText = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        MaterialButton loginButton = findViewById(R.id.buttonSignUp);
        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = userNameText.getText().toString();
        String password = passwordText.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        // Check if the passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(Signup.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }


        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "SignInWithEmail:Success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }else{
                            Log.w(TAG, "SignInWithEmail:failure", task.getException());
                            Toast.makeText(Signup.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                            reload();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User registration successful, redirect to login page
            reload();
        }
    }

    private void reload() {
        Intent intent = new Intent(Signup.this, Login.class);
        startActivity(intent);
        finish(); // Close the current activity
    }

}
