package com.example.iot;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class IstorijaAdapter extends RecyclerView.Adapter<IstorijaAdapter.ViewHolder> {
    private Context mContext;
    private List<DanZaKorisnika> mDanZaKorisnikas;

    public IstorijaAdapter(Context mContext, List<DanZaKorisnika> mDanZaKorisnikas) {
        this.mContext = mContext;
        this.mDanZaKorisnikas = mDanZaKorisnikas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_jedan_dolazak, parent, false);
        return new IstorijaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DanZaKorisnika dan = mDanZaKorisnikas.get(position);
        holder.datum.setText(dan.getDatum());
        holder.dolazak.setText(dan.getDolazak());
        holder.odlazak.setText(dan.getOdlazak());
    }

    @Override
    public int getItemCount() {
        return mDanZaKorisnikas.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView datum;
        public TextView dolazak;
        public TextView odlazak;


        public ViewHolder(View itemView) {
            super(itemView);

            datum = itemView.findViewById(R.id.datum1);
            dolazak = itemView.findViewById(R.id.dolazak1);
            odlazak = itemView.findViewById(R.id.odlazak1);


        }
    }
}
