package com.example.ders1;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

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

public class SignupActivity extends AppCompatActivity  {

    EditText signupName, signupStdNo, signupEmail, signupPassword, confirmPassword, phoneNumber, departmentName, degreeName;
    TextView loginRedirectText;
    Button signupButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupStdNo = findViewById(R.id.signup_studentNo);
        signupPassword = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.signup_password2);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);
        phoneNumber = findViewById(R.id.phone_number);
        departmentName = findViewById(R.id.deptName);
        degreeName = findViewById(R.id.degreeName);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredentials();
            }
        });

        loginRedirectText.setOnClickListener(view -> {
            Log.d(TAG,"onClick: tiklandi");
            Intent intent = new Intent(SignupActivity.this,MainActivity.class);
            startActivity(intent);
        });
    }

    private void checkCredentials() {
        String name = signupName.getText().toString();
        String password = signupPassword.getText().toString();
        String passwordAgain = confirmPassword.getText().toString();
        String email = signupEmail.getText().toString();
        String studentNumber = signupStdNo.getText().toString();
        String dept = departmentName.getText().toString();
        String phone = phoneNumber.getText().toString();
        String degree = degreeName.getText().toString();

        if (studentNumber.length() != 8) {
            Toast.makeText(this, "Öğrenci numaranız 8 haneli olmalıdır", Toast.LENGTH_SHORT).show();
            signupStdNo.requestFocus();
        }
        else if (name.isEmpty()) {
            signupName.setError("Lütfen isminizi giriniz!");
            signupName.requestFocus();
        }else if (email.isEmpty() || !email.contains("@std.yildiz.edu.tr")||!email.contains("@")) {
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
                                    UserInfos writeUserInfo = new UserInfos(name, password, studentNumber, email, dept, phone, degree);

                                    DatabaseReference refProfile = FirebaseDatabase.getInstance().getReference("users"); //Registered users
                                    refProfile.child(user.getUid()).setValue(writeUserInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                user.sendEmailVerification();
                                                Toast.makeText(SignupActivity.this, "Kayıt işlemi başarıyla tamamlandı. Lütfen e-posta adresinizi doğrulayın.", Toast.LENGTH_LONG).show();

                                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish(); //Sign up activity'nin kapanması için yazdım bunu
                                            } else {
                                                Toast.makeText(SignupActivity.this, "Kullanıcı bilgileri kaydedilirken bir hata oluştu.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(SignupActivity.this, "Kullanıcı bilgileri alınamadı.", Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(SignupActivity.this, "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }
    }
}