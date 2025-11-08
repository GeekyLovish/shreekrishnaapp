package com.raman.kumar.shrikrishan.CommentFiles;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.shrikrishan.PhotoFullPopupWindow;
import com.raman.kumar.shrikrishan.listeners.EditCommentDialogListener;
import com.raman.kumar.modals.comments.commnetLike.CommentLikeModel;
import com.raman.kumar.modals.comments.getAllComments.Datum;
import com.raman.kumar.modals.comments.postComment.PostComentsModel;
import com.raman.kumar.modals.comments.updateComment.UploadCommentModel;
import com.raman.kumar.modals.getaModal.DeleteGetaModal;
import com.raman.kumar.shrikrishan.ImageUriInterface;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.util.CommonListeners;
import com.raman.kumar.shrikrishan.util.PrefHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> implements CommonListeners {
    CommentActivity context;
    List<Datum> commentsList;
    String postId, userID;
    PrefHelper prefHelper;
    ProgressDialog progress;
    String block = "", section;
    String post_image;
    ImageUriInterface imageUriInterface;
    EditCommentDialogListener listener;
    CommonListeners commonListener;


    private OnReplyClickListener onReplyClickListener;

    public CommentsAdapter(CommentActivity context, List<Datum> commentsList, String postId, String userID, String post_image, String section, ImageUriInterface imageUriInterface, EditCommentDialogListener listener,CommonListeners commonListener) {
        this.onReplyClickListener = context;
        this.context = context;
        this.commentsList = commentsList;
        this.postId = postId;
        this.userID = userID;
        this.post_image = post_image;
        this.section = section;
        this.listener = listener;
        this.commonListener = commonListener;
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference();
        prefHelper = new PrefHelper(context);
        progress = new ProgressDialog(context);
        this.imageUriInterface = imageUriInterface;
    }


    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_row, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String createdAt = Extensions.formatDate(commentsList.get(position).getCreatedAt());
        String likeCount = commentsList.get(position).getCommentLikesCount().toString();
        Boolean isLikedByMe = commentsList.get(position).getLikedByMe();
        holder.userName.setText(commentsList.get(position).getCommentedBy().getName());
        if (commentsList.get(position).getComment().isEmpty()) {
            holder.userComment.setVisibility(View.GONE);
        } else {
            holder.userComment.setVisibility(View.VISIBLE);
            holder.userComment.setText(commentsList.get(position).getComment());
        }
        if ((commentsList.get(position).getImage()) != null) {
            holder.commentImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(commentsList.get(position).getImage()).placeholder(R.drawable.placeholder_image).into(holder.commentImage);
            holder.commentImage.setOnClickListener(v -> new PhotoFullPopupWindow(context, R.layout.popup_photo_full, v, commentsList.get(position).getImage(), null));
        } else {
            holder.commentImage.setVisibility(View.GONE);
        }
        holder.timeTextView.setText(createdAt);
        holder.likeCount.setText(likeCount);

        if (commentsList.get(position).getCommentedBy().getProfilePic() == null) {
            Log.d("Uri", "Uri Position :: :: " + "null");
        } else {
            Log.d("Uri", "Uri Position :: " + " not null");
        }


        if (isLikedByMe) {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_solid));
        } else {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_fb));
        }


//        if (prefHelper.getuId().equals(Constants.ADMIN_USER_ID)) {
//            holder.replyButton.setVisibility(View.VISIBLE);
//            holder.likeButton.setVisibility((View.VISIBLE));
//            holder.likeCount.setVisibility((View.VISIBLE));
//        } else {
//            holder.replyButton.setVisibility(View.VISIBLE);
//            holder.likeButton.setVisibility(View.GONE);
//            holder.likeCount.setVisibility(View.GONE);
//        }

        if (commentsList.get(position).getCommentedBy().getName().isEmpty()) {
            holder.profilePicture.setVisibility(View.GONE);
            holder.userName.setVisibility(View.GONE);
        } else {
            Log.d("tag", "visibility............");
            holder.profilePicture.setVisibility(View.VISIBLE);
            holder.userName.setVisibility(View.VISIBLE);
        }


        Glide.with(context).load(commentsList.get(position).getCommentedBy().getProfilePic()).placeholder(R.drawable.ic_account).into(holder.profilePicture);


        ReplyAdapter adapter = new ReplyAdapter(context, commentsList.get(position).getReplies(), prefHelper, postId, context);
        holder.replyRecycleView.setAdapter(adapter);

        if (prefHelper.getuId().equalsIgnoreCase(commentsList.get(position).getUserId().toString())) {
            holder.replyButton.setVisibility(View.VISIBLE);
            holder.likeButton.setVisibility((View.VISIBLE));
            holder.likeCount.setVisibility((View.VISIBLE));
        } else if (prefHelper.getuRole().equals("Administrator")) {
            holder.replyButton.setVisibility(View.VISIBLE);
            holder.likeButton.setVisibility(View.GONE);
            holder.likeCount.setVisibility(View.GONE);
        } else {
            holder.replyButton.setVisibility(View.GONE);
            holder.likeButton.setVisibility(View.GONE);
            holder.likeCount.setVisibility(View.GONE);
        }

        if (commentsList.get(position).getCommentedBy().getName().isEmpty()) {
            holder.profilePicture.setVisibility(View.GONE);
            holder.userName.setVisibility(View.GONE);
        } else {
            holder.profilePicture.setVisibility(View.VISIBLE);
            holder.userName.setVisibility(View.VISIBLE);
        }

        holder.likeButton.setOnClickListener(v -> {
            if (isLikedByMe) {
                removeCommentLike(postId, commentsList.get(position).getId().toString(), position, holder);
            } else {
                commentLike(postId, commentsList.get(position).getId().toString(), position, holder);
            }
        });

        holder.replyButton.setOnClickListener(v -> {
            sendToReplyActivity(position);
        });

