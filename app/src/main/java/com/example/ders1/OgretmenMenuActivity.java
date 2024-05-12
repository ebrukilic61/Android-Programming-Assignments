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
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;

public class OgretmenMenuActivity extends AppCompatActivity {
    ImageView userIconMenu, passIconMenu, dersMenu, dersler, csvYukle, raporla;
    ImageButton logOutButton;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        context = this;

        userIconMenu = findViewById(R.id.userIconMenu);
        passIconMenu = findViewById(R.id.changePassMenu);
        dersMenu = findViewById(R.id.dersMenu);
        dersler = findViewById(R.id.dersler);
        csvYukle = findViewById(R.id.csvYukle);
        raporla = findViewById(R.id.raporla);
        logOutButton = findViewById(R.id.logOutButton);

        passIconMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OgretmenMenuActivity.this,ChangePassword.class);
                startActivity(intent);
            }
        });

        userIconMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OgretmenMenuActivity.this,OgretmenProfileActivity.class);
                startActivity(intent);
            }
        });

        dersMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OgretmenMenuActivity.this, DersEkle.class);
                startActivity(intent);
            }
        });

        dersler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OgretmenMenuActivity.this,CourseListActivity.class);
                startActivity(intent);
            }
        });

        csvYukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OgretmenMenuActivity.this,UploadCsvActivity.class);
                startActivity(intent);
            }
        });

        raporla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OgretmenMenuActivity.this,ReportList.class);
                startActivity(intent);
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OgretmenMenuActivity.ViewDialogLogOut viewDialogLogOut = new OgretmenMenuActivity.ViewDialogLogOut();
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
                    Intent intent = new Intent(OgretmenMenuActivity.this, MainActivity.class);
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