package com.raman.kumar.shrikrishan.loginfiles;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.raman.kumar.modals.authentication.login.LoginModal;
import com.raman.kumar.shrikrishan.ForgotPasswordActivity;
import com.raman.kumar.shrikrishan.MainActivity;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.apiNetworking.SharedPrefManager;
import com.raman.kumar.shrikrishan.phoneAuth.PhoneAuthActivity;
import com.raman.kumar.shrikrishan.util.PrefHelper;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class LoginActivity extends AppCompatActivity {

    EditText userEmailEdit, userPasswordEdit;
    ImageView backButton;
    ConstraintLayout signInButton, emailLayout, passwordLayout, phoneSignInButton, signUpButton;
    TextView forgotPassword, SignUp;
    ProgressDialog progress;
    FirebaseAuth mAuth;
    String refreshedToken = null;
    String block = "";
    private String SHOWCASE_ID = "Krishna Login";
    PrefHelper prefHelper;
    boolean digitsOnly;
    SharedPrefManager sharedPrefManager;


    private ImageView passwordToggleIcon;
    private boolean isPasswordVisible = false; // Track current visibility state
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//Showcase/Tutorials for the specific option in the app to guide new Users

        sharedPrefManager = new SharedPrefManager(getApplicationContext());
        prefHelper = new PrefHelper(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        registerFCM();
        FirebaseApp.initializeApp(this);
        initViews();
        new MaterialShowcaseView.Builder(LoginActivity.this)
                .setTarget(signUpButton)
                .withRectangleShape()
                .setDismissOnTouch(true)
                .setMaskColour(getResources().getColor(R.color.app_color_transparent))
                .setContentText("पिक्चर्स पर लाइक कोममेंट्स करने के लिए आपको ऐप मे एक एकाउंट बनाना होगा उसके लिए आप लॉगिन विद फोन बटन से अपने फोन नम्बर पर ओ टी पी लेकर अपने नाम से एक एकाउंट बना सकते है या क्रिएट एकाउंट बटन से अपना नाम , ईमेल , फोन नम्बर व कोई भी पासवर्ड रखकर सम्बइट करें फिर ऐप द्वारा आपकी जीमेल पर भेजे गए मेल को खोले, उसमें दिए गए नीले लिंक को खोलकर अपनी आई डी कन्फर्म करें और दोबारा उसी ईमेल पासवर्ड साईन इन करें ऐसे भी ऐप में एकाउंट बना सकते है")
//            .setContentText("As part of the security features in the SAFE4R application, you can black out your screen by double tapping it at any time during your SAFE4R Alert. Though the screen is blacked out everything is still functioning appropriately. To view your live video stream again, simply double tap your screen. \n\n Tap anywhere to close this")
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .show();
        // sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(LoginActivity.this, SHOWCASE_ID);
        sequence.setConfig(config);
        sequence.start();
        listeners();
    }

    public void registerFCM() {
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

    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    private void listeners() {
        backButton.setOnClickListener(v -> onBackPressed());
        userEmailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                digitsOnly = TextUtils.isDigitsOnly(userEmailEdit.getText());
                if (digitsOnly) {
                    passwordLayout.setVisibility(View.GONE);
                } else {
                    passwordLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signInButton.setOnClickListener(v -> {
            if (digitsOnly) {
                String mobile = userEmailEdit.getText().toString().trim();
                if (mobile.isEmpty() || mobile.length() < 10) {
                    userEmailEdit.setError("Enter a valid mobile");
                    userEmailEdit.requestFocus();
                    return;
                }
                verify_phone(mobile);

            } else {
                if (isValidateLogin()) {
                    if (isNetworkConnected()) {
                        showProgressDialog();
                        loginUser(userEmailEdit.getText().toString().trim(), userPasswordEdit.getText().toString().trim());
                    }else {
                        Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Invalid password!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        phoneSignInButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, PhoneAuthActivity.class);
            startActivity(intent);
//            openDialog();
        });

        passwordToggleIcon = findViewById(R.id.passwordToggleIcon);

        passwordToggleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            userPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggleIcon.setImageResource(R.drawable.ic_eye_closed); // Your closed eye icon
        } else {
            // Show password
            userPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggleIcon.setImageResource(R.drawable.ic_eye_open); // Your open eye icon
        }

        // Move cursor to end of text
        userPasswordEdit.setSelection(userPasswordEdit.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void openDialog() {

        Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.confirm_popup);
        dialog.setCanceledOnTouchOutside(false);
        RelativeLayout yesButton = dialog.findViewById(R.id.yesButton);
        RelativeLayout noButton = dialog.findViewById(R.id.noButton);

        Window window = dialog.getWindow();
        window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
//        dialog.getWindow().getAttributes() = lp;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.CENTER);
        dialog.show();

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void loginUser(String email, String password) {
        showProgressDialog();
        Call<LoginModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .login(email,password,refreshedToken);

        call.enqueue(new Callback<LoginModal>() {
            @Override
            public void onResponse(Call<LoginModal> call, Response<LoginModal> response) {
                LoginModal loginResponse = response.body();
                if (response.isSuccessful()  && loginResponse != null) {
                    progress.dismiss();

                    prefHelper.setuName(loginResponse.getData().getName());
                    prefHelper.setPhone(loginResponse.getData().getPhoneNumber());
                    prefHelper.setLogedIn("yes");
                    prefHelper.setuId(loginResponse.getData().getId().toString());
                    prefHelper.setuEmail(loginResponse.getData().getEmail());
                    prefHelper.setAuthToken(loginResponse.getData().getToken());

                    String role  = loginResponse.getData().getRole().getName();
                    System.out.println("dsfhdsjhajhfja  role login" +role);
                    prefHelper.setuRole(role);

//                        prefHelper.setAdminId(loginResponse.getData().getAdmin_user_id());
                    prefHelper.setuImage(loginResponse.getData().getProfilePic());
//                        sharedPrefManager.saveUser(loginResponse.getUser());
                    Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent switchOnHome = new Intent(LoginActivity.this, MainActivity.class);
                    switchOnHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(switchOnHome);
                }
                else
                {
                    progress.dismiss();
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyStr = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorBodyStr);
                            String message = jsonObject.optString("message", "Unknown error occurred.");
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            Log.d("API_ERROR", message);
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to login, please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        Log.e("API_ERROR", "Parsing error: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModal> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Login failed!! email not verified", Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
        });

    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    private void initViews() {
        progress = new ProgressDialog(this);
        userEmailEdit = findViewById(R.id.userEmailEdit);
        userPasswordEdit = findViewById(R.id.userPasswordEdit);
        backButton = findViewById(R.id.backButton);
        signInButton = findViewById(R.id.signInButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        SignUp = findViewById(R.id.SignUp);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        phoneSignInButton = findViewById(R.id.phoneSignInButton);
        signUpButton = findViewById(R.id.signUpButton);
    }

    boolean isValidateLogin() {
        boolean isValid = true;

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (userEmailEdit.getText().toString().isEmpty()) {
            emailLayout.setBackgroundResource((R.drawable.rejected_edit_back));
            isValid = false;
        } else {
            if (!userEmailEdit.getText().toString().trim().matches(emailPattern)) {
                Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        }

        if (userPasswordEdit.getText().toString()
                .isEmpty() || userPasswordEdit.getText().length() < 6 || userPasswordEdit.getText().length() > 20
        ) {
            passwordLayout.setBackgroundResource(R.drawable.rejected_edit_back);
            isValid = false;
        }
        return isValid;
    }

    void verify_phone(String phoneNumber)
    {
        Intent intent = new Intent(LoginActivity.this, PhoneVerifyActivity.class);
                            intent.putExtra("phonenumber", phoneNumber);
                            startActivity(intent);
//        showProgressDialog();
//        Call<PhoneLoginResponse> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .phoneLogin(phoneNumber);
//
//        call.enqueue(new Callback<PhoneLoginResponse>() {
//            @Override
//            public void onResponse(Call<PhoneLoginResponse> call, Response<PhoneLoginResponse> response) {
//                PhoneLoginResponse loginResponse = response.body();
//                if (response.isSuccessful()) {
//                    progress.dismiss();
//                    if (loginResponse.getSuccess()) {
//                        if(loginResponse.getSuccess())
//                        {
////                        if (loginResponse.getMsg() == "Not Registered")
////                        {
////                            Toast.makeText(getApplicationContext(), "हमने चेक किया कि अपने अभी तक ऐप में रजिस्टर नहीं किया है। कृपा पहले रजिस्टर करें साइनअप फॉर्म भर कर के।", Toast.LENGTH_SHORT).show();
////                            Intent switchOnLogin = new Intent(PhoneAuthActivity.this, LoginActivity.class);
////                            switchOnLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                            startActivity(switchOnLogin);
////                        }
////                        else {
//                            progress.dismiss();
//                            Intent intent = new Intent(LoginActivity.this, PhoneVerifyActivity.class);
//                            intent.putExtra("userId", loginResponse.getPhoneLogin().getUserId());
//                            intent.putExtra("otp", loginResponse.getPhoneLogin().getOtp());
//                            startActivity(intent);
//                        }
//
//                    } else {
//                        progress.dismiss();
//                        Toast.makeText(LoginActivity.this, loginResponse.getMsg(), Toast.LENGTH_SHORT).show();
//                    }
//                }else {
//                    progress.dismiss();
//                    Toast.makeText(LoginActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PhoneLoginResponse> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Login failed! Try again", Toast.LENGTH_LONG).show();
//                progress.dismiss();
//            }
//        });
    }


    @Override
    public void onStart() {
        super.onStart();
        if (prefHelper.getLogedIn()=="yes")
        {
            Intent switchOnHome = new Intent(LoginActivity.this, MainActivity.class);
            switchOnHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(switchOnHome);
        }
    }

    @Override
    protected void onResume() {
        if (prefHelper.getLogedIn()=="yes")
        {
            Intent switchOnHome = new Intent(LoginActivity.this, MainActivity.class);
            switchOnHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(switchOnHome);
        }
//        registerFCM();
//        if (refreshedToken != null) {
//            String androidId = String.valueOf(new Random().nextLong());
//            SharedPreferences prefs = getSharedPreferences("fcm_token", MODE_PRIVATE);
//            boolean tokenStatus = prefs.getBoolean("status", false);
//            if (!tokenStatus) {
//                saveToken(this, refreshedToken, androidId);
//            }
//        }
        super.onResume();
    }
}
