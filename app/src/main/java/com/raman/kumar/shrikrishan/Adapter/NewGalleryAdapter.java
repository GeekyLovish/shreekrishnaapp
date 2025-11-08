package com.raman.kumar.shrikrishan.Adapter;

import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.raman.kumar.modals.pictureByParts.getPictureByPart.Datum;
import com.raman.kumar.shrikrishan.Activity.GalleryActivity1;
import com.raman.kumar.shrikrishan.Activity.WallpaperActivity;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.model.GetImagesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dell- on 6/6/2018.
 */


public class NewGalleryAdapter extends RecyclerView.Adapter<NewGalleryAdapter.MyViewHolder> {
    FragmentTransaction ft;
    private List<Datum> imageList;
    ProgressDialog progressDialog;


    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textview1;
        ImageView imageView;
        private ItemClickListener clickListener;

        public MyViewHolder(View view) {
            super(view);

            textview1 = (TextView) view.findViewById(R.id.title);
            imageView = (ImageView) view.findViewById(R.id.wallpaper_image);
            imageView.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }


    public NewGalleryAdapter(List<Datum> arrayList, Context context, FragmentTransaction ft) {
        this.imageList = arrayList;
        this.mContext = context;
        this.ft = ft;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_gallery_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final String title = imageList.get(position).getName();
        final String id = imageList.get(position).getId().toString();
        holder.textview1.setText(title);


//        final String url = getUrl(position);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, GalleryActivity1.class);
//                i.putExtra("url", url);
                i.putExtra("title", title);
                i.putExtra("id", id);
                mContext.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

}