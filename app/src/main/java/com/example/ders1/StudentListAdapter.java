package com.example.ders1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<UserInfos> ogrList;

    public StudentListAdapter(Context context, ArrayList<UserInfos> ogrList) {
        this.context = context;
        this.ogrList = ogrList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ogrenci_bilgileri, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserInfos student = ogrList.get(position);
        holder.studentName.setText(student.getName());
        holder.studentEmail.setText(student.getEmail());
        holder.ogrNumText.setText(student.getStudentNumber());
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeStudentFromCourse(student.getUserId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return ogrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, ogrNumText;
        TextView studentEmail;
        ImageButton removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.ogrNameText);
            studentEmail = itemView.findViewById(R.id.ogrEmailText);
            ogrNumText = itemView.findViewById(R.id.ogrNumText);
            removeButton = itemView.findViewById(R.id.removeBtn);
        }
    }

    private void removeStudentFromCourse(String studentId) {
        DatabaseReference userCoursesRef = FirebaseDatabase.getInstance().getReference().child("kullanici_dersleri").child(studentId);
        userCoursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    // öğrencinin kurstan kaydını silmek için:
                    courseSnapshot.getRef().removeValue();
                }
                // öğrenciyi listeden kaldırmak için:
                int position = getStudentPosition(studentId);
                if (position != -1) {
                    ogrList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Öğrenci dersten çıkartıldı", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Öğrenci bulunamadı", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getStudentPosition(String studentId) {
        for (int i = 0; i < ogrList.size(); i++) {
            if (ogrList.get(i).getUserId().equals(studentId)) {
                return i;
            }
        }
        return -1;
    }
}