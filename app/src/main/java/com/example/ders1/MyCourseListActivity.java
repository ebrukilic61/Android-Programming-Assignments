package com.example.ders1;

import android.os.Bundle;
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

public class MyCourseListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference db;
    FirebaseUser currentUser;
    MyAdapter myAdapter;
    ArrayList<Dersler> myCourseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list_activity);
        recyclerView = findViewById(R.id.recyclerViewOgrenci);
        db = FirebaseDatabase.getInstance().getReference("kullanici_dersleri");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myCourseList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(this, myCourseList);
        recyclerView.setAdapter(myAdapter);
        myAdapter.setDeleteEnabled(true);
        getUserCourses();
    }

    private void getUserCourses() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    myCourseList.clear();
                    // Kullanıcının derslerini okuyup myCourseList'e ekliyorum
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String courseId = snapshot.getKey(); // Dersin kimliğini al
                        DatabaseReference courseRef = db.child(userId).child(courseId);
                        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Dersler course = dataSnapshot.getValue(Dersler.class);
                                myCourseList.add(course);
                                myAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(MyCourseListActivity.this, "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MyCourseListActivity.this, "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
