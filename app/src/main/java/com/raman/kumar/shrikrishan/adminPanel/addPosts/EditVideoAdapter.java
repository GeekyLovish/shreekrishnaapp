package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.video.GetVideoModal.Datum;
import com.raman.kumar.modals.getaModal.DeleteGetaModal;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;

import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditVideoAdapter extends RecyclerView.Adapter<EditVideoAdapter.ViewHolder> {
    Context context;
    List<Datum> videoList;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public EditVideoAdapter(EditVideoActivity editVideoActivity, List<Datum> videoList) {
        context = editVideoActivity;
        this.videoList = videoList;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_video_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final String videoUrl = videoList.get(position).getUrl();
        holder.title.setText(Html.fromHtml(videoList.get(position).getTitle()));
        long interval = 5000 * 1000;
        String imgUrl = "";

        try{
            String[] parts = videoUrl.split(Pattern.quote("?v="));
            String part1 = parts[0]; // 004
            String part2 = parts[1];
            imgUrl = "https://img.youtube.com/vi/" + part2 + "/0.jpg";
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        Glide.with(context)
                .load(imgUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.loading_image)
                        .error(R.drawable.image_broken)
                        .centerCrop())
                .into(holder.video);

        holder.editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogForOptions(position, holder.editImageView);

            }
        });
    }

    private void openDialogForOptions(int position, ImageView editImageView) {
        PopupMenu popup = new PopupMenu(context, editImageView);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        // Set up individual item click listeners
        popup.getMenu().findItem(R.id.edit).setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(context, VideoUploadActivity.class);
            intent.putExtra("title", videoList.get(position).getTitle());
            intent.putExtra("content", videoList.get(position).getTitle());
            intent.putExtra("url", videoList.get(position).getUrl());
            intent.putExtra("id", videoList.get(position).getId().toString());
            context.startActivity(intent);
            return true; // Indicate that the event was handled
        });


        popup.getMenu().findItem(R.id.delete).setOnMenuItemClickListener(item -> {
            Call<DeleteGetaModal> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .deleteVideo("application/json", Extensions.getBearerToken(),videoList.get(position).getId().toString());
            call.enqueue(new Callback<DeleteGetaModal>() {
                @Override
                public void onResponse(Call<DeleteGetaModal> call, Response<DeleteGetaModal> response) {
                    DeleteGetaModal deleteVideo = response.body();
                    if (response.isSuccessful()) {
                        if (deleteVideo.getStatus()) {
                            videoList.remove(position);
                            notifyDataSetChanged();
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, deleteVideo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                    Toast.makeText(context, "Failed to delete !", Toast.LENGTH_LONG).show();
                }
            });
            return true; // Indicate that the event was handled
        });



        popup.show();//showing popup menu
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView editImageView, video;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editImageView = itemView.findViewById(R.id.editImageView);
            title = itemView.findViewById(R.id.title);
            video = itemView.findViewById(R.id.video);
        }
    }
}
