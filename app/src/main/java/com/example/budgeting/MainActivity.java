package com.example.budgeting;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);


        MaterialButton loginButton = findViewById(R.id.button2);
        MaterialButton signUpButton = findViewById(R.id.button3);

        // Set click listeners for login and signup buttons
        loginButton.setOnClickListener(v -> openLoginActivity());
        signUpButton.setOnClickListener(v -> openSignUpActivity());


    }


    private void openLoginActivity() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private void openSignUpActivity() {
        Intent intent = new Intent(this, Signup.class);
        startActivity(intent);
    }


}

