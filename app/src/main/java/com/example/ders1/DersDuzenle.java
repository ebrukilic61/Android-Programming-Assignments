package com.example.ders1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class DersDuzenle extends AppCompatActivity {
    EditText dersAdi, dersKodu, stdSayisi, ogrGorevlisi, textViewYil, textViewGun, dersGrubu;
    Button guncelleBtn;
    CheckBox tamamlanmaDurumu;
    DatabaseReference dersRef, userRef;
    FirebaseUser currentUser;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ders_duzenle);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        dersRef = FirebaseDatabase.getInstance().getReference().child("dersler");
        userRef = FirebaseDatabase.getInstance().getReference().child("users_ogretmen").child(currentUser.getUid());

        dersAdi = findViewById(R.id.dersAdi1);
        dersKodu = findViewById(R.id.dersKodu1);
        stdSayisi = findViewById(R.id.stdSayisi1);
        ogrGorevlisi = findViewById(R.id.ogrGorevlisi1);
        textViewYil = findViewById(R.id.textViewYil1);
        textViewGun = findViewById(R.id.textViewGun1);
        dersGrubu = findViewById(R.id.dersGrubu1);
        guncelleBtn = findViewById(R.id.guncelleBtn);
        tamamlanmaDurumu = findViewById(R.id.tamamlanmaDurumu);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Kullanıcı öğretmen olarak yetkilendirilmemişse, ders eklemeye izin vermemeli:
                    guncelleBtn.setEnabled(false);
                    Toast.makeText(DersDuzenle.this, "Öğretmen yetkiniz bulunmamaktadır", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DersDuzenle.this, "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Dersin mevcut bilgilerini Firebase veritabanından alıp ilgili text alanlarına yerleştirmesi icin:
        dersRef.child("dersKodu").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String dersAdiText = dataSnapshot.child("dersAdi").getValue(String.class);
                    if (dersAdiText != null) {
                        dersAdi.setText(dersAdiText);
                    }
                    String dersKoduText = dataSnapshot.child("dersKodu").getValue(String.class);
                    if (dersKoduText != null) {
                        dersKodu.setText(dersKoduText);
                    }
                    String stdSayisiText = dataSnapshot.child("stdSayisi").getValue(String.class);
                    if (stdSayisiText != null) {
                        stdSayisi.setText(stdSayisiText);
                    }
                    String ogrGorevlisiText = dataSnapshot.child("ogretimGorevlisi").getValue(String.class);
                    if (ogrGorevlisiText != null) {
                        ogrGorevlisi.setText(ogrGorevlisiText);
                    }
                    String textViewYilText = dataSnapshot.child("yil").getValue(String.class);
                    if (textViewYilText != null) {
                        textViewYil.setText(textViewYilText);
                    }
                    String textViewGunText = dataSnapshot.child("gun").getValue(String.class);
                    if (textViewGunText != null) {
                        textViewGun.setText(textViewGunText);
                    }
                    String dersGrubuText = dataSnapshot.child("dersGrubu").getValue(String.class);
                    if (dersGrubuText != null) {
                        dersGrubu.setText(dersGrubuText);
                    }
                    //bunu bir daha kontrol edecegim:? tamamlama durumunu ders ekleme alanından silmistim cunku
                    String tamamlanmaDurumuText = dataSnapshot.child("tamamlamaDurumu").getValue(String.class);
                    if (tamamlanmaDurumuText == "Tamamlandı") {
                        tamamlanmaDurumu.setChecked(tamamlanmaDurumuText.equals("Tamamlandı"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DersDuzenle.this, "Ders bilgileri alınamıyor", Toast.LENGTH_SHORT).show();
            }
        });
        // Güncelle butonuna tıklandığında güncelleme işlemlerini gerçekleştirmesi icin:
        guncelleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText'ten girilen yeni değerleri alalım:
                String yeniDersAdi = dersAdi.getText().toString().trim();
                String yeniDersKodu = dersKodu.getText().toString().trim();
                String yeniStdSayisi = stdSayisi.getText().toString().trim();
                String yeniOgrGorevlisi = ogrGorevlisi.getText().toString().trim();
                String yeniYil = textViewYil.getText().toString().trim();
                String yeniGun = textViewGun.getText().toString().trim();
                String yeniDersGrubu = dersGrubu.getText().toString().trim();
                String yeniTamamlanmaDurumu = tamamlanmaDurumu.isChecked() ? "Tamamlandı" : "Devam Ediyor";

                // Firebase veritabanında güncelleme işlemleri
                dersRef.child("dersKodu").setValue(yeniDersKodu);
                dersRef.child("dersAdi").setValue(yeniDersAdi);
                dersRef.child("stdSayisi").setValue(yeniStdSayisi);
                dersRef.child("ogretimGorevlisi").setValue(yeniOgrGorevlisi);
                dersRef.child("yil").setValue(yeniYil);
                dersRef.child("dersiEkleyen").setValue(currentUser.getUid());
                dersRef.child("gun").setValue(yeniGun);
                dersRef.child("dersGrubu").setValue(yeniDersGrubu);
                dersRef.child("tamamlamaDurumu").setValue(yeniTamamlanmaDurumu)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dersRef.child("dersKodu").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String dersAdiText = dataSnapshot.child("dersAdi").getValue(String.class);
                                            if(dersAdiText != null){
                                                dersAdi.setText(dersAdiText);
                                            }
                                            String dersKoduText = dataSnapshot.child("dersKodu").getValue(String.class);
                                            if(dersKoduText != null){
                                                dersKodu.setText(dersKoduText);
                                            }
                                            String stdSayisiText = dataSnapshot.child("stdSayisi").getValue(String.class);
                                            if(stdSayisiText != null){
                                                stdSayisi.setText(stdSayisiText);
                                            }
                                            String ogrGorevlisiText = dataSnapshot.child("ogrGorevlisi").getValue(String.class);
                                            if(ogrGorevlisiText != null){
                                                ogrGorevlisi.setText(ogrGorevlisiText);
                                            }
                                            String textViewYilText = dataSnapshot.child("yil").getValue(String.class);
                                            if(textViewYilText != null){
                                                textViewYil.setText(textViewYilText);
                                            }
                                            String textViewGunText = dataSnapshot.child("gun").getValue(String.class);
                                            if(textViewGunText != null){
                                                textViewGun.setText(textViewGunText);
                                            }
                                            String dersGrubuText = dataSnapshot.child("dersGrubu").getValue(String.class);
                                            if(dersGrubuText != null){
                                                dersGrubu.setText(dersGrubuText);
                                            }
                                            //bunu bir daha kontrol edecegim:? tamamlama durumunu ders ekleme alanından silmistim cunku
                                            String tamamlanmaDurumuText = dataSnapshot.child("tamamlamaDurumu").getValue(String.class);
                                            if(tamamlanmaDurumuText == "Tamamlandı"){
                                                tamamlanmaDurumu.setChecked(tamamlanmaDurumuText.equals("Tamamlandı"));
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(DersDuzenle.this, "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                // Güncelleme başarılı olduğunda bir mesaj göster
                                Toast.makeText(DersDuzenle.this, "Ders başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Güncelleme başarısız olduğunda bir hata mesajı göster
                                Toast.makeText(DersDuzenle.this, "Ders güncellenirken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
