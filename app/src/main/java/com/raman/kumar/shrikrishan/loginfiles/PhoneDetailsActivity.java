package com.raman.kumar.shrikrishan.loginfiles;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.raman.kumar.shrikrishan.MainActivity;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.model.UpdateProfileResponse;
import com.raman.kumar.shrikrishan.phoneAuth.PhoneAuthActivity;
import com.raman.kumar.shrikrishan.util.PrefHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneDetailsActivity extends AppCompatActivity {

    EditText userNameEdit, userEmailEdit, userPasswordEdit, confirmPasswordEdit, userPhoneEdit;
    ImageView backButton;
    ConstraintLayout signUpButton, nameLayout, emailLayout, passwordLayout, confirmLayout, phoneLayout;
    TextView SignIn;
    ProgressDialog progress;
    FirebaseAuth mAuth;
    String refreshedToken = null;
    String userID;
    String token;
    PrefHelper prefHelper;
    File imageFile;
    Uri FilePathUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_details);

        Intent intent = getIntent();
        if (intent.hasExtra("userID")) {
            userID = intent.getStringExtra("userID");
            token = intent.getStringExtra("token");
        }

        if (intent.hasExtra("phone_number")) {
            String phonenumber = intent.getStringExtra("phone_number");
        }

        prefHelper = new PrefHelper(getApplicationContext());
        FirebaseApp.initializeApp(this);
        initViews();
        registerFCM();
        listeners();
    }

    public void registerFCM() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("FCM TOKEN Failed", task.getException());
                } else {
                    refreshedToken = task.getResult();
                    Log.e("FCM TOKEN", refreshedToken);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerFCM();
        if (refreshedToken != null) {
            String androidId = String.valueOf(new Random().nextLong());
            SharedPreferences prefs = getSharedPreferences("fcm_token", MODE_PRIVATE);
            boolean tokenStatus = prefs.getBoolean("status", false);
            if (!tokenStatus) {
                MainActivity.saveToken(this, refreshedToken, androidId);
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show();
    }

    private boolean containsReligiousName(String input) {
        String[] restrictedWords = {
                // English
                "ram", "shree ram", "shri ram", "krishna", "shree krishna", "shri krishna",
                "shree", "shri", "god", "lord", "bhagwan", "allah", "jesus", "mahadev", "shiv", "shiva",
                "waheguru", "buddha", "guru", "deva", "mata", "durga", "ganesha", "sai", "kabir", "om",

                // Hindi
                "राम", "श्री राम", "श्रीराम", "कृष्ण", "श्री कृष्ण", "भगवान", "ईश्वर", "शिव", "महादेव",
                "अल्लाह", "यीशु", "गुरु", "देव", "माता", "दुर्गा", "साई", "गणेश", "ओम", "कबीर"
        };

        input = input.toLowerCase();

        for (String word : restrictedWords) {
            if (input.contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


    private void listeners() {
        backButton.setOnClickListener(v -> onBackPressed());

        signUpButton.setOnClickListener(v -> {
            String username = userNameEdit.getText().toString().trim();
            if (containsReligiousName(username)) {
                Toast.makeText(this, "आप भगवान जी का नाम उपयोग नहीं कर सकते। कृपया कोई और नाम चुनें।", Toast.LENGTH_LONG).show();
                return;
            }

            if (isNetworkConnected()) {
                signupService(username);
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        SignIn.setOnClickListener(v -> {
            Intent intent = new Intent(PhoneDetailsActivity.this, PhoneAuthActivity.class);
            startActivity(intent);
            finishAffinity();
        });
    }

    private void signupService(String name) {
        showProgressDialog();

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + token);

        RequestBody userIDPart = RequestBody.create(MediaType.parse("multipart/form-data"), userID);
        RequestBody requestName = RequestBody.create(MediaType.parse("multipart/form-data"), name);

        FilePathUri = Uri.parse("android.resource://com.raman.kumar.shrikrishan/drawable/home_screen_new");
        Bitmap bitmap;
        File f = new File(FilePathUri.getPath());
        String fname = f.getName();

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
            File filesDir = getApplicationContext().getFilesDir();
            imageFile = new File(filesDir, fname + ".jpg");

            try (OutputStream os = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultipartBody.Part requestImage = MultipartBody.Part.createFormData("image", imageFile.getName(),
                RequestBody.create(MediaType.parse("image/*"), imageFile));

        Call<UpdateProfileResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .updateProfile(headers, userIDPart, requestName, requestImage);

        call.enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                progress.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    UpdateProfileResponse signupResponse = response.body();
                    if (signupResponse.getSuccess()) {
                        prefHelper.setuName(signupResponse.getUpdateProfile().getName());
                        prefHelper.setuImage(signupResponse.getUpdateProfile().getImage());
                        prefHelper.setLogedIn("yes");
                        Toast.makeText(PhoneDetailsActivity.this, signupResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        Intent switchOnHome = new Intent(PhoneDetailsActivity.this, MainActivity.class);
                        switchOnHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(switchOnHome);
                    } else {
                        Toast.makeText(PhoneDetailsActivity.this, signupResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PhoneDetailsActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Sign up failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initViews() {
        progress = new ProgressDialog(this);
        userNameEdit = findViewById(R.id.userNameEdit);
        userEmailEdit = findViewById(R.id.userEmailEdit);
        userPasswordEdit = findViewById(R.id.userPasswordEdit);
        confirmPasswordEdit = findViewById(R.id.confirmPasswordEdit);
        userPhoneEdit = findViewById(R.id.userPhoneEdit);
        backButton = findViewById(R.id.backButton);
        signUpButton = findViewById(R.id.signUpButton);
        SignIn = findViewById(R.id.SignIn);
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmLayout = findViewById(R.id.confirmLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
    }
}
