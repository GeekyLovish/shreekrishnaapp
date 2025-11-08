package com.raman.kumar.shrikrishan;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.client.Firebase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.raman.kumar.NewFullCommentScreen;
import com.raman.kumar.shrikrishan.Activity.GalleryActivity;
import com.raman.kumar.shrikrishan.Activity.GeetaActivity;
import com.raman.kumar.shrikrishan.Activity.NewGalleryActivity;
import com.raman.kumar.shrikrishan.Activity.VideoActivity;
import com.raman.kumar.shrikrishan.Activity.WallpaperActivity;
import com.raman.kumar.shrikrishan.CommentFiles.AdminPanel;
import com.raman.kumar.shrikrishan.apiNetworking.MultipartUtils;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.audio_player.AppRemovalDetectionService;
import com.raman.kumar.shrikrishan.loginfiles.LoginActivity;
import com.raman.kumar.shrikrishan.model.UpdateProfileResponse;
import com.raman.kumar.shrikrishan.networking.NetworkingCallbackInterface;
import com.raman.kumar.shrikrishan.networking.RequestHandler;
import com.raman.kumar.shrikrishan.tmrMusic.TmrMusicNewActivity;
import com.raman.kumar.shrikrishan.util.PrefHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final int REQUEST_CODE_GALLERY = 1001;
    DrawerLayout drawer;
    public static boolean loginFlag = false;
    ProgressDialog progress;
    String refreshedToken = null;
    FirebaseAuth mAuth;
    PrefHelper prefHelper;

    private ImageView mProfilePicture, edit;
    private TextView mProfileName,toolbarLogin;




    private static final int REQUEST_CODE_CAMERA = 1002;


    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;
    private boolean isCameraAction; // Flag to identify camera or gallery action


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView backImage = findViewById(R.id.backImage);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        prefHelper = new PrefHelper(getApplicationContext());
        progress = new ProgressDialog(this);
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance());
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        registerFCM();

        LinearLayout lay1 = findViewById(R.id.lay1);
        LinearLayout lay2 = findViewById(R.id.lay2);
        LinearLayout lay3 = findViewById(R.id.lay3);
        LinearLayout lay4 = findViewById(R.id.lay4);

        LinearLayout lay5 = findViewById(R.id.lay5);
        LinearLayout lay6 = findViewById(R.id.lay6);
        LinearLayout lay7 = findViewById(R.id.lay7);
        LinearLayout lay8 = findViewById(R.id.lay8);
        LinearLayout lay9 = findViewById(R.id.lay9);
        setTitle(getString(R.string.app_name));
        updateProfileImage(prefHelper.getuImage());
        
