package com.example.ders1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class OgretmenLoginActivity extends AppCompatActivity {
    FirebaseAuth auth;
    EditText loginEmail, loginPassword;
    TextView signupRedirecttext, forgetPassword, ogrenciGirisi;
    Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ogretmen_login);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.editTextEmail2);
        loginPassword = findViewById(R.id.editTextPassword2);
        btn2 = findViewById(R.id.button2_2);
        signupRedirecttext = findViewById(R.id.toSignupRedirectText2);
        forgetPassword = findViewById(R.id.changePass2);
        ogrenciGirisi = findViewById(R.id.ogrenciGirisi);

        btn2.setOnClickListener(v -> {
            String email = loginEmail.getText().toString();
            String password = loginPassword.getText().toString();

            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!password.isEmpty()) {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(authResult -> {
                                Toast.makeText(OgretmenLoginActivity.this, "Giriş Yapılıyor", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(OgretmenLoginActivity.this, OgretmenMenuActivity.class);
                                startActivity(intent);
                                finish();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(OgretmenLoginActivity.this, "Giriş Yapılamıyor", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(OgretmenLoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            });
                } else {
                    loginPassword.setError("Şifre boş bırakılamaz");
                }
            } else if (email.isEmpty()) {
                loginEmail.setError("email alanı boş bırakılamaz");
            } else {
                loginEmail.setError("Geçerli bir email giriniz");
            }
        });
        signupRedirecttext.setOnClickListener(view -> startActivity(new Intent(OgretmenLoginActivity.this, OgretmenSignupActivity.class)));
        forgetPassword.setOnClickListener(view -> startActivity(new Intent(OgretmenLoginActivity.this, ResetPassword.class)));
        ogrenciGirisi.setOnClickListener(view -> startActivity(new Intent(OgretmenLoginActivity.this, MainActivity.class)));
    }
}