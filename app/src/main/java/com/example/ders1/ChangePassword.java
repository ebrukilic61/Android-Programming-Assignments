package com.example.ders1;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePassword extends AppCompatActivity {
    EditText oldPass, passNew, passNewAgain;
    Button pswGuncelle;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        oldPass = findViewById(R.id.OldPassword);
        passNew = findViewById(R.id.PasswordNew);
        passNewAgain = findViewById(R.id.PasswordNewAgain);
        pswGuncelle = findViewById(R.id.buttonPassGuncelle);

        //veritabanı islemleri:
        auth = FirebaseAuth.getInstance(); // FirebaseAuth'ı başlatın
        FirebaseUser currentUser = auth.getCurrentUser();
        DatabaseReference usrRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

        pswGuncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPsw = passNew.getText().toString();
                String oldPsw = oldPass.getText().toString();
                String newPswAgn = passNewAgain.getText().toString();

                if (TextUtils.isEmpty(newPsw) || TextUtils.isEmpty(oldPsw)) {
                    Toast.makeText(ChangePassword.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                }
                if(newPsw.equals(oldPsw)){
                    Toast.makeText(ChangePassword.this,"Yeni şifreniz eskisiyle aynı", Toast.LENGTH_LONG).show();
                }
                if (newPsw.length() < 8) {
                    Toast.makeText(ChangePassword.this, "Yeni şifre en az 9 karakter olmalıdır", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!newPsw.equals(newPswAgn)) {
                    Toast.makeText(ChangePassword.this, "Yeni şifreler eşleşmiyor", Toast.LENGTH_LONG).show();
                    return;
                }
                currentUser.updatePassword(newPsw).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Firebase veritabanındaki şifreyi güncelleyin
                            usrRef.child("password").setValue(newPsw).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ChangePassword.this, "Şifre başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ChangePassword.this, "Şifre güncellenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ChangePassword.this, "Şifre güncellenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
