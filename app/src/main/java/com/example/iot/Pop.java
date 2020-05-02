package com.example.iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Pop extends AppCompatActivity {
    TextView username;
    ImageView slika;
    private RecyclerView recyclerView;

    private IstorijaAdapter istorijaAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        username = findViewById(R.id.txtPom);
        slika = findViewById(R.id.imageView2);
        recyclerView = findViewById(R.id.recycler_view);

        User user = (User) getIntent().getSerializableExtra("user");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.9),(int) (height*.8));

        username.setText(user.getUsername());
        if (user.getImageURL().equals("")) {
            slika.setImageResource(R.drawable.userimg);
        } else {
            Glide.with(Pop.this).load(user.getImageURL()).into(slika);

        }
        readDates(user.getId());
    }

    private void readDates(final String ID) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Evidencija");
        final ArrayList<DanZaKorisnika> mDanZaKorisnikas = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mDanZaKorisnikas.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.child("id").getValue(String.class);
                    String datu = snapshot.child("datum").getValue(String.class);
                    String dol = snapshot.child("dolazak").getValue(String.class);
                    String odl = snapshot.child("odlazak").getValue(String.class);

                    DanZaKorisnika dan = new DanZaKorisnika(id, datu, dol,odl);
                    if (dan.getId().equals(ID)) {
                        mDanZaKorisnikas.add(dan);
                    }

                }
                istorijaAdapter = new IstorijaAdapter(Pop.this, mDanZaKorisnikas);
                recyclerView.setAdapter(istorijaAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(Pop.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