//        holder.lastcomment.setOnClickListener(v -> {
//            sendToReplyActivity(position);
//        });

        holder.itemView.setOnLongClickListener(v -> {
            String userIds = commentsList.get(position).getUserId().toString();

            openDialogBox(position, holder);
            return false;
        });

    }

    private void sendToReplyActivity(int position) {
        String myRole = prefHelper.getuRole();
        if ("Administrator".equals(myRole) || (commentsList.get(position).getUserId().toString()).equals(prefHelper.getuId())) {
            String commentId = commentsList.get(position).getId().toString();
            String profilePic = commentsList.get(position).getCommentedBy().getProfilePic();
            String userName = commentsList.get(position).getCommentedBy().getName();
            onReplyClickListener.onReplyClick(commentId, userName, profilePic);
        }
    }


    private void openDialogBox(int position, ViewHolder holder) {


        CharSequence[] colors;

        String myName = prefHelper.getuName();
        String myRole = prefHelper.getuRole();
        String myUserId = prefHelper.getuId();
        String otherUserId = commentsList.get(position).getUserId().toString();
        Integer isBlocked = commentsList.get(position).getCommentedBy().getIsBlocked();

        if ("Administrator".equals(myRole)) {
            // Admin role: Add "Delete", "Edit", and "Block" options
            if (isBlocked == 0) {
                colors = new CharSequence[]{"Delete", "Edit", "Block", "Cancel"};
            } else {
                colors = new CharSequence[]{"Delete", "Edit", "Unblock", "Cancel"};
            }
        } else {
            if (Objects.equals(myUserId, otherUserId)) {
                // Non-admin role: Only "Delete" and "Block" options
                colors = new CharSequence[]{"Delete", "Edit", "Cancel"};
            } else {
                return;
            }

        }


        System.out.println("sahfaisfhjh     myName " + myName);
        System.out.println("sahfaisfhjh     myRole " + myRole);
        System.out.println("sahfaisfhjh     myUserId " + myUserId);
        System.out.println("sahfaisfhjh     otherUserId " + otherUserId);
        System.out.println("sahfaisfhjh     isBlocked " + isBlocked);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select an Option");
        CharSequence[] finalColors = colors;
        builder.setItems(colors, (dialog, pos) -> {
            if (finalColors[pos].equals("Delete")) {
                deleteComment(dialog, position, holder);
            } else if (finalColors[pos].equals("Edit")) {
                listener.openEditCommentDialog(position,commentsList.get(position),commonListener);
                dialog.dismiss();
            } else if (finalColors[pos].equals("Unblock")) {
                unblockUser(position, dialog);
            } else if (finalColors[pos].equals("Block")) {
                blockUser(position, dialog);
            } else {
                dialog.dismiss();
            }

        });
        builder.show();

    }

    private void unblockUser(int position, DialogInterface dialog) {
        blockAPI(commentsList.get(position).getUserId().toString(), "0", dialog);
    }

    private void blockUser(int position, DialogInterface dialog) {
        blockAPI(commentsList.get(position).getUserId().toString(), "1", dialog);
    }

    void blockAPI(String userID, String value, DialogInterface dialog) {
        Call<DeleteGetaModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .blockUnblockUser("application/json", Extensions.getBearerToken(), userID);

        call.enqueue(new Callback<DeleteGetaModal>() {
            @Override
            public void onResponse(Call<DeleteGetaModal> call, retrofit2.Response<DeleteGetaModal> response) {
                DeleteGetaModal sresponse = response.body();
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    if (sresponse.getStatus()) {
                        context.retrieveAllComments(postId);
                        notifyDataSetChanged();
                        Log.d("msg", sresponse.getMessage());
                        Toast.makeText(context, sresponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, sresponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                Toast.makeText(context, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error", "Message:" + t.getMessage());
                dialog.dismiss();
            }
        });

    }


    private void deleteComment(DialogInterface dialog, int position, ViewHolder holder) {

        delete_comment(postId, commentsList.get(position).getId().toString(), position, dialog);

    }

    void delete_comment(String postId, String commentId, int position, DialogInterface dialog) {

        Call<DeleteGetaModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .deleteComment("application/json", Extensions.getBearerToken(), commentId);

        call.enqueue(new Callback<DeleteGetaModal>() {
            @Override
            public void onResponse(Call<DeleteGetaModal> call, retrofit2.Response<DeleteGetaModal> response) {
                DeleteGetaModal likeResponse = response.body();
                if (response.isSuccessful()) {
//                    progress.dismiss();
                    if (likeResponse.getStatus()) {
                        commentsList.remove(position);
                        notifyDataSetChanged();
                        notifyItemRemoved(position);
                        dialog.dismiss();
                        Log.d("msg", likeResponse.getMessage());

                    } else {
                        Toast.makeText(context, likeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    progress.dismiss();
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                Toast.makeText(context, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error", "Message:" + t.getMessage());
//                progress.dismiss();
            }
        });

    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        Log.e("Listsss", commentsList.size() + "========");
        return commentsList.size();
    }

    @Override
    public void onCameraClick(@NonNull Bitmap bitmap, @NonNull byte[] url) {

    }

    @Override
    public void onGalleryClick(@NonNull Bitmap bitmap, @NonNull byte[] url) {
    }

    @Override
    public void onCommentAdded(@NonNull Response<PostComentsModel> response) {
    }

    @Override
    public void onCommentEdited(int position, @NonNull Response<UploadCommentModel> response) {
        commentsList.remove(position);
        notifyDataSetChanged();
        notifyItemRemoved(position);
        context.isCommentAdded = true;
        context.retrieveAllComments(postId);
        notifyItemChanged(position);
        notifyDataSetChanged();
    }

    @Override
    public void onActivityResults(int requestCode, int resultCode, @NonNull Intent data) {
    }

    @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void onReplyAdded(@NonNull Response<UploadCommentModel> response) {

    }

    @Override
    public void onReplyFailure() {
    }

    @Override
    public void onCommentFailed() {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView replyButton, userComment, userName, likeCount;
        RelativeTimeTextView timeTextView;
        //        LinearLayout lastcomment;
        ImageView likeButton, profilePicture, commentImage;
        RecyclerView replyRecycleView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            replyRecycleView = itemView.findViewById(R.id.replyRecyclerView);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            likeButton = itemView.findViewById(R.id.likeButton);
            replyButton = itemView.findViewById(R.id.replyButton);
            userComment = itemView.findViewById(R.id.userComment);
            commentImage = itemView.findViewById(R.id.commentImage);
            userName = itemView.findViewById(R.id.userName);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            likeCount = itemView.findViewById(R.id.likeCount);

            userName.setTypeface(userName.getTypeface(), Typeface.BOLD);
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
            context.showDialog(progress_bar_type);
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
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

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
                MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, title);
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
            pDialog.setProgress(100);
            pDialog.setMessage("Download Completed");
            pDialog.setCancelable(true);

            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.dismiss();
                    }
                }, 2000);
                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public ProgressDialog showDownloadingProgress(ProgressDialog dialog) {
        dialog = new ProgressDialog(context);
        //  progressDialog.setTitle("Loading");
        dialog.setMessage("Downloading..");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        // progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        dialog.show();
        return dialog;
    }

    void removeCommentLike(String postId, String commentId, int position, ViewHolder holder) {

        Call<DeleteGetaModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .deleteCommentLike("application/json", Extensions.getBearerToken(), postId, commentId);

        call.enqueue(new Callback<DeleteGetaModal>() {
            @Override
            public void onResponse(Call<DeleteGetaModal> call, retrofit2.Response<DeleteGetaModal> response) {
                DeleteGetaModal likeResponse = response.body();
                if (response.isSuccessful()) {
//                    progress.dismiss();
                    if (likeResponse.getStatus()) {


                        context.retrieveAllComments(postId);
                        notifyDataSetChanged();

                    } else {
                        Toast.makeText(context, likeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    progress.dismiss();
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                Toast.makeText(context, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error", "Message:" + t.getMessage());
//                progress.dismiss();
            }
        });

    }

    void commentLike(String postId, String commentId, int position, ViewHolder holder) {


        Call<CommentLikeModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .commentLike("application/json", Extensions.getBearerToken(), postId, commentId, "like");

        call.enqueue(new Callback<CommentLikeModel>() {
            @Override
            public void onResponse(Call<CommentLikeModel> call, retrofit2.Response<CommentLikeModel> response) {
                CommentLikeModel likeResponse = response.body();
                if (response.isSuccessful()) {
//                    progress.dismiss();
                    if (likeResponse.getStatus()) {
                        context.retrieveAllComments(postId);
                        notifyDataSetChanged();

                    } else {
                        Toast.makeText(context, likeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    progress.dismiss();
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentLikeModel> call, Throwable t) {
                Toast.makeText(context, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error", "Message:" + t.getMessage());
//                progress.dismiss();
            }
        });

    }


}
