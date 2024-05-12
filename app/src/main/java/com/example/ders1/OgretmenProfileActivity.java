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

public class OgretmenProfileActivity extends AppCompatActivity {
    TextView EditName, EditMail, EditDept, phoneNo, socialMedia, socialMedia2, EditUnvan;
    ImageButton EditBtn;
    FirebaseAuth auth;
    DatabaseReference userRef;
    FirebaseUser currentUser;
    ImageView imgView;
    FloatingActionButton fltButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ogretmen_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.lavender)));
        }
        imgView = findViewById(R.id.userIconOgr);
        fltButton = findViewById(R.id.floatingActionButtonOgr);
        //galeri islemi icin githubta buldugum bir library'den faydalandim
        fltButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(OgretmenProfileActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("users_ogretmen").child(currentUser.getUid());

        EditName = findViewById(R.id.EditNameSurnameOgr);
        EditMail = findViewById(R.id.EditEmailOgr);
        EditDept = findViewById(R.id.EditDepartmentOgr);
        phoneNo = findViewById(R.id.phoneNumberOgr);
        socialMedia = findViewById(R.id.social_mediaOgr);
        socialMedia2 = findViewById(R.id.social_media2Ogr);
        EditBtn = findViewById(R.id.EditBtnOgr);
        EditUnvan = findViewById(R.id.EditUnvan);

        // Firebase'den kullanıcı bilgilerini alıp edit textlere yerleştirdim
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String dept = dataSnapshot.child("dept").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String sosMedia = dataSnapshot.child("linkedin").getValue(String.class);
                    String sosMedia2 = dataSnapshot.child("instagram").getValue(String.class);
                    String unvan = dataSnapshot.child("user_roles").getValue(String.class);

                    EditName.setText(name);
                    EditMail.setText(email);
                    EditDept.setText(dept);
                    phoneNo.setText(phone);
                    socialMedia.setText(sosMedia);
                    socialMedia2.setText(sosMedia2);
                    EditUnvan.setText(unvan);
                }

                // Kullanıcının rolünü belirleme işlemi
                if (currentUser != null) {
                    String email = currentUser.getEmail();
                    // Eğer e-posta adresi @yildiz.edu.tr ile bitiyorsa öğretim görevlisidir
                    if (email != null && email.endsWith("@yildiz.edu.tr")) {
                        setUserRole("Öğretim Görevlisi");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hata mesajını logcat'e yazdır
                Log.e("Firebase Error", "Veritabanı Hatası: " + databaseError.getMessage());
                // Hata kodunu da yazdırabilirsiniz
                Log.e("Firebase Error", "Hata Kodu: " + databaseError.getCode());
                Toast.makeText(OgretmenProfileActivity.this, "Veriler alınamıyor", Toast.LENGTH_SHORT).show();
            }
        });

        //email uygulamasına yönlendirme:
        EditMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = EditMail.getText().toString();
                // E-posta adresi geçerliyse e-posta uygulaması açılır:
                if (!emailAddress.isEmpty()) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
                    if (emailIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(emailIntent);
                    } else {
                        // Eğer uygun bir e-posta uygulaması bulunamazsa:
                        Toast.makeText(OgretmenProfileActivity.this, "E-posta uygulaması bulunamadı.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Düzenle butonu ile düzenleme sayfasına yönlenecek
        EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OgretmenProfileActivity.this, OgretmenEditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

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

    //galeri ve kamera islemi icin
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
                        Toast.makeText(OgretmenProfileActivity.this, "Fotoğraf yüklenirken hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
