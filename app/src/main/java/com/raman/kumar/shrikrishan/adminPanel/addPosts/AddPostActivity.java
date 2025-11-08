package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import static com.raman.kumar.customClasses.Extensions.removeUTFCharacters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.gallary.postAmritGallary.PostAmirtGallaryModal;
import com.raman.kumar.shrikrishan.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import com.raman.kumar.shrikrishan.Constants;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPostActivity extends AppCompatActivity {

//    RichTextEditor ImageCaptionEditText;
//    private AllCommandsEditorToolbar editorToolbar;
    private WebView webView;

    EditText titleEditText, adEditText;
    TextView audioTextView;
    ImageView ShowImageView;
    Button ButtonChooseImage, ButtonUploadImage;
    CheckBox gallery, amrit, picsByParts;
    String gallerySection = "", amritSection = "", byPartsSection = "", id = "", url = "", content = "", title = "", createdAt = "",
            position = "", adTitle = "", adLink = "";

    Uri FilePathUri;
    File imageFile;

    RadioButton learnMoreRadio, watchVideoButton, visitWebRadio, visitPageRadio;
    int Image_Request_Code = 7;
    private String token;
    String linkType = "";
    String postType = "";
    Intent intent;
    ProgressDialog progressDialog;

    String serverKey = "key=AAAArdlsbkk:APA91bGpNofn6BcldTG1nV2-P_n81KEcu1n901ypcv3CBThuuekVlkeoZdvvJxRzRbC_SEW_D1tg8v-OfJyHYGC0Bs54JP5GAajHKrZADoKNt5X0AdMheOWIK27ORyWjcoZ5wMKvGYsX";
    List<ImageUploadInfo> list = new ArrayList<>();
    int permissions_code = 42;
    String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        initViews();
        listeners();
        if (!position.equals("")) {
            setData();
        }
//        getAllImages();

        if (Objects.equals(postType, "gallery")){
            gallery.setChecked(true);
        }else if (Objects.equals(postType, "amrit")){
            amrit.setChecked(true);
        }else{
            amrit.setChecked(false);
            gallery.setChecked(false);
        }
    }

    Boolean isChanged = false;

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        intent = getIntent();
        if (intent.hasExtra("id")) {
            id = intent.getStringExtra("id");
            System.out.println("sdksdk  " +id);


            title = intent.getStringExtra("title");
            content = intent.getStringExtra("content");
            url = intent.getStringExtra("url");
            createdAt = intent.getStringExtra("createdAt");
            gallerySection = intent.getStringExtra("gallery");
            amritSection = intent.getStringExtra("amrit");
            byPartsSection = intent.getStringExtra("byParts");
            position = intent.getStringExtra("position");

            adTitle = intent.getStringExtra("adTitle");
            adLink = intent.getStringExtra("adLink");
            linkType = intent.getStringExtra("linkType");
            postType = intent.getStringExtra("postType");

            System.out.println("dsfgajkdfg  " + postType);


        }

        progressDialog = new ProgressDialog(AddPostActivity.this);


        // Initialize WebView
        webView = findViewById(R.id.webViewEditor);

        // Configure WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript
        webView.setWebChromeClient(new WebChromeClient());

        // Load the Quill Editor HTML
        webView.loadUrl("file:///android_asset/quill_editor.html");

        //Initialize views
//        ImageCaptionEditText = findViewById(R.id.ImageCaptionEditText);
//        editorToolbar = findViewById(R.id.editorToolbar);
//        editorToolbar.setEditor(ImageCaptionEditText);
//        ImageCaptionEditText.setPadding(10, 10, 10, 10);
        ShowImageView = findViewById(R.id.ShowImageView);
        ButtonChooseImage = findViewById(R.id.ButtonChooseImage);
        ButtonUploadImage = findViewById(R.id.ButtonUploadImage);
        learnMoreRadio = findViewById(R.id.learnMoreRadio);
        watchVideoButton = findViewById(R.id.watchVideoButton);
        visitWebRadio = findViewById(R.id.visitWebRadio);
        visitPageRadio = findViewById(R.id.visitPageRadio);

        gallery = findViewById(R.id.gallery);
        amrit = findViewById(R.id.amrit);
        picsByParts = findViewById(R.id.picsByParts);
        titleEditText = findViewById(R.id.titleEditText);
        adEditText = findViewById(R.id.adEditText);
        adEditText.setText(adTitle);
        titleEditText.setText(adLink);

