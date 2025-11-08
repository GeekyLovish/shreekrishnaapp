package com.raman.kumar.shrikrishan.Adapter;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.devs.readmoreoption.ReadMoreOption;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.comments.getAllComments.AllComentsModel;
import com.raman.kumar.modals.comments.getAllComments.Datum;
import com.raman.kumar.modals.comments.postLike.PostLikeModel;
import com.raman.kumar.modals.comments.whoLikes.WhoLikeModel;
import com.raman.kumar.modals.gallary.getGallary.GalleryData;
import com.raman.kumar.modals.gallary.getGallary.LikedByMeType;
import com.raman.kumar.modals.getaModal.DeleteGetaModal;
import com.raman.kumar.shrikrishan.Activity.GalleryActivity;
import com.raman.kumar.shrikrishan.Activity.WallpaperActivity;
import com.raman.kumar.shrikrishan.CommentFiles.CommentActivity;
import com.raman.kumar.shrikrishan.FullScreenDialogFragment;
import com.raman.kumar.shrikrishan.ImageDownloadManager;
import com.raman.kumar.shrikrishan.OnGalleryAdapterListener;
import com.raman.kumar.shrikrishan.PhotoFullPopupWindow;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.APICalls;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.application.MyApp;
import com.raman.kumar.shrikrishan.fbreaction.ReactionPopup;
import com.raman.kumar.shrikrishan.fbreaction.ReactionsConfigBuilder;
import com.raman.kumar.shrikrishan.loginfiles.LoginActivity;
import com.raman.kumar.shrikrishan.util.BackgroundNotificationService;
import com.raman.kumar.shrikrishan.util.CommonListeners;
import com.raman.kumar.shrikrishan.util.PrefHelper;
import com.raman.kumar.shrikrishan.util.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.raman.kumar.shrikrishan.fbreaction.ReactionPopup;
//import com.raman.kumar.shrikrishan.fbreaction.ReactionsConfigBuilder;

