package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.raman.kumar.shrikrishan.CommentFiles.AdminPanel;
import com.raman.kumar.shrikrishan.Constants;
import com.raman.kumar.shrikrishan.MainActivity;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.loginfiles.LoginActivity;
import com.raman.kumar.shrikrishan.model.CodeResponse;
import com.raman.kumar.shrikrishan.model.LoginResponse;
import com.raman.kumar.shrikrishan.util.PrefHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordActivity extends AppCompatActivity {

    EditText codeEditText;
    Button submitButton;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private String code;
    PrefHelper prefHelper;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        prefHelper = new PrefHelper(getApplicationContext());
        codeEditText = findViewById(R.id.codeEditText);
        submitButton = findViewById(R.id.submitButton);
        progress = new ProgressDialog(this);
        getCode();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (codeEditText.getText().toString().isEmpty()) {
                    codeEditText.setError("Enter Code");
                } else if (codeEditText.getText().toString().length() != 4) {
                    codeEditText.setError("Code Should be alphanumeric");
                } else if (!codeEditText.getText().toString().equals(code)) {
                    codeEditText.setError("Invalid Code");
                } else {
                    if (prefHelper.getuId().equals(Constants.ADMIN_USER_ID) || prefHelper.getuId().equals(Constants.VICE_ADMIN_USER_ID)) {
                        Intent intent = new Intent(PasswordActivity.this, AdminPanel.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(PasswordActivity.this, "You cannot access this section.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    private void getCode() {
        showProgressDialog();
        Call<CodeResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .getCode();

        call.enqueue(new Callback<CodeResponse>() {
            @Override
            public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                CodeResponse codeResponse = response.body();
                if (response.isSuccessful()) {
                    if (codeResponse.getSuccess()) {
                        code = String.valueOf(codeResponse.getCodeData().getCode());
//                        Toast.makeText(PasswordActivity.this, "Code:"+code, Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    } else {
                        Toast.makeText(PasswordActivity.this, codeResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                }
                else
                {
                    Toast.makeText(PasswordActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<CodeResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to get code!", Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
        });

//        Query query = myRef.child("Code");
//        query.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
//                CodeData code1 = dataSnapshot.getValue(CodeData.class);
//                assert code1 != null;
//                code = code1.getCode();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
    }
}
