package com.raman.kumar.shrikrishan.util;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.raman.kumar.shrikrishan.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * Created by Hado on 27-Nov-16.
 */

public class Util {
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * @return Application's External Data Directory
     * */
    public static File getAppInternalDirectory(Context context) {

        File[] files
                = ContextCompat.getExternalFilesDirs(context, null);
        if(files.length > 0) {
            String internalDir = getExternalStorageRoot(files[0], context);

            if(internalDir != null && !internalDir.isEmpty()){
                return new File(internalDir);
            }
            return files[0];
        }
        return null;
    }


    public static Dialog customizedialog(Context context, Integer interger) {
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(interger);

        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        window.setGravity(Gravity.CENTER);

        return dialog;
    }

    public static String getExternalStorageRoot(final File file, Context context) {
        String filePath;
        try {
            filePath = file.getCanonicalPath();
        } catch (IOException | SecurityException e) {
            return null;
        }

        List<String> extSdPaths = getExtSdCardPaths(context);
        for (String extSdPath : extSdPaths) {
            if (filePath.startsWith(extSdPath)) return extSdPath;
        }
        return null;
    }

    @NonNull
    public static List<String> getExtSdCardPaths(Context context) {
        File[] externalStorageFilesDirs = new File[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            externalStorageFilesDirs = context.getExternalFilesDirs(null);
        }
        /*File primaryStorageFilesDir = context.getExternalFilesDir(null);*/
        List<String> externalStorageRoots = new ArrayList<>();
        for (File extFilesDir : externalStorageFilesDirs) {
            if (extFilesDir != null /*&& !extFilesDir.equals(primaryStorageFilesDir)*/) {
                int rootPathEndIndex
                        = extFilesDir.getAbsolutePath().lastIndexOf("/Android/data");
                if (rootPathEndIndex >= 0) {
                    String path = extFilesDir.getAbsolutePath().substring(0, rootPathEndIndex);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException ignored) {
                    }
                    externalStorageRoots.add(path);
                }
            }
        }

        return unmodifiableList(externalStorageRoots);
    }

    public static void insertImageInEditText(Context context,Bitmap bitmap, EditText commentEditText) {
        String text = commentEditText.getText().toString().trim().replace("[img]", "").trim();
        commentEditText.setText(text);
        // Resize the bitmap so it fits well inside EditText
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

        // Create an ImageSpan
        ImageSpan imageSpan = new ImageSpan(context, resizedBitmap, ImageSpan.ALIGN_BOTTOM);

        // Create a Spannable string to hold the image
        SpannableString spannableString = new SpannableString(" [img] ");
        spannableString.setSpan(imageSpan, 1, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Append the image to EditText
        int cursorPos = commentEditText.getSelectionStart();
        commentEditText.getText().insert(cursorPos, spannableString);
    }
}
