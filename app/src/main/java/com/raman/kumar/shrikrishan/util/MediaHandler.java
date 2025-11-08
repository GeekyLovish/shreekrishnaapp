package com.raman.kumar.shrikrishan.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

public class MediaHandler {
    public static Bitmap rotateImageIfRequired(Bitmap img, Context context, Uri selectedImage) {
        try {
            boolean secondTry = true;
            if (selectedImage.getScheme().equals("content")) {
                try {
                    String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
                    Cursor c = context.getContentResolver().query(selectedImage, projection, null, null, null);
                    if (c.moveToFirst()) {
                        final int rotation = c.getInt(0);
                        c.close();
                        return rotateImage(img, rotation);
                    }
                    return img;
                } catch (Exception ignored) {
                    secondTry = true;
                }
            }
            if (secondTry) {
                androidx.exifinterface.media.ExifInterface ei = new androidx.exifinterface.media.ExifInterface(selectedImage.getPath());
                //   ExifInterface ei = new ExifInterface(selectedImage.getPath());
                //  int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int orientation = ei.getRotationDegrees();
                // Timber.d("orientation: %s", orientation);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return rotateImage(img, 90);
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return rotateImage(img, 180);
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return rotateImage(img, 270);
                    default:
                        return rotateImage(img, orientation);
                    //    return img;
                }
            }
        } catch (Exception ignored) {
            return img;
        }
        return img;
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        }catch (Exception ignored){
            return img;
        }
    }
}
