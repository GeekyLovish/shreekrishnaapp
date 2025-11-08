package com.raman.kumar.shrikrishan;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.raman.kumar.shrikrishan.networking.ImageFilePath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mann on 24/1/18.
 */

public class UploadedImages extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private static final int PERMISSION_REQUEST_AUDIO = 5;
    private static final int PERMISSION_REQUEST_STORAGE = 2;
    private static final int REQUEST_CAMERA = 1000;
    private static final int SELECT_FILE = 2000;
    static final int CAMERA_PIC_REQUEST = 1000;
    static final int GALLERY_PIC_REQUEST = 2000;
    static final int VIDEO_CAPTURE = 4000;
    String mCurrentPhotoPath;
    public ArrayList<String> imageURLList = new ArrayList<>();
    ImageView imagePreview;
    RecyclerView recyclerView;
    private ImagesAdapter mAdapter;
    private List<String> imagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploaded_images_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView uploadImage=(ImageView)findViewById(R.id.setImage);
TextView uploadImg=(TextView)findViewById(R.id.uploadImage);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDailogBox();

            }
        });

    }
    public void createDailogBox() {
        CharSequence colors[] = new CharSequence[]{"Camera",
                "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an Options");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {


                if (pos == 0) {
                    showCameraPreview();
                } else if (pos == 1) {
                    showStoragereview();
                }
            }
        });
        builder.show();

    }
    private void showCameraPreview() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            startCamera();
        } else {
            // Permission is missing and must be requested.
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CAMERA);

        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CAMERA);
        }
    }


    public void startCamera() {
        File f = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }
        startActivityForResult(takePictureIntent, REQUEST_CAMERA);
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        File filepath = Environment.getExternalStorageDirectory();
        File dir = new File(filepath.getAbsolutePath() + "/MTCollect/images/profile");

        dir.mkdirs();
        // Create a name for the saved image
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        String saveImageName = "profile_" +/*UserDAO.getInstance(getActivity()).getUserId()*/currentDateandTime + ".jpg";


        File file = new File(dir, saveImageName);
        return file;
    }
    private void showStoragereview() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            startGallery();
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_STORAGE);

        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_STORAGE);
        }
    }

    public void startGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                SELECT_FILE);
    }
    public String uploadData(String imageCategory, File file, String fileName, String url) {
        String imageUploadResponse = "msg";
        try {
            UploadDownloadFileClient client = new UploadDownloadFileClient(
                    url);
            //client.setmProgressListener(getApplicationContext());
            client.connectForMultipart();
            client.addFormPart("requestType", "upload");
            //client.addFormPart("localId", uniqueId);
            if (imageCategory.equalsIgnoreCase("receipt") || imageCategory.equalsIgnoreCase("cheque") || imageCategory.equalsIgnoreCase("others")) {
                client.addFormPart("imageCategory", imageCategory);
            }
            client.addDocumentFilePart("file",
                    (fileName), file);
            client.finishMultipart();
            imageUploadResponse = client.getResponse();
            System.out.println("Upload response " + imageUploadResponse);
        } catch (Exception e) {
            e.printStackTrace();
            imageUploadResponse = "msg";
        }
        return imageUploadResponse;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bm = null;
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            // bm = (Bitmap) data.getExtras().get("data");

            if (mCurrentPhotoPath != null) {
                bm = getBitmapWithOrientation(mCurrentPhotoPath, 768);
                mCurrentPhotoPath = null;
            }
            imageURLList.add(saveFinalImage(bm));
            if(imagePreview!=null){
                imagePreview.setImageBitmap(bm);
            }

        } else if (requestCode == GALLERY_PIC_REQUEST && resultCode == RESULT_OK
                && null != data) {

            InputStream is = null;
            try {
                String realPath = ImageFilePath.getPath(this, data.getData());
                Bitmap bitmap = getBitmapWithOrientation(realPath, 768);

                imageURLList.add(saveFinalImage(bitmap));
                if(imagePreview!=null){
                    imagePreview.setImageBitmap(bm);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
    public static Bitmap getBitmapWithOrientation(String path, int newSize) {
        Bitmap myBitmap = BitmapFactory.decodeFile(path);

        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            int width = myBitmap.getWidth();
            int height = myBitmap.getHeight();
            if (newSize != 0) {
                if (height > width) {
                    if (width > newSize) {
                        float ratio = (float) height / (float) width;
                        width = newSize;
                        height = (int) ((float) width * ratio);
                    }
                } else {
                    if (height > newSize) {
                        float ratio = (float) width / (float) height;
                        height = newSize;
                        width = (int) ((float) height * ratio);
                    }
                }
            }
            myBitmap = Bitmap.createScaledBitmap(myBitmap, width, height, false);
            myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true); // rotating bitmap
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myBitmap;
    }
    public String saveFinalImage(Bitmap bm) {
        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        // getAbsolutePath for sd card
        File dir;
            dir = new File(filepath.getAbsolutePath() + "/MTCollect/images/others");


        dir.mkdirs();
        // Create a name for the saved image
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        String saveImageName = "";
            saveImageName = "" + (imageURLList.size() + 1) + ".jpg";


        File file = new File(dir, saveImageName);

        // Show a toast message on successful save
        /*Toast.makeText(getApplicationContext(), "Image Saved to SD Card",
                Toast.LENGTH_SHORT).show();*/
        try {

            OutputStream output = new FileOutputStream(file);

            //  Bitmap bm = cropBorderFromBitmap(finalImg);

            // Compress into png format image from 0% - 100%
            bm.compress(Bitmap.CompressFormat.JPEG, 50, output);
            output.flush();
            output.close();

            refreshGallery(file);
            //	MediaScannerConnection.scanFile(this, new String[] { dir.getPath() }, new String[] { "image/jpeg" }, null);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return dir + "/" + saveImageName;

    }
    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }
    public  void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.success_error_view1);
         imagePreview=(ImageView) findViewById(R.id.imagePreview);

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button okButton = (Button) dialog.findViewById(R.id.okButton);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                createDailogBox();
               // dialog.dismiss();
            }
        });
        dialog.show();

    }

}
