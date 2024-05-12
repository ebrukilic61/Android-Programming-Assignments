package com.example.ders1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OgrenciListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference db;
    FirebaseUser currentUser;
    StudentListAdapter myAdapter;
    ArrayList<UserInfos> ogrList;
    ArrayList<Dersler> derslerArrayList;
    TextView detailedDersKodu, detailedDersAdi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_detailed);

        recyclerView = findViewById(R.id.ogrRecyclerView);
        db = FirebaseDatabase.getInstance().getReference("kullanici_dersleri");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ogrList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new StudentListAdapter(this, ogrList);
        recyclerView.setAdapter(myAdapter);
        //myAdapter.setDeleteEnabled(true);

        detailedDersKodu = findViewById(R.id.detailedDersKodu);
        detailedDersAdi = findViewById(R.id.detailedDersAdi);

        Intent intent = getIntent();
        if (intent.hasExtra("ders")) {
            Dersler ders = (Dersler) intent.getSerializableExtra("ders");
            if (ders != null) {
                // Öğretmenin seçtiği dersin ID'sini almak için dersler veritabanından veriyi çekiyorum
                DatabaseReference derslerRef = FirebaseDatabase.getInstance().getReference().child("dersler");
                derslerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Dersler dersSnapshot = snapshot.getValue(Dersler.class);
                            if (dersSnapshot != null && dersSnapshot.getDersKodu().equals(ders.getDersKodu())) {
                                String dersID = snapshot.getKey();
                                // Seçilen dersin öğrencilerini listelemek için showStudentsForCourse metodunu çağırdım
                                showStudentsForCourse(dersID);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(OgrenciListActivity.this, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                derslerRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Dersler dersSnapshot = dataSnapshot.getValue(Dersler.class);
                            if (dersSnapshot != null && dersSnapshot.getDersKodu().equals(ders.getDersKodu())) {
                                String derskodu = dersSnapshot.getDersKodu();
                                String dersadi = dersSnapshot.getDersAdi();
                                if(derskodu != null){
                                    detailedDersKodu.setText(derskodu);
                                    detailedDersAdi.setText(dersadi);
                                }
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(OgrenciListActivity.this, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Ders bilgisi alınamadı", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ders bilgisi alınamadı", Toast.LENGTH_SHORT).show();
        }
    }

    public void showStudentsForCourse(String courseId) {
        DatabaseReference userCoursesRef = FirebaseDatabase.getInstance().getReference().child("kullanici_dersleri");
        userCoursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> userIds = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (userSnapshot.hasChild(courseId)) {
                        // Seçilen kursa kayıtlı olan ogrencilerin idlerini alıyorum
                        userIds.add(userSnapshot.getKey());
                    }
                }
                // Kullanıcıların bilgilerini almak için getUserInfos metodunu çağırıyorum
                getUserInfos(userIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OgrenciListActivity.this, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInfos(ArrayList<String> userIds) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<UserInfos> students = new ArrayList<>();
                for (String userId : userIds) {
                    DataSnapshot userSnapshot = snapshot.child(userId);
                    if (userSnapshot.exists()) {
                        String userName = userSnapshot.child("name").getValue(String.class);
                        String userEmail = userSnapshot.child("email").getValue(String.class);
                        String userStdNum = userSnapshot.child("studentNumber").getValue(String.class);
                        UserInfos userInfo = new UserInfos();
                        userInfo.setUserId(userId); // userId'yi set ediyorum
                        userInfo.setStudentNumber(userStdNum); //ogrenciye aait ogrenci numarasını set ediyorum
                        //userInfo.setName(userName);
                        //userInfo.setEmail(userEmail);
                        //students.add(userInfo);
                        students.add(new UserInfos(userId, userName, userEmail, userStdNum));
                    }
                }
                // Öğrenci listesini RecyclerView'de göstermek icin:
                displayStudents(students);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OgrenciListActivity.this, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayStudents(ArrayList<UserInfos> students) {
        // Öğrenci listesini RecyclerView'de göster
        ogrList.clear();
        ogrList.addAll(students);
        myAdapter.notifyDataSetChanged();
    }

}