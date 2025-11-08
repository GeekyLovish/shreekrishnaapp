package com.raman.kumar.shrikrishan.CommentFiles;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import com.raman.kumar.modals.comments.commnetLike.CommentLikeModel;
import com.raman.kumar.modals.comments.getAllComments.Datum;
import com.raman.kumar.modals.comments.postComment.PostComentsModel;
import com.raman.kumar.modals.comments.updateComment.UploadCommentModel;
import com.raman.kumar.modals.getaModal.DeleteGetaModal;
import com.raman.kumar.shrikrishan.ImageUriInterface;
import com.raman.kumar.shrikrishan.PhotoFullPopupWindow;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.APICalls;
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


public class CommentsAdapterViaNotification extends RecyclerView.Adapter<CommentsAdapterViaNotification.ViewHolder> implements CommonListeners {
//    CommentActivity context;
    Context context;
    List<Datum> commentsList;
    String postId, userID;
    PrefHelper prefHelper;
    ProgressDialog progress;
    String block = "", section;
    String post_image;
    ImageUriInterface imageUriInterface;
    private final CommentAdapterListener listener;
    private final ReplyAdapter.ReplyAdapterListener listenerReply;

    private OnReplyClickListener onReplyClickListener;
//
//    public CommentsAdapterViaNotification(CommentActivity context, List<Datum> commentsList, String postId, String userID, String post_image, String section, ImageUriInterface imageUriInterfacex) {
//        this.onReplyClickListener = context;
//        this.context = context;
//        this.commentsList = commentsList;
//        this.postId = postId;
//        this.userID = userID;
//        this.post_image = post_image;
//        this.section = section;
////        database = FirebaseDatabase.getInstance();
////        myRef = database.getReference();
//        prefHelper = new PrefHelper(context);
//        progress = new ProgressDialog(context);
//        this.imageUriInterface = imageUriInterface;
//    }
    public CommentsAdapterViaNotification(Context context, List<Datum> commentsList, String postId, String userID, String post_image, String section, ImageUriInterface imageUriInterface, CommentAdapterListener listener, ReplyAdapter.ReplyAdapterListener listenerReply,OnReplyClickListener onReplyClickListener) {
        this.commentsList = commentsList;
        this.postId = postId;
        this.userID = userID;
        this.context=context;
        this.post_image = post_image;
        this.section = section;
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference();
        prefHelper = new PrefHelper(context);
        progress = new ProgressDialog(context);
        this.imageUriInterface = imageUriInterface;
        this.listener = listener;
        this.listenerReply = listenerReply;
        this.onReplyClickListener = onReplyClickListener;
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
        if(commentsList.get(position).getComment().isEmpty()){
            holder.userComment.setVisibility(View.GONE);
        }else{
            holder.userComment.setVisibility(View.VISIBLE);
            holder.userComment.setText(commentsList.get(position).getComment());
        }
        if((commentsList.get(position).getImage())!=null){
            holder.commentImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(commentsList.get(position).getImage()).placeholder(R.drawable.placeholder_image).into(holder.commentImage);
            holder.commentImage.setOnClickListener(v -> new PhotoFullPopupWindow(context, R.layout.popup_photo_full, v, commentsList.get(position).getImage(), null));
        }else{
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


        if (prefHelper.getuId().equals(commentsList.get(position).getUserId())) {
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
            Log.d("tag", "visibility............");
            holder.profilePicture.setVisibility(View.VISIBLE);
            holder.userName.setVisibility(View.VISIBLE);
        }


        Glide.with(context).load(commentsList.get(position).getCommentedBy().getProfilePic()).placeholder(R.drawable.ic_account).into(holder.profilePicture);


        ReplyAdapter adapter = new ReplyAdapter(context,commentsList.get(position).getReplies(),prefHelper,postId,listenerReply);
        holder.replyRecycleView.setAdapter(adapter);

        if (prefHelper.getuId().equalsIgnoreCase(commentsList.get(position).getUserId().toString())) {
            holder.replyButton.setVisibility(View.VISIBLE);
            holder.likeButton.setVisibility((View.VISIBLE));
            holder.likeCount.setVisibility((View.VISIBLE));
        } else {
            holder.replyButton.setVisibility(View.VISIBLE);
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
                removeCommentLike(postId,commentsList.get(position).getId().toString(),position,holder);
            }
            else {
                commentLike(postId,commentsList.get(position).getId().toString(),position,holder);
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
        if ("Administrator".equals(myRole) || (commentsList.get(position).getUserId().toString()).equals(prefHelper.getuId())){
            String commentId = commentsList.get(position).getId().toString();
            String profilePic = commentsList.get(position).getCommentedBy().getProfilePic();
            String userName = commentsList.get(position).getCommentedBy().getName();
            onReplyClickListener.onReplyClick(commentId,userName,profilePic);
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
            if (isBlocked == 0){
                colors = new CharSequence[]{"Delete", "Edit", "Block", "Cancel"};
            }else{
                colors = new CharSequence[]{"Delete", "Edit", "Unblock", "Cancel"};
            }
        } else {
            if (Objects.equals(myUserId, otherUserId)){
                // Non-admin role: Only "Delete" and "Block" options
                colors = new CharSequence[]{"Delete", "Edit", "Cancel"};
            }else{
                return;
            }

        }




        System.out.println("sahfaisfhjh     myName "+myName);
        System.out.println("sahfaisfhjh     myRole "+myRole);
        System.out.println("sahfaisfhjh     myUserId "+myUserId);
        System.out.println("sahfaisfhjh     otherUserId "+otherUserId);
        System.out.println("sahfaisfhjh     isBlocked "+isBlocked);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select an Option");
        CharSequence[] finalColors = colors;
        builder.setItems(colors, (dialog, pos) -> {
            if (finalColors[pos].equals("Delete")) {
                deleteComment(dialog, position, holder);
            } else if (finalColors[pos].equals("Edit")) {
                updateComment(position);
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
        blockAPI(commentsList.get(position).getUserId().toString(),"0",dialog);
    }

    private void blockUser(int position, DialogInterface dialog) {
        blockAPI(commentsList.get(position).getUserId().toString(),"1",dialog);
    }

    void blockAPI(String userID,String value,DialogInterface dialog)
    {
        Call<DeleteGetaModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .blockUnblockUser("application/json", Extensions.getBearerToken(),userID);

        call.enqueue(new Callback<DeleteGetaModal>() {
            @Override
            public void onResponse(Call<DeleteGetaModal> call, Response<DeleteGetaModal> response) {
                DeleteGetaModal sresponse = response.body();
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    if (sresponse.getStatus()) {
                        if (listener != null) {
                            listener.onRequestRefreshComments(postId);
                        }
                        notifyDataSetChanged();
                        Log.d("msg",sresponse.getMessage());
                        Toast.makeText(context, sresponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, sresponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    dialog.dismiss();
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                Toast.makeText(context, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error","Message:"+t.getMessage());
                dialog.dismiss();
            }
        });

    }

    private void updateComment(int position) {
        Dialog editDialog = new Dialog(context);
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.edit_comment_dialog_layout);
        editDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView commentText = editDialog.findViewById(R.id.commentText);
        EditText editComment = editDialog.findViewById(R.id.editComment);
        Button okButton = editDialog.findViewById(R.id.okButton);
        Button cancelButton = editDialog.findViewById(R.id.cancelButton);

        commentText.setText(commentsList.get(position).getComment());

        okButton.setOnClickListener(arg0 -> {
            System.out.println("sahfaisfhjh   Clicked on Edit ");

            APICalls.editComment(context,commentsList.get(position).getId().toString(),editComment.getText().toString().trim(),position,editDialog,this);
        });


        cancelButton.setOnClickListener(v -> editDialog.dismiss());

        editDialog.show();
    }

    private void deleteComment(DialogInterface dialog, int position, ViewHolder holder) {

       delete_comment(postId,commentsList.get(position).getId().toString(), position,dialog);

    }

    void delete_comment(String postId, String commentId, int position,DialogInterface dialog)
    {

        Call<DeleteGetaModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .deleteComment("application/json", Extensions.getBearerToken(),commentId);

        call.enqueue(new Callback<DeleteGetaModal>() {
            @Override
            public void onResponse(Call<DeleteGetaModal> call, Response<DeleteGetaModal> response) {
                DeleteGetaModal likeResponse = response.body();
                if (response.isSuccessful()) {
//                    progress.dismiss();
                    if (likeResponse.getStatus()) {
                        commentsList.remove(position);
                        notifyDataSetChanged();
                        notifyItemRemoved(position);
                        dialog.dismiss();
                        Log.d("msg",likeResponse.getMessage());

                    } else {
                        Toast.makeText(context, likeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
//                    progress.dismiss();
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                Toast.makeText(context, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error","Message:"+t.getMessage());
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
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void onActivityResults(int requestCode, int resultCode, @NonNull Intent data) {
    }

    @Override
    public void onCommentEdited(int position, @NonNull Response<UploadCommentModel> response) {
        commentsList.remove(position);
        notifyDataSetChanged();
        notifyItemRemoved(position);
//                        context.isCommentAdded = true;
        if (listener != null) {
            listener.onRequestRefreshComments(postId);
        }
        notifyItemChanged(position);
        notifyDataSetChanged();
    }

    @Override
    public void onCommentAdded(@NonNull Response<PostComentsModel> response) {
    }

    @Override
    public void onGalleryClick(@NonNull Bitmap bitmap, @NonNull byte[] url) {
    }

    @Override
    public void onCameraClick(@NonNull Bitmap bitmap, @NonNull byte[] url) {
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


    void removeCommentLike(String postId, String commentId,int position,ViewHolder holder)
    {

        Call<DeleteGetaModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .deleteCommentLike("application/json", Extensions.getBearerToken(),postId,commentId);

        call.enqueue(new Callback<DeleteGetaModal>() {
            @Override
            public void onResponse(Call<DeleteGetaModal> call, Response<DeleteGetaModal> response) {
                DeleteGetaModal likeResponse = response.body();
                if (response.isSuccessful()) {
//                    progress.dismiss();
                    if (likeResponse.getStatus()) {


                    if (listener != null) {
                            listener.onRequestRefreshComments(postId);
                        }
                        notifyDataSetChanged();

                    } else {
                        Toast.makeText(context, likeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
//                    progress.dismiss();
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                Toast.makeText(context, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error","Message:"+t.getMessage());
//                progress.dismiss();
            }
        });

    }
    void commentLike(String postId, String commentId,int position,ViewHolder holder)
    {


        Call<CommentLikeModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .commentLike("application/json", Extensions.getBearerToken(),postId,commentId,"like");

        call.enqueue(new Callback<CommentLikeModel>() {
            @Override
            public void onResponse(Call<CommentLikeModel> call, Response<CommentLikeModel> response) {
                CommentLikeModel likeResponse = response.body();
                if (response.isSuccessful()) {
//                    progress.dismiss();
                    if (likeResponse.getStatus()) {
                        if (listener != null) {
                            listener.onRequestRefreshComments(postId);
                        }
                        notifyDataSetChanged();

                    } else {
                        Toast.makeText(context, likeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
//                    progress.dismiss();
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentLikeModel> call, Throwable t) {
                Toast.makeText(context, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error","Message:"+t.getMessage());
//                progress.dismiss();
            }
        });

    }

    public interface CommentAdapterListener {
        void onRequestRefreshComments(String postId);
    }


}
