package com.example.iot;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProveraZaposlenihActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    TextView username;
    ImageView slika;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provera_zaposlenih);

        username = findViewById(R.id.txtUsernameMain);
        slika = findViewById(R.id.imageView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
                    Glide.with(ProveraZaposlenihActivity.this).load(user.getImageURL()).into(slika);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new PoDatumuFragment(),"Po danima");
        viewPagerAdapter.addFragment(new PoZaposlenomFragment(),"Po zaposlenima");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
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
            Intent i = new Intent(ProveraZaposlenihActivity.this,About.class);
            startActivity(i);
            return true;
        }
//        if (item.getItemId() == R.id.item2) {
//            Intent i = new Intent(ProveraZaposlenihActivity.this,Opcije.class);
//            startActivity(i);
//            return true;
//        }
        return false;

    }

}
