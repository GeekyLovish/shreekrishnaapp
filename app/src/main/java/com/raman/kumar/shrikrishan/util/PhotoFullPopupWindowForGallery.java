package com.raman.kumar.shrikrishan.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.comments.getAllComments.AllComentsModel;
import com.raman.kumar.modals.comments.getAllComments.Datum;
import com.raman.kumar.modals.comments.postLike.PostLikeModel;
import com.raman.kumar.modals.comments.whoLikes.WhoLikeModel;
import com.raman.kumar.modals.getaModal.DeleteGetaModal;
import com.raman.kumar.shrikrishan.CommentFiles.CommentActivity;
import com.raman.kumar.shrikrishan.CommentFiles.ReplyActivity;
import com.raman.kumar.shrikrishan.Constants;
import com.raman.kumar.shrikrishan.Pojo.PostLIkePojo;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.fbreaction.OnItemClickListener;
import com.raman.kumar.shrikrishan.fbreaction.ReactionPopup;
import com.raman.kumar.shrikrishan.fbreaction.ReactionsConfigBuilder;
import com.raman.kumar.shrikrishan.loginfiles.LoginActivity;
import com.raman.kumar.shrikrishan.model.CommentsResponse;
import com.raman.kumar.shrikrishan.model.CommonResponse;
import com.raman.kumar.shrikrishan.model.LikePostResponse;
import com.raman.kumar.shrikrishan.model.LikesResponse;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Dell- on 4/30/2018.
 */

public class PhotoFullPopupWindowForGallery extends PopupWindow {

    View view;
    Context mContext;
    PhotoView photoView;
    ProgressBar loading;
    ViewGroup parent;
    String user_id;
    FirebaseAuth mAuth;
    List<Datum> commentList;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    String post_id;
    int position;
    PrefHelper prefHelper;
    ProgressDialog progress;
    List<PostLIkePojo> userPostLike = new ArrayList<>();
    PostLIkePojo postLIkePojo;
    private static PhotoFullPopupWindowForGallery instance = null;
    String likeType = "";

