package com.raman.kumar.shrikrishan.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.application.MyApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * used for status bar
 * */

public class UtilFile {

    /* Prevent to add emoji to Edittext */
    public static InputFilter EMOJI_FILTER = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int index = start; index < end; index++) {

                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE) {
                    return "";
                }
            }
            return null;
        }
    };

    private static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    /*
     * to remove the TITLE of the app
     * */
    public static void withOutTheTitle(Activity activity) {

        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    /*
     * to hide the keyboard cursor until click method
     * */
    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window Token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window Token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /*
     * to remove the app TITLE, so that APP opens in full screen
     * */
    public static void withOutTheTitleFlag(Activity activity) {

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            UtilFile.setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            UtilFile.setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            activity.getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    /*
     * change status bar color method
     * */
    public static void changingStatusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.white));
        }
    }

    /* Get Dummy File for Reference */
    public static File getPictureFile(Context mContext) {
        final String IMAGE_EXTENSION = ".jpg";

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = mContext.getResources().getString(R.string.app_name) + timeStamp;
//        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if(storageDir!=null){
            if(!storageDir.exists()){
                storageDir.mkdirs();
            }
            return new File(storageDir, pictureFile+IMAGE_EXTENSION);
        }
        else {
            return null;
        }


//        try {
//            return File.createTempFile(pictureFile, IMAGE_EXTENSION, storageDir);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
    }



    /* Get Bitmap from URI */
    public static Bitmap getThumbnail(Uri uri, Context mContext) {
        Bitmap bitmap = null;
        try {
            InputStream input = mContext.getContentResolver().openInputStream(uri);
            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            //  onlyBoundsOptions.inDither = true;  //optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
            assert input != null;
            input.close();
            if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
                return null;

            int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

            double ratio = (originalSize > 180) ? ((float)originalSize / 180) : 1.0;
//        double ratio = 1.0;
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
            //     bitmapOptions.inDither = true;//optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional

            input = mContext.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            assert input != null;
            input.close();
        } catch (Exception ignored) {
        }
        return bitmap;
    }



    /* Calculate Ratio Size for Bitmaps */
    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }


    public File getFileImage(Bitmap myBitmap,Context context) {
        String path = "";
        File file = null;
        try {
            OutputStream output;


            /*
            *  val file =  File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                                            System.currentTimeMillis()
                                                .toString() + "." + str
                                        )

            * */
            File folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + MyApp.getApplication().getPackageName());
            if (!folder.exists()) {
                folder.mkdirs();
            }
            file = new File(folder.getAbsolutePath(), "wc" + System.currentTimeMillis() + ".jpg");
            try {
                output = new FileOutputStream(file);
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                output.flush();
                output.close();
                path = file.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public Uri getUri(Context mContext, File mImageFile){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // first add provider in AndroidManifest.xml and create an xml file in xml file folder
            // for new versions or greater than marshmallow we need to use fileProvide
            try {
                return FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", mImageFile);
            } catch (Exception ignored) {
                return Uri.fromFile(mImageFile);
            }
        } else {
            // Below 23 Version Api get Uri directly
            return Uri.fromFile(mImageFile);
        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}





