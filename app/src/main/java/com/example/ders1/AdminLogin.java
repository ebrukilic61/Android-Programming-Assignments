package com.example.ders1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminLogin extends AppCompatActivity {
    EditText adminMail, adminPsw;
    Button LoginBtn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        adminMail = findViewById(R.id.adminMail);
        adminPsw = findViewById(R.id.adminPsw);
        LoginBtn = findViewById(R.id.LoginBtn);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = adminMail.getText().toString();
                String password = adminPsw.getText().toString();
                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!password.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener(authResult -> {
                                    Toast.makeText(AdminLogin.this, "Giriş Yapılıyor", Toast.LENGTH_SHORT).show();
                                    //Intent intent = new Intent(MainActivity.this, CourseListActivity.class);
                                    Intent intent = new Intent(AdminLogin.this, MenuActivity.class);
                                    startActivity(intent);
                                    finish();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(AdminLogin.this, "Giriş Yapılamıyor, Girdiğiniz Bilgiler Hatalı", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AdminLogin.this, AdminLogin.class);
                                    startActivity(intent);
                                    finish();
                                });
                    } else {
                        adminPsw.setError("Şifre boş bırakılamaz");
                    }
                }else {
                    adminMail.setError("Geçerli bir email giriniz");
                }

            }
        });
    }
}