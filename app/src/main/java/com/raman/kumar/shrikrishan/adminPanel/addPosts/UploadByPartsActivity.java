package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.pictureByParts.getPictureByPart.Datum;
import com.raman.kumar.modals.pictureByParts.getPictureByPart.GetPictureByPartModal;
import com.raman.kumar.modals.pictureByParts.postPictureByPart.PostPartImageModel;
import com.raman.kumar.shrikrishan.Activity.NewGalleryActivity;
import com.raman.kumar.shrikrishan.Adapter.NewGalleryAdapter;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.model.UploadByPartsResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadByPartsActivity extends AppCompatActivity {
    ProgressDialog progress;
    private List<Datum> gallerySection = new ArrayList<>();
    Button selectTypeButton, ButtonChooseImage, ButtonUploadImage;
    String picType = "", id = "", url = "", section = "";
    String picTypeId = "";
    int Image_Request_Code = 1;
    Uri FilePathUri;
    ImageView ShowImageView;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    String Storage_Path = "By_Parts_Images/";
    String Database_Path = "By_Parts_Images_Database";
    Toolbar toolbar;
    Intent intent;
    File imageFile;
    List<ByPartsData> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_by_parts);
        intent = getIntent();
        if (intent.hasExtra("id")) {
            id = intent.getStringExtra("id");
            url = intent.getStringExtra("url");
            picType = intent.getStringExtra("section");

        }
        picTypeId = id;
        initViews();
        listeners();

    }

    private void listeners() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        selectTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupMenu(selectTypeButton);
            }
        });

        ButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });

        ButtonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (url.equalsIgnoreCase("")) {
                    UploadImageFile();
                } else {
                    updateImageFile();
                }
            }
        });
    }

    private void updateImageFile() {
//        if (FilePathUri != null) {
        System.out.println("sfaklsfjakf picType"  + picType );
        System.out.println("sfaklsfjakf picTypeId"  + picTypeId );
        if (picTypeId.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please select part too", Toast.LENGTH_LONG).show();
            return;
        }
        if (imageFile.toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please select image", Toast.LENGTH_LONG).show();
            return;
        }


            MultipartBody.Part requestImage = MultipartBody.Part.createFormData("image", imageFile.getName(), RequestBody.create(MediaType.parse("image/*"), imageFile));
            RequestBody picTypeIdPart = RequestBody.create(MediaType.parse("multipart/form-data"), picTypeId);
            RequestBody method = RequestBody.create(MediaType.parse("multipart/form-data"), "PUT");

            Call<UploadByPartsResponse> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .updateImageByParts("application/json",Extensions.getBearerToken(),id,picTypeIdPart,method,requestImage);

            progress.setTitle("Image is Updating...");
            progress.show();

            call.enqueue(new Callback<UploadByPartsResponse>() {
                @Override
                public void onResponse(Call<UploadByPartsResponse> call, Response<UploadByPartsResponse> response) {
                    UploadByPartsResponse postImageResponse = response.body();
                    if (response.isSuccessful()) {
                        if (postImageResponse.getSuccess()) {
                            progress.dismiss();
                            Toast.makeText(getApplicationContext(), "Image Updated Successfully", Toast.LENGTH_LONG).show();
                            onBackPressed();

                        } else {
                            progress.dismiss();
                            Toast.makeText(UploadByPartsActivity.this, postImageResponse.getMsg(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }

                @Override
                public void onFailure(Call<UploadByPartsResponse> call, Throwable t) {
                    Log.v("Image Upload",t.getMessage());
                    Toast.makeText(getApplicationContext(), "Failed to update image!", Toast.LENGTH_LONG).show();
                    progress.dismiss();
                }
            });

    }

    private void initViews() {
        progress = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        toolbar = findViewById(R.id.toolbar);
        selectTypeButton = findViewById(R.id.selectTypeButton);
        ButtonChooseImage = findViewById(R.id.ButtonChooseImage);
        ButtonUploadImage = findViewById(R.id.ButtonUploadImage);
        ShowImageView = findViewById(R.id.ShowImageView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Pictures By Parts");

        if (picType.equalsIgnoreCase("")) {
            selectTypeButton.setText("Select Part/Section");
        } else {
            selectTypeButton.setText(picType);
            Glide.with(UploadByPartsActivity.this)
                    .load(url)
                    .into(ShowImageView);
        }

        getPictureByPartData();

//        gallerySection.add("Shri krishan kishor roop");
//        gallerySection.add("Shri krishan bal roop");
//        gallerySection.add("Shri krishan Varnan");
//        gallerySection.add("Shri krishan katha");
//        gallerySection.add("Yashoda Maa");
//        gallerySection.add("Shri Krishan Lila");
//        gallerySection.add("Shri Radha Krishan Prem Lila");
//        gallerySection.add("Shri Krishan Nav Roop");
//        gallerySection.add("Shri krishan balram");
//        gallerySection.add("Shri krishan Gita");
//        gallerySection.add("Shri krishan Narayan Roop");
//        gallerySection.add("Avtars");

        getAllImages();
    }


    private void getPictureByPartData() {
           showProgressDialog();
        Call<GetPictureByPartModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .getPictureByPart(1,100);

        call.enqueue(new Callback<GetPictureByPartModal>() {
            @Override
            public void onResponse(Call<GetPictureByPartModal> call, Response<GetPictureByPartModal> response) {
                GetPictureByPartModal imagesResponse = response.body();
                if (response.isSuccessful()) {
                    if (imagesResponse.getStatus()) {
                        gallerySection.clear();
                        gallerySection.addAll(imagesResponse.getData());

                        progress.dismiss();
                    } else {
                        Toast.makeText(UploadByPartsActivity.this, imagesResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                }
                else
                {
                    Toast.makeText(UploadByPartsActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetPictureByPartModal> call, Throwable t) {
                Toast.makeText(UploadByPartsActivity.this, "Failed to load!", Toast.LENGTH_LONG).show();
                Log.d("error","Message:"+t.getMessage());
                progress.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            FilePathUri = data.getData();
            Bitmap bitmap = null;
            File f = new File(FilePathUri.getPath());
            String name = f.getName();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                ShowImageView.setImageBitmap(bitmap);
                ButtonChooseImage.setText("Image Selected");
                File filesDir = getApplicationContext().getFilesDir();
                imageFile = new File(filesDir, name + ".jpg");
                OutputStream os;
                try {
                    os = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }
//                Toast.makeText(this, "File: "+imageFile, Toast.LENGTH_SHORT).show();
//                Log.d("image file",imageFile.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getAllImages() {
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ByPartsData imageUploadInfo = postSnapshot.getValue(ByPartsData.class);
                    list.add(imageUploadInfo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void UploadImageFile() {
        if (FilePathUri != null) {
            if (picTypeId.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please select part too", Toast.LENGTH_LONG).show();
                return;
            }



            MultipartBody.Part requestImage = MultipartBody.Part.createFormData("image", imageFile.getName(), RequestBody.create(MediaType.parse("image/*"), imageFile));

            RequestBody typeId = RequestBody.create(MediaType.parse("multipart/form-data"), picTypeId);

            Call<PostPartImageModel> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .postImageByParts("application/json",Extensions.getBearerToken(),typeId,requestImage);
            progress.setTitle("Image is Uploading...");
            progress.show();
            call.enqueue(new Callback<PostPartImageModel>() {
                @Override
                public void onResponse(Call<PostPartImageModel> call, Response<PostPartImageModel> response) {
                    progress.dismiss();
                    if (response.isSuccessful()) {
                        PostPartImageModel postImageResponse = response.body();
                        if (postImageResponse != null && postImageResponse.getStatus()) {
                            onBackPressed();
                        } else {
                            Toast.makeText(UploadByPartsActivity.this, postImageResponse != null ? postImageResponse.getMessage() : "Upload failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UploadByPartsActivity.this, "Upload failed with response code: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PostPartImageModel> call, Throwable t) {
                    progress.dismiss();
                    Log.v("Image Upload", t.getMessage());
                    Toast.makeText(getApplicationContext(), "Failed to upload image!", Toast.LENGTH_LONG).show();
                }
            });


//            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
//            storageReference2nd.putFile(FilePathUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//                    return storageReference2nd.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()) {
//                        Uri downloadUri = task.getResult();
//                        String time = getDate(System.currentTimeMillis());
//                        progress.dismiss();
//                        Toast.makeText(getApplicationContext(), "Image Uploaded Successfully", Toast.LENGTH_LONG).show();
////                        String ImageUploadId = String.valueOf(new Random().nextInt(10000));
//                        String ImageUploadId = "";
//                        assert downloadUri != null;
//                        ByPartsData byPartsData = null;
//                        if (list.size() == 0) {
//                            ImageUploadId = "1";
//                        } else {
//                            int newId = Integer.parseInt(list.get(list.size() - 1).id) + 1;
//                            ImageUploadId = String.valueOf(newId);
//                        }
//                        byPartsData = new ByPartsData(ImageUploadId, downloadUri.toString(), time, picType);
//
//                        databaseReference.child(ImageUploadId).setValue(byPartsData);
//                    } else {
//                        Toast.makeText(UploadByPartsActivity.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });

        } else {
            Toast.makeText(UploadByPartsActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();
        }
    }

//    private void openPopupMenu(Button selectTypeButton) {
//        PopupMenu popup = new PopupMenu(UploadByPartsActivity.this, selectTypeButton);
//        for (String s : gallerySection) {
//            popup.getMenu().add(s);
//        }
//
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                picType = String.valueOf(item.getTitle());
//                selectTypeButton.setText(picType);
//                return true;
//            }
//        });
//        popup.show();
//    }


    private void openPopupMenu(Button selectTypeButton) {
        PopupMenu popup = new PopupMenu(UploadByPartsActivity.this, selectTypeButton);

        // Populate popup with names from gallerySection
        for (Datum datum : gallerySection) {
            popup.getMenu().add(datum.getName());
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String selectedName = item.getTitle().toString();
                selectTypeButton.setText(selectedName);

                // Find the datum with the selected name and get its ID
                Integer selectedId = null;
                for (Datum datum : gallerySection) {
                    if (datum.getName().equals(selectedName)) {
                        selectedId = datum.getId();
                        break;
                    }
                }
                // You can now use selectedId as needed
                // e.g., store it in a variable or pass it to another function
                picTypeId = selectedId.toString();

                return true;
            }
        });

        popup.show();
    }


    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    String getDate(Long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
