package com.raman.kumar.shrikrishan.apiNetworking;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.comments.postComment.PostComentsModel;
import com.raman.kumar.modals.comments.updateComment.UploadCommentModel;
import com.raman.kumar.modals.comments.whoLikes.Datum;
import com.raman.kumar.modals.comments.whoLikes.WhoLikeModel;
import com.raman.kumar.shrikrishan.Activity.GalleryActivity;
import com.raman.kumar.shrikrishan.Activity.WallpaperActivity;
import com.raman.kumar.shrikrishan.CommentFiles.CommentActivity;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.UploadDownloadFileClient;
import com.raman.kumar.shrikrishan.model.CommentImageResponse;
import com.raman.kumar.shrikrishan.util.CommonListeners;
import com.raman.kumar.shrikrishan.util.PrefHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APICalls {

    public static List<Datum> peopleWhoLikes(PrefHelper prefHelper, String post_id, String from, Context mContext) {
        List<Datum> userPostLikesList = new ArrayList<>();
        System.out.println("safnaskf clicked");


        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + prefHelper.getAuthToken());
        Call<WhoLikeModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .likes("application/json", Extensions.getBearerToken(), post_id);

        call.enqueue(new Callback<WhoLikeModel>() {
            @Override
            public void onResponse(Call<WhoLikeModel> call, Response<WhoLikeModel> response) {
                WhoLikeModel likeResponse = response.body();
                if (response.isSuccessful()) {
                    if (likeResponse.getStatus()) {
                        System.out.println("sadknfksanfk   from " + from);
                        userPostLikesList.addAll(likeResponse.getData());
                        if (from.equalsIgnoreCase("gallery")) {
//                    ((GalleryActivity) mContext).openLikesPopup(userPostLikesList);
                            ((GalleryActivity) mContext).openLikesPopup(userPostLikesList);
                        } else {
//                    ((WallpaperActivity) mContext).openLikesPopup(userPostLikesList);
                            ((WallpaperActivity) mContext).openLikesPopup(userPostLikesList);
                        }
//                        Toast.makeText(mContext, likeResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.d("msg", likeResponse.getMessage());

                    } else {
//                        Toast.makeText(mContext, likeResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.d("msg", likeResponse.getMessage());

                    }
                } else {
//                    Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WhoLikeModel> call, Throwable t) {
//                Toast.makeText(mContext, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error", "Message:" + t.getMessage());
            }
        });
        return userPostLikesList;
    }


    public static void addComment(@Nullable byte[] file, Context context, PrefHelper prefHelper, String postId, String comment, CommonListeners listeners, ProgressRequestBody.UploadCallbacks progressListener) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + prefHelper.getAuthToken());
        MultipartBody.Part body = null;

        if (file != null) {
            try {
                // Save byte[] temporarily to File for progress tracking
                File tempFile = new File(context.getCacheDir(), System.currentTimeMillis() + ".jpg");
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(file);
                fos.close();

                ProgressRequestBody progressRequestBody =
                        new ProgressRequestBody(tempFile, "image/*", progressListener);

                body = MultipartBody.Part.createFormData(
                        "image",
                        tempFile.getName(),
                        progressRequestBody
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestBody postIdPart = RequestBody.create(MediaType.parse("multipart/form-data"), postId);
        RequestBody commentPart = RequestBody.create(MediaType.parse("multipart/form-data"), comment.replace("[img]", "").trim());
        RequestBody uId = RequestBody.create(MediaType.parse("multipart/form-data"), prefHelper.getuId());
        RequestBody token = RequestBody.create(MediaType.parse("multipart/form-data"), prefHelper.getAuthToken());

        RetrofitClient.getInstance().getApi()
                .postComment(headers, postIdPart, commentPart, body, uId, token)
                .enqueue(new Callback<PostComentsModel>() {

                    @Override
                    public void onResponse(Call<PostComentsModel> call, retrofit2.Response<PostComentsModel> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getStatus()) {
                            listeners.onCommentAdded(response);
                        } else {
                            Log.d(TAG, "Add comment failed: " + (response.body() != null ? response.body().getMessage() : "null"));
                        }
                    }

                    @Override
                    public void onFailure(Call<PostComentsModel> call, Throwable t) {
                        listeners.onCommentFailed();
                        Log.d(TAG, "Error adding comment: " + t.getMessage());
                    }
                });
    }

    public static void editComment(Context context, String commentId, String comment, int position, Dialog editDialog, CommonListeners listeners) {
        Call<UploadCommentModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .editComment("application/json", Extensions.getBearerToken(), commentId, comment);

        call.enqueue(new Callback<UploadCommentModel>() {
            @Override
            public void onResponse(Call<UploadCommentModel> call, retrofit2.Response<UploadCommentModel> response) {
                UploadCommentModel likeResponse = response.body();
                if (response.isSuccessful()) {
//                    progress.dismiss();
                    if (likeResponse.getStatus()) {
                        Log.d("msg", likeResponse.getMessage());
                        listeners.onCommentEdited(position, response);
                        editDialog.dismiss();
                    } else {
                        Toast.makeText(context, likeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    progress.dismiss();
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadCommentModel> call, Throwable t) {
                Toast.makeText(context, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error", "Message:" + t.getMessage());
//                progress.dismiss();
            }
        });

    }

    public static void replyToUser(@Nullable byte[] file, Context context, String replyText, String postId, String replyCommentId, PrefHelper prefHelper, CommonListeners commonListeners, ProgressRequestBody.UploadCallbacks progressListener) {
        if (replyText.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.write_reply), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + prefHelper.getAuthToken());
        MultipartBody.Part body = null;

        if (file != null) {
            try {
                // Save byte[] temporarily to File for progress tracking
                File tempFile = new File(context.getCacheDir(), System.currentTimeMillis() + ".jpg");
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(file);
                fos.close();

                ProgressRequestBody progressRequestBody =
                        new ProgressRequestBody(tempFile, "image/*", progressListener);

                body = MultipartBody.Part.createFormData(
                        "image",
                        tempFile.getName(),
                        progressRequestBody
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestBody postIdPart = RequestBody.create(MediaType.parse("multipart/form-data"), postId);
        RequestBody commentPart = RequestBody.create(MediaType.parse("multipart/form-data"), replyText.replace("[img]", "").trim());
        RequestBody commentId = RequestBody.create(MediaType.parse("multipart/form-data"), replyCommentId);

        RetrofitClient.getInstance().getApi()
                .postReply(headers, postIdPart, commentPart,body, commentId)
                .enqueue(new Callback<UploadCommentModel>() {

                    @Override
                    public void onResponse(Call<UploadCommentModel> call, retrofit2.Response<UploadCommentModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UploadCommentModel replyResponse = response.body();
                            Toast.makeText(context, replyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            if (replyResponse.getStatus()) {
                                commonListeners.onReplyAdded(response);
                            }
                        } else {
                            Toast.makeText(context, "Reply failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadCommentModel> call, Throwable t) {
                        Toast.makeText(context, "Reply error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Reply error", t);
                    }
                });
    }
}
