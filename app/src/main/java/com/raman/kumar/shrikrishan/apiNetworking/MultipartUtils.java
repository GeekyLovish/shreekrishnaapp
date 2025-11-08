package com.raman.kumar.shrikrishan.apiNetworking;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.File;
import java.io.FileOutputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MultipartUtils {

    // Convert Uri to MultipartBody.Part
    public static MultipartBody.Part prepareFilePartFromUri(Context context, String partName, Uri fileUri) {
        File file = new File(getRealPathFromURI(context, fileUri));
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    // Convert Bitmap to MultipartBody.Part
    public static MultipartBody.Part prepareFilePartFromBitmap(Context context, String partName, Bitmap bitmap) {
        File file = new File(context.getCacheDir(), "image.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // Save Bitmap directly
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    // Get the real file path from a Uri
    private static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String result = cursor.getString(columnIndex);
            cursor.close();
            return result;
        }
        return contentUri.getPath();
    }
}