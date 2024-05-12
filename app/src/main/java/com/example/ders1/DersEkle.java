package com.example.ders1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DersEkle extends AppCompatActivity {
    EditText dersAdi, dersKodu, stdSayisi, ogrGorevlisi, textViewYil, textViewGun, dersGrubu;
    Button dersEkle;
    private FirebaseAuth auth;
    private DatabaseReference dersRef, userRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ders_ekle);

        dersAdi = findViewById(R.id.dersAdi);
        dersKodu = findViewById(R.id.dersKodu);
        stdSayisi = findViewById(R.id.stdSayisi);
        ogrGorevlisi = findViewById(R.id.ogrGorevlisi);
        textViewYil = findViewById(R.id.textViewYilEkle);
        textViewGun = findViewById(R.id.textViewGun);
        dersGrubu = findViewById(R.id.dersGrubu);
        dersEkle = findViewById(R.id.btnEkle);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        dersRef = FirebaseDatabase.getInstance().getReference().child("dersler");
        userRef = FirebaseDatabase.getInstance().getReference().child("users_ogretmen").child(currentUser.getUid());

        // Kullanıcının öğretmen olarak yetkilendirilip yetkilendirilmediğini kontrol edelim:
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Kullanıcı öğretmen olarak yetkilendirilmemişse, ders eklemeye izin vermemeli:
                    dersEkle.setEnabled(false);
                    Toast.makeText(DersEkle.this, "Öğretmen yetkiniz bulunmamaktadır", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DersEkle.this, "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dersEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dersAdiText = dersAdi.getText().toString().trim();
                String dersKoduText = dersKodu.getText().toString().trim();
                String stdSayisiText = stdSayisi.getText().toString().trim();
                String ogrGorevlisiText = ogrGorevlisi.getText().toString().trim();
                String YilText = textViewYil.getText().toString().trim();
                String gunText = textViewGun.getText().toString().trim();
                String dersGR = dersGrubu.getText().toString().trim();

                // Veri doğrulama işlemleri
                if (dersAdiText.isEmpty() || dersKoduText.isEmpty() || stdSayisiText.isEmpty()) {
                    Toast.makeText(DersEkle.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                    return;
                }
                int stdSayisiInt;
                try {
                    stdSayisiInt = Integer.parseInt(stdSayisiText);
                    if (stdSayisiInt <= 0) {
                        Toast.makeText(DersEkle.this, "Öğrenci sayısı geçersiz", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(DersEkle.this, "Öğrenci sayısı geçersiz", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase veritabanına dersi eklemek için:
                dersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            boolean duplicateFound = false;
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                String existingGroup = childSnapshot.child("dersGrubu").getValue(String.class);
                                String existingCode = childSnapshot.child("dersKodu").getValue(String.class);
                                if (existingGroup != null && existingCode != null && existingGroup.equals(dersGR) && existingCode.equals(dersKoduText)) {
                                    // duplication var, mesaj yazdır ve kaydetme bu veriyi
                                    Toast.makeText(DersEkle.this, "Bu ders kodu ve grubu zaten kullanılıyor", Toast.LENGTH_SHORT).show();
                                    duplicateFound = true;
                                    break;
                                }
                            }
                            if (!duplicateFound) {
                                // Eğer duplication yoksa dersi ekle
                                DatabaseReference yeniDersRef = dersRef.push();
                                yeniDersRef.child("dersAdi").setValue(dersAdiText);
                                yeniDersRef.child("dersKodu").setValue(dersKoduText);
                                yeniDersRef.child("stdSayisi").setValue(stdSayisiText);
                                yeniDersRef.child("ogretimGorevlisi").setValue(ogrGorevlisiText);
                                yeniDersRef.child("dersiEkleyen").setValue(currentUser.getUid());
                                yeniDersRef.child("gun").setValue(gunText);
                                yeniDersRef.child("yil").setValue(YilText);
                                yeniDersRef.child("dersGrubu").setValue(dersGR);
                                Toast.makeText(DersEkle.this, "Ders başarıyla eklendi", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DersEkle.this, CourseListActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            // Eğer snapshot boşsa, veri tabanına kaydet
                            DatabaseReference yeniDersRef = dersRef.push();
                            yeniDersRef.child("dersAdi").setValue(dersAdiText);
                            yeniDersRef.child("dersKodu").setValue(dersKoduText);
                            yeniDersRef.child("stdSayisi").setValue(stdSayisiText);
                            yeniDersRef.child("ogretimGorevlisi").setValue(ogrGorevlisiText);
                            yeniDersRef.child("dersiEkleyen").setValue(currentUser.getUid());
                            yeniDersRef.child("gun").setValue(gunText);
                            yeniDersRef.child("yil").setValue(YilText);
                            yeniDersRef.child("dersGrubu").setValue(dersGR);
                            Toast.makeText(DersEkle.this, "Ders başarıyla eklendi", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DersEkle.this, CourseListActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Hata durumunda kullanıcıyı uyar
                        Toast.makeText(DersEkle.this, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}

