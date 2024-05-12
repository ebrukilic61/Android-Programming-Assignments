package com.example.ders1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    EditText editFullName, editStuNo, editEmail, editDept, editDeg, editPhoneNo, editSocial1, editSocial2;
    Switch switchTel, switchLink, switchInsta;
    Button guncelBtn;
    DatabaseReference userRef;
    FirebaseUser currentUser;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_duzenle);

        // Firebase ve bileşenlerin referanslarını oluştur
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

        editFullName = findViewById(R.id.AEditNameSurname);
        editStuNo = findViewById(R.id.AEditStudentNo);
        editEmail = findViewById(R.id.AEditEmail);
        editDept = findViewById(R.id.AEditDepartment);
        editDeg = findViewById(R.id.AEditDegree);
        editPhoneNo = findViewById(R.id.APhoneNumber);
        editSocial1 = findViewById(R.id.Asocial_media);
        editSocial2 = findViewById(R.id.Asocial_media2);

        guncelBtn = findViewById(R.id.GuncelleBtn);
        switchTel = findViewById(R.id.switchTel);
        switchLink = findViewById(R.id.switchLink);
        switchInsta = findViewById(R.id.switchInsta);

        // Kullanıcı bilgilerini veritabanından alıp ve EditText bileşenlerine yerleştir
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    if (name != null) {
                        editFullName.setText(name);
                    }
                    String stuNo = dataSnapshot.child("studentNumber").getValue(String.class);
                    if(stuNo != null){
                        editStuNo.setText(stuNo);
                    }
                    String email = dataSnapshot.child("email").getValue(String.class);
                    if(email != null){
                        editEmail.setText(email);
                    }
                    String dept = dataSnapshot.child("dept").getValue(String.class);
                    if(dept != null){
                        editDept.setText(dept);
                    }
                    String degree = dataSnapshot.child("degree").getValue(String.class);
                    if(degree != null){
                        editDeg.setText(degree);
                    }
                    switchTel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            String phone_one = "**** *** ** **";
                            if(isChecked){
                                //OFF
                                editPhoneNo.setText(phone_one);
                            }else{
                                //ON
                                String phone = dataSnapshot.child("phone").getValue(String.class);
                                if(phone != null){
                                    editPhoneNo.setText(phone);
                                }
                            }
                        }
                    });
                    String socialMedia1 = dataSnapshot.child("linkedin").getValue(String.class);
                    if(socialMedia1 != null){
                        editSocial1.setText(socialMedia1);
                    }
                    String socialMedia2 = dataSnapshot.child("instagram").getValue(String.class);
                    if(socialMedia2 != null){
                        editSocial2.setText(socialMedia2);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this, "Bilgiler alınamıyor", Toast.LENGTH_SHORT).show();
            }
        });

        guncelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditTextlere girilen yeni değerleri alalım:
                String newName = editFullName.getText().toString().trim();
                String newStudentNumber = editStuNo.getText().toString().trim();
                String newEmail = editEmail.getText().toString().trim();
                String newDept = editDept.getText().toString().trim();
                String newDegree = editDeg.getText().toString().trim();
                String newPhone = editPhoneNo.getText().toString().trim();
                String newSocialMedia1 = editSocial1.getText().toString().trim();
                String newSocialMedia2 = editSocial2.getText().toString().trim();

                if (newName.isEmpty() || newStudentNumber.length() != 8 || newEmail.isEmpty() || !newEmail.contains("@") || newDept.isEmpty() ||
                        newDegree.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Lütfen gerekli bilgileri eksiksiz ve doğru şekilde girin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Yeni bilgileri Firebase veritabanında güncelle
                userRef.child("name").setValue(newName);
                userRef.child("studentNumber").setValue(newStudentNumber);
                userRef.child("email").setValue(newEmail);
                userRef.child("dept").setValue(newDept);
                userRef.child("degree").setValue(newDegree);
                userRef.child("phone").setValue(newPhone);
                userRef.child("linkedin").setValue(newSocialMedia1);
                userRef.child("instagram").setValue(newSocialMedia2)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Güncelleme başarılı olduğunda profil bilgilerini tekrar alıp EditText'lere yerleştir
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // Yeniden alınan verilerle EditText bileşenlerini güncelle
                                            String name = dataSnapshot.child("name").getValue(String.class);
                                            if (name != null) {
                                                editFullName.setText(name);
                                            }
                                            String stuNo = dataSnapshot.child("studentNumber").getValue(String.class);
                                            if(stuNo != null){
                                                editStuNo.setText(stuNo);
                                            }
                                            String email = dataSnapshot.child("email").getValue(String.class);
                                            if(email != null){
                                                editEmail.setText(email);
                                            }
                                            String dept = dataSnapshot.child("dept").getValue(String.class);
                                            if(dept != null){
                                                editDept.setText(dept);
                                            }
                                            String degree = dataSnapshot.child("degree").getValue(String.class);
                                            if(degree != null){
                                                editDeg.setText(degree);
                                            }
                                            switchTel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    String phone_one = "**** *** ** **";
                                                    if(isChecked){
                                                        //OFF
                                                        editPhoneNo.setText(phone_one);
                                                    }else{
                                                        //ON
                                                        String phone = dataSnapshot.child("phone").getValue(String.class);
                                                        if(phone != null){
                                                            editPhoneNo.setText(phone);
                                                        }
                                                    }
                                                }
                                            });
                                            String socialMedia1 = dataSnapshot.child("linkedin").getValue(String.class);
                                            if(socialMedia1 != null){
                                                editSocial1.setText(socialMedia1);
                                            }
                                            String socialMedia2 = dataSnapshot.child("instagram").getValue(String.class);
                                            if(socialMedia2 != null){
                                                editSocial2.setText(socialMedia2);
                                            }
                                            // Başarıyla güncellendiğine dair bir mesaj göster
                                            Toast.makeText(EditProfileActivity.this, "Bilgileriniz başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(EditProfileActivity.this,ProfileActivity.class);
                                            startActivity(intent);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Hata durumunda kullanıcıya bilgi verebilirsiniz
                                        Toast.makeText(EditProfileActivity.this, "Bilgiler alınamıyor", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                // Başarıyla güncellendiğine dair bir mesaj göster
                                Toast.makeText(EditProfileActivity.this, "Bilgileriniz başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditProfileActivity.this,ProfileActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Güncelleme başarısız olduğunda bir hata mesajı göster
                                Toast.makeText(EditProfileActivity.this, "Bilgilerinizi güncellerken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });
    }

}