    public PhotoFullPopupWindowForGallery(Context ctx, int layout, View v, String imageUrl, Bitmap bitmap, Spanned spanned,
                                          String post_id, int position, String user_id, FirebaseAuth mAuth, DatabaseReference myRef,
                                          List<Datum> commentList, FirebaseDatabase database) {

        super(((LayoutInflater) ((Activity)ctx).getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.popup_photo_full_gallery, null), ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        if (Build.VERSION.SDK_INT >= 21) {
            setElevation(5.0f);
        }

        this.mContext = ctx;
        this.view = getContentView();
        this.database = database;
        this.myRef = myRef;
        this.commentList = commentList;
        this.user_id = user_id;
        this.mAuth = mAuth;
        this.post_id = post_id;
        this.position = position;

        prefHelper = new PrefHelper(mContext);

        ImageButton closeButton = view.findViewById(R.id.ib_close);
        TextView title = view.findViewById(R.id.title);
        LinearLayout postLikeButton = view.findViewById(R.id.postLikeButton);
        LinearLayout postComments = view.findViewById(R.id.postComments);
        LinearLayout sharePost = view.findViewById(R.id.sharePost);
        TextView likeText = view.findViewById(R.id.likeText);
        TextView commentText = view.findViewById(R.id.commentText);
        ImageView likeIcon = view.findViewById(R.id.likeIcon);
        CardView reactLayout = view.findViewById(R.id.reactLayout);
        ImageButton imgButtonOne, imgButtonTwo;
        imgButtonOne = view.findViewById(R.id.imgButtonOne);
        imgButtonTwo = view.findViewById(R.id.imgButtonTwo);
        title.setText(spanned);
        setOutsideTouchable(true);

        setFocusable(true);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        //---------Begin customising this popup--------------------
        photoView = view.findViewById(R.id.image);
        loading = view.findViewById(R.id.loading);
        photoView.setMaximumScale(6);
        parent = (ViewGroup) photoView.getParent();
        // ImageUtils.setZoomable(imageView);
        //----------------------------
        if (bitmap != null) {
            loading.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 16) {
                parent.setBackground(new BitmapDrawable(mContext.getResources(), Constants.fastblur(Bitmap.createScaledBitmap(bitmap, 50, 50, true))));// ));
            } else {
                onPalette(Palette.from(bitmap).generate());

            }
            photoView.setImageBitmap(bitmap);
        } else {
            loading.setIndeterminate(true);
            loading.setVisibility(View.VISIBLE);
            Glide.with(ctx).asBitmap()
                    .load(imageUrl)
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            loading.setIndeterminate(false);
                            loading.setBackgroundColor(Color.LTGRAY);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            if (Build.VERSION.SDK_INT >= 16) {
                                parent.setBackground(new BitmapDrawable(mContext.getResources(), Constants.fastblur(Bitmap.createScaledBitmap(resource, 50, 50, true))));// ));
                            } else {
                                onPalette(Palette.from(resource).generate());

                            }
                            photoView.setImageBitmap(resource);

                            loading.setVisibility(View.GONE);
                            return false;
                        }
                    })


                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(photoView);

            showAtLocation(v, Gravity.CENTER, 0, 0);

            /*postLikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (prefHelper.getLogedIn().equals("no")) {
                        showLoginDialog();
                    } else {
                        if (postLikeButton.getTag() == "Like") {
                            PostLIkePojo postLIkePojo = new PostLIkePojo(prefHelper.getuId(), "like", prefHelper.getuName());
                            myRef.child("postLikes").child(post_id).child(prefHelper.getuId()).setValue(postLIkePojo);
                        } else {
                            postLikeButton.setTag("Like");
                            likeIcon.setImageResource(R.drawable.like_fb_white);
                            likeText.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
                            likeText.setText("Like");
                            likeText.setTypeface(null, Typeface.NORMAL);
                            myRef.child("postLikes").child(post_id).child(prefHelper.getuId()).removeValue();
                        }
                    }
                }
            });*/

            int margin = mContext.getResources().getDimensionPixelSize(R.dimen.margin_10);
            ReactionPopup popup = new ReactionPopup(mContext, new ReactionsConfigBuilder(mContext)
                    .withReactions(new int[]{
                            R.drawable.like_,
                            R.drawable.love
                    })
                    .withReactionTexts(R.array.reaction)
                    .withPopupColor(Color.LTGRAY)
                    .withReactionSize(mContext.getResources().getDimensionPixelSize(R.dimen.margin_40))
                    .withHorizontalMargin(margin)
                    .withVerticalMargin(margin / 2)
                    .withTextBackground(new ColorDrawable(Color.GRAY))
                    .withTextColor(Color.WHITE)
                    .withTextSize(mContext.getResources().getDimension(R.dimen.txt_6))
                    .build(), null, new OnItemClickListener() {
                @Override
                public void onItemClick() {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (prefHelper.getLogedIn().equals("no")) {
                        showLoginDialog();
                    } else {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", prefHelper.getAuthToken());

                        if (postLikeButton.getTag() == "Like") {
//                            PostLIkePojo postLIkePojo = new PostLIkePojo(prefHelper.getuId(), "like", prefHelper.getuName());
//                            myRef.child("postLikes").child(post_id).child(prefHelper.getuId()).setValue(postLIkePojo);

                            Call<PostLikeModel> call = RetrofitClient
                                    .getInstance()
                                    .getApi()
                                    .likePost(headers,post_id,"like");

                            call.enqueue(new Callback<PostLikeModel>() {
                                @Override
                                public void onResponse(Call<PostLikeModel> call, Response<PostLikeModel> response) {
                                    PostLikeModel likeResponse = response.body();
                                    if (response.isSuccessful()) {
                                        if (likeResponse.getStatus()) {
                                            MediaPlayer mPlayer = MediaPlayer.create(mContext, R.raw.bell);
                                            mPlayer.start();
                                            Log.d("msg",likeResponse.getMessage());
                                        } else {
                                            Toast.makeText(mContext, likeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<PostLikeModel> call, Throwable t) {
                                    Toast.makeText(mContext, "Failed to load!", Toast.LENGTH_LONG).show();
                                    Log.d("error","Message:"+t.getMessage());
                                }
                            });

                        } else {

//                            myRef.child("postLikes").child(post_id).child(prefHelper.getuId()).removeValue();

                            Call<DeleteGetaModal> call = RetrofitClient
                                    .getInstance()
                                    .getApi()
                                    .removeLike(headers,post_id);

                            call.enqueue(new Callback<DeleteGetaModal>() {
                                @Override
                                public void onResponse(Call<DeleteGetaModal> call, Response<DeleteGetaModal> response) {
                                    DeleteGetaModal likeResponse = response.body();
                                    if (response.isSuccessful()) {
                                        if (likeResponse.getStatus()) {
                                            postLikeButton.setTag("Like");
                                            likeIcon.setImageResource(R.drawable.like_fb_white);
                                            likeText.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
                                            likeText.setText("Like");
                                            likeText.setTypeface(null, Typeface.NORMAL);
                                            Log.d("msg",likeResponse.getMessage());

                                        } else {
                                            Toast.makeText(mContext, likeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {

                                        Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                                    Toast.makeText(mContext, "Failed to load!", Toast.LENGTH_LONG).show();
                                    Log.d("error","Message:"+t.getMessage());
                                }
                            });

        }
                    }
                }
            });

            popup.setReactionSelectedListener((position1) -> {

                if(position1 == 0 || position1 ==1){
                    if(position1 == 0){
                        likeType = "like";
                    } else {
                        likeType = "love";
                    }

                    reactLayout.setVisibility(View.GONE);
                    postLikeButton.setTag("Like");
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (prefHelper.getLogedIn().equals("no")) {
                        showLoginDialog();
                    } else {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", prefHelper.getAuthToken());
                        if (postLikeButton.getTag() == "Like") {
//                            PostLIkePojo postLIkePojo = new PostLIkePojo(prefHelper.getuId(), likeType, prefHelper.getuName());
//                            myRef.child("postLikes").child(post_id).child(prefHelper.getuId()).setValue(postLIkePojo);

                            Call<PostLikeModel> call = RetrofitClient
                                    .getInstance()
                                    .getApi()
                                    .likePost(headers,post_id,likeType);

                            call.enqueue(new Callback<PostLikeModel>() {
                                @Override
                                public void onResponse(Call<PostLikeModel> call, Response<PostLikeModel> response) {
                                    PostLikeModel likeResponse = response.body();
                                    if (response.isSuccessful()) {
                                        if (likeResponse.getStatus()) {
                                            MediaPlayer mPlayer = MediaPlayer.create(mContext, R.raw.bell);
                                            mPlayer.start();
                                            Log.d("msg",likeResponse.getMessage());
                                        } else {
                                            Toast.makeText(mContext, likeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<PostLikeModel> call, Throwable t) {
                                    Toast.makeText(mContext, "Failed to load!", Toast.LENGTH_LONG).show();
                                    Log.d("error","Message:"+t.getMessage());
                                }
                            });


                        } else {

//                            myRef.child("postLikes").child(post_id).child(prefHelper.getuId()).removeValue();

                            Call<DeleteGetaModal> call = RetrofitClient
                                    .getInstance()
                                    .getApi()
                                    .removeLike(headers,post_id);

                            call.enqueue(new Callback<DeleteGetaModal>() {
                                @Override
                                public void onResponse(Call<DeleteGetaModal> call, Response<DeleteGetaModal> response) {
                                    DeleteGetaModal likeResponse = response.body();
                                    if (response.isSuccessful()) {
                                        if (likeResponse.getStatus()) {
                                            postLikeButton.setTag("Like");
                                            likeIcon.setImageResource(R.drawable.like_fb_white);
                                            likeText.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
                                            likeText.setText("Like");
                                            likeText.setTypeface(null, Typeface.NORMAL);
                                            Log.d("msg",likeResponse.getMessage());

                                        } else {
                                            Toast.makeText(mContext, likeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                                    Toast.makeText(mContext, "Failed to load!", Toast.LENGTH_LONG).show();
                                    Log.d("error","Message:"+t.getMessage());
                                }
                            });


                        }
                    }
                }


                return true;
            });

            postLikeButton.setOnTouchListener(popup);


            /*postLikeButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    reactLayout.setVisibility(View.VISIBLE);
                    return true;
                }
            });*/

            /*imgButtonOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likeType = "like";
                    reactLayout.setVisibility(View.GONE);
                    postLikeButton.setTag("Like");
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (prefHelper.getLogedIn().equals("no")) {
                        showLoginDialog();
                    } else {
                        if (postLikeButton.getTag() == "Like") {
                            PostLIkePojo postLIkePojo = new PostLIkePojo(prefHelper.getuId(), likeType, prefHelper.getuName());
                            myRef.child("postLikes").child(post_id).child(prefHelper.getuId()).setValue(postLIkePojo);
                        } else {
                            postLikeButton.setTag("Like");
                            likeIcon.setImageResource(R.drawable.like_fb_white);
                            likeText.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
                            likeText.setText("Like");
                            likeText.setTypeface(null, Typeface.NORMAL);
                            myRef.child("postLikes").child(post_id).child(prefHelper.getuId()).removeValue();
                        }
                    }
                }
            });

            imgButtonTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likeType = "love";
                    reactLayout.setVisibility(View.GONE);
                    postLikeButton.setTag("Like");
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (prefHelper.getLogedIn().equals("no")) {
                        showLoginDialog();
                    } else {
                        if (postLikeButton.getTag() == "Like") {
                            PostLIkePojo postLIkePojo = new PostLIkePojo(prefHelper.getuId(), likeType, prefHelper.getuName());
                            myRef.child("postLikes").child(post_id).child(prefHelper.getuId()).setValue(postLIkePojo);
                        } else {
                            postLikeButton.setTag("Like");
                            likeIcon.setImageResource(R.drawable.like_fb_white);
                            likeText.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
                            likeText.setText("Like");
                            likeText.setTypeface(null, Typeface.NORMAL);
                            myRef.child("postLikes").child(post_id).child(prefHelper.getuId()).removeValue();
                        }
                    }
                }
            });*/


            sharePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareImageFromUrl(imageUrl);
                }
            });

            postComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (prefHelper.getLogedIn().equals("no")) {
                        showLoginDialog();
                    } else {
                        Intent intent = new Intent(mContext, CommentActivity.class);
                        intent.putExtra("post_id", post_id);
                        intent.putExtra("post_image", imageUrl);
                        mContext.startActivity(intent);
                    }
                }
            });
            if (!prefHelper.getLogedIn().equals("no")) {
//                isLike(post_id, postLikeButton, myRef, user_id, likeText, likeIcon);
                numberOfLikes(post_id, myRef, likeText);
                retrieveAllComments(post_id, myRef, commentText, user_id);
            }
        }
        //------------------------------
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

    private void numberOfLikes(String post_id, DatabaseReference myRef, TextView likeTextView) {

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
//                        if (likeResponse.getLikesDetails().getTotallike() == 1)
//                        {
//                            likeTextView.setText(likeResponse.getLikesDetails().getTotallike() + " like");
//                        }else
//                        {
//                            likeTextView.setText(likeResponse.getLikesDetails().getTotallike() + " likes");
//
//                        }

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
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }


/*
    private void isLike(String post_id, LinearLayout postLikeButton, DatabaseReference myRef, String user_id, TextView likeText, ImageView likeIcon) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept","application/json");
        headers.put("Authorization","Bearer "+prefHelper.getAuthToken());

        Call<LikePostResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .isliked(headers,prefHelper.getuId(),post_id);

        call.enqueue(new Callback<LikePostResponse>() {
            @Override
            public void onResponse(Call<LikePostResponse> call, retrofit2.Response<LikePostResponse> response) {
                LikePostResponse likeResponse = response.body();
                if (response.isSuccessful()) {
                    if (likeResponse.getSuccess()) {
                        if (likeResponse.getPostlike() != null)
                        {
                            for (int i = 0; i < userPostLike.size(); i++) {
                                if (userPostLike.get(i).getUser_id().equalsIgnoreCase(prefHelper.getuId())) {
                                    if (userPostLike.get(i).getType().equalsIgnoreCase("like")) {
                                        likeIcon.setImageResource(R.drawable.like_);
                                        likeText.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                                        likeText.setText("Like");
                                        likeText.setTypeface(null, Typeface.BOLD);
                                        postLikeButton.setTag("Liked");
                                    } else {
                                        likeIcon.setImageResource(R.drawable.love);
                                        likeText.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                                        likeText.setText("Love");
                                        likeText.setTypeface(null, Typeface.BOLD);
                                        postLikeButton.setTag("Liked");
                                    }
                                } else {
                                    postLikeButton.setTag("Like");
                                    likeIcon.setImageResource(R.drawable.like_fb_white);
                                    likeText.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
                                }
                            }
                        }else {
                            postLikeButton.setTag("Like");
                        }

                        Log.d("msg",likeResponse.getMsg());

                    } else {
                        Log.d("msg",likeResponse.getMsg());
//                        Toast.makeText(mContext, likeResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Log.d("msg",likeResponse.getMsg());
//                    Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LikePostResponse> call, Throwable t) {
                Toast.makeText(mContext, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error","Message:"+t.getMessage());
            }
        });


//        DatabaseReference likesRef = myRef.child("postLikes").child(post_id).child(prefHelper.getuId());
//        likesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    postLIkePojo = dataSnapshot.getValue(PostLIkePojo.class);
//                    userPostLike.add(postLIkePojo);
//                    if (postLIkePojo != null) {
//                        for (int i = 0; i < userPostLike.size(); i++) {
//                            if (userPostLike.get(i).getUser_id().equalsIgnoreCase(prefHelper.getuId())) {
//                                if (userPostLike.get(i).getType().equalsIgnoreCase("like")) {
//                                    likeIcon.setImageResource(R.drawable.like_);
//                                    likeText.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
//                                    likeText.setText("Like");
//                                    likeText.setTypeface(null, Typeface.BOLD);
//                                    postLikeButton.setTag("Liked");
//                                } else {
//                                    likeIcon.setImageResource(R.drawable.love);
//                                    likeText.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
//                                    likeText.setText("Love");
//                                    likeText.setTypeface(null, Typeface.BOLD);
//                                    postLikeButton.setTag("Liked");
//                                }
//                            } else {
//                                postLikeButton.setTag("Like");
//                                likeIcon.setImageResource(R.drawable.like_fb_white);
//                                likeText.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
//                            }
//                        }
//                    } else {
//
//                    }
//                } else {
//                    postLikeButton.setTag("Like");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }
*/

    private void retrieveAllComments(String post_id, DatabaseReference myRef, TextView commentText, String user_id) {

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
                        if (commentList.size() == 1) {
                            commentText.setText("Comment (" + commentList.size() + ")");
                        } else {
                            commentText.setText("Comments (" + commentList.size() + ")");
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
//                commentList = new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Comment comment = snapshot.getValue(Comment.class);
//                    String image="";
//                    if (comment.getImage()!=null && !comment.getImage().equals("")){
//                        image=comment.getImage();
//                    }
//                    commentList.add(new Comment(Objects.requireNonNull(comment).getComment(), comment.getDate_added(), comment.getUser_name(), comment.getUserId(), comment.getComment_likes(), comment.getUserReplies(), comment.getUser_token(),image,snapshot.getKey()));
//                    if (commentList.size() == 1) {
//                        commentText.setText("Comment (" + commentList.size() + ")");
//                    } else {
//                        commentText.setText("Comments (" + commentList.size() + ")");
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
    }

    private void shareImageFromUrl(String fullImage) {
        Picasso.get().load(fullImage).into(new com.squareup.picasso.Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                loading.setIndeterminate(true);
                loading.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.share_link));
                intent.setPackage("com.whatsapp");
                intent.putExtra(Intent.EXTRA_STREAM, getBitmapFromView(bitmap));
                mContext.startActivity(Intent.createChooser(intent, "Share Image"));
                loading.setIndeterminate(false);
                loading.setVisibility(View.GONE);
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

    public void onPalette(Palette palette) {
        if (null != palette) {
            ViewGroup parent = (ViewGroup) photoView.getParent().getParent();
            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
        }
    }

}
