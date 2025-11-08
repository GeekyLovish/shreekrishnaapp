package com.raman.kumar.shrikrishan.phoneAuth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.raman.kumar.modals.authentication.verifyOtp.VerifyOtpModel;
import com.raman.kumar.shrikrishan.MainActivity;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.loginfiles.PhoneDetailsActivity;
import com.raman.kumar.shrikrishan.util.PrefHelper;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyPhoneActivity extends AppCompatActivity {

    private String verificationId;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private EditText editText;
    private PrefHelper prefHelper;
    private String refreshedToken;
    private String phonenumber;
    private String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        initializeUI();

        FirebaseApp.initializeApp(this);
        registerFCM();
        sendVerificationCode(phonenumber);

        findViewById(R.id.buttonSignIn).setOnClickListener(view -> {
            String code = editText.getText().toString().trim();
            if (code.isEmpty()) {
                editText.setError("Enter code...");
                editText.requestFocus();
                return;
            }
            verifyCode(code);
        });
    }

    private void initializeUI() {
        progressBar = findViewById(R.id.progressbar);
        editText = findViewById(R.id.editTextCode);
        ImageView backButton = findViewById(R.id.backButton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");

        prefHelper = new PrefHelper(getApplicationContext());
        phonenumber = getIntent().getStringExtra("phonenumber");
        ID = getIntent().getStringExtra("ID");

        mAuth = FirebaseAuth.getInstance();

        backButton.setOnClickListener(view -> onBackPressed());
    }

    private void registerFCM() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("FCM TOKEN Failed", task.getException());
                refreshedToken = "FCM TOKEN Failed";
            } else {
                refreshedToken = task.getResult();
                Log.e("FCM TOKEN", refreshedToken);
            }
        });
    }


    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    // Auto-retrieval or instant validation succeeded
                    String code = credential.getSmsCode();
                    if (code != null) {
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(VerifyPhoneActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCodeSent(@NonNull String s,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    super.onCodeSent(s, token);
                    verificationId = s; // Save this for verification later
                }
            };


    private void verifyCode(String code) {
        if (verificationId != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneAuthCredential(credential,code);
        } else {
            Toast.makeText(this, "Verification ID is null", Toast.LENGTH_SHORT).show();
        }
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, String code) {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
//                        FirebaseUser user = task.getResult().getUser();
//                        String uid = user.getUid();
//                        String phone = user.getPhoneNumber();

                        Toast.makeText(this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();

                        // Optionally authenticate with your server here
                        authenticateWithServer( code);

                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(this, "OTP verification failed: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void authenticateWithServer( String code) {
        progressBar.setVisibility(View.VISIBLE);

        Call<VerifyOtpModel> call = RetrofitClient.getInstance().getApi().verifyOTP(ID,refreshedToken,code);
        call.enqueue(new Callback<VerifyOtpModel>() {
            @Override
            public void onResponse(@NonNull Call<VerifyOtpModel> call, @NonNull Response<VerifyOtpModel> response) {
                progressBar.setVisibility(View.GONE);
                VerifyOtpModel loginResponse = response.body();
                if (response.isSuccessful() && loginResponse != null && loginResponse.getStatus()) {
                    handleUserLogin(loginResponse);
                } else {
                    Toast.makeText(VerifyPhoneActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<VerifyOtpModel> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(VerifyPhoneActivity.this, "Login failed!"+t.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    private void handleUserLogin(VerifyOtpModel loginResponse) {
        String name = loginResponse.getData().getName();
        if (name.isEmpty()){
            prefHelper.setLogedIn("no");

            prefHelper.setPhone(loginResponse.getData().getPhoneNumber());
            prefHelper.setuId(loginResponse.getData().getId().toString());
            prefHelper.setuEmail(loginResponse.getData().getEmail());
            prefHelper.setAuthToken(loginResponse.getData().getToken());
            String role  = loginResponse.getData().getRole().getName();
            prefHelper.setuRole(role);

            navigateToPhoneDetailsActivity(loginResponse);
        }else{

            prefHelper.setuName(loginResponse.getData().getName());
            prefHelper.setPhone(loginResponse.getData().getPhoneNumber());
            prefHelper.setLogedIn("yes");
            prefHelper.setuId(loginResponse.getData().getId().toString());
            prefHelper.setuEmail(loginResponse.getData().getEmail());
            prefHelper.setAuthToken(loginResponse.getData().getToken());
            String role  = loginResponse.getData().getRole().getName();
            prefHelper.setuRole(role);


            navigateToMainActivity();
        }
    }

    private void navigateToPhoneDetailsActivity(VerifyOtpModel loginResponse) {
        prefHelper.setuName(loginResponse.getData().getName());
        prefHelper.setPhone(loginResponse.getData().getPhoneNumber());

        Toast.makeText(getApplicationContext(), "Please register by filling the signup form.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(VerifyPhoneActivity.this, PhoneDetailsActivity.class);
        intent.putExtra("phone_number", loginResponse.getData().getPhoneNumber());
        intent.putExtra("userID", loginResponse.getData().getId().toString());
        intent.putExtra("token", loginResponse.getData().getToken());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
    }
}
