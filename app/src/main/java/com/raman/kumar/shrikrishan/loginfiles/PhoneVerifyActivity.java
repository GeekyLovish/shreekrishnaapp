package com.raman.kumar.shrikrishan.loginfiles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.google.firebase.messaging.FirebaseMessaging;
import com.raman.kumar.shrikrishan.MainActivity;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.model.VerifyPhoneResponse;
import com.raman.kumar.shrikrishan.phoneAuth.VerifyPhoneActivity;
import com.raman.kumar.shrikrishan.util.PrefHelper;
import com.raman.kumar.shrikrishan.util.User;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneVerifyActivity extends AppCompatActivity {


    //These are the objects needed
    //It is the verification id that will be sent to the user
    private String mVerificationId;

    //The edittext to input the code
    private EditText editTextCode;
    private ProgressBar progressBar;

    //firebase auth object
    private FirebaseAuth mAuth;
    PrefHelper prefHelper;
    String refreshedToken = null;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);
        prefHelper = new PrefHelper(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        //initializing objects
        mAuth = FirebaseAuth.getInstance();
        editTextCode = findViewById(R.id.editTextCode);
        progressBar = findViewById(R.id.progressBar);
        registerFCM();
        FirebaseApp.initializeApp(this);

        //getting mobile number from the previous activity
        //and sending the verification code to the number
        Intent intent = getIntent();
        mobile = intent.getStringExtra("phonenumber");
//        String userId = intent.getStringExtra("userId");
//        String otp = intent.getStringExtra("otp");
        sendVerificationCode(mobile);

        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCode.getText().toString().trim();
                if (code.isEmpty()) {
                    editTextCode.setError("Enter valid code");
                    editTextCode.requestFocus();
                    return;
                }

                //verifying the code entered manually
                verifyVerificationCode(code);

//                verifyOTP(userId, otp);
            }
        });

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

//    private void verifyOTP(String phonenumber) {
//
//        Call<VerifyPhoneResponse> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .phoneLogin(phonenumber);
//
//        call.enqueue(new Callback<VerifyPhoneResponse>() {
//            @Override
//            public void onResponse(Call<VerifyPhoneResponse> call, Response<VerifyPhoneResponse> response) {
//                VerifyPhoneResponse loginResponse = response.body();
//                if (response.isSuccessful()) {
//                    if (loginResponse.getSuccess()) {
//                        if (loginResponse.getMsg().equals("Register Sucessfully") ) {
//
//                            Toast.makeText(getApplicationContext(), "हमने चेक किया कि अपने अभी तक ऐप में रजिस्टर नहीं किया है। कृपा पहले रजिस्टर करें साइनअप फॉर्म भर कर के।", Toast.LENGTH_SHORT).show();
//                            prefHelper.setuName(loginResponse.getUser().getUsername());
//                            prefHelper.setPhone(loginResponse.getUser().getPhone_number());
//                            prefHelper.setLogedIn("no");
//                            prefHelper.setuId(loginResponse.getUser().getUser_id());
//                            prefHelper.setuEmail(loginResponse.getUser().getEmail());
//                            prefHelper.setAuthToken(loginResponse.getUser().getUserToken());
//
//                            Intent intent = new Intent(PhoneVerifyActivity.this, PhoneDetailsActivity.class);
//                            intent.putExtra("phone_number", loginResponse.getUser().getPhone_number());
//                            intent.putExtra("userID", loginResponse.getUser().getUser_id());
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);
//                        }else
//                        {
//                            prefHelper.setuName(loginResponse.getUser().getUsername());
//                            prefHelper.setPhone(loginResponse.getUser().getPhone_number());
//                            prefHelper.setLogedIn("yes");
//                            prefHelper.setuId(loginResponse.getUser().getUser_id());
//                            prefHelper.setuEmail(loginResponse.getUser().getEmail());
//                            prefHelper.setAuthToken(loginResponse.getUser().getUserToken());
//                            Intent intent = new Intent(PhoneVerifyActivity.this, MainActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finishAffinity();
//                        }
//
//                    } else {
//                        progressBar.setVisibility(View.GONE);
//                        Toast.makeText(PhoneVerifyActivity.this, loginResponse.getMsg(), Toast.LENGTH_SHORT).show();
//                    }
//                }else
//                {
//                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(PhoneVerifyActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<VerifyPhoneResponse> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "Login failed!", Toast.LENGTH_LONG).show();
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editTextCode.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneVerifyActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
//        verifyOTP(mobile);
//        signInWithPhoneAuthCredential(credential);
    }

//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(PhoneVerifyActivity.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//                        if (task.isSuccessful()) {
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            if (user != null && user.isEmailVerified()) {
//                                String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//                                getUserDetails(userID);
//                                //verification successful we will start the profile activity
//                                Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_LONG).show();
//                                Intent intent = new Intent(PhoneVerifyActivity.this, MainActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                                finishAffinity();
//                            }
//                        } else {
//
//                            //verification unsuccessful.. display an error message
//
//                            String message = "Somthing is wrong, we will fix it soon...";
//
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                message = "Invalid code entered...";
//                            }
//
//                            Toast.makeText(PhoneVerifyActivity.this, message, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private void getUserDetails(String userID) {
        Query query = myRef.child("users").child(userID);
        query.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                prefHelper.setuName(user.getUsername());
                prefHelper.setPhone(user.getPhone_number());
                prefHelper.setLogedIn("yes");
                prefHelper.setuId(userID);
                prefHelper.setuEmail(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
