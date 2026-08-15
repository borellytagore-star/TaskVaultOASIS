package com.example.taskvault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;

    private Button btnLogin;
    private TextView tvRegister;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_login
        );

        databaseHelper =
                new DatabaseHelper(this);

        etEmail =
                findViewById(R.id.etEmail);

        etPassword =
                findViewById(R.id.etPassword);

        btnLogin =
                findViewById(R.id.btnLogin);

        tvRegister =
                findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(
                v -> loginUser()
        );

        tvRegister.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    LoginActivity.this,
                                    RegisterActivity.class
                            );

                    startActivity(intent);
                }
        );
    }

    private void loginUser() {

        String email =
                etEmail.getText()
                        .toString()
                        .trim();

        String password =
                etPassword.getText()
                        .toString();

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

        String passwordHash =
                hashPassword(password);

        Cursor cursor =
                databaseHelper
                        .getReadableDatabase()
                        .query(
                                "users",
                                new String[]{
                                        "id",
                                        "password_hash"
                                },
                                "email = ?",
                                new String[]{
                                        email
                                },
                                null,
                                null,
                                null
                        );

        if (cursor.moveToFirst()) {

            int userId =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "id"
                            )
                    );

            String storedHash =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "password_hash"
                            )
                    );

            if (passwordHash.equals(storedHash)) {

                // Save session
                SharedPreferences preferences =
                        getSharedPreferences(
                                "TaskVaultSession",
                                MODE_PRIVATE
                        );

                preferences.edit()
                        .putInt("user_id", userId)
                        .apply();

                cursor.close();

                Toast.makeText(
                        this,
                        "Login successful!",
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent =
                        new Intent(
                                LoginActivity.this,
                                MainActivity.class
                        );

                startActivity(intent);

                finish();

            } else {

                cursor.close();

                Toast.makeText(
                        this,
                        "Incorrect password",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {

            cursor.close();

            Toast.makeText(
                    this,
                    "Account not found",
                    Toast.LENGTH_SHORT
            ).show();
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