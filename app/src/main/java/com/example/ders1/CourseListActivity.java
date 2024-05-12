package com.example.ders1;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CourseListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference db;
    MyAdapter myAdapter;
    ImageButton filtre;
    SearchView searchView;
    ArrayList<Dersler> list;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list_activity);
        context = this;

        recyclerView = findViewById(R.id.recyclerViewList);
        searchView = findViewById(R.id.searchView);
        filtre = findViewById(R.id.filtre);
        db = FirebaseDatabase.getInstance().getReference("dersler");
        list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(this, list);
        recyclerView.setAdapter(myAdapter);

        // Spinner'a eklemek için dönemleri oluşturdum:
        String[] dersDonemleri = {"Bahar Dönemi", "Güz Dönemi"}; //spinnerı en son hallet
        filtre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDiologFiltrele viewDiologFiltrele = new ViewDiologFiltrele();
                viewDiologFiltrele.showDialog(context);
            }
        });

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Dersler ders = dataSnapshot.getValue(Dersler.class);
                    list.add(ders);
                }
                myAdapter.notifyDataSetChanged(); // Adaptera değişiklikleri bildir
                Log.d("TAG", "Veri alındı, liste boyutu: " + list.size());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CourseListActivity", "Veritabanı Hatası", error.toException());
            }
        });

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(CourseListActivity.this,1);
        recyclerView.setLayoutManager(gridLayoutManager);
        list = new ArrayList<>();
        myAdapter = new MyAdapter(CourseListActivity.this,list);
        recyclerView.setAdapter(myAdapter);
    }
    private void searchList(String text){
        ArrayList<Dersler> dersSearchList = new ArrayList<>();
        for(Dersler ders : list){
            if(ders.getDersAdi().toLowerCase().contains(text.toLowerCase()) ||
                ders.getDersKodu().toLowerCase().contains(text.toLowerCase())){
                dersSearchList.add(ders);
            }
        }if(dersSearchList.isEmpty()){
            Toast.makeText(this, "Aradığınız ders bulunamadı",Toast.LENGTH_SHORT).show();
        }else{
            myAdapter.setSearchList(dersSearchList);
        }
    }

    private int getGunIndex(String gun) {
        String lowerCaseGun = gun.toLowerCase();
        if (lowerCaseGun.equals("pazartesi")) {
            return 1;
        } else if (lowerCaseGun.equals("salı") || lowerCaseGun.equals("sali")) {
            return 2;
        } else if (lowerCaseGun.equals("çarşamba") || lowerCaseGun.equals("carsamba")) {
            return 3;
        } else if (lowerCaseGun.equals("perşembe") || lowerCaseGun.equals("persembe")) {
            return 4;
        } else if (lowerCaseGun.equals("cuma")) {
            return 5;
        } else if (lowerCaseGun.equals("cumartesi")) {
            return 6;
        } else if (lowerCaseGun.equals("pazar")) {
            return 7;
        } else {
            return -1; // Geçersiz gün ismi varsa -1 döndürecek
        }
    }

    private void filterListAsc() {
        Comparator<Dersler> GuneGoreSiralaAsc = new Comparator<Dersler>() {
            @Override
            public int compare(Dersler d1, Dersler d2) {
                // Donemlerin null kontrolü ekleniyor

                int gunIndex1 = getGunIndex(d1.getGun());
                int gunIndex2 = getGunIndex(d2.getGun());
                return Integer.compare(gunIndex1, gunIndex2);

            }
        };
        // Dersleri tarihlerine göre sıralar
        Collections.sort(list, GuneGoreSiralaAsc);
        // Sıralanmış ders listesini göstermek icin:
        myAdapter.notifyDataSetChanged();
    }

    private void filterListDesc() {
        Comparator<Dersler> GuneGoreSiralaDesc = new Comparator<Dersler>() {
            @Override
            public int compare(Dersler d1, Dersler d2) {
                // Donemlerin null kontrolü ekleniyor
                int gunIndex1 = getGunIndex(d1.getGun());
                int gunIndex2 = getGunIndex(d2.getGun());
                return Integer.compare(gunIndex2, gunIndex1);
            }
        };
        // Dersleri tarihlerine göre sıralar
        Collections.sort(list, GuneGoreSiralaDesc);
        // Sıralanmış ders listesini göstermek icin:
        myAdapter.notifyDataSetChanged();
    }

    public class ViewDiologFiltrele{
        public void showDialog(Context context){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.filter_detay);

            AppCompatButton donemVeGun, donemVeGunDesc;
            donemVeGun = dialog.findViewById(R.id.donemVeGun);
            donemVeGunDesc = dialog.findViewById(R.id.donemVeGunDesc);
            donemVeGun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterListAsc();
                    dialog.dismiss();
                }
            });

            donemVeGunDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterListDesc();
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

}