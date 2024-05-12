package com.example.ders1;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<Dersler> list;
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private boolean isDeleteEnabled = false;

    public void setSearchList(ArrayList<Dersler> dersSearchList){
        this.list = dersSearchList;
        notifyDataSetChanged();
    }

    public void setDeleteEnabled(boolean enabled) {
        this.isDeleteEnabled = enabled;
    }

    public MyAdapter(Context context, ArrayList<Dersler> list) {
        this.context = context;
        this.list = list;
        userRef = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("users_ogretmen").child(currentUser.getUid());

        Dersler lessons= list.get(position);

        holder.dersAdiTxt.setText(lessons.getDersAdi());
        holder.grupNo.setText(lessons.getDersGrubu());
        holder.instructorNameTxt.setText(lessons.getOgretimGorevlisi());
        holder.dersGunText.setText(lessons.getGun());
        holder.dersKoduTxt.setText(lessons.getDersKodu());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tıklanan dersin verilerini OgrenciListActivity'e aktarabilmek için intent oluşturdum
                Intent intent = new Intent(context, OgrenciListActivity.class);
                intent.putExtra("ders", (Serializable) lessons);
                context.startActivity(intent);
            }
        });

        if (isDeleteEnabled) {
            holder.removeBtn.setVisibility(View.VISIBLE);
        } else {
            holder.removeBtn.setVisibility(View.GONE);
        }

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dersler selectedCourse = list.get(position);
                // Dersi silme işlemini gerçekleştir
                deleteCourse(selectedCourse);
            }
        });

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.imageButton.setVisibility(View.VISIBLE);
                } else {
                    holder.imageButton.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Düzenleme (güncelleme) işlemi
                ViewDialogUpdate viewDialogUpdate = new ViewDialogUpdate();
                viewDialogUpdate.showDialog(context, lessons);
            }
        });

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.dersEkle.setVisibility(View.GONE); //öğretmenler değil öğrenciler ders ekleyeceği için bu buton öğretmenlere görünür değil
                }else{
                    holder.dersEkle.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.dersEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dersler selectedCourse = list.get(position);
                DatabaseReference derslerRef = FirebaseDatabase.getInstance().getReference().child("dersler");
                Query query = derslerRef.orderByChild("dersKodu").equalTo(selectedCourse.getDersKodu());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Ders bulundu, dersin ID'sini al
                            String dersId = "";
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                dersId = snapshot.getKey();
                                break; // Sadece ilk dersin ID'sini al
                            }
                            saveCourseToDatabase(selectedCourse, dersId);
                        } else {
                            Toast.makeText(context, "Ders bulunamadı", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context, "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.deleteBtn.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteBtn.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialogDelete viewDialogDelete = new ViewDialogDelete();
                viewDialogDelete.showDialog(context, lessons);
            }
        });
    }
    private void deleteCourse(Dersler selectedCourse) {
        DatabaseReference userCoursesRef = FirebaseDatabase.getInstance().getReference().child("kullanici_dersleri").child(currentUser.getUid());
        // Seçilen dersi veritabanından silmek icin:
        userCoursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Dersler course = dataSnapshot.getValue(Dersler.class);
                        if (course != null && course.getDersKodu().equals(selectedCourse.getDersKodu())) {
                            dataSnapshot.getRef().removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Silme işlemi başarılı olduğunda kullanıcıya bildir
                                                Toast.makeText(context, "Ders başarıyla silindi", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Hata durumunda kullanıcıya bildir
                                                Toast.makeText(context, "Silme işlemi başarısız oldu", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView dersAdiTxt, grupNo, instructorNameTxt, dersGunText, ogrSayisiTxt, dersKoduTxt;
        ImageButton imageButton, deleteBtn, dersEkle, removeBtn;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            dersAdiTxt = itemView.findViewById(R.id.dersAdiTxt);
            grupNo = itemView.findViewById(R.id.grupNo);
            instructorNameTxt = itemView.findViewById(R.id.instructorNameTxt);
            dersGunText = itemView.findViewById(R.id.dersGunText);
            dersKoduTxt = itemView.findViewById(R.id.dersKoduTxt);
            imageButton = itemView.findViewById(R.id.imageButton);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            dersEkle = itemView.findViewById(R.id.dersEkle);
            removeBtn = itemView.findViewById(R.id.removeBtn);
        }
    }

    private void saveCourseToDatabase(@NonNull Dersler selectedCourse, String dersId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userCoursesRef = database.getReference().child("kullanici_dersleri").child(currentUser.getUid());
        // Kullanıcı derslerine eklenen dersin ders ID'si ile birlikte kaydedilmesi
        selectedCourse.setDersId(dersId);
        DatabaseReference newCourseRef = userCoursesRef.child(dersId);
        userCoursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isCourseExist = false;
                if (snapshot.exists()) {
                    // Kullanıcının mevcut derslerini kontrol et
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Dersler course = dataSnapshot.getValue(Dersler.class);
                        if (course != null && course.getDersKodu().equals(selectedCourse.getDersKodu())) {
                            isCourseExist = true;
                            break;
                        }
                    }
                }
                if (!isCourseExist) {
                    newCourseRef.setValue(selectedCourse).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Başarıyla eklendiğinde kullanıcıyı bilgilendir
                                Toast.makeText(context, "Ders başarıyla eklendi", Toast.LENGTH_SHORT).show();
                            } else {
                                // Hata durumunda kullanıcıyı bilgilendir
                                Toast.makeText(context, "Ders eklenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, "Bu ders zaten listenizde kayıtlı", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ViewDialogUpdate {
        public void showDialog(Context context, Dersler ders) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.activity_ders_duzenle);

            EditText dersAdi1, dersKodu1, stdSayisi1, ogrGorevlisi1, textViewYil1, dersGrubu1, textViewGun1;
            Button guncelleBtn;
            ImageButton kapatBtn;

            dersAdi1 = dialog.findViewById(R.id.dersAdi1);
            dersKodu1 = dialog.findViewById(R.id.dersKodu1);
            stdSayisi1 = dialog.findViewById(R.id.stdSayisi1);
            ogrGorevlisi1 = dialog.findViewById(R.id.ogrGorevlisi1);
            textViewYil1 = dialog.findViewById(R.id.textViewYil1);
            dersGrubu1 = dialog.findViewById(R.id.dersGrubu1);
            textViewGun1 = dialog.findViewById(R.id.textViewGun1);

            dersAdi1.setText(ders.getDersAdi());
            dersKodu1.setText(ders.getDersKodu());
            stdSayisi1.setText(ders.getStdSayisi());
            ogrGorevlisi1.setText(ders.getOgretimGorevlisi());
            textViewYil1.setText(ders.getDonem());
            dersGrubu1.setText(ders.getDersGrubu());
            textViewGun1.setText(ders.getGun());

            guncelleBtn = dialog.findViewById(R.id.guncelleBtn);
            guncelleBtn.setText("Güncelle");

            kapatBtn = dialog.findViewById(R.id.kapatBtn);
            guncelleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference derslerRef = FirebaseDatabase.getInstance().getReference().child("dersler");
                    derslerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Dersler dersSnapshot = snapshot.getValue(Dersler.class);
                                if (dersSnapshot != null && dersSnapshot.getDersKodu().equals(ders.getDersKodu())) {
                                    String dersID = snapshot.getKey();
                                    DatabaseReference dersRef = derslerRef.child(dersID);
                                    // Güncelleme işlemi burada gerçekleştirilir
                                    dersRef.child("dersAdi").setValue(dersAdi1.getText().toString());
                                    dersRef.child("stdSayisi").setValue(stdSayisi1.getText().toString());
                                    dersRef.child("ogretimGorevlisi").setValue(ogrGorevlisi1.getText().toString());
                                    dersRef.child("yil").setValue(textViewYil1.getText().toString());
                                    dersRef.child("dersGrubu").setValue(dersGrubu1.getText().toString());
                                    dersRef.child("gun").setValue(textViewGun1.getText().toString());
                                    Toast.makeText(context,"Ders Bilgileri Başarıyla Güncellendi",Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            kapatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

    public class ViewDialogDelete {
        public void showDialog(Context context, Dersler ders){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.ders_delete);

            Button buttonDelete = dialog.findViewById(R.id.buttonDelete);
            Button iptalBtn = dialog.findViewById(R.id.iptalBtn);

            iptalBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference dersRef = FirebaseDatabase.getInstance().getReference().child("dersler");
                    dersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Dersler dersSnapshot = snapshot.getValue(Dersler.class);
                                if (dersSnapshot != null && dersSnapshot.getDersKodu().equals(ders.getDersKodu()) && dersSnapshot.getDersGrubu().equals(ders.getDersGrubu())) {
                                    String dersID = snapshot.getKey();
                                    // Silme işlemi:
                                    dersRef.child(dersID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Ders başarıyla silindi", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Silme işlemi sırasında bir hata oluştu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                            dialog.dismiss();
                                        }
                                    });
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Veritabanı hatası: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}