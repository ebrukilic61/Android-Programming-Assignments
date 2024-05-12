package com.example.ders1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private ArrayList<Reports> reportList;
    private Context context;
    private FirebaseAuth auth;
    private DatabaseReference userRef, ogrRef;
    private FirebaseUser currentUser;

    public ReportAdapter(Context context, ArrayList<Reports> raporList) {
        this.context = context;
        this.reportList = raporList;
        userRef = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, @SuppressLint("RecyclerView") int position) {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("users_ogretmen").child(currentUser.getUid());
        ogrRef = FirebaseDatabase.getInstance().getReference().child("users");
        Reports reports = reportList.get(position);
        Log.d("ReportAdapter", "Rapor bilgileri: " + reports.toString());
        System.out.println("Rapor bilgileri: " + reports.getRaporId()+reports.getRaporMesaj()+reports.getUserID()+reports.getPersonName());

        holder.commentName.setText(reports.getPersonName());
        holder.commentText.setText(reports.getRaporMesaj());
        holder.commentTime.setText(reports.getTimestamp());
        holder.reportSubject.setText(reports.getRaporKonu());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ReportList.class);
                intent.putExtra("ders", (Serializable) reports);
                context.startActivity(intent);
            }
        });

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.deleteReport.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteReport.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.deleteReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reports selectedReport = reportList.get(position);
                deleteSelectedReport(selectedReport);
            }
        });

    }

    private void deleteSelectedReport(Reports selectedReports){
        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference().child("report");
        reportRef.child(selectedReports.getRaporId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"Seçtiğiniz rapor başarıyla silindi",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Rapor silinirken bir hata oluştu",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }
    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView commentName, commentText, commentTime, reportSubject;
        ImageButton deleteReport;
        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            commentName = itemView.findViewById(R.id.commentname);
            commentText = itemView.findViewById(R.id.commenttext);
            commentTime = itemView.findViewById(R.id.commenttime);
            reportSubject = itemView.findViewById(R.id.reportSubject);
            deleteReport = itemView.findViewById(R.id.deleteReport);
        }
    }
}