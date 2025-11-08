package com.raman.kumar.shrikrishan.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.raman.kumar.modals.getaModal.getGeetaModal.Datum;
import com.raman.kumar.shrikrishan.FullScreenTextActivity;
import com.raman.kumar.shrikrishan.R;

import java.util.List;

/**
 * Created by mann on 20/2/18.
 */

public class GeetaAdapter extends RecyclerView.Adapter<GeetaAdapter.MyViewHolder> {
    FragmentTransaction ft;
    private List<Datum> geetaList;


    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textview1;
        LinearLayout mainLay;
        AdView adView;
        private ItemClickListener clickListener;

        public MyViewHolder(View view) {
            super(view);

            textview1 = view.findViewById(R.id.textview1);
            mainLay = view.findViewById(R.id.mainLay);
            adView = view.findViewById(R.id.adView);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }


    }


    public GeetaAdapter(List<Datum> arrayList, Context context, FragmentTransaction ft) {
        this.geetaList = arrayList;
        this.mContext = context;
        this.ft = ft;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final String description = geetaList.get(position).getContent();
        String title = geetaList.get(position).getTitle();

        /*AdRequest adRequest = new AdRequest.Builder().build();
        holder.adView.loadAd(adRequest);
        if (position % 10 == 0){
            if (position == 0){
                holder.adView.setVisibility(View.GONE);
            }else {
                holder.adView.setVisibility(View.VISIBLE);
            }
        }else {
            holder.adView.setVisibility(View.GONE);
        }*/

        holder.textview1.setText(title);
        holder.mainLay.setOnClickListener(view -> {
            Intent i = new Intent(mContext, FullScreenTextActivity.class);
            i.putExtra("title", geetaList.get(position).getTitle());
            i.putExtra("content", geetaList.get(position).getContent());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return geetaList.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


}