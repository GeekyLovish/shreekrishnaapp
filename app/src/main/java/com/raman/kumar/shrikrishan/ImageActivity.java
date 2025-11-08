package com.raman.kumar.shrikrishan;

import static com.raman.kumar.shrikrishan.util.ShimmerHelper.startShimmer;
import static com.raman.kumar.shrikrishan.util.ShimmerHelper.stopShimmer;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.getWallPaper.Datum;
import com.raman.kumar.getWallPaper.GetWallpaperModal;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.model.CommonResponse;
import com.raman.kumar.shrikrishan.util.PrefHelper;
import com.raman.kumar.shrikrishan.util.ShimmerLay;
import com.raman.kumar.uploadWallpaper.UploadWallpaperModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 7777;
    private static final int GALLERY_REQUEST_CODE = 2000;
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private static final int PERMISSION_REQUEST_STORAGE = 2;

    private RecyclerView recyclerView;
    private LinearLayout uploadImage;
    private AdView adView;
    private ProgressDialog progress;
    private PrefHelper prefHelper;
    private String mCurrentPhotoPath;
    private Uri fileUri;
    private Dialog dialog;

    private List<Datum> imagesList = new ArrayList<>();
    private ImagesAdapter imagesAdapter;

    private int currentPage = 1;
    private final int perPageLimit = 40; // You can adjust the number of items per page
    private boolean isLoading = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        Toolbar toolbar = findViewById(R.id.toolbar);
        prefHelper = new PrefHelper(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        uploadImage = findViewById(R.id.uploadImage);
        adView = findViewById(R.id.adView);

        // Load ad
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        setTitle("Wallpaper");


        // Setup RecyclerView layout
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // Check user role and show upload button
        if (prefHelper.getuRole().equals("Administrator")) {
            uploadImage.setVisibility(View.VISIBLE);
        } else {
            uploadImage.setVisibility(View.GONE);
        }

        // Setup upload button click
        uploadImage.setOnClickListener(view -> createDialogBox());

        // Set toolbar navigation
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        // Initialize progress dialog
        progress = new ProgressDialog(this);

        // Check network and fetch images
        if (isNetworkConnected()) {
            getAllImages();
        } else {
            Toast.makeText(this, "Internet connection not available", Toast.LENGTH_SHORT).show();
        }



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Check if the user has reached the bottom
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        currentPage++; // Increment the page number
                        getAllImages(); // Load more images
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        progress.dismiss();
        finish();
    }

    // Check if network is connected
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    // Show dialog to select Camera or Gallery
    private void createDialogBox() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an Option");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                requestCameraPermission();
            } else if (which == 1) {
                requestStoragePermission();
            }
        });
        builder.show();
    }

    // Request camera permission
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

    // Request storage permission
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For devices below Android 10 (API 29)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
                }
            } else {
                // Scoped Storage for Android 10 (API 29) and above
                openGallery();
            }
        } else {
            openGallery(); // For devices below Android 6.0
        }
    }

    // Start camera intent
    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error creating file for camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Create image file for camera photo
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + storageDir.getAbsolutePath());
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Open gallery to pick an image
    private void openGallery() {
        // For Android 10 (API 29) and above, use MediaStore to access the gallery
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        } else {
            // For older Android versions, use this method to access the gallery
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }
    }
    // Handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            // If permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Open gallery
                openGallery();
            } else {
                // Permission denied, show a toast
                Toast.makeText(this, "Permission Denied to access Gallery", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle result from camera or gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                File file = new File(mCurrentPhotoPath);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                showCustomDialog(bitmap, file);
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    File savedFile = saveBitmapToFile(bitmap, "gallery_image.jpg");
                    showCustomDialog(bitmap, savedFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Save bitmap to a file
    private File saveBitmapToFile(Bitmap bitmap, String fileName) throws IOException {
        File filesDir = getFilesDir();
        File imageFile = new File(filesDir, fileName);
        try (OutputStream os = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        }
        return imageFile;
    }

    // Show dialog with preview and option to upload image
    private void showCustomDialog(Bitmap bitmap, File file) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.success_error_view1);
        dialog.setCancelable(true);

        // Set dialog width and height to wrap content
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);

        ImageView imagePreview = dialog.findViewById(R.id.imagePreview);
        Button okButton = dialog.findViewById(R.id.okButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        imagePreview.setImageBitmap(bitmap);

        okButton.setOnClickListener(view -> {
            dialog.dismiss();
            uploadImage(file);
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }



    public void uploadImage(File file) {

        //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        final int random = new Random().nextInt(61) + 20; // [0, 60] + 20 => [20, 80]

        MultipartBody.Part requestImage = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        RequestBody requestname = RequestBody.create(MediaType.parse("multipart/form-data"), file.getName());

        Call<UploadWallpaperModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .postWallpaper("application/json", Extensions.getBearerToken(),requestname, requestImage);

        call.enqueue(new Callback<UploadWallpaperModel>() {
            @Override
            public void onResponse(Call<UploadWallpaperModel> call, Response<UploadWallpaperModel> response) {
                UploadWallpaperModel wallpaperResponse = response.body();
                if (response.isSuccessful()) {
                    if (wallpaperResponse.getStatus()) {
                        progress.dismiss();
                        Toast.makeText(getApplicationContext(), "Image Uploaded Successfully", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        onBackPressed();

                    } else {
                        progress.dismiss();
                        Toast.makeText(ImageActivity.this, wallpaperResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<UploadWallpaperModel> call, Throwable t) {
                Log.v("Image Upload",t.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to upload image!", Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
        });

    }

    // Show loading progress dialog
    private void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show();
    }

    // Get all images from server
//    public void getAllImages() {
//
//        Call<GetWallpaperModal> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .getWallpapers();
//
//        call.enqueue(new Callback<GetWallpaperModal>() {
//            @Override
//            public void onResponse(Call<GetWallpaperModal> call, Response<GetWallpaperModal> response) {
//                GetWallpaperModal imagesResponse = response.body();
//                if (response.isSuccessful()) {
//                    progress.dismiss();
//                    if (imagesResponse.getStatus()) {
//                        imagesList.clear();
//                        imagesList.addAll(imagesResponse.getData());
//                        Log.d("pos", "list: "+imagesResponse.getData());
//                        imagesAdapter  = new ImagesAdapter(imagesList, getApplicationContext(), getSupportFragmentManager()
//                                .beginTransaction());
//                        recyclerView.setAdapter(imagesAdapter);
//
//                    } else {
//                        Toast.makeText(ImageActivity.this, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else
//                {
//                    progress.dismiss();
//                    Toast.makeText(ImageActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetWallpaperModal> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Failed to load!", Toast.LENGTH_LONG).show();
//                Log.d("error","Message:"+t.getMessage());
//                progress.dismiss();
//            }
//        });
//    }

    public void getAllImages() {
        if (isLoading) return; // Prevent multiple simultaneous requests

        isLoading = true;

        // Show progress dialog only for the first page
        if (currentPage == 1) {
            startShimmer(ImageActivity.this, ShimmerLay.WALLPAPERLAY);
        }

        // API call with page and limit parameters
        Call<GetWallpaperModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .getWallpapers(currentPage, perPageLimit); // Pass page and limit

        call.enqueue(new Callback<GetWallpaperModal>() {
            @Override
            public void onResponse(Call<GetWallpaperModal> call, Response<GetWallpaperModal> response) {
                isLoading = false;

                // Dismiss progress dialog for the first page
                if (currentPage == 1) {
                    stopShimmer(ImageActivity.this, ShimmerLay.WALLPAPERLAY);
                }

                if (response.isSuccessful() && response.body() != null) {
                    GetWallpaperModal imagesResponse = response.body();

                    if (imagesResponse.getStatus()) {
                        if (currentPage == 1) {
                            imagesList.clear(); // Clear the list for the first page
                        }

                        imagesList.addAll(imagesResponse.getData()); // Append new data

                        // Update the adapter if it's the first time, otherwise notify data change
                        if (imagesAdapter == null) {
                            imagesAdapter = new ImagesAdapter(imagesList, getApplicationContext(), getSupportFragmentManager().beginTransaction());
                            recyclerView.setAdapter(imagesAdapter);
                        } else {
                            imagesAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(ImageActivity.this, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ImageActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetWallpaperModal> call, Throwable t) {
                isLoading = false;
                if (currentPage == 1) {
                    stopShimmer(ImageActivity.this, ShimmerLay.WALLPAPERLAY);
                }
                Toast.makeText(getApplicationContext(), "Failed to load!", Toast.LENGTH_LONG).show();
            }
        });
    }




}

