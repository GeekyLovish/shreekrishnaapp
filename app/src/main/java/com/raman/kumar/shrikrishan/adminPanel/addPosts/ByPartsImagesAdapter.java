package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import android.content.Intent;
import android.text.Html;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.getaModal.DeleteGetaModal;
import com.raman.kumar.modals.pictureByParts.pictureByPart.Datum;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.model.CommonResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ByPartsImagesAdapter extends RecyclerView.Adapter<ByPartsImagesAdapter.ViewHolder> {

    ShowPicsByParts applicationContext;
    List<Datum> list;
    Firebase postReference = new Firebase("https://shri-krishan.firebaseio.com/By_Parts_Images_Database");

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    ByPartsImagesAdapter(ShowPicsByParts applicationContext, List<Datum> list) {
        this.applicationContext = applicationContext;
        this.list = list;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.by_pictures_edit_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
            Intent intent = new Intent(applicationContext, UploadByPartsActivity.class);
            intent.putExtra("id", list.get(position).getId().toString());
            intent.putExtra("url", list.get(position).getUrl().toString());
//            intent.putExtra("section", list.get(position).getSection());
            applicationContext.startActivity(intent);
            return true; // Indicate that the event was handled
        });

        // Set up individual item click listeners
        popup.getMenu().findItem(R.id.delete).setOnMenuItemClickListener(item -> {

            Call<DeleteGetaModal> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .deleteImageByParts("application/json", Extensions.getBearerToken(),list.get(position).getId().toString());

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
            imageView = itemView.findViewById(R.id.imageView);
            moreOptions = itemView.findViewById(R.id.moreOptions);
        }
    }
}