// Set up the launchers
        setupLaunchers();

        // To maintain FB Login session
        lay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    Intent i = new Intent(MainActivity.this, TmrMusicNewActivity.class);
                    i.putExtra("intentValue", "");
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    Intent i = new Intent(MainActivity.this, ImageActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (isNetworkConnected()) {
                    Intent i = new Intent(MainActivity.this, WallpaperActivity.class);
                    i.putExtra("url", "");
                    i.putExtra("title", "Wallpaper");
                    i.putExtra("backToActivity", "");
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Share App");
                    i.putExtra(Intent.EXTRA_TEXT, "\uD83C\uDF3F\uD83C\uDF3Fश्री कृष्णा\uD83C\uDF3F\uD83C\uDF3F" + "\n" + "श्री कृष्ण भगवान की सुन्दर तस्वीरें देखने Download करने व Wallpaper , Video , Ringtone , Bhajan , Use करने के लिए श्री कृष्णा App को Download करें" + "\n" + "App Link: https://play.google.com/store/apps/details?id=com.raman.kumar.shrikrishan");
                    startActivity(Intent.createChooser(i, "Share via"));
                } else {
                    Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lay5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    Intent i = new Intent(MainActivity.this, TextActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lay6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    Intent i = new Intent(MainActivity.this, GeetaActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lay7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    Intent i = new Intent(MainActivity.this, GalleryActivity.class);
                    i.putExtra("url", "");
                    i.putExtra("backToActivity", "");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lay8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()) {
                    Intent i = new Intent(MainActivity.this, NewGalleryActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lay9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()) {
                    Intent i = new Intent(MainActivity.this, VideoActivity.class);
                    i.putExtra("url", "");
                    i.putExtra("backToActivity", "");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        View navigationLayout = navigationView.getHeaderView(0);
        if (navigationLayout != null) {
            mProfilePicture = navigationLayout.findViewById(R.id.profilePicture);
            mProfileName = navigationLayout.findViewById(R.id.profileName);
            edit = navigationLayout.findViewById(R.id.edit);
        }

        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((!prefHelper.getLogedIn().equals("no") && !prefHelper.getLogedIn().equals(""))) {
                    if (drawer != null) drawer.closeDrawer(GravityCompat.START);

                    createDailogBox();
                }


            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!prefHelper.getLogedIn().equals("no") && !prefHelper.getLogedIn().equals(""))) {
                    if (drawer != null) drawer.closeDrawer(GravityCompat.START);

                    createDailogBox();
                }


            }
        });


        navigationView.setNavigationItemSelectedListener(this);
        Menu menuNav = navigationView.getMenu();

        final MenuItem loginItem = menuNav.findItem(R.id.login);
        final MenuItem logoutItem = menuNav.findItem(R.id.log_out);
        final MenuItem adminPanel = menuNav.findItem(R.id.admin);


        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {

            }

            @Override
            public void onDrawerOpened(View view) {
                if (prefHelper.getLogedIn().equals("no")) {
                    adminPanel.setVisible(false);
                    loginItem.setVisible(true);
                    logoutItem.setVisible(false);
                } else
                {
                            loginItem.setVisible(false);
                            logoutItem.setVisible(true);
                            String role  = prefHelper.getuRole();
                            System.out.println("dsfhdsjhajhfja  role" +role);
                            if (role.equals("Administrator")) {
                                System.out.println("dsfhdsjhajhfja  true" +role);
                                adminPanel.setVisible(true);
                            } else {
                                System.out.println("dsfhdsjhajhfja  false" +role);
                                adminPanel.setVisible(false);

                            }
                        }

            }

            @Override
            public void onDrawerClosed(View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
        toggle.syncState();

        checkVersion();

        Intent serviceIntent = new Intent(this, AppRemovalDetectionService.class);
        startService(serviceIntent);
    }

private void setUpToolbarLogin(){
    TextView toolbarLogin = findViewById(R.id.toolbarLogin);
    if (prefHelper.getLogedIn().equals("no") || prefHelper.getLogedIn().equals("")) {
        toolbarLogin.setVisibility(View.VISIBLE);
        toolbarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }else{
        toolbarLogin.setVisibility(View.GONE);
    }
}

    private void setupLaunchers() {
        // Camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        if (bitmap != null) {
                            updateProfilePicture(bitmap);

//                            imageView.setImageBitmap(bitmap);
                            Toast.makeText(this, "Image captured successfully!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Camera operation canceled.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            updateProfilePicture(selectedImageUri);
//                            imageView.setImageURI(selectedImageUri);
                            Toast.makeText(this, "Image selected: " + selectedImageUri.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Gallery operation canceled.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Permission launcher for both Camera and Gallery
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (boolean isGranted : result.values()) {
                        if (!isGranted) {
                            allGranted = false;
                            break;
                        }
                    }
                    if (allGranted) {
                        // Launch camera or gallery based on the user's action
                        if (isCameraAction) {
                            openCamera();
                        } else {
                            openGallery();
                        }
                    } else {
                        Toast.makeText(this, "Permission is required for this feature.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.login) {
            if (prefHelper.getLogedIn().equals("no") || prefHelper.getLogedIn().equals("")) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.contact_us) {
            showContactUsDialog();
        } else if (id == R.id.support_us) {
            Intent intent = new Intent(MainActivity.this, SupportActivity.class);
            startActivity(intent);
        } else if (id == R.id.log_out) {
            if (prefHelper.getLogedIn().equals("yes")) {
                signOut();
                prefHelper.clearAllPreferences();
                prefHelper.setuName("");
                prefHelper.setPhone("");
                prefHelper.setuEmail("");
                prefHelper.setuId("");
                prefHelper.setLogedIn("no");
                prefHelper.setFirebaseToken("");
                deleteCache(getApplicationContext());
                Toast.makeText(getApplicationContext(), "Log out successfully", Toast.LENGTH_SHORT).show();
                loginFlag = false;

                updateDrawerProfile();
                setUpToolbarLogin();
            }

        } else if (id == R.id.admin) {
            if (prefHelper.getuRole().equals("Administrator")) {
//                Intent intent = new Intent(MainActivity.this, PasswordActivity.class);
                Intent intent = new Intent(MainActivity.this, AdminPanel.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "You cannot access this section.", Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }



    public void createDailogBox() {
        CharSequence[] colors = new CharSequence[]{"Camera",
                "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an Options");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                if (pos == 0) {
                    isCameraAction = true;
                    checkPermissionsAndProceed();
                } else if (pos == 1) {
                    isCameraAction = false;
                    checkPermissionsAndProceed();
                }
            }
        });
        builder.show();

    }




    // Check permissions and proceed
    private void checkPermissionsAndProceed() {
        if (isCameraAction) {
            // Check camera permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                permissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
            }
        } else {
            // Check gallery permissions based on Android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13 and above, no permission needed to access gallery
                openGallery();
            } else {
                // For Android versions below Android 13 (API 33)
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    permissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
                }
            }
        }
    }




    /**
     * Handling camera intent here
     */
    // Camera intent
    private void cameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }


    /**
     * Handling gallery intent
     */
    // Gallery intent
    private void galleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickPhoto);
    }

    /**
     * Handle Permission Selection Callbacks
     */
    // Step 3: Handle Permissions Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                openCamera();
            } else if (requestCode == REQUEST_CODE_GALLERY) {
                openGallery();
            }
        } else {
            Toast.makeText(this, "Permission is required for this feature.", Toast.LENGTH_SHORT).show();
        }
    }

    // Step 4: Open Camera
    // Launch the camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    // Step 5: Open Gallery
    // Launch the gallery
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }


