package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import android.content.Context;
import android.content.Intent;
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

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.raman.kumar.AudiosModal.Datum;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.getaModal.DeleteGetaModal;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.model.CommonResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAudioAdapter extends RecyclerView.Adapter<EditAudioAdapter.ViewHolder> {
    Context context;
    List<Datum> audioList;
    Firebase postReference = new Firebase("https://shri-krishan.firebaseio.com/All_Audio_Uploads_Database");
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public EditAudioAdapter(EditAudioActivity editAudioActivity, List<Datum> audioList) {
        context = editAudioActivity;
        this.audioList = audioList;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_audio_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleTextView.setText(audioList.get(position).getTitle());
        holder.timeTextView.setText(audioList.get(position).getDuration());
        holder.editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogForOptions(position, holder.editImageView);
            }
        });
    }

    private void openDialogForOptions(int position, ImageView moreOptions) {
        PopupMenu popup = new PopupMenu(context, moreOptions);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        popup.getMenu().findItem(R.id.edit).setOnMenuItemClickListener(item -> {
            // Start VideoUploadActivity with the necessary data
            Intent intent = new Intent(context, AudioPostActivity.class);
            intent.putExtra("id", audioList.get(position).getId().toString());
            intent.putExtra("audio_type", audioList.get(position).getType());
            intent.putExtra("created_at", audioList.get(position).getCreatedAt());
            intent.putExtra("display_name", audioList.get(position).getTitle());
            intent.putExtra("song_duration", audioList.get(position).getDuration());
            intent.putExtra("song_path", audioList.get(position).getPath());
            intent.putExtra("title", audioList.get(position).getTitle());
            context.startActivity(intent);
            return true; // Indicate that the event was handled
        });
        // Set up individual item click listeners
        popup.getMenu().findItem(R.id.delete).setOnMenuItemClickListener(item -> {
            // Start VideoUploadActivity with the necessary data

            Call<DeleteGetaModal> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .deleteAudio("application/json", Extensions.getBearerToken(),audioList.get(position).getId().toString());

            call.enqueue(new Callback<DeleteGetaModal>() {
                @Override
                public void onResponse(Call<DeleteGetaModal> call, Response<DeleteGetaModal> response) {
                    DeleteGetaModal imagesResponse = response.body();
                    if (response.isSuccessful()) {
                        if (imagesResponse.getStatus()) {
                            audioList.remove(position);
                            notifyDataSetChanged();
                            notifyItemRemoved(position);
                            Toast.makeText(context, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                    Toast.makeText(context, "Failed to delete!", Toast.LENGTH_LONG).show();
                }
            });
            return true; // Indicate that the event was handled
        });


        popup.show();//showing popup menu
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, timeTextView;
        ImageView editImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            editImageView = itemView.findViewById(R.id.editImageView);
        }
    }
}
