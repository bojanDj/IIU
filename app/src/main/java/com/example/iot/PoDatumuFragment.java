package com.example.iot;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PoDatumuFragment extends Fragment {
    Spinner spinner;
    PoDatumuAdapter poDatumuAdapter;
    private RecyclerView recyclerView;
    final ArrayList<String> datumi = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_po_datumu, container, false);
        spinner = view.findViewById(R.id.spnDatumi);
        recyclerView = view.findViewById(R.id.recycler_view2);


        Date datum = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        ArrayList<String> datumi = new ArrayList<>();
        getDates();
        //datumi.add(sdf.format(datum));



        return view;
    }
    private void setAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, datumi);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String datum = (String) parent.getSelectedItem();
                read(datum);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void getDates() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Evidencija");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String datu = snapshot.child("datum").getValue(String.class);

                    if (!datumi.contains(datu)) {
                        datumi.add(datu);
                    }

                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void read(final String datum) {

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

                    if (dan.getDatum().equals(datum)) {
                        mDanZaKorisnikas.add(dan);
                    }

                }

                poDatumuAdapter = new PoDatumuAdapter(getContext(), mDanZaKorisnikas);
                recyclerView.setAdapter(poDatumuAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
