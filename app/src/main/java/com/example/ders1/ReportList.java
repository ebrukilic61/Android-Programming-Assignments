package com.example.ders1;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import java.util.ArrayList;

public class ReportList extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference db;
    ReportAdapter repAdapter;
    ArrayList<Reports> repList;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_report);
        context = this;

        recyclerView = findViewById(R.id.recyclerViewReport);
        db = FirebaseDatabase.getInstance().getReference("report");
        repList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        repAdapter = new ReportAdapter(this, repList);
        recyclerView.setAdapter(repAdapter);

        getReports();
    }

    private void getReports(){
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                repList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Reports reps = dataSnapshot.getValue(Reports.class);
                    repList.add(reps);
                }
                // Raporları tarihlerine göre sırala
                Collections.sort(repList, new Comparator<Reports>() {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                    @Override
                    public int compare(Reports r1, Reports r2) {
                        try {
                            Date date1 = dateFormat.parse(r1.getTimestamp());
                            Date date2 = dateFormat.parse(r2.getTimestamp());
                            return date2.compareTo(date1); // En yeni olanı en üstte göstermek için ters sıralama yapılır
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });

                repAdapter.notifyDataSetChanged();
                Log.d("TAG", "Veri alındı, liste boyutu: " + repList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ReportList", "Veritabanı Hatası", error.toException());
            }
        });
    }
}