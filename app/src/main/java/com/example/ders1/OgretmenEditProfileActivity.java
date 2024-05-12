package com.example.ders1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class OgretmenEditProfileActivity extends AppCompatActivity {

    EditText editFullName1, editEmail1, editDept1, editPhoneNo1, editSocial1_1, editSocial2_1;
    Button guncelBtn;
    DatabaseReference userRef;
    FirebaseUser currentUser;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ogretmen_edit_profile);

        // Firebase ve bileşenlerin referanslarını oluştur
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("users_ogretmen").child(currentUser.getUid());

        editFullName1 = findViewById(R.id.OEditNameSurname);
        editEmail1 = findViewById(R.id.OEditEmail);
        editDept1 = findViewById(R.id.OEditDepartment);
        editPhoneNo1 = findViewById(R.id.OPhoneNumber);
        editSocial1_1 = findViewById(R.id.Osocial_media);
        editSocial2_1 = findViewById(R.id.Osocial_media2);
        guncelBtn = findViewById(R.id.OGuncelleBtn2);

        // Kullanıcı bilgilerini veritabanından alıp ve EditText bileşenlerine yerleştir
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    if (name != null) {
                        editFullName1.setText(name);
                    }
                    String email = dataSnapshot.child("email").getValue(String.class);
                    if(email != null){
                        editEmail1.setText(email);
                    }
                    String dept = dataSnapshot.child("dept").getValue(String.class);
                    if(dept != null){
                        editDept1.setText(dept);
                    }
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    if(phone != null){
                        editPhoneNo1.setText(phone);
                    }
                    String socialMedia1 = dataSnapshot.child("linkedin").getValue(String.class);
                    if(socialMedia1 != null){
                        editSocial1_1.setText(socialMedia1);
                    }
                    String socialMedia2 = dataSnapshot.child("instagram").getValue(String.class);
                    if(socialMedia2 != null){
                        editSocial2_1.setText(socialMedia2);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OgretmenEditProfileActivity.this, "Bilgiler alınamıyor", Toast.LENGTH_SHORT).show();
            }
        });
        guncelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditTextlere girilen yeni değerleri alalım:
                String newName = editFullName1.getText().toString().trim();
                String newEmail = editEmail1.getText().toString().trim();
                String newDept = editDept1.getText().toString().trim();
                String newPhone = editPhoneNo1.getText().toString().trim();
                String newSocialMedia1 = editSocial1_1.getText().toString().trim();
                String newSocialMedia2 = editSocial2_1.getText().toString().trim();

                if (newName.isEmpty() || newEmail.isEmpty() || !newEmail.contains("@") || newDept.isEmpty()) {
                    Toast.makeText(OgretmenEditProfileActivity.this, "Lütfen gerekli bilgileri eksiksiz ve doğru şekilde girin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Yeni bilgileri Firebase veritabanında güncelle
                userRef.child("name").setValue(newName);
                userRef.child("email").setValue(newEmail);
                userRef.child("dept").setValue(newDept);
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
                                                editFullName1.setText(name);
                                            }
                                            String email = dataSnapshot.child("email").getValue(String.class);
                                            if(email != null){
                                                editEmail1.setText(email);
                                            }
                                            String dept = dataSnapshot.child("dept").getValue(String.class);
                                            if(dept != null){
                                                editDept1.setText(dept);
                                            }
                                            String phone = dataSnapshot.child("phone").getValue(String.class);
                                            if(phone != null){
                                                editPhoneNo1.setText(phone);
                                            }
                                            String socialMedia1 = dataSnapshot.child("linkedin").getValue(String.class);
                                            if(socialMedia1 != null){
                                                editSocial1_1.setText(socialMedia1);
                                            }
                                            String socialMedia2 = dataSnapshot.child("instagram").getValue(String.class);
                                            if(socialMedia2 != null){
                                                editSocial2_1.setText(socialMedia2);
                                            }
                                            // Başarıyla güncellendiğine dair bir mesaj göster
                                            Toast.makeText(OgretmenEditProfileActivity.this, "Bilgileriniz başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(OgretmenEditProfileActivity.this,OgretmenProfileActivity.class);
                                        }
                                        //return false;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Hata durumunda kullanıcıya bilgi verebilirsiniz
                                        Toast.makeText(OgretmenEditProfileActivity.this, "Bilgiler alınamıyor", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                // Başarıyla güncellendiğine dair bir mesaj göster
                                Toast.makeText(OgretmenEditProfileActivity.this, "Bilgileriniz başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(OgretmenEditProfileActivity.this,OgretmenProfileActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Güncelleme başarısız olduğunda bir hata mesajı göster
                                Toast.makeText(OgretmenEditProfileActivity.this, "Bilgilerinizi güncellerken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }

        });

    }
}