package com.raman.kumar.shrikrishan.CommentFiles;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.messaging.FirebaseMessaging;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.shrikrishan.apiNetworking.ProgressRequestBody;
import com.raman.kumar.shrikrishan.listeners.EditCommentDialogListener;
import com.raman.kumar.modals.comments.getAllComments.AllComentsModel;
import com.raman.kumar.modals.comments.getAllComments.Datum;
import com.raman.kumar.modals.comments.postComment.PostComentsModel;
import com.raman.kumar.modals.comments.updateComment.UploadCommentModel;
import com.raman.kumar.shrikrishan.ImageUriInterface;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.APICalls;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.util.Comment;
import com.raman.kumar.shrikrishan.util.CommonListeners;
import com.raman.kumar.shrikrishan.util.DialogHelper;
import com.raman.kumar.shrikrishan.util.ImageSelectionHelper;
import com.raman.kumar.shrikrishan.util.PrefHelper;
import com.raman.kumar.shrikrishan.util.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity implements ImageUriInterface, OnReplyClickListener, ReplyAdapter.ReplyAdapterListener, CommonListeners, EditCommentDialogListener {

    EditText commentEditText;
    TextView userNameTv, percentage;
    ImageView profilePictureIv, replyCancelBtn;
    LinearLayoutCompat replyLayout, relativeLayout, progressLay;

    LinearProgressIndicator uploadProgress;

    RecyclerView commentRecyclerView;
    ImageView backButton, sendButton, attachfile;
    Firebase postReference;
    Intent intent;
    String postId = "", post_image, section, replyCommentId = "";
    int commentId = 0;
    List<String> usersLike = new ArrayList<>();
    byte[] imageFile;
    byte[] replyImageFile;
    List<Datum> commentsList;
    List<UserReply> commentsUserReplyList = new ArrayList<>();
    CommentsAdapter commentsAdapter;
    public String edit = "0";
    private String userID;
    ProgressDialog progress;
    private PrefHelper prefHelper;
    private final String TAG = "CommentsActivity";
    public boolean isCommentAdded = false;
    private long startLimit = -1;
    Comment comment_model;
    String adminToken = "";
    Handler handler;
    public static String Update = "";
    String imageUrl;

    Uri FilePathUri;

    int Image_Request_Code = 4444;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int MEDIA_PERMISSION_REQUEST_CODE = 1001;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Debug-only
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        FirebaseMessaging.getInstance().subscribeToTopic("POST_COMMENT");

        prefHelper = new PrefHelper(getApplicationContext());

        intent = getIntent();
        if (intent != null) {
            postId = intent.getStringExtra("post_id");
            post_image = intent.getStringExtra("post_image");
            section = intent.getStringExtra("section");
        }

        if (postId == null) {
            Toast.makeText(this, "Post ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        postReference = new Firebase("https://shri-krishan.firebaseio.com/postComments");

        initView();
        listeners();

        commentsList = new ArrayList<>();
        comment_model = new Comment();
        comment_model.setImage(post_image);

        userID = prefHelper.getuId();

        commentsAdapter = new CommentsAdapter(
                this,
                commentsList,
                postId,
                userID,
                post_image,
                section,
                this::imageUri,
                this, this
        );

        if (commentRecyclerView != null) {
            commentRecyclerView.setAdapter(commentsAdapter);
        }

        if (userID != null) {
            Log.d(TAG, "Activity UserID: " + userID);
        }

        retrieveAllComments(postId);
    }

    private void initView() {
        progressLay = findViewById(R.id.progressLay);
        uploadProgress = findViewById(R.id.uploadProgress);
        percentage = findViewById(R.id.percentage);
        replyCancelBtn = findViewById(R.id.replyCancelBtn);
        replyLayout = findViewById(R.id.replyLayout);
        relativeLayout = findViewById(R.id.relativeLayout);
        profilePictureIv = findViewById(R.id.profilePicture);
        userNameTv = findViewById(R.id.userNameTv);
        commentEditText = findViewById(R.id.commentEditText);
        sendButton = findViewById(R.id.sendButton);
        attachfile = findViewById(R.id.attachfile);
        backButton = findViewById(R.id.backButton);
        commentRecyclerView = findViewById(R.id.commentList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        commentRecyclerView.setLayoutManager(layoutManager);
        replyLayout.setVisibility(View.GONE);
        ViewCompat.setOnApplyWindowInsetsListener(relativeLayout, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bottomInset);
            return insets;
        });

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

    private void listeners() {
        backButton.setOnClickListener(v -> onBackPressed());

        sendButton.setOnClickListener(v -> {
            String comment = commentEditText.getText().toString().trim();
            if (comment.isEmpty()) {
                Toast.makeText(this, "Write a comment to send", Toast.LENGTH_SHORT).show();
                return;
            }
            disableSendLay();
            if (replyCommentId.isEmpty()) {
                isCommentAdded = true;
                addNewComment(postId, comment);
            } else {
                String replyText = commentEditText.getText().toString().trim();
                APICalls.replyToUser(replyImageFile, this, replyText, postId, replyCommentId, prefHelper, this, new ProgressRequestBody.UploadCallbacks() {
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
        attachfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions();
            }
        });

       /* attachfile.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 12);
                    return;
                }
            }

            new AlertDialog.Builder(this)
                    .setTitle("Select")
                    .setMessage("Select Image From?")
                    .setPositiveButton("Camera", (dialog, which) -> {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, 55);
                    })
                    .setNegativeButton("Gallery", (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Image"), Image_Request_Code);
                    })
                    .setIcon(android.R.drawable.ic_menu_report_image)
                    .show();
        });*/
    }

    private void hideReplyLayout() {
        replyImageFile = null;
        replyCommentId = "";
        replyLayout.setVisibility(View.GONE);
        commentEditText.setText("");
        commentEditText.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
        }
    }

    private void disableSendLay(){
        commentEditText.setClickable(false);
        commentEditText.setFocusable(false);
        sendButton.setClickable(false);
        sendButton.setFocusable(false);
        attachfile.setClickable(false);
        attachfile.setFocusable(false);
    }
    private void enableSendLay(){
        commentEditText.setClickable(true);
        commentEditText.setFocusable(true);
        sendButton.setClickable(true);
        sendButton.setFocusable(true);
        attachfile.setClickable(true);
        attachfile.setFocusable(true);
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
                            commentsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(CommentActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CommentActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onResponse Exception: ", e);
                    Toast.makeText(CommentActivity.this, "Parsing error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AllComentsModel> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                Toast.makeText(CommentActivity.this, "Failed to load comments", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void addNewComment(final String postId, final String comment) {
        usersLike.clear();
        usersLike.add("");
        commentsUserReplyList.clear();
        commentsUserReplyList.add(new UserReply("", "", "", "", "", ""));
        APICalls.addComment(imageFile, this, prefHelper, postId, comment, this, new ProgressRequestBody.UploadCallbacks() {
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

    @Override
    public void imageUri(String uri) {
        imageUrl = uri;
    }

    @Override
    public void onReplyClick(String commentId, String userName, String profilePic) {
        replyCommentId = commentId;
        replyLayout.setVisibility(View.VISIBLE);
        Glide.with(this).load(profilePic).placeholder(R.drawable.ic_account).into(profilePictureIv);
        userNameTv.setText(userName);
    }


    @Override
    public void onRequestRefreshComments(@Nullable String postId) {
        retrieveAllComments(postId);
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                showCustomDialog();
            }
        } else {
            // Android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                showCustomDialog();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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
                Toast.makeText(this, "Permissions are required to proceed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showCustomDialog() {
        String[] options = {"Camera", "Gallery"};

        new AlertDialog.Builder(this)
                .setTitle("Select Photo From")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        ImageSelectionHelper.INSTANCE.init(this, "Camera", this);
                        //openCamera();
                    } else {
                        ImageSelectionHelper.INSTANCE.init(this, "Gallery", this);
                        //openGallery();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

   /* private void openCamera() {
        imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                System.currentTimeMillis() + ".jpg");
        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider",
                imageFile
        );
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }*/

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

    @Override
    public void onCameraClick(@NonNull Bitmap bitmap, @NonNull byte[] url) {
        Util.insertImageInEditText(this, bitmap, commentEditText);
        if (replyCommentId.isEmpty()) {
            imageFile = url;
        } else {
            replyImageFile = url;
        }
    }

    @Override
    public void onGalleryClick(@NonNull Bitmap bitmap, @NonNull byte[] url) {
        Util.insertImageInEditText(this, bitmap, commentEditText);
        if (replyCommentId.isEmpty()) {
            imageFile = url;
        } else {
            replyImageFile = url;
        }
    }

    @Override
    public void onCommentAdded(retrofit2.Response<PostComentsModel> response) {
        enableSendLay();
        retrieveAllComments(postId);
    }

    @Override
    public void onActivityResults(int requestCode, int resultCode, @NonNull Intent data) {
    }

    @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void onCommentEdited(int position, @NonNull Response<UploadCommentModel> response) {

    }

    @Override
    public void onReplyAdded(@NonNull Response<UploadCommentModel> response) {
        hideReplyLayout();
        enableSendLay();
        retrieveAllComments(postId);
    }

    @Override
    public void onReplyFailure() {
        enableSendLay();
    }

    @Override
    public void openEditCommentDialog(int position, Datum commentData, @Nullable CommonListeners listeners) {
        DialogHelper.editCommentDialog(this, position, commentData, this);
    }

    @Override
    public void onCommentFailed() {
        enableSendLay();
    }
}