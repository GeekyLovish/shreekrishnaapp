package com.raman.kumar.shrikrishan.phoneAuth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.raman.kumar.modals.authentication.loginMobile.LoginMobileModel;
import com.raman.kumar.shrikrishan.MainActivity;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.loginfiles.LoginActivity;
import com.raman.kumar.shrikrishan.model.LoginResponse;
import com.raman.kumar.shrikrishan.model.PhoneLoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneAuthActivity extends AppCompatActivity {
    private Spinner spinner;
    private EditText editText;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        progress = new ProgressDialog(this);
        spinner = findViewById(R.id.spinnerCountries);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        editText = findViewById(R.id.userPhoneEdit);

        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];

                String number = editText.getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    editText.setError("Valid number is required");
                    editText.requestFocus();
                    return;
                }

                String phoneNumber = "+" + code + number;
                verify_phone(phoneNumber);
            }
        });
    }

    void verify_phone(String phoneNumber)
    {
//        Intent intent = new Intent(PhoneAuthActivity.this, VerifyPhoneActivity.class);
//        intent.putExtra("phonenumber", phoneNumber);
////        intent.putExtra("otp", loginResponse.getPhoneLogin().getOtp());
//        startActivity(intent);
//        showProgressDialog();
        Call<LoginMobileModel> call = RetrofitClient
                .getInstance()
                .getApi()
                .phoneLogin(phoneNumber);

        call.enqueue(new Callback<LoginMobileModel>() {
            @Override
            public void onResponse(Call<LoginMobileModel> call, Response<LoginMobileModel> response) {
                LoginMobileModel loginResponse = response.body();
                if (response.isSuccessful()) {
                    progress.dismiss();
                        if(loginResponse.getStatus()) {
//                        if (loginResponse.getMsg() == "Not Registered")
//                        {
//                            Toast.makeText(getApplicationContext(), "हमने चेक किया कि अपने अभी तक ऐप में रजिस्टर नहीं किया है। कृपा पहले रजिस्टर करें साइनअप फॉर्म भर कर के।", Toast.LENGTH_SHORT).show();
//                            Intent switchOnLogin = new Intent(PhoneAuthActivity.this, LoginActivity.class);
//                            switchOnLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(switchOnLogin);
//                        }
//                        else {
                            progress.dismiss();
                          String id = loginResponse.getData().getId().toString();

                          Intent intent = new Intent(PhoneAuthActivity.this, VerifyPhoneActivity.class);
                          intent.putExtra("phonenumber", phoneNumber);
                          intent.putExtra("ID", id);
                            //        intent.putExtra("otp", loginResponse.getPhoneLogin().getOtp());
                          startActivity(intent);

                    } else {
                        progress.dismiss();
                        Toast.makeText(PhoneAuthActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    progress.dismiss();

                    Toast.makeText(PhoneAuthActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginMobileModel> call, Throwable t) {
                Log.d("error", t.getMessage());
                Toast.makeText(getApplicationContext(), "Login failed! Try again", Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
        });
    }

        public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}