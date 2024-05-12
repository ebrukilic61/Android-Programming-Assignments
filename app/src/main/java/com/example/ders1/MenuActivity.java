package com.example.ders1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    ImageView userIconMenu, passIconMenu, dersMenu, dersler, csvYukle, raporla;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        userIconMenu = findViewById(R.id.userIconMenu);
        passIconMenu = findViewById(R.id.changePassMenu);
        dersler = findViewById(R.id.dersler);
        dersMenu = findViewById(R.id.dersMenu);
        csvYukle = findViewById(R.id.csvYukle);
        raporla = findViewById(R.id.raporla);

        passIconMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,ChangePassword.class);
                startActivity(intent);
            }
        });

        userIconMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

        dersMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,DersEkle.class);
                startActivity(intent);
            }
        });

        dersler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,CourseListActivity.class);
                startActivity(intent);
            }
        });

        csvYukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,UploadCsvActivity.class);
                startActivity(intent);
            }
        });

        raporla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,ReportList.class);
                startActivity(intent);
            }
        });
    }
}