//    private void uploadImage() {
//        progress.setMessage("Uploading");
//        String name = mProfileName.getText().toString();
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Accept", "application/json");
//        headers.put("Authorization", "Bearer "+prefHelper.getAuthToken());
//
//        RequestBody requestName = RequestBody.create(MediaType.parse("multipart/form-data"), name);
//        RequestBody token = RequestBody.create(MediaType.parse("multipart/form-data"),  prefHelper.getuId());
////        File file = new File(FilePathUri.getPath());
//        MultipartBody.Part requestImage = MultipartBody.Part.createFormData("image", mImageFile.getName(), RequestBody.create(MediaType.parse("image/*"),mImageFile));
//
//        Call<UpdateProfileResponse> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .updateProfile(headers,token, requestName, requestImage);
//
//        call.enqueue(new Callback<UpdateProfileResponse>() {
//            @Override
//            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
//                UpdateProfileResponse signupResponse = response.body();
//                if (response.isSuccessful()) {
//                    progress.dismiss();
//                    if (signupResponse.getSuccess()) {
//                        prefHelper.setuImage(signupResponse.getUpdateProfile().getImage());
////                        prefHelper.(signupResponse.getUser().getBlock());
////                        sharedPrefManager.saveUser(signupResponse.getUser());
////                        Toast.makeText(MainActivity.this, signupResponse.getMsg(), Toast.LENGTH_SHORT).show();
////                        prefHelper.setuImage();
//                        updateProfileImage(prefHelper.getuImage());
//                        updateDrawerProfile();
//                    } else {
//                        progress.dismiss();
//                        Toast.makeText(MainActivity.this, signupResponse.getMsg(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else {
//                    progress.dismiss();
//                    Log.d("error", String.valueOf(response.body()));
//                    Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
//                Log.d("error",t.getMessage());
//                Toast.makeText(getApplicationContext(), " failed ! ", Toast.LENGTH_LONG).show();
//                progress.dismiss();
//            }
//        });
//
//    }





    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void showProgressDialog() {

        progress.setMessage("Login In ....");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        //Todo getdata
    }

    private void signOut() {
        mAuth.signOut();
    }



    public void showContactUsDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.contact_us_lay);


        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button okButton = dialog.findViewById(R.id.okButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    public void checkVersion() {
        {
            JSONObject json = new JSONObject();
            String url = "http://ramankumarynr.com/api/?json=get_post&id=1609";
            //  showProgressDialog();
            RequestHandler.getAllAudios(url, this, json, new NetworkingCallbackInterface() {
                @Override
                public void onSuccess(NetworkResponse response, boolean fromCache) {
                    System.out.print("response........" + response);
                    //   mProgressDialog.dismiss();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        System.out.print("jsonArrayresponse........" + jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(String response, boolean fromCache) {
                    System.out.print("response........" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("ok")) {
                            showDialog();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(VolleyError error) {
                    //mProgressDialog.dismiss();
                    //  Toast.makeText(MainActivity.this, "Connection not available",Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onNetworkFailure(String error) {
                    // mProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Internet connection not available", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    public void showDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.contact_us_lay);
        TextView titleText = dialog.findViewById(R.id.titleText);
        TextView errorView = dialog.findViewById(R.id.errorView);
        errorView.setText("A new version has been released. Please update a latest version from play store");

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button okButton = dialog.findViewById(R.id.okButton);
        titleText.setText("Update");
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void registerFCM() {
//        refreshedToken = FirebaseAuth.getInstance().getToken();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    refreshedToken = task.getException().getMessage();
                    Log.w("FCM TOKEN Failed", task.getException());
                } else {
                    refreshedToken = task.getResult();
                    Log.e("FCM TOKEN", refreshedToken);
                }
            }
        });



        System.out.println("device id is ==>>" + refreshedToken);
    }

    public static void saveToken(final Context context, String token, String id) {
        JSONObject json = new JSONObject();
        try {
            json.put("fcm_token", token);
            json.put("key", getMd5("ramankumarynr"));
            System.out.print("token.........#######" + token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestHandler.saveToken(context, token, getMd5("ramankumarynr"), json, new NetworkingCallbackInterface() {
            @Override
            public void onSuccess(NetworkResponse response, boolean fromCache) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    System.out.print(jsonArray);
                    SharedPreferences.Editor editor = context.getSharedPreferences("fcm_token", MODE_PRIVATE).edit();
                    editor.putBoolean("status", true);
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccess(String response, boolean fromCache) {
                try {
                    Toast.makeText(context.getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = context.getSharedPreferences("fcm_token", MODE_PRIVATE).edit();
                    editor.putBoolean("status", true);
                    editor.apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(VolleyError error) {
                Toast.makeText(context.getApplicationContext(), "Connection not available", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNetworkFailure(String error) {
                Toast.makeText(context.getApplicationContext(), "Internet Connection not available", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public static String getMd5(String input) {
        try {
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onResume() {
        /* Update Profile Drawer */
        updateDrawerProfile();
        setUpToolbarLogin();
        super.onResume();

    }

    /**
     * Update Navigation Drawer Profile Data
     */
    private void updateDrawerProfile() {
        String userName = getString(R.string.app_name);
        String userImage = "";
        String userEmail = "";
        if ((!prefHelper.getLogedIn().equals("no") && !prefHelper.getLogedIn().equals(""))) {

            if (!prefHelper.getuName().trim().isEmpty())
                userName = prefHelper.getuName();
            userEmail = prefHelper.getuEmail();
            updateProfileImage(prefHelper.getuImage());
//            if (!prefHelper.getuImage().trim().isEmpty()) {
            userImage = prefHelper.getuImage();
//            }
        }
        if (mProfileName != null && userEmail.equalsIgnoreCase("ramanrajniynr@gmail.com")) {
            mProfileName.setText(getString(R.string.app_name));
        } else if (mProfileName != null) {
            mProfileName.setText(userName);
        }

    }

    private void updateProfileImage(Object url) {

        try {
            if (mProfilePicture != null) {
                Glide.with(this)
                        .load(url)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_account)
                                .error(R.drawable.ic_account)
                                .centerCrop()
                                .circleCrop())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mProfilePicture);
            }
        }catch (Exception e){

        }
    }

    private void updateProfilePicture(Object image) {
        MultipartBody.Part body;

        if (image instanceof Bitmap) {
            body = MultipartUtils.prepareFilePartFromBitmap(this, "profile", (Bitmap) image);
        } else if (image instanceof Uri) {
            body = MultipartUtils.prepareFilePartFromUri(this, "profile", (Uri) image);
        } else {
            throw new IllegalArgumentException("Unsupported image type");
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + prefHelper.getAuthToken());

        Call<UpdateProfileResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .updateProfilePic(headers, body);

        call.enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                progress.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    UpdateProfileResponse signupResponse = response.body();
                    if (signupResponse.getSuccess()) {
                        prefHelper.setuImage(signupResponse.getUpdateProfile().getImage());
                    } else {
                        Toast.makeText(MainActivity.this, signupResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Sign up failed!", Toast.LENGTH_LONG).show();
            }
        });



    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
