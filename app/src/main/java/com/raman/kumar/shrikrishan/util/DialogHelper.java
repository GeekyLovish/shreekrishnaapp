package com.raman.kumar.shrikrishan.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.raman.kumar.modals.comments.getAllComments.Datum;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.APICalls;

import javax.annotation.Nullable;

public class DialogHelper {
    public static  void editCommentDialog(Context context, int  position, Datum commentData,CommonListeners listeners) {
        Dialog editDialog = new Dialog(context);
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.edit_comment_dialog_layout);
        editDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView commentText = editDialog.findViewById(R.id.commentText);
        EditText editComment = editDialog.findViewById(R.id.editComment);
        Button okButton = editDialog.findViewById(R.id.okButton);
        Button cancelButton = editDialog.findViewById(R.id.cancelButton);


        commentText.setText(commentData.getComment());

        okButton.setOnClickListener(arg0 -> {
            System.out.println("sahfaisfhjh   Clicked on Edit ");

            APICalls.editComment(context, commentData.getId().toString(), editComment.getText().toString().trim(), position, editDialog, listeners);
        });


        cancelButton.setOnClickListener(v -> editDialog.dismiss());

        editDialog.show();
    }
}
