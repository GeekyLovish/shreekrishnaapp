package com.raman.kumar.shrikrishan;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.comments.getAllComments.AllComentsModel;
import com.raman.kumar.modals.comments.getAllComments.Datum;
import com.raman.kumar.modals.comments.postComment.PostComentsModel;
import com.raman.kumar.modals.comments.postLike.PostLikeModel;
import com.raman.kumar.modals.comments.updateComment.UploadCommentModel;
import com.raman.kumar.modals.gallary.getGallary.GalleryData;
import com.raman.kumar.modals.gallary.getGallary.LikedByMeType;
import com.raman.kumar.modals.getaModal.DeleteGetaModal;
import com.raman.kumar.shrikrishan.CommentFiles.CommentActivity;
import com.raman.kumar.shrikrishan.CommentFiles.CommentsAdapterViaNotification;
import com.raman.kumar.shrikrishan.CommentFiles.OnReplyClickListener;
import com.raman.kumar.shrikrishan.CommentFiles.ReplyAdapter;
import com.raman.kumar.shrikrishan.CommentFiles.UserReply;
import com.raman.kumar.shrikrishan.apiNetworking.APICalls;
import com.raman.kumar.shrikrishan.apiNetworking.ProgressRequestBody;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.fbreaction.OnItemClickListener;
import com.raman.kumar.shrikrishan.fbreaction.ReactionPopup;
import com.raman.kumar.shrikrishan.fbreaction.ReactionsConfigBuilder;
import com.raman.kumar.shrikrishan.loginfiles.LoginActivity;
import com.raman.kumar.shrikrishan.model.CommentImageResponse;
import com.raman.kumar.shrikrishan.util.CommonListeners;
import com.raman.kumar.shrikrishan.util.ImageSelectionHelper;
import com.raman.kumar.shrikrishan.util.PhotoFullPopupWindowForGallery;
import com.raman.kumar.shrikrishan.util.PrefHelper;
import com.raman.kumar.shrikrishan.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullScreenDialogFragment extends DialogFragment implements CommentsAdapterViaNotification.CommentAdapterListener, ReplyAdapter.ReplyAdapterListener, OnReplyClickListener, CommonListeners {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    OnGalleryAdapterListener refreshListener;
    Context mContext;
    int commentId = 0;
    int Image_Request_Code = 4444;
    public boolean isCommentAdded = false;
    ImageView imageView, profilePictureIv, replyCancelBtn, sendButton, attachfile, likeIcon, voteTypeLike, voteTypeLove;
    NestedScrollView scrollView;
    RecyclerView commentRecyclerView;
    List<String> usersLike = new ArrayList<>();
    List<UserReply> commentsUserReplyList = new ArrayList<>();
    LinearLayout postLikeButton;

    LinearLayoutCompat replyLayout, relativeLayout, progressLay;

    LinearProgressIndicator uploadProgress;
    Intent intent;
    Uri FilePathUri;
    byte[] imageFile = null;
    byte[] replyImageFile = null;
    EditText commentEditText;
    ProgressBar loading;
    TextView userNameTv, likeText, likeTextView, commentText, percentage;
    String user_id;
    List<Datum> commentsList;
    CommentsAdapterViaNotification commentsAdapter;
    GalleryData data;
    PrefHelper prefHelper;
    String post_image, section, replyCommentId = "";
    CardView btnCardLogin;
    private static PhotoFullPopupWindowForGallery instance = null;
    String likeType = "";
    boolean isIncremented = false;
    private final String TAG = "FullScreenDialogFragment";
    private OnDialogCloseListener listener;
    Boolean isLiked = false;

    View likeCommentStatus;

    ImageButton closeButton;
    LinearLayout postComments, sharePost, votesCount;
    CardView reactLayout;

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        refreshListener.onRefresh(data);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        // Create the dialog
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Custom behavior for back press in dialog
        dialog.setOnKeyListener((dialogInterface, keyCode, event) -> {
            // Check if the back button (KEYCODE_BACK) was pressed
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                // Optionally handle the back press here
                dismiss();
                return true; // Indicate that the back event is handled
            }
            return false; // Propagate other key events as usual
        });

        // Remove the title of the dialog
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return dialog;
    }

    public void setOnDialogCloseListener(OnDialogCloseListener listener) {
        this.listener = listener;
    }

    public FullScreenDialogFragment(GalleryData data, String user_id, OnGalleryAdapterListener refreshListener) {
        this.user_id = user_id;
        this.data = data;
        this.refreshListener = refreshListener;
    }

    public CommonListeners getListener() {
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setCancelable(true);
        return inflater.inflate(R.layout.popup_photo_full_gallery, container, false);
    }

    @Override
    public int getTheme() {
        return R.style.DialogTheme;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        intent = getActivity().getIntent();
        if (intent != null) {
            post_image = intent.getStringExtra("post_image");
            section = intent.getStringExtra("section");
        }

        initialize(view);
        setViewChanges();
        setEvents();
    }

    private void initialize(View view) {
        prefHelper = new PrefHelper(mContext);
        progressLay = view.findViewById(R.id.progressLay);
        uploadProgress = view.findViewById(R.id.uploadProgress);
        percentage = view.findViewById(R.id.percentage);
        votesCount = view.findViewById(R.id.votesCount);
        voteTypeLike = view.findViewById(R.id.voteTypeLike);
        voteTypeLove = view.findViewById(R.id.voteTypeLove);
        likeCommentStatus = view.findViewById(R.id.likeCommentStatus);
        userNameTv = view.findViewById(R.id.userNameTv);
        closeButton = view.findViewById(R.id.ib_close);
        sendButton = view.findViewById(R.id.sendButton);
        attachfile = view.findViewById(R.id.attachfile);
        replyCancelBtn = view.findViewById(R.id.replyCancelBtn);
        postLikeButton = view.findViewById(R.id.postLikeButton);
        postComments = view.findViewById(R.id.postComments);
        sharePost = view.findViewById(R.id.sharePost);
        likeText = view.findViewById(R.id.likeText);
        likeTextView = view.findViewById(R.id.likeTextView);
        commentText = view.findViewById(R.id.commentText);
        likeIcon = view.findViewById(R.id.likeIcon);
        reactLayout = view.findViewById(R.id.reactLayout);
        commentRecyclerView = view.findViewById(R.id.commentList);
        scrollView = view.findViewById(R.id.scrollContainer);
        profilePictureIv = view.findViewById(R.id.profilePicture);
        replyLayout = view.findViewById(R.id.replyLayout);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        btnCardLogin = view.findViewById(R.id.btnCardLogin);
        commentEditText = view.findViewById(R.id.commentEditText);
        imageView = view.findViewById(R.id.image);
        loading = view.findViewById(R.id.loading);
        loading.setIndeterminate(true);
        commentsList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        commentRecyclerView.setLayoutManager(layoutManager);
        commentRecyclerView.setNestedScrollingEnabled(false);
        if (!prefHelper.getLogedIn().equals("no")) {
        }
    }

    private void disableSendLay() {
        commentEditText.setClickable(false);
        commentEditText.setFocusable(false);
        sendButton.setClickable(false);
        sendButton.setFocusable(false);
        attachfile.setClickable(false);
        attachfile.setFocusable(false);
    }

    private void enableSendLay() {
        commentEditText.setClickable(true);
        commentEditText.setFocusable(true);
        sendButton.setClickable(true);
        sendButton.setFocusable(true);
        attachfile.setClickable(true);
        attachfile.setFocusable(true);
    }

    private void setViewChanges() {
        if (prefHelper.getLogedIn().equals("no")) {
            btnCardLogin.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
        } else {
            btnCardLogin.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }
        setLikesCount();
        retrieveAllComments(data.getId().toString());
        likeType = (data.getLikedByMeType() != null)
                ? data.getLikedByMeType().getType()
                : "";
        if (likeType == null || likeType.isEmpty()) {
            // Default "unliked" state
            likeIcon.setImageResource(R.drawable.like_fb);
            likeText.setTextColor(mContext.getResources().getColor(R.color.textGreyColor));
            likeText.setText("Like");
            likeText.setTypeface(null, Typeface.NORMAL);
        } else {
            // Liked state based on likeType
            int likeColor = likeType.equalsIgnoreCase("like") ? R.color.colorPrimary : R.color.colorAccent;
            int likeIconType = likeType.equalsIgnoreCase("like") ? R.drawable.like_ : R.drawable.love;
            likeIcon.setImageResource(likeIconType);
            likeText.setTextColor(mContext.getResources().getColor(likeColor));
            likeText.setText(likeType.substring(0, 1).toUpperCase() + likeType.substring(1));
            likeText.setTypeface(null, Typeface.BOLD);
        }
        postLikeButton.setTag(likeType);


        Glide.with(mContext)
                .load(data.getUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(imageView);
    }


    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                showCustomDialog();
            }
        } else {
            // Android 12 and below
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                showCustomDialog();
            }
        }
    }

    private void showCustomDialog() {
        String[] options = {"Camera", "Gallery"};

        new AlertDialog.Builder(getActivity())
                .setTitle("Select Photo From")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        ImageSelectionHelper.INSTANCE.init(getActivity(), "Camera", this);
                        //openCamera();
                    } else {
                        ImageSelectionHelper.INSTANCE.init(getActivity(), "Gallery", this);
                        //openGallery();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setEvents() {
        attachfile.setOnClickListener(v -> {
            checkAndRequestPermissions();
        });
        btnCardLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefHelper.getLogedIn().equals("no")) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(intent);
                }
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//listener.onDialogClosed(likeTypeByMe,like_count,likesType);
                dismiss();
            }
        });
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
                if (prefHelper.getLogedIn().equals("no")) {
                    showLoginDialog();
                } else {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("Authorization", "Bearer " + prefHelper.getAuthToken());


                    if (postLikeButton.getTag().toString().equals("like") || postLikeButton.getTag().toString().isEmpty()) {


                        Call<PostLikeModel> call = RetrofitClient
                                .getInstance()
                                .getApi()
                                .likePost(headers, data.getId().toString(), "love");

                        call.enqueue(new Callback<PostLikeModel>() {
                            @Override
                            public void onResponse(Call<PostLikeModel> call, Response<PostLikeModel> response) {
                                if (response.isSuccessful() && response.body().getStatus()) {
                                    isLiked = true;
                                    if (likeType.isEmpty()) {
                                        int count = data.getLikesCount();
                                        data.setLikesCount(count + 1);
                                    }
                                    LikedByMeType type = new LikedByMeType();
                                    likeType = "love";
                                    type.setType("love");
                                    data.setLikedByMeType(type);
                                    data.setLikedByMe(true);
                                    setViewChanges();
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


                    } else {
                        Call<DeleteGetaModal> call = RetrofitClient
                                .getInstance()
                                .getApi()
                                .removeLike(headers, data.getId().toString());

                        call.enqueue(new Callback<DeleteGetaModal>() {
                            @Override
                            public void onResponse(Call<DeleteGetaModal> call, Response<DeleteGetaModal> response) {
                                DeleteGetaModal likeResponse = response.body();
                                if (response.isSuccessful()) {
                                    if (likeResponse.getStatus()) {
                                        isLiked = false;
                                        likeType = "";
                                        LikedByMeType type = new LikedByMeType();
                                        type.setType("");
                                        data.setLikedByMeType(type);
                                        data.setLikedByMe(false);
                                        int count = data.getLikesCount();
                                        data.setLikesCount((count - 1));
                                        setViewChanges();
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
                }
            }
        });
        popup.setReactionSelectedListener((position1) -> {
            String selectedType = position1 == 0 ? "like" : "love";
            updateLikeUI(selectedType); // Update UI with selected reaction type

            if (position1 == 0 || position1 == 1) {
                if (likeType.isEmpty()) {
                    isIncremented = true;
                }
                if (position1 == 0) {
                    likeType = "like";
                } else {
                    likeType = "love";
                }


                reactLayout.setVisibility(View.GONE);
                postLikeButton.setTag(likeType);
                if (prefHelper.getLogedIn().equals("no")) {
                    showLoginDialog();
                } else {

                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("Authorization", "Bearer " + prefHelper.getAuthToken());

                    Call<PostLikeModel> call = RetrofitClient
                            .getInstance()
                            .getApi()
                            .likePost(headers, data.getId().toString(), likeType);

                    call.enqueue(new Callback<PostLikeModel>() {
                        @Override
                        public void onResponse(Call<PostLikeModel> call, Response<PostLikeModel> response) {
                            if (response.isSuccessful() && response.body().getStatus()) {

                                String likeTypes = response.body().getData().getType();
                                isLiked = true;
                                LikedByMeType type = new LikedByMeType();
                                type.setType(likeType);
                                if (isIncremented) {
                                    isIncremented = false;
                                    int count = data.getLikesCount();
                                    data.setLikesCount(count + 1);
                                }
                                data.setLikedByMeType(type);
                                data.setLikedByMe(true);
                                setViewChanges();
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
            }


            return true;
        });

        postLikeButton.setOnTouchListener(popup);


        sharePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImageFromUrl(data.getUrl());
            }
        });
        sendButton.setOnClickListener(v -> {
            String comment = commentEditText.getText().toString().trim();
            if (comment.isEmpty()) {
                Toast.makeText(getActivity(), "Write a comment to send", Toast.LENGTH_SHORT).show();
                return;
            }
            disableSendLay();
            if (replyCommentId.isEmpty()) {
                isCommentAdded = true;
                addNewComment(data.getId().toString(), comment);
            } else {
                String replyText = commentEditText.getText().toString().trim();
                APICalls.replyToUser(replyImageFile, getContext(), replyText, data.getId().toString(), replyCommentId, prefHelper, this, new ProgressRequestBody.UploadCallbacks() {
                    @Override
                    public void onProgressUpdate(int percentage) {
                        showUploadPercentage(percentage);
                    }

                    @Override
                    public void onError() {
                        hideUploadPercentage();
                    }

                    @Override
                    public void onFinish() {
                        hideUploadPercentage();
                    }
                });
            }
        });
        replyCancelBtn.setOnClickListener(v -> hideReplyLayout());
        commentText.setOnClickListener(v -> {
            openComments(data.getId().toString(), data.getUrl());
        });
        votesCount.setOnClickListener(v -> {
            refreshListener.openLikesPopup(data.getId().toString());
        });

        postComments.setOnClickListener(v -> {
            openComments(data.getId().toString(), data.getUrl());
        });
    }

    private void setLikesCount() {
        AtomicInteger likesCount = new AtomicInteger(data.getLikesCount());

        if (likesCount.get() == 0) {
            voteTypeLike.setVisibility(View.GONE);
            voteTypeLove.setVisibility(View.GONE);
            likeTextView.setVisibility(View.GONE);
        } else {
            if (data.getLikesTypes() != null) {
                List<String> likesTypes = data.getLikesTypes();

                if (likesTypes.contains("like") && likesTypes.contains("love")) {
                    // Show both views
                    voteTypeLike.setVisibility(View.VISIBLE);
                    voteTypeLove.setVisibility(View.VISIBLE);
                } else if (likesTypes.contains("love")) {
                    // Show only the "love" view
                    voteTypeLove.setVisibility(View.VISIBLE);
                    voteTypeLike.setVisibility(View.GONE);
                } else if (likesTypes.contains("like")) {
                    // Show only the "like" view
                    voteTypeLike.setVisibility(View.VISIBLE);
                    voteTypeLove.setVisibility(View.GONE);
                } else {
                    // Hide both views if neither is present
                    voteTypeLike.setVisibility(View.GONE);
                    voteTypeLove.setVisibility(View.GONE);
                }
            }
            likeTextView.setVisibility(View.VISIBLE);
            likeTextView.setText(String.valueOf(likesCount.get()));
        }
    }

    private void showUploadPercentage(int percentage) {
        progressLay.setVisibility(View.VISIBLE);
        uploadProgress.setProgress(percentage);
        this.percentage.setText(percentage + "%");
    }

    private void hideUploadPercentage() {
        progressLay.setVisibility(View.GONE);
        uploadProgress.setProgress(0);
        this.percentage.setText("0%");
    }

    public void addNewComment(final String postId, final String comment) {
        usersLike.clear();
        usersLike.add("");
        commentsUserReplyList.clear();
        commentsUserReplyList.add(new UserReply("", "", "", "", "", ""));
        APICalls.addComment(imageFile, getContext(), prefHelper, postId, comment, this, new ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
                showUploadPercentage(percentage);
            }

            @Override
            public void onError() {
                hideUploadPercentage();
            }

            @Override
            public void onFinish() {
                hideUploadPercentage();
            }
        });
        commentEditText.setText("");
        FilePathUri = null;
        commentId++;
    }

    private void hideReplyLayout() {
        replyImageFile = null;
        replyCommentId = "";
        replyLayout.setVisibility(View.GONE);
        commentEditText.setText("");
        commentEditText.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
        }
    }

    private void hitLikeApi(String likeType) {

    }

    private void showToast(String likeTypes) {
        Toast.makeText(mContext, likeTypes, Toast.LENGTH_SHORT).show();
    }

    private void openComments(String post_id, String images) {
        if (prefHelper.getLogedIn().equals("no")) {
            showLoginDialog();
        } else {
            Intent intent = new Intent(mContext, CommentActivity.class);
            intent.putExtra("post_id", post_id);
            intent.putExtra("post_image", images);
            mContext.startActivity(intent);
        }
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

    private void shareImageFromUrl(String fullImage) {

        Glide.with(mContext)
                .asBitmap()
                .load(data.getUrl())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.share_link));
                        intent.putExtra(Intent.EXTRA_STREAM, getBitmapFromView(resource));
                        mContext.startActivity(Intent.createChooser(intent, "Share Image"));
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


    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void retrieveAllComments(String postId) {
        if (prefHelper == null || prefHelper.getAuthToken() == null) {
            Log.e(TAG, "Auth token or prefHelper is null!");
            return;
        }

        Call<AllComentsModel> call = RetrofitClient.getInstance().getApi()
                .getComments("application/json", Extensions.getBearerToken(), postId);

        call.enqueue(new Callback<AllComentsModel>() {
            @Override
            public void onResponse(Call<AllComentsModel> call, retrofit2.Response<AllComentsModel> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        AllComentsModel result = response.body();
                        if (Boolean.TRUE.equals(result.getStatus()) && result.getData() != null) {
                            commentsList.clear();
                            commentsList.addAll(result.getData());
                            int count = commentsList.size();
                            commentText.setText(count < 1 ? "" : count + " Comment" + (count > 1 ? "s" : ""));

//                            commentsAdapter.notifyDataSetChanged();
                            setupCommentsAdapter();
                        } else {
                            Toast.makeText(requireContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(requireContext(), "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onResponse Exception: ", e);
                    Toast.makeText(requireContext(), "Parsing error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AllComentsModel> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                Toast.makeText(requireContext(), "Failed to load comments", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setupCommentsAdapter() {
        commentsAdapter = new CommentsAdapterViaNotification(
                requireContext(),
                commentsList,
                data.getId().toString(),
                user_id,
                post_image,
                section,
                this::shareImageFromUrl,
                this,
                this,
                this
        );
        if (commentRecyclerView != null) {
            commentRecyclerView.setAdapter(commentsAdapter);
        }
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Ensure the RecyclerView has a LayoutManager
                RecyclerView.LayoutManager layoutManager = commentRecyclerView.getLayoutManager();
                if (layoutManager != null) {
                    View secondItem = layoutManager.findViewByPosition(1); // index 1 = 2nd item

                    if (secondItem != null) {
                        // Get Y position of 2nd item relative to scrollView
                        int y = secondItem.getTop() + commentRecyclerView.getTop();
                        scrollView.smoothScrollTo(0, y);
                    }
                }
            }
        });


    }

    @Override
    public void onRequestRefreshComments(String postId) {
        // call the method in your DialogFragment — likely:
        retrieveAllComments(postId);
    }

    @Override
    public void onReplyClick(String commentId, String userName, String profilePic) {
        replyCommentId = commentId;
        replyLayout.setVisibility(View.VISIBLE);
        Glide.with(this).load(profilePic).placeholder(R.drawable.ic_account).into(profilePictureIv);
        userNameTv.setText(userName);
    }

    private void updateLikeUI(String likeType) {
        if (likeType == null || likeType.isEmpty()) {
            // Default "unliked" state
            likeIcon.setImageResource(R.drawable.like_fb);
            likeText.setTextColor(mContext.getResources().getColor(R.color.textGreyColor));
            likeText.setText("Like");
            likeText.setTypeface(null, Typeface.NORMAL);
            postLikeButton.setTag(likeType);
        } else {
            // Liked state based on likeType
            int likeColor = likeType.equalsIgnoreCase("like") ? R.color.colorPrimary : R.color.colorAccent;
            int likeIconDrawable = likeType.equalsIgnoreCase("like") ? R.drawable.like_ : R.drawable.love;
            likeIcon.setImageResource(likeIconDrawable);
            likeText.setTextColor(mContext.getResources().getColor(likeColor));
            likeText.setText(likeType.substring(0, 1).toUpperCase() + likeType.substring(1));
            likeText.setTypeface(null, Typeface.BOLD);
            postLikeButton.setTag(likeType);
        }
    }

    @Override
    public void onCameraClick(@NonNull Bitmap bitmap, @NonNull byte[] url) {

        Util.insertImageInEditText(getContext(), bitmap, commentEditText);
        if (replyCommentId.isEmpty()) {
            imageFile = url;
        } else {
            replyImageFile = url;
        }
    }

    @Override
    public void onGalleryClick(@NonNull Bitmap bitmap, @NonNull byte[] url) {
        Util.insertImageInEditText(getContext(), bitmap, commentEditText);
        if (replyCommentId.isEmpty()) {
            imageFile = url;
        } else {
            replyImageFile = url;
        }
    }

    @Override
    public void onCommentAdded(@NonNull Response<PostComentsModel> response) {
        enableSendLay();
        retrieveAllComments(response.body().getData().getPostId().toString());
    }

    @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                showCustomDialog();
            } else {
                Toast.makeText(getContext(), "Permissions are required to proceed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onReplyAdded(@NonNull Response<UploadCommentModel> response) {
        hideReplyLayout();
        enableSendLay();
        retrieveAllComments(data.getId().toString());
    }

    @Override
    public void onCommentEdited(int position, @NonNull Response<UploadCommentModel> response) {
    }

    @Override
    public void onReplyFailure() {
        enableSendLay();
    }

    @Override
    public void onCommentFailed() {
        enableSendLay();
    }

    public interface OnDialogCloseListener {
        void onDialogClosed(String likeTypeByMe, Integer like_count, List<String> likesType);
    }


    @Override
    public void onActivityResults(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if ((requestCode == ImageSelectionHelper.INSTANCE.getGALLERY() && resultCode == Activity.RESULT_OK) && data != null) {
                ImageSelectionHelper.INSTANCE.onActivityResult(requestCode, resultCode, data);
            } else if (requestCode == ImageSelectionHelper.INSTANCE.getCAMERA() && resultCode == Activity.RESULT_OK) {
                ImageSelectionHelper.INSTANCE.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            Log.e("onActivityResultError", e.getLocalizedMessage());
        }
    }

}
