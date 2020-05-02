package com.example.iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class MainActivityAdmin extends AppCompatActivity {
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    TextView username;
    ImageView slika;

//    Button izlaz;
//    Button licniPodaci;
//    Button novi;
//    Button istorija;
//    Button provera;
//    Button prijava;
//    Button odjava;
    CardView izlaz, licniPodaci, novi, istorija, provera, prijava, odjava;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        username = findViewById(R.id.txtUsernameMain);
        slika = findViewById(R.id.imageView);

        izlaz = findViewById(R.id.btnIzlaz);
        licniPodaci = findViewById(R.id.btnLicni);
        //novi = findViewById(R.id.btnNovi);
        istorija = findViewById(R.id.btnIstorija);
        provera = findViewById(R.id.btnProvera);
        prijava = findViewById(R.id.btnDolazak);
        odjava = findViewById(R.id.btnOdlazak);


        izlaz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        licniPodaci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivityAdmin.this,Opcije.class);
                startActivity(i);
            }
        });

//        novi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MainActivityAdmin.this,NewEmp.class);
//                startActivity(i);
//            }
//        });

        provera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivityAdmin.this,ProveraZaposlenihActivity.class);
                startActivity(i);
            }
        });

        istorija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivityAdmin.this,IstorijaActivity.class);
                startActivity(i);
            }
        });

        prijava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivityAdmin.this,QRScanner.class);
                i.putExtra("prijava",1);
                startActivity(i);
            }
        });

        odjava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivityAdmin.this,QRScanner.class);
                i.putExtra("prijava",0);
                startActivity(i);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        if (Opcije.sharedPreferencesOpcije != null) {
            String velicinaFonta = Opcije.sharedPreferencesOpcije.getString("VelicinaFonta", "");
            if (!velicinaFonta.equals("")) {
                username.setTextSize(Float.valueOf(velicinaFonta));
            }
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null)  {
                    username.setText(user.getUsername());
                    if (user.getImageURL().equals("")) {
                        slika.setImageResource(R.drawable.userimg);
                    } else {
                        Glide.with(MainActivityAdmin.this).load(user.getImageURL()).into(slika);

                    }
                }
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
            Intent i = new Intent(MainActivityAdmin.this,About.class);
            startActivity(i);
            return true;
        }
//        if (item.getItemId() == R.id.item2) {
//            Intent i = new Intent(MainActivityAdmin.this,Opcije.class);
//            startActivity(i);
//            return true;
//        }
        return false;

    }
}
