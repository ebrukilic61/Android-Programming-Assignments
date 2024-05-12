package com.example.ders1;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity2 extends AppCompatActivity {
    ConstraintLayout cnsr1, cnsr2, cnsr3, cnsr4, cnsr5;
    ImageButton logOutBtn;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);
        context = this;

        cnsr1 = findViewById(R.id.constraint1);
        cnsr2 = findViewById(R.id.constraint2);
        cnsr3 = findViewById(R.id.constraint3);
        cnsr4 = findViewById(R.id.constraint4);
        cnsr5 = findViewById(R.id.constraint5);
        logOutBtn = findViewById(R.id.logOutBtn);

        cnsr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity2.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

        cnsr2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity2.this,MyCourseListActivity.class);
                startActivity(intent);
            }
        });

        cnsr3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity2.this,ChangePassword.class);
                startActivity(intent);
            }
        });

        cnsr5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity2.this, CourseListActivity.class);
                startActivity(intent);
            }
        });

        cnsr4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity2.this, ReportEkle.class);
                startActivity(intent);
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialogLogOut viewDialogLogOut = new ViewDialogLogOut();
                viewDialogLogOut.showDialog(context);
            }
        });
    }

    public class ViewDialogLogOut{
        public void showDialog(Context context){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.cikis_yap);

            AppCompatButton cikisYap;
            cikisYap = dialog.findViewById(R.id.cikisYap);
            cikisYap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MenuActivity2.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}