/**
 * Created by y on 20/2/18.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private List<GalleryData> imageList;

    CommonListeners commonListeners;
    private OnGalleryAdapterListener refreshListener;
    ProgressDialog progressDialog;
    ShareFacebook shareFacebook;
    ProgressDialog newProgressDialog;

    Context mContext;
    String likeType = "", url, from;
    FirebaseAuth mAuth;
    PrefHelper prefHelper;
    List<com.raman.kumar.modals.comments.whoLikes.Datum> userPostLikesList = new ArrayList<>();
    final ArrayList<Datum> commentList = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View likeCommentStatus;
        ImageView voteTypeLike, voteTypeLove;
        TextView commentText;
        TextView userComment, userName, likeCount;
        RelativeTimeTextView timeTextView;
        ImageView profilePicture, commentImage;

        private ItemClickListener clickListener;
        TextView likeText, likeTextView, linkTextView, buttonText, createdAtTextView, createdTimeTextView, updateProfile;
        ImageView imageView, likeIcon;
        TextView textview1;
        LinearLayout postComments, postLikeButton, commentLinearLayout;
        LinearLayout sharePost, votesCount, downloadImage;
        RelativeLayout learnMoreButton;
        ImageView profilesubPictures, replyImage;
        ImageButton imgButtonOne, imgButtonTwo;
        TextView profilesubPicture;
        AdView adView;
        TextView adsTextView, usersubComment, usersubname;
        RelativeLayout learnMoreRelativeLayout;

        public MyViewHolder(View view) {
            super(view);

            likeCommentStatus = view.findViewById(R.id.likeCommentStatus);
            voteTypeLike = view.findViewById(R.id.voteTypeLike);
            voteTypeLove = view.findViewById(R.id.voteTypeLove);

            commentText = view.findViewById(R.id.commentText);
//            comment_attach = view.findViewById(R.id.comment_attach);
            votesCount = view.findViewById(R.id.votesCount);
            adView = view.findViewById(R.id.adView);
            commentLinearLayout = view.findViewById(R.id.commentLinearLayout);
            profilesubPictures = view.findViewById(R.id.profilesubPictures);
            textview1 = view.findViewById(R.id.title);
            likeText = view.findViewById(R.id.likeText);
            imageView = view.findViewById(R.id.wallpaper_image);
            postComments = view.findViewById(R.id.postComments);
            postLikeButton = view.findViewById(R.id.postLikeButton);
            usersubname = view.findViewById(R.id.usersubname);
            usersubComment = view.findViewById(R.id.usersubComment);
            replyImage = view.findViewById(R.id.replyImage);
            profilesubPicture = view.findViewById(R.id.profilesubPicture);
            sharePost = view.findViewById(R.id.sharePost);
            downloadImage = view.findViewById(R.id.downloadImage);
            likeIcon = view.findViewById(R.id.likeIcon);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            userComment = itemView.findViewById(R.id.userComment);
            commentImage = itemView.findViewById(R.id.commentImage);
            userName = itemView.findViewById(R.id.userName);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            likeCount = itemView.findViewById(R.id.likeCount);
            userName.setTypeface(userName.getTypeface(), Typeface.BOLD);

            likeTextView = view.findViewById(R.id.likeTextView);
            linkTextView = view.findViewById(R.id.linkTextView);
            adsTextView = view.findViewById(R.id.adsTextView);
            createdAtTextView = view.findViewById(R.id.createdAtTextView);
            updateProfile = view.findViewById(R.id.updateprofile);
            learnMoreButton = view.findViewById(R.id.learnMoreButton);
            buttonText = view.findViewById(R.id.buttonText);
            createdTimeTextView = view.findViewById(R.id.createdTimeTextView);
            learnMoreRelativeLayout = view.findViewById(R.id.learnMoreRelativeLayout);
            //      reactLayout = view.findViewById(R.id.reactLayout);
            imgButtonTwo = view.findViewById(R.id.imgButtonTwo);
            imgButtonOne = view.findViewById(R.id.imgButtonOne);
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

    public GalleryAdapter(List<GalleryData> arrayList, Context context, String url, String from, OnGalleryAdapterListener onGalleryAdapterListener) {
        setHasStableIds(true);
        this.imageList = arrayList;
        this.mContext = context;
        this.url = url;
        this.from = from;
        this.refreshListener = onGalleryAdapterListener;

        prefHelper = new PrefHelper(mContext);
        progressDialog = new ProgressDialog(mContext);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wallpaper_layout, parent, false);
/*
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(lp);*/

        itemView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        itemView.requestLayout();

        return new MyViewHolder(itemView);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        setupAdVisibility(holder, position);
        setupContentDisplay(holder, position);

        Boolean isBlocked = imageList.get(position).getIsBlocked();


        setupImage(holder, position);
        setupDateTime(holder, position);
        setupComments(holder, position, isBlocked);
        setupReactionPopup(holder, position);
        setupButtonActions(holder, position);
    }

    private void setupAdVisibility(MyViewHolder holder, int position) {
        holder.adView.setVisibility(position % 3 == 0 && position != 0 ? View.VISIBLE : View.GONE);
        AdRequest adRequest = new AdRequest.Builder().build();
        holder.adView.loadAd(adRequest);
    }

    private void setupContentDisplay(MyViewHolder holder, int position) {
        //holder.commentLinearLayout.setVisibility(View.GONE);
        ReadMoreOption readMoreOption = new ReadMoreOption.Builder(mContext)
                .textLength(3, ReadMoreOption.TYPE_LINE)
                .moreLabel("See more")
                .lessLabel("See less")
                .moreLabelColor(Color.BLUE)
                .lessLabelColor(Color.BLUE)
                .labelUnderLine(true)
                .expandAnimation(true)
                .build();
        String title = imageList.get(position).getContent();
        String addsTitle = imageList.get(position).getTitle();
        String link = imageList.get(position).getLink();

        title = title.replace("\\u003C", "<").replace("\\u003E", ">");
        if (title.startsWith("\"") && title.endsWith("\"")) {
            title = title.substring(1, title.length() - 1);
        }
        Spanned spannedContent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spannedContent = Html.fromHtml(title, Html.FROM_HTML_MODE_LEGACY);
        } else {
            spannedContent = Html.fromHtml(title);
        }


        readMoreOption.addReadMoreTo(holder.textview1, spannedContent);
        holder.adsTextView.setText(addsTitle);
        holder.linkTextView.setText(link);
    }

    private void setupImage(MyViewHolder holder, int position) {
        String images = imageList.get(position).getUrl();
        Glide.with(mContext)
                .load(images)
                .apply(new RequestOptions()
                        .fitCenter()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL))
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imageView);


        holder.imageView.setOnClickListener(view -> openFullScreenDialog(position));
        holder.imageView.setOnLongClickListener(view -> {
            showDialog(images, imageList.get(position).getContent());
            return true;
        });
    }

    private void setupDateTime(MyViewHolder holder, int position) {
        String createdAt = imageList.get(position).getCreatedAt();
        String updatedAt = imageList.get(position).getUpdatedAt();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = format.parse(updatedAt);
            if (date != null) {
                holder.createdAtTextView.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date));
                holder.createdTimeTextView.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setupComments(MyViewHolder holder, int position, Boolean isBlocked) {
        String postId = imageList.get(position).getId().toString();
        String imageUrl = imageList.get(position).getUrl();
        holder.likeCommentStatus.setOnClickListener(v -> openComments(postId, imageUrl, isBlocked));
        holder.postComments.setOnClickListener(v -> openComments(postId, imageUrl, isBlocked));
        holder.commentText.setOnClickListener(v -> openComments(postId, imageUrl, isBlocked));
        retrieveAllComments(postId, holder);
    }


    private void setupReactionPopup(MyViewHolder holder, int position) {
        int margin = mContext.getResources().getDimensionPixelSize(R.dimen.margin_10);

        // Get initial like status and type for the current post
        boolean isLikedByMe = imageList.get(position).getLikedByMe(); // Track like status for each post

        // Check if LikedByMeType is null before calling getType()
        String likeTypeByMe = (imageList.get(position).getLikedByMeType() != null)
                ? imageList.get(position).getLikedByMeType().getType()
                : ""; // Default to an empty string if null

        updateLikeUI(holder, likeTypeByMe);

        // Use AtomicInteger to allow modification inside lambda expression
        AtomicInteger likesCount = new AtomicInteger(imageList.get(position).getLikesCount());

        if (likesCount.get() == 0) {
            holder.voteTypeLike.setVisibility(View.GONE);
            holder.voteTypeLove.setVisibility(View.GONE);
            holder.likeTextView.setVisibility(View.GONE);
        } else {
            List<String> likesTypes = imageList.get(position).getLikesTypes();
            if (likesTypes.contains("like") && likesTypes.contains("love")) {
                // Show both views
                holder.voteTypeLike.setVisibility(View.VISIBLE);
                holder.voteTypeLove.setVisibility(View.VISIBLE);
            } else if (likesTypes.contains("love")) {
                // Show only the "love" view
                holder.voteTypeLove.setVisibility(View.VISIBLE);
                holder.voteTypeLike.setVisibility(View.GONE);
            } else if (likesTypes.contains("like")) {
                // Show only the "like" view
                holder.voteTypeLike.setVisibility(View.VISIBLE);
                holder.voteTypeLove.setVisibility(View.GONE);
            } else {
                // Hide both views if neither is present
                holder.voteTypeLike.setVisibility(View.GONE);
                holder.voteTypeLove.setVisibility(View.GONE);
            }
            holder.likeTextView.setVisibility(View.VISIBLE);
            holder.likeTextView.setText(String.valueOf(likesCount.get()));
        }

        ReactionPopup popup = new ReactionPopup(mContext, new ReactionsConfigBuilder(mContext)
                .withReactions(new int[]{R.drawable.like_, R.drawable.love})
                .withReactionTexts(R.array.reaction)
                .withPopupColor(Color.LTGRAY)
                .withReactionSize(mContext.getResources().getDimensionPixelSize(R.dimen.margin_40))
                .withHorizontalMargin(margin)
                .withVerticalMargin(margin / 2)
                .withTextBackground(new ColorDrawable(Color.GRAY))
                .withTextColor(Color.WHITE)
                .withTextSize(mContext.getResources().getDimension(R.dimen.txt_6))
                .build());

        popup.setReactionSelectedListener((reactionPosition) -> {
            String selectedType = reactionPosition == 0 ? "like" : "love";

            if (prefHelper.getLogedIn().equals("no")) {
                showLoginDialog();
                return true;
            }

            if (isLikedByMe) {
                // Already liked
                if (likeTypeByMe.equals(selectedType)) {
                    // Same → Unlike
                    handleRemoveLikeAction(holder, position);
                    imageList.get(position).setLikedByMe(false);
                    imageList.get(position).setLikesCount(likesCount.get() - 1);
                    setLikeType(position, "");
                    updateLikeUI(holder, "");
                } else {
                    // Different → Replace without count change
                    handleRemoveLikeAction(holder, position); // remove old
                    handleLikeAction(holder, position, selectedType);
                    setLikeType(position, selectedType);// add new
                    updateLikeUI(holder, selectedType);
                }
            } else {
                // Not liked → Add new reaction
                handleLikeAction(holder, position, selectedType);
                imageList.get(position).setLikedByMe(true);
                setLikeType(position, selectedType);
                imageList.get(position).setLikesCount(likesCount.get() + 1);
                updateLikeUI(holder, selectedType);
            }
            notifyDataSetChanged();
            return true;
        });
//
        holder.postLikeButton.setOnTouchListener(popup);

    }

    private void setLikeType(int position, String selectedType) {
        if (imageList.get(position).getLikedByMeType() != null) {
            imageList.get(position).getLikedByMeType().setType(selectedType);
        } else {
            LikedByMeType type = new LikedByMeType();
            type.setType(selectedType);
            imageList.get(position).setLikedByMeType(type);
        }
    }


    private void setupButtonActions(MyViewHolder holder, int position) {
        String postId = imageList.get(position).getId().toString();
        holder.sharePost.setOnClickListener(v -> shareImageFromUrl(imageList.get(position).getUrl()));
        holder.downloadImage.setOnClickListener(view -> {
            ImageDownloadManager.downloadImage(mContext, imageList.get(position).getUrl());
        });

        holder.votesCount.setOnClickListener(v -> APICalls.peopleWhoLikes(prefHelper, postId, from, mContext));
        setupLearnMoreButton(holder, position);
    }

