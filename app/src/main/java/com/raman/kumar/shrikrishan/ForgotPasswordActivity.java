package com.raman.kumar.shrikrishan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.loginfiles.LoginActivity;
import com.raman.kumar.shrikrishan.model.ForgetPassResponse;
import com.raman.kumar.shrikrishan.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button submitButton;
    private RelativeLayout backRelativeLayout;
    FirebaseAuth auth;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        progress = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        initViews();
        listeners();
    }

    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    private void listeners() {
        backRelativeLayout.setOnClickListener(v -> onBackPressed());

        submitButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
            } else {
                showProgressDialog();
                forgotPasswordService(email);
            }
        });
    }

    private void forgotPasswordService(String email) {
        Call<ForgetPassResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .forget_pass(email);

        call.enqueue(new Callback<ForgetPassResponse>() {
            @Override
            public void onResponse(Call<ForgetPassResponse> call, Response<ForgetPassResponse> response) {
                ForgetPassResponse forgetPassResponse = response.body();
                if (response.isSuccessful()) {
                    if (forgetPassResponse.getSuccess()) {
                        progress.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "कृपया अपनी ई-मेल आईडी जांचें। हमने आपको आपका पासवर्ड रीसेट करने के लिए आपके पंजीकृत ईमेल पर निर्देश भेजे हैं!", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        progress.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, forgetPassResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ForgetPassResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to send reset email!", Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
        });
//        auth.sendPasswordResetEmail(email)
//                .addOnCompleteListener(task -> {
//                    progress.dismiss();
//                    if (task.isSuccessful()) {
//                        Toast.makeText(ForgotPasswordActivity.this, "कृपया अपनी ई-मेल आईडी जांचें। हमने आपको आपका पासवर्ड रीसेट करने के लिए आपके पंजीकृत ईमेल पर निर्देश भेजे हैं!", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        submitButton = findViewById(R.id.submitButton);
        backRelativeLayout = findViewById(R.id.backRelativeLayout);
    }
}
