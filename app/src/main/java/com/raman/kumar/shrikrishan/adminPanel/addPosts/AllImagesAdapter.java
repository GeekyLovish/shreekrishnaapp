package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.gallary.getGallary.GalleryData;
import com.raman.kumar.modals.getaModal.DeleteGetaModal;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.model.CommonResponse;
import com.raman.kumar.shrikrishan.model.PostImageResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllImagesAdapter extends RecyclerView.Adapter<AllImagesAdapter.ViewHolder> {

    DisplayAllImagesActivity applicationContext;
    List<GalleryData> list;
    Firebase postReference = new Firebase("https://shri-krishan.firebaseio.com/All_Image_Uploads_Database");

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    AllImagesAdapter(DisplayAllImagesActivity applicationContext, List<GalleryData> list) {
        this.applicationContext = applicationContext;
        this.list = list;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String content = list.get(position).getContent();
        content = content.replace("\\u003C", "<").replace("\\u003E", ">");
        if (content.startsWith("\"") && content.endsWith("\"")) {
            content = content.substring(1, content.length() - 1);
        }
        Spanned spannedContent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spannedContent = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY);
        } else {
            spannedContent = Html.fromHtml(content);
        }


        holder.ImageNameTextView.setText(spannedContent);
        String images = list.get(position).getUrl();
        Glide.with(applicationContext)
                .load(images)
                .into(holder.imageView);

        holder.moreOptions.setOnClickListener(v -> {
            openDialogForOptions(position, holder.moreOptions);
        });
    }

    private void openDialogForOptions(int position, ImageView moreOptions) {
        PopupMenu popup = new PopupMenu(applicationContext, moreOptions);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        // Set up individual item click listeners
        popup.getMenu().findItem(R.id.edit).setOnMenuItemClickListener(item -> {

            Intent intent = new Intent(applicationContext, AddPostActivity.class);
            intent.putExtra("id", list.get(position).getId().toString());
            intent.putExtra("title", list.get(position).getTitle());
            intent.putExtra("content", list.get(position).getContent());
            intent.putExtra("url", list.get(position).getUrl());
            intent.putExtra("postType", list.get(position).getType());
//            intent.putExtra("gallery", list.get(position).getGallery());
//            intent.putExtra("amrit", list.get(position).getAmrit());
//            intent.putExtra("byParts", list.get(position).getByParts());
            intent.putExtra("createdAt", list.get(position).getCreatedAt());
            intent.putExtra("position", String.valueOf(position));
            intent.putExtra("adTitle", list.get(position).getTitle());
            intent.putExtra("adLink", list.get(position).getLink());
            Toast.makeText(applicationContext, "link: "+list.get(position).getLinkType(), Toast.LENGTH_SHORT).show();
            if (list.get(position).getLinkType().equalsIgnoreCase(null)) {
                intent.putExtra("linkType", "");
            } else {
                intent.putExtra("linkType", list.get(position).getLinkType());
            }
            applicationContext.startActivity(intent);
            return true; // Indicate that the event was handled
        });

        // Set up individual item click listeners
        popup.getMenu().findItem(R.id.delete).setOnMenuItemClickListener(item -> {
            String storageImage = list.get(position).getUrl();
            Call<DeleteGetaModal> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .deleteAmritGalleryImage("application/json", Extensions.getBearerToken(),list.get(position).getId().toString());

            call.enqueue(new Callback<DeleteGetaModal>() {
                @Override
                public void onResponse(Call<DeleteGetaModal> call, Response<DeleteGetaModal> response) {
                    DeleteGetaModal imagesResponse = response.body();
                    if (response.isSuccessful()) {
                        if (imagesResponse.getStatus()) {
                            list.remove(position);
                            notifyDataSetChanged();
                            notifyItemRemoved(position);
                            Toast.makeText(applicationContext, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                    Log.d("errorr",t.getMessage());
                    Toast.makeText(applicationContext, "Failed to delete!", Toast.LENGTH_LONG).show();
                }
            });

            return true; // Indicate that the event was handled
        });


        popup.show();//showing popup menu
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ImageNameTextView;
        ImageView imageView, moreOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ImageNameTextView = itemView.findViewById(R.id.ImageNameTextView);
            imageView = itemView.findViewById(R.id.imageView);
            moreOptions = itemView.findViewById(R.id.moreOptions);
        }
    }
}