//        ImageCaptionEditText.addDidHtmlChangeListener(aBoolean -> {
//            isChanged = aBoolean;
//            Log.e("Erorrrr",isChanged+"");
//            return null;
//        });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    token = task.getException().getMessage();
                    Log.w("FCM TOKEN Failed", task.getException());
                } else {
                    token = task.getResult();
                    Log.e("FCM TOKEN", token);
                }
            }
        });
    }

    public static String replaceDoubleQuotes(String input) {
        // Replace all occurrences of " with space
        return input.replace("\"", " ");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void listeners() {
        ButtonChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
        });

        ButtonUploadImage.setOnClickListener(v -> {
            Log.e("Erorrrr",isChanged+""+position.toString()+"=======Out===");

            webView.evaluateJavascript("getEditorContent();", content -> {
                String modifiedContent = replaceDoubleQuotes(content);
                String updatedContent = removeUTFCharacters(modifiedContent);
                if (position.equals("")) {
//                Log.e("Erorrrr",isChanged+"");
                    UploadImageFile(updatedContent);
                } else {
//                Log.e("Erorrrr",isChanged+"===="+id.toString()+"==========");
                    updateFile(updatedContent);
                }
            });






        });
        gallery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gallerySection = "gallery";
                    amritSection = "";
                    amrit.setChecked(false);
                } else {
                    gallerySection = "";
                }
            }
        });

        amrit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    amritSection = "amrit";
                    gallerySection = "";
                    gallery.setChecked(false);
                } else {
                    amritSection = "";
                }
            }
        });

        picsByParts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    byPartsSection = "byParts";
                } else {
                    byPartsSection = "";
                }
            }
        });

        learnMoreRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linkType = "learn";
                }
            }
        });

        watchVideoButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linkType = "watch";
                }
            }
        });

        visitWebRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linkType = "web";
                }
            }
        });

        visitPageRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linkType = "page";
                }

            }
        });

//        ImageCaptionEditText.setOnTouchListener((view, event) -> {
//            if (ImageCaptionEditText.hasFocus()) {
//                view.getParent().requestDisallowInterceptTouchEvent(true);
//                switch (event.getAction() & MotionEvent.ACTION_MASK){
//                    case MotionEvent.ACTION_SCROLL:
//                        view.getParent().requestDisallowInterceptTouchEvent(false);
//                        return true;
//                }
//            }
//            return false;
//        });
    }


    private void setData() {
//        ImageCaptionEditText.setHtml(content + " ");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Code to be executed after 1 second
            webView.evaluateJavascript("setEditorContent('" + content +"');",null);

        }, 1000); // Delay in milliseconds (1000ms = 1 second)

        Glide.with(AddPostActivity.this)
                .load(url)
                .into(ShowImageView);
