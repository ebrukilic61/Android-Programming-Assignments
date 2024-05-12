package com.example.ders1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    EditText loginEmail, loginPassword;
    ImageButton adminButton;
    TextView signupRedirectText, forgetPassword, egitmenGirisi;
    Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance(); //authentication işlemi için firebase'in FirebaseAuth özelliğini kullandım
        loginEmail = findViewById(R.id.editTextEmail);
        loginPassword = findViewById(R.id.editTextPassword);
        btn2 = findViewById(R.id.button2);
        signupRedirectText = findViewById(R.id.toSignupRedirectText);
        forgetPassword = findViewById(R.id.changePass);
        egitmenGirisi = findViewById(R.id.egitmenGirisi);
        adminButton = findViewById(R.id.adminButton);

        btn2.setOnClickListener(v -> {
            String email = loginEmail.getText().toString();
            String password = loginPassword.getText().toString();
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() &&email.contains("@std.yildiz.edu.tr")) {
                if (!password.isEmpty()) {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(authResult -> {
                                Toast.makeText(MainActivity.this, "Giriş Yapılıyor", Toast.LENGTH_SHORT).show();
                                //Intent intent = new Intent(MainActivity.this, CourseListActivity.class);
                                Intent intent = new Intent(MainActivity.this, MenuActivity2.class);
                                startActivity(intent);
                                finish();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(MainActivity.this, "Giriş Yapılamıyor, Girdiğiniz Bilgiler Hatalı", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            });
                } else {
                    loginPassword.setError("Şifre boş bırakılamaz");
                }
            }else {
                loginEmail.setError("Geçerli bir email giriniz");
            }
        });
        signupRedirectText.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SignupActivity.class)));
        forgetPassword.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ResetPassword.class)));
        egitmenGirisi.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, OgretmenLoginActivity.class))); //simdilik gecici versiyonu yazdim yildiz uzantili mail adresim olmadigi icin
        adminButton.setOnClickListener(View -> startActivity(new Intent(MainActivity.this,AdminLogin.class)));
    }
}
