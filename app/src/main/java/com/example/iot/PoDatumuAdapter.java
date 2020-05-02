package com.example.iot;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PoDatumuAdapter extends RecyclerView.Adapter<PoDatumuAdapter.ViewHolder> {
    private Context mContext;
    private List<DanZaKorisnika> mDanZaKorisnikas;

    public PoDatumuAdapter(Context mContext, List<DanZaKorisnika> mDanZaKorisnikas) {
        this.mContext = mContext;
        this.mDanZaKorisnikas = mDanZaKorisnikas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_jedan_po_datumu, parent, false);
        return new PoDatumuAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final DanZaKorisnika dan = mDanZaKorisnikas.get(position);
        DatabaseReference reference;
        final ArrayList<User> users = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(dan.getId());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 users.add(dataSnapshot.getValue(User.class));

                if (users.get(0).getImageURL().equals("")) {
                    holder.slika.setImageResource(R.drawable.userimg);
                } else {
                     Glide.with(mContext).load(users.get(0).getImageURL()).into(holder.slika);
                }
                holder.username.setText(users.get(0).getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.dolazak.setText(dan.getDolazak());
        holder.odlazak.setText(dan.getOdlazak());
    }

    @Override
    public int getItemCount() {
        return mDanZaKorisnikas.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView dolazak;
        public TextView odlazak;
        public ImageView slika;


        public ViewHolder(final View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username2);
            dolazak = itemView.findViewById(R.id.dolazak2);
            odlazak = itemView.findViewById(R.id.odlazak2);
            slika = itemView.findViewById(R.id.imageView2);
        }
    }
}
