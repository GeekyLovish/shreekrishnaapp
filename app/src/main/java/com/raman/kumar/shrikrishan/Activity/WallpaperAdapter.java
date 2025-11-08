package com.raman.kumar.shrikrishan.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.comments.getAllComments.AllComentsModel;
import com.raman.kumar.modals.comments.getAllComments.Datum;
import com.raman.kumar.modals.comments.postLike.PostLikeModel;
import com.raman.kumar.modals.comments.whoLikes.WhoLikeModel;
import com.raman.kumar.modals.getaModal.DeleteGetaModal;
import com.raman.kumar.shrikrishan.CommentFiles.CommentActivity;
import com.raman.kumar.shrikrishan.CommentFiles.ReplyActivity;
import com.raman.kumar.shrikrishan.Pojo.PostLIkePojo;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.adminPanel.addPosts.ImageUploadInfo;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.application.MyApp;
import com.raman.kumar.shrikrishan.loginfiles.LoginActivity;
import com.raman.kumar.shrikrishan.model.CommentsResponse;
import com.raman.kumar.shrikrishan.model.CommonResponse;
import com.raman.kumar.shrikrishan.model.LikePostResponse;
import com.raman.kumar.shrikrishan.model.LikesResponse;
import com.raman.kumar.shrikrishan.util.Comment;
import com.raman.kumar.shrikrishan.util.PhotoFullPopupWindowForGallery;
import com.raman.kumar.shrikrishan.util.PrefHelper;
import com.raman.kumar.shrikrishan.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Aman on 20/2/2019.
 */

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.MyViewHolder> {

    private List<ImageUploadInfo> imageList;
    ShareFacebook shareFacebook;
    ProgressDialog newProgressDialog;
    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    Context mContext;
    SharedPreferences sharedPreferences;
    FirebaseAuth mAuth;
    String likeType = "";
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    PrefHelper prefHelper;
    PostLIkePojo userPostLike;
    //   List<Comment> commentList;
    PostLIkePojo postLIkePojo;
    final ArrayList<Datum> commentList = new ArrayList<>();


    public WallpaperAdapter(List<ImageUploadInfo> arrayList, Context context) {
        setHasStableIds(true);
        this.imageList = arrayList;
        this.mContext = context;

        prefHelper = new PrefHelper(mContext);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wallpaper_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final String postId = imageList.get(position).getId();
        final String imageUrl = imageList.get(position).getUrl();
        final String title = imageList.get(position).getTitle();
        final String content = imageList.get(position).getContent();
        final String dtStart = imageList.get(position).getCreatedAt();

        // Load Image with Glide
        Glide.with(mContext)
                .load(imageUrl)
                .into(holder.imageView);

        // Set Ad Title
        holder.adsTextView.setText(imageList.get(position).getAdTitle());

        // Format and Set Date and Time
        Date date = parseDate(dtStart);
        if (date != null) {
            String newDate = String.valueOf(DateFormat.format("dd MMM yyyy", date));
            String newTime = String.valueOf(DateFormat.format("hh:mm a", date));
            holder.createdAtTextView.setText(newDate);
            holder.createdTimeTextView.setText(newTime);
        }

        // Set Content
        holder.textview1.setText(Html.fromHtml(content));

        // Set OnClick and LongClick for Image
        setImageViewOnClickListeners(holder, imageUrl, content, postId, position);

        // Set OnClick for Comments
        holder.postComments.setOnClickListener(v -> openComments(postId, imageUrl));

        // Set OnClick for Like Button
        setLikeButtonClickListener(holder, postId);

        // Set OnClick for React Layout
        setReactLayoutClickListener(holder);

        // Set OnClick for Share Post
        holder.sharePost.setOnClickListener(v -> shareImageFromUrl(imageUrl));

        // Set Learn More Button Visibility and Action
        setLearnMoreButton(holder, position);

        // Set Link Text View
        String url = imageList.get(position).getTitle();
        holder.linkTextView.setText(url);
        setLinkButton(holder, url);
    }

    private Date parseDate(String dtStart) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.parse(dtStart);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setImageViewOnClickListeners(MyViewHolder holder, String imageUrl, String content, String postId, int position) {
        holder.imageView.setOnClickListener(view -> new PhotoFullPopupWindowForGallery(
                mContext, R.layout.popup_photo_full_gallery, view, imageUrl, null, Html.fromHtml(content), postId, position,
                prefHelper.getuId(), mAuth, myRef, new ArrayList<>(), database));

        holder.imageView.setOnLongClickListener(view -> {
            showDialog(imageUrl, content);
            return true;
        });
    }

    private void openComments(String postId, String imageUrl) {
        if (prefHelper.getLogedIn().equals("no") || prefHelper.getLogedIn().equals("")) {
            showLoginDialog();
        } else {
            Intent intent = new Intent(mContext, CommentActivity.class);
            intent.putExtra("post_id", postId);
            intent.putExtra("post_image", imageUrl);
            intent.putExtra("section", "amrit");
            mContext.startActivity(intent);
        }
    }

    private void setLikeButtonClickListener(MyViewHolder holder, String postId) {
        holder.postLikeButton.setOnClickListener(v -> {
            if (prefHelper.getLogedIn().equals("no")) {
                showLoginDialog();
            } else {
                handleLikeButtonClick(holder, postId);
            }
        });
    }

    private void handleLikeButtonClick(MyViewHolder holder, String postId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", prefHelper.getAuthToken());

        if ("Like".equals(holder.postLikeButton.getTag())) {
            likePost(headers, postId, holder);
        } else {
            removeLike(headers, postId, holder);
        }
    }

    private void likePost(Map<String, String> headers, String postId, MyViewHolder holder) {
        Call<PostLikeModel> call = RetrofitClient.getInstance()
                .getApi().likePost(headers, postId, likeType);
        call.enqueue(new Callback<PostLikeModel>() {
            @Override
            public void onResponse(Call<PostLikeModel> call, Response<PostLikeModel> response) {
                handleLikePostResponse(response, holder);
            }
            @Override
            public void onFailure(Call<PostLikeModel> call, Throwable t) {
                Toast.makeText(mContext, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error", "Message:" + t.getMessage());
            }
        });
    }

    private void removeLike(Map<String, String> headers, String postId, MyViewHolder holder) {
        Call<DeleteGetaModal> call = RetrofitClient.getInstance()
                .getApi().removeLike(headers, postId);
        call.enqueue(new Callback<DeleteGetaModal>() {
            @Override
            public void onResponse(Call<DeleteGetaModal> call, Response<DeleteGetaModal> response) {
//                handleRemoveLikeResponse(response, holder);
            }
            @Override
            public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                Toast.makeText(mContext, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error", "Message:" + t.getMessage());
            }
        });
    }

    private void handleLikePostResponse(Response<PostLikeModel> response, MyViewHolder holder) {
        if (response.isSuccessful()) {
            PostLikeModel likeResponse = response.body();
            if (likeResponse.getStatus()) {
                MediaPlayer mPlayer = MediaPlayer.create(mContext, R.raw.bell);
                mPlayer.start();
                Log.d("msg", likeResponse.getMessage());
            } else {
                Toast.makeText(mContext, likeResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleRemoveLikeResponse(Response<CommonResponse> response, MyViewHolder holder) {
        if (response.isSuccessful()) {
            CommonResponse likeResponse = response.body();
            if (likeResponse.getSuccess()) {
                holder.postLikeButton.setTag("Like");
                holder.likeIcon.setImageResource(R.drawable.like_fb);
                holder.likeText.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
                holder.likeText.setText("Like");
                holder.likeText.setTypeface(null, Typeface.NORMAL);
                Log.d("msg", likeResponse.getMsg());
            } else {
                Toast.makeText(mContext, likeResponse.getMsg(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setReactLayoutClickListener(MyViewHolder holder) {
        holder.postLikeButton.setOnLongClickListener(v -> {
            holder.reactLayout.setVisibility(View.VISIBLE);
            return true;
        });
    }

    private void setLearnMoreButton(MyViewHolder holder, int position) {
        if (imageList.get(position).getLinkType().isEmpty() || imageList.get(position).getLinkType().equalsIgnoreCase("null")) {
            holder.learnMoreRelativeLayout.setVisibility(View.GONE);
        } else {
            holder.learnMoreRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setLinkButton(MyViewHolder holder, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        final String finalUrl = url;
        holder.learnMoreButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));
            mContext.startActivity(browserIntent);
        });
    }

    private void shareImageFromUrl(String fullImage) {
        Picasso.get().load(fullImage).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ((WallpaperActivity) mContext).showProgressDialog();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.share_link));
//                intent.setPackage("com.whatsapp");
                intent.putExtra(Intent.EXTRA_STREAM, getBitmapFromView(bitmap));
                mContext.startActivity(Intent.createChooser(intent, "Share Image"));
                ((WallpaperActivity) mContext).progress.dismiss();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    private Uri getBitmapFromView(Bitmap bitmap) {

        Uri bmpUri = null;
        try {
            File file = new File(mContext.getExternalCacheDir(), System.currentTimeMillis() + ".jpg");

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void numberOfLikes(TextView likeTextView, String post_id) {

//        voteTypeLike.setVisibility(View.GONE);
//        voteTypeLove.setVisibility(View.GONE);

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept","application/json");
        headers.put("Authorization","Bearer "+prefHelper.getAuthToken());
        Call<WhoLikeModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .likes("application/json", Extensions.getBearerToken(),post_id);

        call.enqueue(new Callback<WhoLikeModel>() {
            @Override
            public void onResponse(Call<WhoLikeModel> call, Response<WhoLikeModel> response) {
                WhoLikeModel likeResponse = response.body();
                if (response.isSuccessful()) {
                    if (likeResponse.getStatus()) {
//                        likeTextView.setText(likeResponse.getData().get());
//                        likes.addAll(likeResponse.getLikesCount().getLikes());
                        Toast.makeText(mContext, likeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("msg",likeResponse.getMessage());

                    } else {
                        Log.d("msg",likeResponse.getMessage());
//                        Toast.makeText(mContext, likeResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Log.d("msg",likeResponse.getMessage());
//                    Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WhoLikeModel> call, Throwable t) {
                Toast.makeText(mContext, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error","Message:"+t.getMessage());
            }
        });



//        DatabaseReference likesRef = myRef.child("postLikes").child(post_id);
//        likesRef.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    if (dataSnapshot.getChildrenCount() == 1) {
//                        likeTextView.setText(dataSnapshot.getChildrenCount() + " like");
//                    } else {
//                        likeTextView.setText(dataSnapshot.getChildrenCount() + " likes");
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

//    private void isLike(String post_id, LinearLayout postLikeButton, MyViewHolder holder) {
//
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Accept","application/json");
//        headers.put("Authorization","Bearer "+prefHelper.getAuthToken());
//
//        Call<LikePostResponse> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .isliked(headers, prefHelper.getuId(), post_id);
//
//        call.enqueue(new Callback<LikePostResponse>() {
//            @Override
//            public void onResponse(Call<LikePostResponse> call, retrofit2.Response<LikePostResponse> response) {
//                LikePostResponse likeResponse = response.body();
//                if (response.isSuccessful()) {
//                    if (likeResponse.getSuccess()) {
//                        if (likeResponse.getPostlike() != null)
//                        {
//                            userPostLike = likeResponse.getPostlike().getLikedata();
//                                    if (userPostLike != null && userPostLike.getUser_id() != null &&
//                                            userPostLike.getUser_id().equalsIgnoreCase(prefHelper.getuId())) {
//                                        if (userPostLike.getType().equalsIgnoreCase("like")) {
//                                            holder.likeIcon.setImageResource(R.drawable.like_);
//                                            holder.likeText.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
//                                            holder.likeText.setText("Like");
//                                            holder.likeText.setTypeface(null, Typeface.BOLD);
//                                            postLikeButton.setTag("Liked");
//
//                                        } else {
//                                            holder.likeIcon.setImageResource(R.drawable.love);
//                                            holder.likeText.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
//                                            holder.likeText.setText("Love");
//                                            holder.likeText.setTypeface(null, Typeface.BOLD);
//                                            postLikeButton.setTag("Liked");
//                                        }
////                                    MediaPlayer mPlayer = MediaPlayer.create(mContext, R.raw.facebook_pop);
////                                    mPlayer.start();
//                                    } else {
//                                        postLikeButton.setTag("Like");
//                                        holder.likeIcon.setImageResource(R.drawable.like_fb);
//                                        holder.likeText.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
//                                    }
//                        }
//                        else
//                        {
//                            postLikeButton.setTag("Like");
//                            holder.likeIcon.setImageResource(R.drawable.like_fb);
//                            holder.likeText.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
//                        }
//
//
//                        Log.d("msg",likeResponse.getMsg());
//
//                    } else {
//                        Log.d("msg",likeResponse.getMsg());
////                        Toast.makeText(mContext, likeResponse.getMsg(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else
//                {
//                    Log.d("msg",likeResponse.getMsg());
////                    Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LikePostResponse> call, Throwable t) {
//                Toast.makeText(mContext, "Failed to load!", Toast.LENGTH_LONG).show();
//                Log.d("error","Message:"+t.getMessage());
//            }
//        });
//
//
//////        DatabaseReference likesRef = myRef.child("postLikes").child(post_id).child(prefHelper.getuId());
//////        likesRef.addValueEventListener(new ValueEventListener() {
//////            @Override
//////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//////                if (dataSnapshot.exists()) {
//////                    postLIkePojo = dataSnapshot.getValue(PostLIkePojo.class);
//////                    userPostLike.add(postLIkePojo);
//////                    if (postLIkePojo != null) {
//////                        for (int i = 0; i < userPostLike.size(); i++) {
//////                            if (!prefHelper.getuId().equalsIgnoreCase("")) {
//////                                if (userPostLike.get(i).getUser_id().equalsIgnoreCase(prefHelper.getuId())) {
//////                                    if (userPostLike.get(i).getType().equalsIgnoreCase("like")) {
//////                                        holder.likeIcon.setImageResource(R.drawable.like_);
//////                                        holder.likeText.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
//////                                        holder.likeText.setText("Like");
//////                                        holder.likeText.setTypeface(null, Typeface.BOLD);
//////                                        postLikeButton.setTag("Liked");
//////                                    } else {
//////                                        holder.likeIcon.setImageResource(R.drawable.love);
//////                                        holder.likeText.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
//////                                        holder.likeText.setText("Love");
//////                                        holder.likeText.setTypeface(null, Typeface.BOLD);
//////                                        postLikeButton.setTag("Liked");
//////                                    }
//////                                } else {
//////                                    postLikeButton.setTag("Like");
//////                                    holder.likeIcon.setImageResource(R.drawable.like_fb);
//////                                    holder.likeText.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
//////                                }
//////                            }
//////                        }
//////                    } else {
//////
//////                    }
//////                } else {
//////                    postLikeButton.setTag("Like");
//////                }
//////            }
//////
//////            @Override
//////            public void onCancelled(@NonNull DatabaseError databaseError) {
//////
//////            }
//////        });
//    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void retrieveAllComments(final String post_id, MyViewHolder holder) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept","application/json");
        headers.put("Authorization","Bearer "+prefHelper.getAuthToken());

        Call<AllComentsModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .getComments("application/json", Extensions.getBearerToken(),post_id);

        call.enqueue(new Callback<AllComentsModel>() {
            @Override
            public void onResponse(Call<AllComentsModel> call, retrofit2.Response<AllComentsModel> response) {
                AllComentsModel commentseResponse = response.body();
                if (response.isSuccessful()) {
                    if (commentseResponse.getStatus()) {
                        Log.d("msg",commentseResponse.getMessage());
                        commentList.addAll(commentseResponse.getData());
                        holder.commentLinearLayout.setVisibility(View.VISIBLE);

                        if (commentList.size() > 1) {
                            holder.userName.setText(commentList.get(commentList.size() - 1).getCommentedBy().getName());
                            if(commentList.get(commentList.size() - 1).getComment().isEmpty()){
                                holder.userComment.setVisibility(View.GONE);
                            }else{
                                holder.userComment.setVisibility(View.VISIBLE);
                                holder.userComment.setText(commentList.get(commentList.size() - 1).getComment());
                            }
                            if((commentList.get(commentList.size() - 1).getImage())!=null){
                                holder.commentImage.setVisibility(View.VISIBLE);
                                Glide.with(mContext).load(commentList.get(commentList.size() - 1).getImage()).placeholder(R.drawable.placeholder_image).into(holder.commentImage);
                            }else{
                                holder.commentImage.setVisibility(View.GONE);
                            }
                            holder.userComment.setText(commentList.get(commentList.size() - 1).getComment());
                            holder.timeTextView.setReferenceTime(Long.valueOf(commentList.get(commentList.size() - 1).getCreatedAt()));
                            if (commentList.get(commentList.size() - 1).getCommentedBy().getProfilePic().equals("")) {
                                holder.comment_attach.setVisibility(View.GONE);
                            } else {
                                holder.comment_attach.setVisibility(View.VISIBLE);
                                updateProfileImages(commentList.get(commentList.size() - 1).getCommentedBy().getProfilePic(), holder.comment_attach);
                            }

                            showProfileImage(commentList.get(commentList.size() - 1).getId().toString(), holder.profilePicture);

                            if (commentList.get(commentList.size() - 1).getCommentedBy().getName().isEmpty()) {
                                holder.profilePicture.setVisibility(View.GONE);
                                holder.userName.setVisibility(View.GONE);
                            } else {
                                holder.profilePicture.setVisibility(View.VISIBLE);
                                holder.userName.setVisibility(View.VISIBLE);
                            }


                            for (int i = commentList.get(commentList.size() - 1).getReplies().size() - 1; i > commentList.get(commentList.size() - 1).getReplies().size() - 2; i--) {
                                if (commentList.get(commentList.size() - 1).getReplies().get(i).getComment().equals("")) {
                                    holder.profilesubPictures.setVisibility(View.GONE);
                                    holder.profilesubPicture.setVisibility(View.GONE);
                                    holder.usersubComment.setVisibility(View.GONE);
                                    holder.usersubname.setVisibility(View.GONE);
                                } else if (commentList.get(commentList.size() - 1).getReplies().size() > 0) {
                                    holder.usersubComment.setVisibility(View.VISIBLE);
                                    holder.usersubname.setVisibility(View.VISIBLE);
                                    holder.profilesubPicture.setVisibility(View.VISIBLE);
                                    holder.profilesubPictures
                                            .setVisibility(View.VISIBLE);
                                    showProfileImage(commentList.get(commentList.size() - 1).getReplies().get(i).getUserId().toString(), holder.profilesubPictures);

                                    holder.usersubComment.setText(commentList.get(commentList.size() - 1).getReplies().get(i).getComment());
                                    holder.usersubname.setText(commentList.get(commentList.size() - 1).getReplies().get(i).getCommentedBy().getName());
                                    if (commentList.get(commentList.size() - 1).getReplies().size() > 1)
                                        holder.profilesubPicture.setText(commentList.get(commentList.size() - 1).getReplies().size() + " Replies");
                                    if (commentList.get(commentList.size() - 1).getReplies().size() == 1)
                                        holder.profilesubPicture.setText(commentList.get(commentList.size() - 1).getReplies().size() + " Reply");

                                }
                            }

                            holder.commentLinearLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    Intent intent = new Intent(mContext, ReplyActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    intent.putExtra("postId", post_id);
//                                    intent.putExtra("commentId", commentList.get(commentList.size() - 1).getId());
//                                    intent.putExtra("name", commentList.get(commentList.size() - 1).getCommentedBy().getName());
//                                    intent.putExtra("comment", commentList.get(commentList.size() - 1).getComment());
//                                    intent.putExtra("image", commentList.get(commentList.size() - 1).getCommentedBy().getProfilePic());
////                                    intent.putExtra("token", commentList.get(commentList.size() - 1).getUser_token());
//                                    intent.putExtra("comment_user_id", commentList.get(commentList.size() - 1).getUserId());
//                                    intent.putExtra("user_image", commentList.get(commentList.size() - 1).getCommentedBy().getProfilePic());
//                                    intent.putExtra("section", "Gallery");
//                                    mContext.startActivity(intent);
                                }
                            });
                        } else {
                            holder.commentLinearLayout.setVisibility(View.GONE);
                        }



                    } else {
                        Toast.makeText(mContext, commentseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {

                    Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AllComentsModel> call, Throwable t) {
                Toast.makeText(mContext, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error","Message:"+t.getMessage());

            }
        });


//        Query query = myRef.child(mContext.getString(R.string.fieldComments)).child(post_id);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                    Comment comment = snapshot.getValue(Comment.class);
//
//                    String image = "";
//                    if (comment.getImage() == null || comment.getImage().equals("")) {
//                        image = comment.getImage();
//                    }
//                    commentList.add(new Comment(Objects.requireNonNull(comment).getComment(), comment.getDate_added(), comment.getUser_name(), comment.getUserId(), comment.getComment_likes(), comment.getUserReplies(), comment.getUser_token(), image, snapshot.getKey()));
//
////                    commentList.add(new Comment(Objects.requireNonNull(comment).getComment(), comment.getDate_added(), comment.getUser_name(), comment.getUserId(), comment.getComment_likes(), comment.getUserReplies(), comment.getUser_token()));
//                    if (commentList.size() == 1) {
//                        holder.commentText.setText("Comment (" + commentList.size() + ")");
//                    } else {
//                        holder.commentText.setText("Comments (" + commentList.size() + ")");
//                    }
//
//                }
//                holder.commentLinearLayout.setVisibility(View.VISIBLE);
//                if (commentList.size() > 1) {
//                    holder.userName.setText(commentList.get(commentList.size() - 1).getUser_name());
//                    holder.userComment.setText(commentList.get(commentList.size() - 1).getComment());
//                    holder.timeTextView.setReferenceTime(commentList.get(commentList.size() - 1).getDate_added());
//                    if (commentList.get(commentList.size() - 1).getImage().equals("")) {
//                        holder.comment_attach.setVisibility(View.GONE);
//                    } else {
//                        holder.comment_attach.setVisibility(View.VISIBLE);
//                        updateProfileImages(commentList.get(commentList.size() - 1).getImage(), holder.comment_attach);
//                    }
//
//                    showProfileImage(commentList.get(commentList.size() - 1).getUserId(), holder.profilePicture);
//
//                    if (commentList.get(commentList.size() - 1).getUser_name().isEmpty()) {
//                        holder.profilePicture.setVisibility(View.GONE);
//                        holder.userName.setVisibility(View.GONE);
//                    } else {
//                        holder.profilePicture.setVisibility(View.VISIBLE);
//                        holder.userName.setVisibility(View.VISIBLE);
//                    }
//
//
//                    for (int i = commentList.get(commentList.size() - 1).getUserReplies().size() - 1; i > commentList.get(commentList.size() - 1).getUserReplies().size() - 2; i--) {
//                        if (commentList.get(commentList.size() - 1).getUserReplies().get(i).getUserComment().equals("")) {
//                            holder.profilesubPictures.setVisibility(View.GONE);
//                            holder.profilesubPicture.setVisibility(View.GONE);
//                            holder.usersubComment.setVisibility(View.GONE);
//                            holder.usersubname.setVisibility(View.GONE);
//                        } else if (commentList.get(commentList.size() - 1).getUserReplies().size() > 0) {
//                            holder.usersubComment.setVisibility(View.VISIBLE);
//                            holder.usersubname.setVisibility(View.VISIBLE);
//                            holder.profilesubPicture.setVisibility(View.VISIBLE);
//                            holder.profilesubPictures
//                                    .setVisibility(View.VISIBLE);
//                            showProfileImage(commentList.get(commentList.size() - 1).getUserReplies().get(i).getUserId(), holder.profilesubPictures);
//
//                            holder.usersubComment.setText(commentList.get(commentList.size() - 1).getUserReplies().get(i).getUserComment());
//                            holder.usersubname.setText(commentList.get(commentList.size() - 1).getUserReplies().get(i).getUserName());
//                            if (commentList.get(commentList.size() - 1).getUserReplies().size() > 1)
//                                holder.profilesubPicture.setText(commentList.get(commentList.size() - 1).getUserReplies().size() + " Replies");
//                            if (commentList.get(commentList.size() - 1).getUserReplies().size() == 1)
//                                holder.profilesubPicture.setText(commentList.get(commentList.size() - 1).getUserReplies().size() + " Reply");
//
//                        }
//                    }
//
//                    holder.commentLinearLayout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(mContext, ReplyActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra("postId", post_id);
//                            intent.putExtra("commentId", commentList.get(commentList.size() - 1).getId());
//                            intent.putExtra("name", commentList.get(commentList.size() - 1).getUser_name());
//                            intent.putExtra("comment", commentList.get(commentList.size() - 1).getComment());
//                            intent.putExtra("image", commentList.get(commentList.size() - 1).getImage());
//                            intent.putExtra("token", commentList.get(commentList.size() - 1).getUser_token());
//                            intent.putExtra("comment_user_id", commentList.get(commentList.size() - 1).getUserId());
//                            intent.putExtra("user_image", commentList.get(commentList.size() - 1).getUser_image());
//                            intent.putExtra("section", "Gallery");
//                            mContext.startActivity(intent);
//                        }
//                    });
//                } else {
//                    holder.commentLinearLayout.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
    }

    private void showProfileImage(String userId, final ImageView imageView) {
        if (userId == null) {
            userId = "";
        }

        try {
            final StorageReference storageRef =
                    FirebaseStorage.getInstance().getReference();
            storageRef.child("userProfileImage/" + userId).getDownloadUrl()
                    .addOnSuccessListener(uri -> updateProfileImage(uri, imageView))
                    .addOnFailureListener(e -> imageView.setImageResource(R.drawable.ic_account));
        } catch (Exception ignored) {
            imageView.setImageResource(R.drawable.ic_account);
        }

    }

    private void updateProfileImage(Object uri, final ImageView imageView) {
        if (imageView != null) {
            Glide.with(MyApp.getApplication())
                    .load(uri)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_account)
                            .error(R.drawable.ic_account)
                            .centerCrop()
                            .circleCrop())
                    .into(imageView);
        }
    }

    private void updateProfileImages(Object uri, final ImageView imageView) {
        if (imageView != null) {
            Glide.with(MyApp.getApplication())
                    .load(uri)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_account)
                            .error(R.drawable.ic_account)
                            .fitCenter()
                    )
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = Util.customizedialog(mContext, R.layout.imagepopup);
                    ImageView image = dialog.findViewById(R.id.image);
                    ImageView close = dialog.findViewById(R.id.close);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Glide.with(MyApp.getApplication())
                            .load(uri)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_account)
                                    .error(R.drawable.ic_account)
                                    .fitCenter()
                            )
                            .into(image);
                    dialog.show();
                }
            });
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textview1, likeText, commentText, likeTextView, linkTextView, buttonText;
        ImageView imageView, likeIcon, comment_attach;
        LinearLayout postComments, postLikeButton;
        LinearLayout sharePost;
        CardView reactLayout;
        RelativeLayout learnMoreButton;
        ImageButton imgButtonOne, imgButtonTwo;
        RelativeLayout learnMoreRelativeLayout;
        TextView adsTextView, createdAtTextView, createdTimeTextView;
        private ItemClickListener clickListener;
        TextView userName, userComment, profilesubPicture, usersubname, usersubComment;
        RelativeTimeTextView timeTextView;
        ImageView profilePicture, profilesubPictures,commentImage;
        LinearLayout commentLinearLayout;

        public MyViewHolder(View view) {
            super(view);
            timeTextView = view.findViewById(R.id.timeTextView);
            profilePicture = view.findViewById(R.id.profilePicture);
            profilesubPictures = view.findViewById(R.id.profilesubPictures);
            userName = view.findViewById(R.id.userName);
            comment_attach = view.findViewById(R.id.comment_attach);
            userComment = view.findViewById(R.id.userComment);
            commentImage = view.findViewById(R.id.commentImage);
            profilesubPicture = view.findViewById(R.id.profilesubPicture);
            usersubname = view.findViewById(R.id.usersubname);
            usersubComment = view.findViewById(R.id.usersubComment);
            textview1 = view.findViewById(R.id.title);
            likeText = view.findViewById(R.id.likeText);
            imageView = view.findViewById(R.id.wallpaper_image);
            postComments = view.findViewById(R.id.postComments);
            postLikeButton = view.findViewById(R.id.postLikeButton);
            adsTextView = view.findViewById(R.id.adsTextView);
            createdAtTextView = view.findViewById(R.id.createdAtTextView);
            sharePost = view.findViewById(R.id.sharePost);
            reactLayout = view.findViewById(R.id.reactLayout);
            imgButtonTwo = view.findViewById(R.id.imgButtonTwo);
            imgButtonOne = view.findViewById(R.id.imgButtonOne);
            likeIcon = view.findViewById(R.id.likeIcon);
            commentText = view.findViewById(R.id.commentText);
            likeTextView = view.findViewById(R.id.likeTextView);
            linkTextView = view.findViewById(R.id.linkTextView);
            buttonText = view.findViewById(R.id.buttonText);
            createdTimeTextView = view.findViewById(R.id.createdTimeTextView);
            learnMoreButton = view.findViewById(R.id.learnMoreButton);
            learnMoreRelativeLayout = view.findViewById(R.id.learnMoreRelativeLayout);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }
    }

    public void setImageListner(ShareFacebook shareFacebook) {
        this.shareFacebook = shareFacebook;
    }

    private void showLoginDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Login Confirmation");
        alertDialog.setMessage("You have to login for like and comments");
        alertDialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public void showDialog(final String images, final String title) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_downloadshare);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final ImageView download = dialog.findViewById(R.id.download);
        final ImageView shareButton = dialog.findViewById(R.id.fb_share_button);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isStoragePermissionGranted()) {
                    shareFacebook.downloadImageFromURL(images, title);
                    dialog.dismiss();
                }
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                newProgressDialog = shareFacebook.showProgress();
                shareFacebook.shareImage(images, title, newProgressDialog);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public interface ShareFacebook {
        void shareImage(String image, String title, ProgressDialog dialog);

        ProgressDialog showProgress();

        void dismissProgressDialog(ProgressDialog progressDialog);

        ProgressDialog showDialoadingProgress();

        void downloadImageFromURL(String image, String title);
    }

    public void downloadImage(String images, final String title) {
        Picasso.get()
                .load(images)
                .into(new Target() {
                          @Override
                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                              try {
                                  String root = Environment.getExternalStorageDirectory().toString();
                                  File myDir = new File(root + "/srikrishna/images");

                                  if (!myDir.exists()) {
                                      myDir.mkdirs();
                                  }

                                  String name = new Date().toString() + ".jpg";
                                  myDir = new File(myDir, name);
                                  FileOutputStream out = new FileOutputStream(myDir);
                                  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                                  out.flush();
                                  out.close();

                                  MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, title, title);
                                  try {
                                      new Handler().postDelayed(new Runnable() {
                                          @Override
                                          public void run() {
                                              Toast.makeText(mContext, " Download completed", Toast.LENGTH_SHORT).show();
                                          }
                                      }, 4000);
                                  } catch (Exception e) {
                                      e.printStackTrace();

                                  }
                              } catch (Exception e) {
                                  e.printStackTrace();
                                  // some action
                              }
                          }

                          @Override
                          public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                              Toast.makeText(mContext, " Download failed", Toast.LENGTH_SHORT).show();

                          }

                          @Override
                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                              Toast.makeText(mContext, " Download failes", Toast.LENGTH_SHORT).show();
                          }
                      }
                );
    }
}