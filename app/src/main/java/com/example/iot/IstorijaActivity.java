package com.example.iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.List;

public class IstorijaActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private IstorijaAdapter istorijaAdapter;
    private List<DanZaKorisnika> mDanZaKorisnikas;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    TextView username;
    ImageView slika;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_istorija);

        recyclerView = findViewById(R.id.recycler_view);

        mDanZaKorisnikas = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = "";
        if (getIntent() != null && getIntent().getStringExtra("userID") != null) {
            userID = getIntent().getStringExtra("userID");
        } else {
            userID = firebaseUser.getUid();
        }
        username = findViewById(R.id.txtUsernameMain);
        slika = findViewById(R.id.imageView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null)  {
                    username.setText(user.getUsername());
                    if (user.getImageURL().equals("")) {
                        slika.setImageResource(R.drawable.userimg);
                    } else {
                        Glide.with(IstorijaActivity.this).load(user.getImageURL()).into(slika);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        readDates(userID);
    }

    private void readDates(final String userID) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Evidencija");

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
                    if (dan.getId().equals(userID)) {
                        Log.d("USAO","DVA");
                        mDanZaKorisnikas.add(dan);
                    }

                }
                Log.d("VELICINA 2",String.valueOf(mDanZaKorisnikas.size()));
                istorijaAdapter = new IstorijaAdapter(IstorijaActivity.this, mDanZaKorisnikas);
                recyclerView.setAdapter(istorijaAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(IstorijaActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meni,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item1) {
            System.exit(0);
            finish();
            return true;
        }
        if (item.getItemId() == R.id.item3) {
            Intent i = new Intent(IstorijaActivity.this,About.class);
            startActivity(i);
            return true;
        }
//        if (item.getItemId() == R.id.item2) {
//            Intent i = new Intent(IstorijaActivity.this,Opcije.class);
//            startActivity(i);
//            return true;
//        }
        return false;

    }
}
