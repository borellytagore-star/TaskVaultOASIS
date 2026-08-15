package com.example.taskvault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;

    private Button btnRegister;
    private TextView tvLogin;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_register
        );

        databaseHelper =
                new DatabaseHelper(this);

        etName =
                findViewById(R.id.etName);

        etEmail =
                findViewById(R.id.etEmail);

        etPassword =
                findViewById(R.id.etPassword);

        btnRegister =
                findViewById(R.id.btnRegister);

        tvLogin =
                findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(
                v -> registerUser()
        );

        tvLogin.setOnClickListener(
                v -> finish()
        );
    }

    private void registerUser() {

        String name =
                etName.getText()
                        .toString()
                        .trim();

        String email =
                etEmail.getText()
                        .toString()
                        .trim();

        String password =
                etPassword.getText()
                        .toString();

        if (name.isEmpty()) {

            etName.setError(
                    "Enter your name"
            );

            etName.requestFocus();

            return;
        }

        if (email.isEmpty()) {

            etEmail.setError(
                    "Enter your email"
            );

            etEmail.requestFocus();

            return;
        }

        if (password.isEmpty()) {

            etPassword.setError(
                    "Enter your password"
            );

            etPassword.requestFocus();

            return;
        }

        if (password.length() < 6) {

            etPassword.setError(
                    "Password must contain at least 6 characters"
            );

            etPassword.requestFocus();

            return;
        }

        String passwordHash =
                hashPassword(password);

        android.content.ContentValues values =
                new android.content.ContentValues();

        values.put("name", name);
        values.put("email", email);
        values.put(
                "password_hash",
                passwordHash
        );

        long result =
                databaseHelper
                        .getWritableDatabase()
                        .insert(
                                "users",
                                null,
                                values
                        );

        if (result == -1) {

            Toast.makeText(
                    this,
                    "Registration failed. Email may already exist.",
                    Toast.LENGTH_LONG
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Registration successful!",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        }
    }

    private String hashPassword(
            String password) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            password.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder hexString =
                    new StringBuilder();

            for (byte b : hash) {

                String hex =
                        Integer.toHexString(
                                0xff & b
                        );

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}