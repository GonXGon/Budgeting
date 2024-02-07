package com.example.budgeting;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfile extends AppCompatActivity {
    private EditText currentPasswordReset;
    private EditText newPasswordReset;
    private FirebaseAuth auth;  // Initialize the FirebaseAuth variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userpage);

        currentPasswordReset = findViewById(R.id.currentPasswordReset);
        newPasswordReset = findViewById(R.id.newPasswordReset);
        Button resetPass = findViewById(R.id.passwordResetbtn);

        auth = FirebaseAuth.getInstance();

        resetPass.setOnClickListener(view ->{
            FirebaseUser user = auth.getCurrentUser();

            String currentPassword = currentPasswordReset.getText().toString().trim();
            String newPassword = newPasswordReset.getText().toString().trim();

            if(TextUtils.isEmpty(currentPassword)){
                Toast.makeText(getApplicationContext(), "Enter your current password", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(user.getEmail(), currentPassword)
                    .addOnCompleteListener(this, task -> {
                        if(task.isSuccessful()){
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(task1 ->{
                                        if(task1.isSuccessful()){
                                            Toast.makeText(getApplicationContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else{
                            Toast.makeText(getApplicationContext(), "Authentication failed. Check your current password.", Toast.LENGTH_SHORT).show();
                        }
                    });

        });

    }

}