//    private void setupLearnMoreButton(MyViewHolder holder, int position) {
//        String url = imageList.get(position).getLink();
//        String linkType = imageList.get(position).getLinkType();
//        holder.learnMoreRelativeLayout.setVisibility(url.isEmpty() ? View.GONE : View.VISIBLE);
//        holder.buttonText.setText(getButtonText(linkType));
//
//        if (!url.startsWith("http")) url = "http://" + url;
//        String finalUrl = url;
//        holder.learnMoreButton.setOnClickListener(v -> {
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));
//            mContext.startActivity(browserIntent);
//        });
//    }
//private void setupLearnMoreButton(MyViewHolder holder, int position) {
//    String url = imageList.get(position).getLink();
//    String linkType = imageList.get(position).getLinkType();
//
//    System.out.println("fsfhhadsfhakd " +url);
//    System.out.println("fsfhhadsfhakd " +linkType);
//
//    // Hide button if the URL is empty
//    holder.learnMoreRelativeLayout.setVisibility(url == null || url.isEmpty() ? View.GONE : View.VISIBLE);
//    holder.buttonText.setText(getButtonText(linkType));
//
//    // Fix URL if needed
//    if (url != null && !url.startsWith("http")) {
//        url = "http://" + url;
//    }
//
//    // Validate URL before setting the listener
//    String finalUrl = url;
//    holder.learnMoreButton.setOnClickListener(v -> {
//        if (finalUrl == null || finalUrl.isEmpty() || !isValidUrl(finalUrl)) {
//            Toast.makeText(mContext, "Invalid URL", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        try {
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));
//            mContext.startActivity(browserIntent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(mContext, "No browser found to open this link", Toast.LENGTH_SHORT).show();
//        }
//    });
//}

    private void setupLearnMoreButton(MyViewHolder holder, int position) {
        String url = imageList.get(position).getLink();
        String linkType = imageList.get(position).getLinkType();


        holder.learnMoreRelativeLayout.setVisibility(url == null || url.isEmpty() ? View.GONE : View.VISIBLE);
        holder.buttonText.setText(getButtonText(linkType));

        if (url != null && !url.startsWith("http")) {
            url = "http://" + url; // Add scheme if missing
        }

        String finalUrl = url;
        holder.learnMoreButton.setOnClickListener(v -> {

            try {
                // Create an intent for Chrome
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));
                browserIntent.setPackage("com.android.chrome"); // Target Google Chrome specifically

                if (browserIntent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(browserIntent);
                } else {
                    Toast.makeText(mContext, "Chrome is not installed on this device", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(mContext, "Failed to open link: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getButtonText(String linkType) {
        switch (linkType.toLowerCase()) {
            case "learn":
                return "Learn More";
            case "watch":
                return "Watch Video";
            case "web":
                return "Visit Website";
            case "page":
                return "Visit FB Page";
            default:
                return "";
        }
    }

    private void openFullScreenDialog(int position) {
        FullScreenDialogFragment dialogFragment = new FullScreenDialogFragment(
                imageList.get(position),
                prefHelper.getuId(), refreshListener);
        if (from.equals("gallery")) {
            ((GalleryActivity) mContext).commonListeners = dialogFragment.getListener();
        } else if (from.equals("wallpaper")) {
            ((WallpaperActivity) mContext).commonListeners = dialogFragment.getListener();
        }
        dialogFragment.setOnDialogCloseListener(new FullScreenDialogFragment.OnDialogCloseListener() {
            @Override
            public void onDialogClosed(String likeTypeByMe, Integer like_count, List<String> likesType) {
                imageList.get(position).getLikedByMeType().setType(likeTypeByMe);
                imageList.get(position).setLikesCount(like_count);
                imageList.get(position).setLikesTypes(likesType);
                notifyItemChanged(position);
            }
        });
        dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "signature");

    }

    private void updateLikeUI(MyViewHolder holder, String likeType) {
        if (likeType == null || likeType.isEmpty()) {
            // Default "unliked" state
            holder.likeIcon.setImageResource(R.drawable.like_fb);
            holder.likeText.setTextColor(mContext.getResources().getColor(R.color.textGreyColor));
            holder.likeText.setText("Like");
            holder.likeText.setTypeface(null, Typeface.NORMAL);
            holder.postLikeButton.setTag("Like");
        } else {
            // Liked state based on likeType
            int likeColor = likeType.equalsIgnoreCase("like") ? R.color.colorPrimary : R.color.colorAccent;
            int likeIcon = likeType.equalsIgnoreCase("like") ? R.drawable.like_ : R.drawable.love;
            if (likeType.equalsIgnoreCase("like")) {
                holder.voteTypeLike.setVisibility(View.VISIBLE);
            } else {
                holder.voteTypeLove.setVisibility(View.VISIBLE);
            }
            holder.likeIcon.setImageResource(likeIcon);
            holder.likeText.setTextColor(mContext.getResources().getColor(likeColor));
            holder.likeText.setText(likeType.substring(0, 1).toUpperCase() + likeType.substring(1));
            holder.likeText.setTypeface(null, Typeface.BOLD);
            holder.postLikeButton.setTag("Liked");
        }
    }

    private void handleLikeAction(MyViewHolder holder, int position, String likeType) {
        String postId = imageList.get(position).getId().toString();
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + prefHelper.getAuthToken());

        Call<PostLikeModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .likePost(headers, postId, likeType);

        call.enqueue(new Callback<PostLikeModel>() {
            @Override
            public void onResponse(Call<PostLikeModel> call, Response<PostLikeModel> response) {
                if (response.isSuccessful() && response.body().getStatus()) {
                    updateLikeUI(holder, response.body().getData().getType());
                    MediaPlayer.create(mContext, R.raw.bell).start();
                } else {
                    Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostLikeModel> call, Throwable t) {
                Toast.makeText(mContext, "Failed to load!", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Method to handle removing like
    private void handleRemoveLikeAction(MyViewHolder holder, int position) {
        // Make an API call to remove the like
        // Example: callRemoveLikeApi(imageList.get(position).getId());
        System.out.println("Like removed");
        String postId = imageList.get(position).getId().toString();

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + prefHelper.getAuthToken());

        Call<DeleteGetaModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .removeLike(headers, postId);

        call.enqueue(new Callback<DeleteGetaModal>() {
            @Override
            public void onResponse(Call<DeleteGetaModal> call, Response<DeleteGetaModal> response) {
                DeleteGetaModal likeResponse = response.body();
                if (response.isSuccessful()) {
                    if (likeResponse.getStatus()) {

//                        updateLikeUI(holder, "");
                    } else {
                        Toast.makeText(mContext, likeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                Toast.makeText(mContext, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error", "Message:" + t.getMessage());
            }
        });


    }


    private void openComments(final String post_id, String images, Boolean isBlocked) {

        if (isBlocked) {
            Toast.makeText(mContext, "User blocked you", Toast.LENGTH_SHORT).show();
            return;
        }


//        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (prefHelper.getLogedIn().equals("no")) {
            showLoginDialog();
        } else {
            Intent intent = new Intent(mContext, CommentActivity.class);
            intent.putExtra("post_id", post_id);
            intent.putExtra("post_image", images);
            intent.putExtra("section", mContext instanceof GalleryActivity ? "gallery" : "amrit");
            mContext.startActivity(intent);
        }
    }

    private void retrieveAllComments(final String post_id, MyViewHolder holder) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + prefHelper.getAuthToken());

        Call<AllComentsModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .getComments("application/json", Extensions.getBearerToken(), post_id);

        call.enqueue(new Callback<AllComentsModel>() {
            @Override
            public void onResponse(Call<AllComentsModel> call, retrofit2.Response<AllComentsModel> response) {
                AllComentsModel commentseResponse = response.body();
                if (response.isSuccessful()) {
                    if (commentseResponse.getStatus()) {
                        Log.d("msg", commentseResponse.getMessage());
                        commentList.clear();
                        commentList.addAll(commentseResponse.getData());
                        if (!commentList.isEmpty()) {

                            String userename = commentList.get(commentList.size() - 1).getCommentedBy().getName();
                            String comment_id = commentList.get(commentList.size() - 1).getId().toString();
                            String comment = commentList.get(commentList.size() - 1).getComment();
                            String commentImage = commentList.get(commentList.size() - 1).getImage();
                            String postID = commentList.get(commentList.size() - 1).getPostId().toString();
                            String image = commentList.get(commentList.size() - 1).getCommentedBy().getProfilePic();
                            String time = commentList.get(commentList.size() - 1).getCreatedAt();
                            String user_image = commentList.get(commentList.size() - 1).getCommentedBy().getProfilePic();
                            Log.d("pic", commentList.get(commentList.size() - 1).getCommentedBy().getProfilePic());
                            String user_id = commentList.get(commentList.size() - 1).getUserId().toString();
                            holder.commentLinearLayout.setVisibility(View.VISIBLE);
                            int count = commentList.size();
                            holder.commentText.setText(count < 1 ? "" : count + " Comment" + (count > 1 ? "s" : ""));
                            holder.commentLinearLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    Intent intent = new Intent(mContext, ReplyActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    intent.putExtra("postId", postID);
//                                    intent.putExtra("commentId",comment_id );
//                                    intent.putExtra("name", userename);
//                                    intent.putExtra("comment", comment);
//                                    intent.putExtra("image", image);
//                                    intent.putExtra("comment_user_id", user_id);
//                                    intent.putExtra("user_image", user_image);
//                                    intent.putExtra("section", "Gallery");
//                                    mContext.startActivity(intent);
//                                    Toast.makeText(mContext, commentList.get(commentList.size() - 1).getComment(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            if (commentList.size() >= 1) {
                                holder.userName.setText(userename);
                                if (comment.isEmpty()) {
                                    holder.userComment.setVisibility(View.GONE);
                                } else {
                                    holder.userComment.setVisibility(View.VISIBLE);
                                    holder.userComment.setText(comment);
                                }
                                if ((commentList.get(commentList.size() - 1).getImage()) != null) {
                                    holder.commentImage.setVisibility(View.VISIBLE);
                                    Glide.with(mContext).load(commentImage).placeholder(R.drawable.placeholder_image).into(holder.commentImage);
                                    holder.commentImage.setOnClickListener(v -> new PhotoFullPopupWindow(mContext, R.layout.popup_photo_full, v, commentImage, null));

                                } else {
                                    holder.commentImage.setVisibility(View.GONE);
                                }
                                holder.timeTextView.setText(time);
//                                if (commentList.get(commentList.size() - 1).getCommentedBy().getProfilePic().equals("")) {
////                                    holder.comment_attach.setVisibility(View.GONE);
//                                } else {
////                                    holder.comment_attach.setVisibility(View.VISIBLE);
////                                    updateProfileImages(image, holder.comment_attach);
//                                }
                                updateProfileImage(user_image, holder.profilePicture);

                            } else {
                                holder.commentLinearLayout.setVisibility(View.GONE);
                            }

//                            showProfileImage(commentList.get(commentList.size() - 1).getUserId(), holder.profilePicture);

                            if (userename.isEmpty()) {
                                holder.profilePicture.setVisibility(View.GONE);
                                holder.userName.setVisibility(View.GONE);
                            } else {
                                holder.profilePicture.setVisibility(View.VISIBLE);
                                holder.userName.setVisibility(View.VISIBLE);
                            }

                            if (commentList.get(commentList.size() - 1).getReplies().size() != 0) {
                                for (int i = commentList.get(commentList.size() - 1).getReplies().size() - 1; i > commentList.get(commentList.size() - 1).getReplies().size() - 2; i--) {
                                    if (commentList.get(commentList.size() - 1).getReplies().get(i).getComment().equals("")) {
                                        holder.profilesubPictures.setVisibility(View.GONE);
                                        holder.profilesubPicture.setVisibility(View.GONE);
                                        holder.usersubComment.setVisibility(View.GONE);
                                        holder.usersubname.setVisibility(View.GONE);
                                        holder.replyImage.setVisibility(View.GONE);
                                    } else if (commentList.get(commentList.size() - 1).getReplies().size() > 0) {
                                        holder.usersubComment.setVisibility(View.VISIBLE);
                                        holder.usersubname.setVisibility(View.VISIBLE);
                                        holder.profilesubPicture.setVisibility(View.VISIBLE);
                                        holder.profilesubPictures.setVisibility(View.VISIBLE);
//                                            showProfileImage(commentList.get(commentList.size() - 1).getUserReplies().get(i).getUserId(), holder.profilesubPictures);
                                        updateProfileImage(commentList.get(commentList.size() - 1).getReplies().get(i).getCommentedBy().getProfilePic(), holder.profilesubPictures);
                                        holder.usersubComment.setText(commentList.get(commentList.size() - 1).getReplies().get(i).getComment());
                                        holder.usersubname.setText(commentList.get(commentList.size() - 1).getReplies().get(i).getCommentedBy().getName());
                                        if (commentList.get(commentList.size() - 1).getReplies().size() > 1)
                                            holder.profilesubPicture.setText(commentList.get(commentList.size() - 1).getReplies().size() + " Replies");
                                        if (commentList.get(commentList.size() - 1).getReplies().size() == 1)
                                            holder.profilesubPicture.setText(commentList.get(commentList.size() - 1).getReplies().size() + " Reply");
                                        String img = commentList.get(commentList.size() - 1).getReplies().get(i).getImage();
                                        if (img!=null){
                                            holder.replyImage.setVisibility(View.VISIBLE);
                                            Glide.with(mContext).load(img).placeholder(R.drawable.placeholder_image).into(holder.replyImage);
                                            holder.replyImage.setOnClickListener(v -> new PhotoFullPopupWindow(mContext, R.layout.popup_photo_full, v, img, null));
                                        }else{
                                            holder.replyImage.setVisibility(View.GONE);
                                        }

                                    }
                                }
                            } else {
                                holder.profilesubPictures.setVisibility(View.GONE);
                                holder.profilesubPicture.setVisibility(View.GONE);
                                holder.usersubComment.setVisibility(View.GONE);
                                holder.usersubname.setVisibility(View.GONE);
                            }
                        } else {
                            holder.commentLinearLayout.setVisibility(View.GONE);
                        }


                    } else {
//                        Toast.makeText(mContext, commentseResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {

//                    Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AllComentsModel> call, Throwable t) {
//                Toast.makeText(mContext, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error", "Message:" + t.getMessage());

            }
        });

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

//    private void shareImageFromUrl(String imageUrl) {
//        Glide.with(mContext)
//                .asBitmap()
//                .load(imageUrl)
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        intent.setType("image/jpg");
//                        intent.putExtra(Intent.EXTRA_TEXT, "\uD83C\uDF3F\uD83C\uDF3Fश्री कृष्णा\uD83C\uDF3F\uD83C\uDF3F" + "\n" + "श्री कृष्ण भगवान की सुन्दर तस्वीरें देखने Download करने व Wallpaper , Video , Ringtone , Bhajan , Use करने के लिए श्री कृष्णा App को Download करें" + "\n" + "App Link: https://play.google.com/store/apps/details?id=com.raman.kumar.shrikrishan");
//                        intent.putExtra(Intent.EXTRA_STREAM, getBitmapFromView(resource));
//                        mContext.startActivity(Intent.createChooser(intent, "Share Image"));
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                    }
//                });
//
//    }

    private void shareImageFromUrl(String imageUrl) {
        Glide.with(mContext)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            // 1. Save bitmap to cache directory
                            File cachePath = new File(mContext.getCacheDir(), "images");
                            cachePath.mkdirs(); // create folder if not exists
                            File imageFile = new File(cachePath, "shared_image.jpg");
                            FileOutputStream stream = new FileOutputStream(imageFile);
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            stream.close();

                            // 2. Get URI using FileProvider
                            Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", imageFile);

                            if (contentUri != null) {
                                // 3. Share image
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("image/*");
                                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, "🌿🌿श्री कृष्णा🌿🌿\n" +
                                        "श्री कृष्ण भगवान की सुन्दर तस्वीरें देखने Download करने व Wallpaper , Video , Ringtone , Bhajan , Use करने के लिए श्री कृष्णा App को Download करें\n" +
                                        "App Link: https://play.google.com/store/apps/details?id=com.raman.kumar.shrikrishan");
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                mContext.startActivity(Intent.createChooser(shareIntent, "Share Image"));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "Image sharing failed!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
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

    private void updateProfileImages(String uri, final ImageView imageView) {
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
                    ImageView save = dialog.findViewById(R.id.save);
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isStoragePermissionGranted()) {
//                                downloadImage(String.valueOf(uri), String.valueOf(System.currentTimeMillis()));
                                shareFacebook.downloadImageFromURL(uri, String.valueOf(System.currentTimeMillis()));
                                Intent intent = new Intent(mContext, BackgroundNotificationService.class);
                                intent.putExtra("url", uri);
                                mContext.startService(intent);
                                new DownloadFileFromURL().execute(uri, String.valueOf(System.currentTimeMillis()));
                            }
                        }
                    });

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

    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        public static final int progress_bar_type = 0;
        private ProgressDialog pDialog;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            mContext.showDialog(progress_bar_type);
            pDialog = showDownloadingProgress(pDialog);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                String title = f_url[1];
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/srikrishna/images");

                if (!myDir.exists()) {
                    myDir.mkdirs();
                }

                String name = new Date().getTime() + ".jpg";
                myDir = new File(myDir, name);

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 9999);

                // Output stream to write file
                OutputStream output = new FileOutputStream(myDir);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(myDir.getAbsolutePath(), bmOptions);
                MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, title, title);
                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String myDir) {
            // dismiss the dialog after the file was downloaded
            //dismissDialog(progress_bar_type);
            // File file=new File(myDir);

            pDialog.setProgress(100);
            pDialog.setMessage("Download Completed");
            pDialog.setCancelable(true);
            //pDialog.dismiss();
            // Displaying downloaded image into image view
            // Reading image path from sdcard
            //  String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
            // setting downloaded into image view
            // my_image.setImageDrawable(Drawable.createFromPath(imagePath));

            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.dismiss();
                    }
                }, 2000);
                Toast.makeText(mContext, "Download Completed", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    String block = "";


    public ProgressDialog showDownloadingProgress(ProgressDialog dialog) {
        dialog = new ProgressDialog(mContext);
        //  progressDialog.setTitle("Loading");
        dialog.setMessage("Downloading..");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        // progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        dialog.show();
        return dialog;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setImageListner(ShareFacebook shareFacebook) {
        this.shareFacebook = shareFacebook;
    }

    public void showDialog(final String images, final String title) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_downloadshare);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        final ImageView download = dialog.findViewById(R.id.download);
        final ImageView shareButton = dialog.findViewById(R.id.fb_share_button);
        //dialog.setCancelable(true);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isStoragePermissionGranted()) {
                    //downloadImage(images,title);
                    dialog.dismiss();
                    shareFacebook.downloadImageFromURL(images, title);
                    Intent intent = new Intent(mContext, BackgroundNotificationService.class);
                    intent.putExtra("url", images);
                    mContext.startService(intent);
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

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public void showProgressDialog() {
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Image downloading");
        progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressDialog.show();
    }

   /* public void downloadImage(String images, final String title) {
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
//progressDialog.dismiss();
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
                                  //Toast.makeText(mContext, " Download completed", Toast.LENGTH_SHORT).show();

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
    }*/

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

    public interface ShareFacebook {
        void shareImage(String image, String title, ProgressDialog dialog);

        ProgressDialog showProgress();

        void dismissProgressDialog(ProgressDialog progressDialog);

        ProgressDialog showDialoadingProgress();

        void downloadImageFromURL(String image, String title);
    }

}