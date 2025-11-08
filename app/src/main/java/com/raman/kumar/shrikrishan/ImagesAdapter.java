package com.raman.kumar.shrikrishan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raman.kumar.getWallPaper.Datum;
import com.raman.kumar.shrikrishan.Pojo.ImagesData;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mann on 29/1/18.
 */

class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyViewHolder> {
    FragmentTransaction ft;
    private List<Datum> imagesList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView images,shareIcon;
        TextView setAsWallpaper;
        LinearLayout mainLay;
        private TextAdapter.ItemClickListener clickListener;
        public MyViewHolder(View view) {
            super(view);
            images = (ImageView) view.findViewById(R.id.img_view);
//            mainLay=(LinearLayout)view.findViewById(R.id.mainLay);
           // view.setOnClickListener(this);
          //  setAsWallpaper=(TextView) view.findViewById(R.id.setAsWallpaper);
        }


        public void setClickListener(TextAdapter.ItemClickListener itemClickListener) {

            this.clickListener = itemClickListener;
        }
    }


    public ImagesAdapter(List<Datum> imagesList, Context context, FragmentTransaction ft) {
        this.imagesList = imagesList;
        this.mContext = context;
        this.ft=ft;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        String images=imagesList.get(position).getUrl();
        Log.d("pos", String.valueOf(imagesList.get(position)));
        Picasso.get()
                .load(images)
                .into(holder.images);
holder.images.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent i=new Intent(mContext,FullScrenActivity.class);
//        i.putExtra("thumbnail",imagesList.get(position).getThumbnail());
        i.putExtra("url",imagesList.get(position).getUrl());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }
});


    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }
}