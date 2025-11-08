package com.raman.kumar.shrikrishan.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.raman.kumar.modals.comments.whoLikes.Datum;
import com.raman.kumar.shrikrishan.Pojo.PostLIkePojo;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.application.MyApp;
import com.raman.kumar.shrikrishan.model.LikePostResponse;
import com.raman.kumar.shrikrishan.model.LoginResponse;
import com.raman.kumar.shrikrishan.util.PostLike;
import com.raman.kumar.shrikrishan.util.PrefHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.ViewHolder> {
    Context context;
    List<Datum> userPostLikesList;
    PrefHelper prefHelper;

    public LikesAdapter(Context applicationContext, List<Datum> userPostLikesList) {
        context = applicationContext;
        this.userPostLikesList = userPostLikesList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.likes_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.usernameTextView.setText(userPostLikesList.get(position).getLikedBy().getName());
        if (userPostLikesList.get(position).getType().equalsIgnoreCase("like")) {
            holder.likeLoveImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
        } else {
            holder.likeLoveImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart));
        }
        /*Glide.with(MyApp.getApplication())
                .load(userPostLikesList.get(position).getImage())
                .apply(new RequestOptions()
                        .override(200, 150)
                        .centerCrop()
                        .circleCrop())
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .into(holder.userImageView);*/
        updateProfileImage(userPostLikesList.get(position).getLikedBy().getProfilePic(), holder.userImageView);

    }



    private void updateProfileImage(Object uri, final ImageView imageView) {
        if (imageView != null) {

            Glide.with(MyApp.getApplication())
                    .load(uri)
                    .apply(new RequestOptions()
                            .override(200, 150)
                            .centerCrop()
                            .circleCrop())
                    .placeholder(R.drawable.ic_account)
                    .error(R.drawable.ic_account)
                    .into(imageView);
        }
    }


    @Override
    public int getItemCount() {
        return userPostLikesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        ImageView userImageView, likeLoveImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            userImageView = itemView.findViewById(R.id.userImageView);
            likeLoveImageView = itemView.findViewById(R.id.likeLoveImageView);
        }
    }
}
