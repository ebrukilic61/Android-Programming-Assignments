package com.example.ders1;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity{
    private TextView EditName, EditStuNo, EditMail, EditDept, phoneNo, EditDeg, socialMedia, socialMedia2, rolText;
    private ImageButton EditBtn;
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private ImageView imgView;
    private FloatingActionButton fltButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_duzenle);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.lavender)));
        }
        imgView = findViewById(R.id.userIcon);
        fltButton = findViewById(R.id.floatingActionButton);
        //galeri islemi icin githubta buldugum bir library'den faydalandim
        fltButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ProfileActivity.this) //ImagePicker modülünün özellikleri
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

        EditName = findViewById(R.id.EditNameSurname);
        EditStuNo = findViewById(R.id.EditStudentNo);
        EditMail = findViewById(R.id.EditEmail);
        EditDept = findViewById(R.id.EditDepartment);
        EditDeg = findViewById(R.id.EditDegree);
        phoneNo = findViewById(R.id.phoneNumber);
        socialMedia = findViewById(R.id.social_media);
        socialMedia2 = findViewById(R.id.social_media2);
        EditBtn = findViewById(R.id.EditBtn);
        rolText = findViewById(R.id.rolText);

        // Firebase'den kullanıcı bilgilerini alıp edit textlere yerleştirdik
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String stuNo = dataSnapshot.child("studentNumber").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String dept = dataSnapshot.child("dept").getValue(String.class);
                    String degree = dataSnapshot.child("degree").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String sosMedia = dataSnapshot.child("linkedin").getValue(String.class);
                    String sosMedia2 = dataSnapshot.child("instagram").getValue(String.class);
                    String rol = dataSnapshot.child("user_roles").getValue(String.class);

                    EditName.setText(name);
                    EditStuNo.setText(stuNo);
                    EditMail.setText(email);
                    EditDept.setText(dept);
                    EditDeg.setText(degree);
                    phoneNo.setText(phone);
                    socialMedia.setText(sosMedia);
                    socialMedia2.setText(sosMedia2);
                    rolText.setText(rol);

                }
                // Kullanıcının rolünü belirleme işlemi
                if (currentUser != null) {
                    String email = currentUser.getEmail();
                    // Eğer e-posta adresi @std.yildiz.edu.tr ile bitiyorsa öğrencidir
                    if (email != null && email.endsWith("@std.yildiz.edu.tr")) {
                        setUserRole("Öğrenci");
                    }
                }

                // Profil fotoğrafı ekleme
                if (dataSnapshot.child("profileImageURL").exists()) {
                    String imageURL = dataSnapshot.child("profileImageURL").getValue(String.class);
                    Glide.with(ProfileActivity.this).load(imageURL).into(imgView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hata mesajını logcat'e yazdır
                Log.e("Firebase Error", "Veritabanı Hatası: " + databaseError.getMessage());
                // Hata kodunu da yazdırabilirsiniz
                Log.e("Firebase Error", "Hata Kodu: " + databaseError.getCode());
                Toast.makeText(ProfileActivity.this, "Veriler alınamıyor", Toast.LENGTH_SHORT).show();
            }
        });

        //email uygulamasına yönlendirme:
        EditMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = EditMail.getText().toString();
                // E-posta adresi geçerliyse e-posta uygulaması açılır:
                if (!emailAddress.isEmpty()) {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
                    if (emailIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(Intent.createChooser(emailIntent, "E-posta uygulaması seçin"));
                    } else {
                        // Eğer uygun bir e-posta uygulaması bulunamazsa:
                        Toast.makeText(ProfileActivity.this, "E-posta uygulaması bulunamadı.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        phoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneNo.getText().toString();
                if (!phone.isEmpty()) {
                    // Telefon numarası başında 0 varsa, 0'ı kaldırarak URL'yi oluşturur
                    if (phone.startsWith("0")) {
                        phone = phone.substring(1);
                    }
                    // WhatsApp URL'sini oluşturur
                    Uri uri = Uri.parse("https://wa.me/90" + phone);
                    Intent whatsappIntent = new Intent(Intent.ACTION_VIEW, uri);
                    whatsappIntent.setPackage("com.whatsapp"); // WhatsApp uygulamasını belirtir
                    if (whatsappIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(whatsappIntent);
                    } else {
                        Toast.makeText(ProfileActivity.this, "WhatsApp uygulaması bulunamadı.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        // Düzenle butonu ile düzenleme sayfasına yönlenecek
        EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    // Galeri işlemi
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            imgView.setImageURI(uri);
            uploadImageToDB(uri);
        }
    }

    private void uploadImageToDB(Uri imageUri) {
        // kullanıcı id'sini alalım:
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference()
                .child("profile_images")
                .child(userId)
                .child("profile.jpg");

        // seçilen fotoğrafı firebase storage'a yüklüyorum:
        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // fotoğrafın indirme url'si
                        profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //url to string
                                String imageURL = uri.toString();

                                // Alınan URL'yi Firebase Realtime Database'e kaydedelim
                                userRef.child("profileImageURL").setValue(imageURL);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Fotoğraf URL'sini alma başarısız: " + e.getMessage());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // yükleme islemi basarısız
                        Log.e(TAG, "Fotoğraf yüklenirken hata oluştu: " + e.getMessage());
                        Toast.makeText(ProfileActivity.this, "Fotoğraf yüklenirken hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Kullanıcı rolünü belirleme
    private void setUserRole(String role) {
        Map<String, Object> roleUpdate = new HashMap<>();
        roleUpdate.put("user_roles", role);
        userRef.updateChildren(roleUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Kullanıcı rolü başarıyla güncellendi.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Kullanıcı rolü güncellenirken hata oluştu: " + e.getMessage());
                    }
                });
    }

}