package com.example.ders1;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OgretmenSignupActivity extends AppCompatActivity {
    EditText signupName, signupEmail, signupDepartment, signupPassword, confirmPassword, signupPhoneNum;
    TextView loginRedirectText;
    Button signupButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ogretmen_signup);

        auth = FirebaseAuth.getInstance();
        signupName = findViewById(R.id.signup_name2);
        signupEmail = findViewById(R.id.signup_email2);
        signupDepartment = findViewById(R.id.deptName2);
        signupPassword = findViewById(R.id.signup_password2);
        confirmPassword = findViewById(R.id.signup_password3);
        loginRedirectText = findViewById(R.id.loginRedirectText2);
        signupButton = findViewById(R.id.signup_button2);
        signupPhoneNum = findViewById(R.id.phone_number2);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredentials();
            }
        });

        loginRedirectText.setOnClickListener(view -> {
            Log.d(TAG, "onClick: tiklandi");
            Intent intent = new Intent(OgretmenSignupActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void checkCredentials() {
        String name = signupName.getText().toString();
        String password = signupPassword.getText().toString();
        String passwordAgain = confirmPassword.getText().toString();
        String email = signupEmail.getText().toString();
        String dept = signupDepartment.getText().toString();
        String phone = signupPhoneNum.getText().toString();

        if (name.isEmpty()) {
            signupName.setError("Lütfen isminizi giriniz!");
            signupName.requestFocus();
        }else if (email.isEmpty() || !email.contains("@yildiz.edu.tr")) {
            signupEmail.setError("Lütfen yildiz uzantılı mail adresinizi giriniz");
            signupEmail.requestFocus();
        } else if (password.isEmpty() || password.length() < 9) {
            signupPassword.setError("Şifreniz en az 9 haneli olmalıdır");
            signupPassword.requestFocus();
        } else if (passwordAgain.isEmpty()) {
            confirmPassword.setError("Şifrenizi doğrulamak için tekrardan giriniz");
            confirmPassword.requestFocus();
        } else if (!passwordAgain.equals(password)) {
            confirmPassword.setError("Şifreler aynı değil");
            confirmPassword.requestFocus();
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                if (user != null) {
                                    UserInfos writeUserInfo = new UserInfos(name, password, email, dept, phone);

                                    DatabaseReference refProfile = FirebaseDatabase.getInstance().getReference("users_ogretmen"); //users_ogretmen tablosuna kaydoldu
                                    refProfile.child(user.getUid()).setValue(writeUserInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                user.sendEmailVerification();
                                                Toast.makeText(OgretmenSignupActivity.this, "Kayıt işlemi başarıyla tamamlandı. Lütfen e-posta adresinizi doğrulayın.", Toast.LENGTH_LONG).show();

                                                Intent intent = new Intent(OgretmenSignupActivity.this, OgretmenLoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish(); //Sign up activity'nin kapanması için yazdım bunu
                                            } else {
                                                Toast.makeText(OgretmenSignupActivity.this, "Kullanıcı bilgileri kaydedilirken bir hata oluştu.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(OgretmenSignupActivity.this, "Kullanıcı bilgileri alınamadı.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    signupPassword.setError("Şifreniz çok zayıf");
                                    signupPassword.requestFocus();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    signupEmail.setError("Geçersiz email");
                                } catch (Exception e) {
                                    Toast.makeText(OgretmenSignupActivity.this, "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }
    }
}