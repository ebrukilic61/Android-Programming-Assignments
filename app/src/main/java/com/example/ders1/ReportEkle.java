package com.example.ders1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
public class ReportEkle extends AppCompatActivity {
    private Spinner reportKonu;
    private TextView textViewCard;
    private EditText dersKoduRep;
    private Button ekleButton;
    private DatabaseReference reportRef, userRef, dersRef;
    private FirebaseUser currentUser;
    private Reports reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_icerik);

        // Gerekli view'leri ve referansları başlat:
        initializeViews();
        initializeFirebase();
        // Spinner'a adapter set etme
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.reportKonusu,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportKonu.setAdapter(adapter);

        ekleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReport();
            }
        });
    }

    private void initializeViews() {
        reportKonu = findViewById(R.id.reportKonu);
        textViewCard = findViewById(R.id.textViewCard);
        dersKoduRep = findViewById(R.id.dersKoduRep);
        ekleButton = findViewById(R.id.ekleButton);
    }

    private void initializeFirebase() {
        reportRef = FirebaseDatabase.getInstance().getReference().child("report");
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        dersRef = FirebaseDatabase.getInstance().getReference().child("dersler");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void addReport() {
        String reportMsg = textViewCard.getText().toString().trim();
        String dersKoduTxt = dersKoduRep.getText().toString().trim();
        String selectedValue = reportKonu.getSelectedItem().toString();

        if (!selectedValue.equals("")) {
            if (!dersKoduTxt.isEmpty()) {
                checkDersKoduAndAddReport(selectedValue, reportMsg, dersKoduTxt);
            } else {
                saveReport(selectedValue, reportMsg, "");
            }
        } else {
            Toast.makeText(ReportEkle.this, "Lütfen bir rapor konusu seçin!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkDersKoduAndAddReport(String selectedValue, String reportMsg, String dersKoduTxt) {
        dersRef.orderByChild("dersKodu").equalTo(dersKoduTxt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    saveReport(selectedValue, reportMsg, dersKoduTxt);
                } else {
                    Toast.makeText(ReportEkle.this, "Bu ders koduna ait bir ders bulunamadı", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportEkle.this, "Veritabanı erişiminde hata oluştu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveReport(String selectedValue, String reportMsg, String dersKoduTxt) {
        String userID = currentUser.getUid();
        String raporId = reportRef.push().getKey();

        userRef.child(userID).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.getValue(String.class);
                    String currentDateAndTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

                    reports = new Reports();
                    reports.setRaporId(raporId);
                    reports.setRaporKonu(selectedValue);
                    reports.setRaporMesaj(reportMsg);
                    reports.setDersKodu(dersKoduTxt);
                    reports.setUserID(userID);
                    reports.setPersonName(userName);
                    reports.setTimestamp(currentDateAndTime);

                    reportRef.child(raporId).setValue(reports).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ReportEkle.this, "Raporunuz başarıyla işlendi, bildiriniz için teşekkür ederiz", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ReportEkle.this,MenuActivity2.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ReportEkle.this, "Rapor kaydedilirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ReportEkle.this, "Kullanıcı adı bulunamadı", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportEkle.this, "Veritabanı erişiminde hata oluştu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
