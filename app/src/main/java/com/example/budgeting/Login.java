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

public class Login extends AppCompatActivity {

    private TextInputEditText userNameText;
    private TextInputEditText passwordText;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        userNameText = findViewById(R.id.editTextUsername);
        passwordText = findViewById(R.id.editTextPassword);

        MaterialButton loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = userNameText.getText().toString();
        String password = passwordText.getText().toString();

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "SignInWithEmail:Success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            reload();
                        }else{
                            Log.w(TAG, "SignInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
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
        Intent intent = new Intent(Login.this, MainScreen.class);
        startActivity(intent);
        finish(); // Close the current activity
    }

}
