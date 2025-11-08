package com.raman.kumar.shrikrishan.Adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdView;
//import com.raman.kumar.shrikrishan.GlideApp;
import com.raman.kumar.modals.video.GetVideoModal.Datum;
import com.raman.kumar.shrikrishan.R;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Dell- on 8/4/2018.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    FragmentTransaction ft;
    private List<Datum> videoList;
    ProgressDialog progressDialog;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView video;
        TextView title;
        private ItemClickListener clickListener;
        AdView adView;

        public MyViewHolder(View view) {
            super(view);
            video = (ImageView) view.findViewById(R.id.video);
            title = (TextView) view.findViewById(R.id.title);
            video.setOnClickListener(this);
            adView = view.findViewById(R.id.adView);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

    public VideoAdapter(List<Datum> arrayList, Context context, FragmentTransaction ft) {
        this.videoList = arrayList;
        this.mContext = context;
        this.ft = ft;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_adapter_lay, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String videoUrl = videoList.get(position).getUrl();
        holder.title.setText(Html.fromHtml(videoList.get(position).getTitle()));
        /*AdRequest adRequest = new AdRequest.Builder().build();
        holder.adView.loadAd(adRequest);
        if (position % 2 == 0) {
            if (position == 0) {
                holder.adView.setVisibility(View.GONE);
            } else {
                holder.adView.setVisibility(View.VISIBLE);
            }
        } else {
            holder.adView.setVisibility(View.GONE);
        }*/
        //  Bitmap image=retriveVideoFrameFromVideo(videoUrl);
        // Bitmap image= ThumbnailUtils.createVideoThumbnail(videoUrl, MediaStore.Video.Thumbnails.MINI_KIND);

        Log.e("URLLllllllllllll",videoUrl+"=asas=0");
        if (videoUrl.contains("youtube")) {
            long interval = 5000 * 1000;
            String[] parts = videoUrl.split(Pattern.quote("?v="));
            String part1 = parts[0]; // 004
            String part2 = parts[1];
            String imgUrl = "https://img.youtube.com/vi/" + part2 + "/0.jpg";

            Glide.with(mContext)
                    .load(imgUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.loading_image)
                            .error(R.drawable.image_broken)
                            .centerCrop())
                    .into(holder.video);
        }else {
            if (videoUrl.contains("http")) {
                Log.e("URLLl====lllllll",videoUrl+"=asasdfhtts=0");
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoUrl, MediaStore.Video.Thumbnails.MINI_KIND);
                holder.video.setImageBitmap(bitmap);
                /*
                RequestOptions requestOptions = new RequestOptions();
                Glide.with(mContext)
                        .load("https://www.instagram.com/reel/CfwRXzQB-To/media?size=t")
                        .apply(requestOptions)
                        .thumbnail(Glide.with(mContext).load("https://www.instagram.com/reel/CfwRXzQB-To/media?size=t"))
                        .into(holder.video);*/
             /*   GlideApp.with(mContext)
                        .asBitmap()
                        .load(videoUrl)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(holder.video);*/
               /* try {
                    holder.video.setImageBitmap(retriveVideoFrameFromVideo(videoUrl));

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    Log.e("URLLl====00llll",throwable.getMessage()+"=asasdfhtts=0");

                }*/

            }
        }
            /*Glide.with(mContext)
                    .load(imgUrl)

                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.image_broken)

                    .into(holder.video);*/
//            RequestOptions options = new RequestOptions().frame(interval);
//            Glide.with(mContext).asBitmap()
//                    .load(imgUrl)
//                    .apply(options)
//                    .into(holder.video);
        //   holder.video.setImageBitmap(image);

        holder.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(videoUrl));
                Log.e("URLLLLLLLLLLL",videoUrl+"======");
                try {
                    if (videoUrl.contains("fb.watch") || videoUrl.contains("facebook")) {

                        mContext.startActivity(new  Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));
                    } else if (videoUrl.contains("youtube")) {
                        String[] parts = videoUrl.split(Pattern.quote("?v="));
                        String part1 = parts[0]; // 004
                        String part2 = parts[1];
                        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + part2));
                        mContext.startActivity(appIntent);
                    } else if (videoUrl.contains("twitter")) {
                        mContext.startActivity(new  Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));

                    } else if (videoUrl.contains("instagram"))
                    {
                        mContext.startActivity(new  Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));

                    }
                    else
                    {
                        mContext.startActivity(new  Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));

                    }

                } catch (ActivityNotFoundException ex) {
                    mContext.startActivity(webIntent);
                }
            }
        });

    }
    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

}