//        if (!byPartsSection.equals("")) {
//            picsByParts.setChecked(true);
//        } else {
//            picsByParts.setChecked(false);
//        }
//        if (!amritSection.equalsIgnoreCase("") || !gallerySection.equalsIgnoreCase("")) {
//            Toast.makeText(this, "Add 1 space after content. Every time while updating", Toast.LENGTH_SHORT).show();
//        }
//
//        if (!amritSection.equals("")) {
//            amrit.setChecked(true);
//        } else {
//            amrit.setChecked(false);
//        }
//        if (!gallerySection.equals("")) {
//            gallery.setChecked(true);
//        } else {
//            gallery.setChecked(false);
//        }
        if (!linkType.equalsIgnoreCase("")) {
            if (linkType.equalsIgnoreCase("learn")) {
                learnMoreRadio.setChecked(true);
            } else if (linkType.equalsIgnoreCase("watch")) {
                watchVideoButton.setChecked(true);
            } else if (linkType.equalsIgnoreCase("web")) {
                visitWebRadio.setChecked(true);
            } else if (linkType.equalsIgnoreCase("page")) {
                visitPageRadio.setChecked(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            FilePathUri = data.getData();
//            Toast.makeText(this, "URI: "+FilePathUri, Toast.LENGTH_SHORT).show();
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
        FirebaseMessaging.getInstance().subscribeToTopic("krishna");
    }

    public void UploadImageFile(String content) {

//        String inputText = ImageCaptionEditText.getHtml();
        // Convert the text to HTML-safe format
//        String htmlText = Html.escapeHtml(inputText);



        String type = "";
        if (gallerySection.isEmpty()){
            type = amritSection;
        }else{
            type = gallerySection;
        }

        if (FilePathUri != null) {
            if (linkType.isEmpty()){
                Toast.makeText(AddPostActivity.this, "Please select link type", Toast.LENGTH_SHORT).show();
                return;
            }

            String adTitle = adEditText.getText().toString().trim();
            if (adTitle.isEmpty()){
                Toast.makeText(AddPostActivity.this, "Please enter all title", Toast.LENGTH_SHORT).show();
                return;
            }
            String webLink = titleEditText.getText().toString().trim();
            if (webLink.isEmpty()){
                Toast.makeText(AddPostActivity.this, "Please enter link first.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (type.isEmpty()){
                Toast.makeText(AddPostActivity.this, "Please select type first.", Toast.LENGTH_SHORT).show();
                return;
            }
            progressDialog.setTitle("Image is Uploading...");
            progressDialog.show();


//            String mcontent = ImageCaptionEditText.getHtml().trim();


            RequestBody accept = RequestBody.create(MediaType.parse("multipart/form-data"), "application/json");
            RequestBody bearerToken = RequestBody.create(MediaType.parse("multipart/form-data"), Extensions.getBearerToken());
            RequestBody requestAdTitle = RequestBody.create(MediaType.parse("multipart/form-data"), adTitle);
            MultipartBody.Part requestImage = MultipartBody.Part.createFormData("image", imageFile.getName(), RequestBody.create(MediaType.parse("image/*"), imageFile));
            RequestBody requestType = RequestBody.create(MediaType.parse("multipart/form-data"), type);
            RequestBody requestContent = RequestBody.create(MediaType.parse("multipart/form-data"), content);
            RequestBody requestLinkType = RequestBody.create(MediaType.parse("multipart/form-data"), linkType);
            RequestBody requestWebLink = RequestBody.create(MediaType.parse("multipart/form-data"), webLink);

            Call<PostAmirtGallaryModal> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .postAmritGalleryImage("application/json",Extensions.getBearerToken(),requestAdTitle, requestImage, requestType,requestContent,requestLinkType,requestWebLink);

            call.enqueue(new Callback<PostAmirtGallaryModal>() {
                @Override
                public void onResponse(Call<PostAmirtGallaryModal> call, Response<PostAmirtGallaryModal> response) {
                    PostAmirtGallaryModal postImageResponse = response.body();
                    if (response.isSuccessful()) {
                        if (postImageResponse.getStatus()) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully", Toast.LENGTH_LONG).show();
                            try {
                                pushNotification(postImageResponse.getData().getId().toString(), postImageResponse.getData().getContent(),
                                        postImageResponse.getData().getUrl(), postImageResponse.getData().getType(),
                                        postImageResponse.getData().getType());
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                            onBackPressed();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(AddPostActivity.this, postImageResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<PostAmirtGallaryModal> call, Throwable t) {
                    Log.v("Image Upload",t.getMessage());
                    Toast.makeText(getApplicationContext(), "Failed to upload image!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

    } else {
            Toast.makeText(AddPostActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }

    private void pushNotification(String imageUploadId, String message, String url, String gallerySection, String amritSection) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("to", "/topics/krishna");
        jsonObject.put("priority", "high");
      /*  String finalMessage = message.replaceAll("\\<[^>]*>", "");
        String finalMessage1 = finalMessage.replaceAll("&nbsp;", " ");
        String finalMessage2 = message.toString().trim();
      */
        String finalMessage = message.replaceAll("\\<[^>]*>", " ");
        String finalMessage1 = finalMessage.replaceAll("&nbsp;", " ");
        String finalMessage2 = finalMessage1.toString().trim();


        JSONObject notiObj = new JSONObject();
        if (gallerySection.equalsIgnoreCase("")) {
            notiObj.put("title", "\uD83C\uDF3F\uD83C\uDF3Fश्री कृष्णा\uD83C\uDF3F\uD83C\uDF3F posted Image in Amrit");
        } else {
            notiObj.put("title", "\uD83C\uDF3F\uD83C\uDF3Fश्री कृष्णा\uD83C\uDF3F\uD83C\uDF3F posted Image in Gallery");
        }

        notiObj.put("message", finalMessage2);
        notiObj.put("sound", "default");


        //JSONObject dataObj = new JSONObject();
       // notiObj.put("message", finalMessage1);
        if (gallerySection.equalsIgnoreCase("")) {
            notiObj.put("title", "\uD83C\uDF3F\uD83C\uDF3Fश्री कृष्णा\uD83C\uDF3F\uD83C\uDF3F posted Image in Amrit");
        } else {
            notiObj.put("title", "\uD83C\uDF3F\uD83C\uDF3Fश्री कृष्णा\uD83C\uDF3F\uD83C\uDF3F posted Image in Gallery");
        }
        if (gallerySection.equalsIgnoreCase("")) {
            notiObj.put("type", amritSection);
        } else {
            notiObj.put("type", gallerySection);
        }
        notiObj.put("image", url);
        notiObj.put("post_id", imageUploadId);

        jsonObject.put("data", notiObj);
        /* jsonObject.put("notification", dataObj);*/

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .method("POST", body)
                .addHeader("Authorization", "key=" + Constants.SERVER_KEY)
                .addHeader("Content-Type", "application/json")
                .build();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                okhttp3.Response response = null;
                try {
                    response = client.newCall(request).execute();
                    JSONObject jsonObject1 = new JSONObject(response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    private void updateFile(String content) {
//        progressDialog.setTitle("Image is Updating...");
        progressDialog.setTitle("Image is Updating..." +imageFile);
        progressDialog.show();
//        if (FilePathUri != null) {
            BitmapDrawable bd = (BitmapDrawable) ShowImageView.getDrawable();
            Bitmap b = bd.getBitmap();
            File filesDir = getApplicationContext().getFilesDir();
            imageFile = new File(filesDir, id + ".jpg");
            OutputStream os;
            try {
            os = new FileOutputStream(imageFile);
            b.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }


        String type = "";
        if (gallerySection.isEmpty()){
            type = amritSection;
        }else{
            type = gallerySection;
        }


        if (linkType.isEmpty()){
            Toast.makeText(AddPostActivity.this, "Please select link type", Toast.LENGTH_SHORT).show();
            return;
        }

        String adTitle = adEditText.getText().toString().trim();
        if (adTitle.isEmpty()){
            Toast.makeText(AddPostActivity.this, "Please enter all title", Toast.LENGTH_SHORT).show();
            return;
        }
        String webLink = titleEditText.getText().toString().trim();
        if (webLink.isEmpty()){
            Toast.makeText(AddPostActivity.this, "Please enter link first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (type.isEmpty()){
            Toast.makeText(AddPostActivity.this, "Please select type first.", Toast.LENGTH_SHORT).show();
            return;
        }
//        String mcontent = ImageCaptionEditText.getHtml().trim();
        progressDialog.setTitle("Image is Uploading...");
        progressDialog.show();

        RequestBody accept = RequestBody.create(MediaType.parse("multipart/form-data"), "application/json");
        RequestBody bearerToken = RequestBody.create(MediaType.parse("multipart/form-data"), Extensions.getBearerToken());
        RequestBody requestAdTitle = RequestBody.create(MediaType.parse("multipart/form-data"), adTitle);
        MultipartBody.Part requestImage = MultipartBody.Part.createFormData("image", imageFile.getName(), RequestBody.create(MediaType.parse("image/*"), imageFile));
        RequestBody requestType = RequestBody.create(MediaType.parse("multipart/form-data"), type);
        RequestBody requestContent = RequestBody.create(MediaType.parse("multipart/form-data"), content);
        RequestBody requestLinkType = RequestBody.create(MediaType.parse("multipart/form-data"), linkType);
        RequestBody requestWebLink = RequestBody.create(MediaType.parse("multipart/form-data"), webLink);
        RequestBody requestMethod = RequestBody.create(MediaType.parse("multipart/form-data"), "PUT");

        Call<PostAmirtGallaryModal> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .updateAmritGalleryImage("application/json",Extensions.getBearerToken(),id,requestAdTitle, requestImage, requestType,requestContent,requestLinkType,requestWebLink,requestMethod);

            call.enqueue(new Callback<PostAmirtGallaryModal>() {
                @Override
                public void onResponse(Call<PostAmirtGallaryModal> call, retrofit2.Response<PostAmirtGallaryModal> response) {
                    PostAmirtGallaryModal postImageResponse = response.body();
                    if (response.isSuccessful()) {
                        if (postImageResponse.getStatus()) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Image Updated Successfully", Toast.LENGTH_LONG).show();
                            try {
                                pushNotification(postImageResponse.getData().getId().toString(), postImageResponse.getData().getContent(),
                                        postImageResponse.getData().getUrl(), postImageResponse.getData().getType(),
                                        postImageResponse.getData().getType());
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                            onBackPressed();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(AddPostActivity.this, postImageResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<PostAmirtGallaryModal> call, Throwable t) {
                    Log.d("loading",t.getMessage());
                    Toast.makeText(getApplicationContext(), "Failed to update image!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });

    }


}
