package com.raman.kumar.shrikrishan.CommentFiles;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.client.Firebase;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.raman.kumar.modals.comments.postComment.PostComentsModel;
import com.raman.kumar.modals.comments.updateComment.Data;
import com.raman.kumar.modals.comments.updateComment.UploadCommentModel;
import com.raman.kumar.shrikrishan.Constants;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.APICalls;
import com.raman.kumar.shrikrishan.apiNetworking.ProgressRequestBody;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.application.MyApp;
import com.raman.kumar.shrikrishan.model.CommentRepliesResponse;
import com.raman.kumar.shrikrishan.model.CommentReplyResponse;
import com.raman.kumar.shrikrishan.model.CommonResponse;
import com.raman.kumar.shrikrishan.util.Comment;
import com.raman.kumar.shrikrishan.util.CommonListeners;
import com.raman.kumar.shrikrishan.util.PrefHelper;
import com.raman.kumar.shrikrishan.util.User;
import com.raman.kumar.shrikrishan.util.Util;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class ReplyActivity extends AppCompatActivity implements CommonListeners {

    private TextView userName, userComment, percentage;
    private RecyclerView replyList;
    private EditText replyEditText;

    LinearLayoutCompat progressLay;

    LinearProgressIndicator uploadProgress;
    byte[] replyImageFile = null;
    private RelativeLayout relativeLayout;
    private ImageView sendButton, backButton, profilePicture, comment_attach;
    private Intent intent;
    private String postId, commentId, name, comment, userID, device_token, token, admin_token = "", commentUserId = "", image = "", post_image = "";
    private PrefHelper prefHelper;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    ProgressDialog progress;
    Firebase postReference;
    private ReplyAdapter replyAdapter;
    private List<Data> userReplies = new ArrayList<>();
    private String section = "";
    private String user_image = "";
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
//        postReference = new Firebase("https://shri-krishan.firebaseio.com/postComments");
//        mAuth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//        mStorage = FirebaseStorage.getInstance();
        prefHelper = new PrefHelper(getApplicationContext());
//        myRef = database.getReference();
//        mStorageRef = mStorage.getReference();


        intent = getIntent();
        if (intent.hasExtra("postId")) {
            postId = intent.getStringExtra("postId");
            commentId = intent.getStringExtra("commentId");
            comment = intent.getStringExtra("comment");
            post_image = intent.getStringExtra("image");
            section = intent.getStringExtra("section");
            commentUserId = intent.getStringExtra("comment_user_id");
            user_image = intent.getStringExtra("user_image");
            name = intent.getStringExtra("name");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    intiViews();
                    listeners();
                }
            });


            System.out.println("asfkasfhkjasf  " + commentId);

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


    private void listeners() {
        backButton.setOnClickListener(v -> onBackPressed());

        sendButton.setOnClickListener(v -> {
            if (replyEditText.getText().toString().equals("")) {
                Toast.makeText(ReplyActivity.this, getResources().getString(R.string.write_reply), Toast.LENGTH_SHORT).show();
            } else {
                showProgressDialog();
                if (userReplies.size() > 0)
                    if (userReplies.get(0).getUserId().equals("")) {
                        userReplies.remove(0);
                    }
                APICalls.replyToUser(replyImageFile, this, replyEditText.getText().toString(), postId, String.valueOf(commentId), prefHelper, this, new ProgressRequestBody.UploadCallbacks() {
                    @Override
                    public void onProgressUpdate(int percentage) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
//                progress.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (prefHelper.getuId().equals(Constants.ADMIN_USER_ID)) {
                            try {
                                CommentActivity.Update = "1";
                                pushNotification(postId, replyEditText.getText().toString().trim(), prefHelper.getuName(), token, comment);
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                pushNotification(postId, replyEditText.getText().toString().trim(), prefHelper.getuName(), admin_token, comment);
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    private void pushNotification(String postId, String comment, String username, String token, String s) throws JSONException, IOException {
        Log.e("Userrrr", "============");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("to", token);
        jsonObject.put("priority", "high");
        JSONObject notiObj = new JSONObject();
        Log.e("Userrrr", Constants.ADMIN_USER_ID + "====" + prefHelper.getuId());
        Log.e("Userrrr", Constants.VICE_ADMIN_USER_ID + "====" + prefHelper.getuId());
        if (prefHelper.getuId().equals(Constants.ADMIN_USER_ID) || prefHelper.getuId().equals(Constants.VICE_ADMIN_USER_ID)) {

            notiObj.put("title", "\uD83C\uDF3Fश्री कृष्णा\uD83C\uDF3F Replied on your comment " + comment);
        } else {
            notiObj.put("title", "\uD83C\uDF3F" + prefHelper.getuName() + "\uD83C\uDF3F Replied on your comment " + comment);
        }
        notiObj.put("message", comment);
        notiObj.put("sound", "default");

        JSONObject dataObj = new JSONObject();
        // notiObj.put("message", comment);
        //notiObj.put("title", "\uD83C\uDF3Fश्री कृष्णा\uD83C\uDF3F Replied on your comment " + comment);
        notiObj.put("type", "reply_" + section);
//        dataObj.put("image", post_image);
        notiObj.put("post_id", postId);
        notiObj.put("comment_id", commentId);
        notiObj.put("username", username);
        notiObj.put("token", token);
        notiObj.put("comment", s);
        notiObj.put("image", post_image);
        notiObj.put("user_image", user_image);
        jsonObject.put("data", notiObj);
        /*  jsonObject.put("notification", dataObj);*/

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .method("POST", body)
                .addHeader("Authorization", "key=" + Constants.SERVER_KEY)
                .addHeader("Content-Type", "application/json")
                .build();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //Your code goes here
                    Response response = null;
                    try {
                        response = client.newCall(request).execute();
                        JSONObject jsonObject1 = new JSONObject(response.body().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            replyEditText.setText("");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    private void intiViews() {
        progress = new ProgressDialog(this);
        prefHelper = new PrefHelper(getApplicationContext());
        progressLay = findViewById(R.id.progressLay);
        uploadProgress = findViewById(R.id.uploadProgress);
        percentage = findViewById(R.id.percentage);
        userName = findViewById(R.id.userName);
        comment_attach = findViewById(R.id.comment_attach);
        profilePicture = findViewById(R.id.profilePicture);
        backButton = findViewById(R.id.backButton);
        userComment = findViewById(R.id.userComment);
        replyList = findViewById(R.id.replyList);
        relativeLayout = findViewById(R.id.relativeLayout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ReplyActivity.this);
//        layoutManager.setStackFromEnd(true);
        replyList.setLayoutManager(layoutManager);
        replyEditText = findViewById(R.id.commentEditText);
        sendButton = findViewById(R.id.sendButton);

        userName.setText(name);
        userComment.setText(comment);
        if (image.equals("")) {
            comment_attach.setVisibility(View.GONE);
        } else {
            comment_attach.setVisibility(View.VISIBLE);
            updateProfileImages(image, comment_attach);
        }
       /* Glide.with(MyApp.getApplication())
                .load(user_image)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_account)
                        .error(R.drawable.ic_account)
                        .centerCrop()
                        .circleCrop())
                .into(profilePicture);*/

        updateProfileImage(user_image, profilePicture);

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
                    Dialog dialog = Util.customizedialog(ReplyActivity.this, R.layout.imagepopup);
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

    @Override
    public void onActivityResults(int requestCode, int resultCode, @NonNull Intent data) {

    }

    @Override
    public void onCameraClick(@NonNull Bitmap bitmap, @NonNull byte[] url) {
    }

    @Override
    public void onCommentAdded(@NonNull retrofit2.Response<PostComentsModel> response) {
    }

    @Override
    public void onCommentEdited(int position, @NonNull retrofit2.Response<UploadCommentModel> response) {
    }

    @Override
    public void onGalleryClick(@NonNull Bitmap bitmap, @NonNull byte[] url) {
    }

    @Override
    public void onReplyAdded(@NonNull retrofit2.Response<UploadCommentModel> response) {
        UploadCommentModel replyResponse = response.body();
        if (response.isSuccessful()) {
            progress.dismiss();
            if (replyResponse.getStatus()) {
                finish();
            } else {
                Toast.makeText(ReplyActivity.this, replyResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            progress.dismiss();
            Toast.makeText(ReplyActivity.this, response.message(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void onReplyFailure() {
        progress.dismiss();
    }

    @Override
    public void onCommentFailed() {
        progress.dismiss();
    }
}
