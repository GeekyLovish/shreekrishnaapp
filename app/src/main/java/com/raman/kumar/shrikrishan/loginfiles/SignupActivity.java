package com.raman.kumar.shrikrishan.loginfiles;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.raman.kumar.modals.authentication.AuthModal.RegisterModal;
import com.raman.kumar.shrikrishan.MainActivity;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.apiNetworking.SharedPrefManager;
import com.raman.kumar.shrikrishan.util.FirebaseMethods;
import com.raman.kumar.shrikrishan.util.PrefHelper;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    EditText userNameEdit, userEmailEdit, userPasswordEdit, confirmPasswordEdit, userPhoneEdit;
    ImageView backButton;
    ConstraintLayout signUpButton, nameLayout, emailLayout, passwordLayout, confirmLayout, phoneLayout;
    TextView SignIn;
    String block = "";
    String phonenumber = "";
    ProgressDialog progress;
    FirebaseAuth mAuth;
    String refreshedToken = null;
    String userID;
    public static final String TAG = "TAG";
    private PrefHelper prefHelper;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private FirebaseMethods firebaseMethods;
    Intent intent;
    SharedPrefManager sharedPrefManager;

    private ImageView passwordToggleIcon,confirmPasswordToggleIcon;
    private boolean isPasswordVisible = false; // Track current visibility state
    private boolean isConfirmPasswordVisible = false; // Track current visibility state


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        sharedPrefManager = new SharedPrefManager(getApplicationContext());
        intent = getIntent();
        if (intent.hasExtra("uId")) {
            userID = intent.getStringExtra("uId");
            phonenumber = intent.getStringExtra("phonenumber");
        }
        prefHelper = new PrefHelper(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        myRef = database.getReference();
        mStorageRef = mStorage.getReference();
        firebaseMethods = new FirebaseMethods(SignupActivity.this);

        registerFCM();
        FirebaseApp.initializeApp(this);
        initViews();
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
        });        System.out.println("device id is ==>>" + refreshedToken);
    }

    @Override
    protected void onResume() {
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    private void listeners() {
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        signUpButton.setOnClickListener(v -> {
            if (isNetworkConnected()) {
                if (isValidateLogin())
                {
                    signupService(userNameEdit.getText().toString().trim(), userEmailEdit.getText().toString().trim(),
                            userPasswordEdit.getText().toString().trim(), "+91"+userPhoneEdit.getText().toString().trim());
                }
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }

        });

        SignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();
        });


        passwordToggleIcon = findViewById(R.id.passwordToggleIcon);
        confirmPasswordToggleIcon = findViewById(R.id.confirmPasswordToggleIcon);

        passwordToggleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
        confirmPasswordToggleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleConfirmPasswordVisibility();
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
    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            // Hide password
            confirmPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmPasswordToggleIcon.setImageResource(R.drawable.ic_eye_closed); // Your closed eye icon
        } else {
            // Show password
            confirmPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            confirmPasswordToggleIcon.setImageResource(R.drawable.ic_eye_open); // Your open eye icon
        }

        // Move cursor to end of text
        confirmPasswordEdit.setSelection(confirmPasswordEdit.getText().length());
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
    }

    private void signupService(String username, String email, String password, String phonenum) {
        showProgressDialog();
        Call<RegisterModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .signUp(username, email, password, phonenum, refreshedToken);

        try {
            call.enqueue(new Callback<RegisterModal>() {
                @Override
                public void onResponse(Call<RegisterModal> call, Response<RegisterModal> response) {
                    RegisterModal signupResponse = response.body();
                    if (response.isSuccessful() && signupResponse != null) {
                        progress.dismiss();
                        // PrefHelper and other logic
                        Toast.makeText(SignupActivity.this, signupResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        getOnBackPressedDispatcher().onBackPressed();
                    } else {
                        progress.dismiss();

                        try {
                            if (response.errorBody() != null) {
                                String errorBodyStr = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(errorBodyStr);
                                String message = jsonObject.optString("message", "Unknown error occurred.");
                                Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
                                Log.d("API_ERROR", message);
                            } else {
                                Toast.makeText(SignupActivity.this, "Failed to sign up, please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(SignupActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            Log.e("API_ERROR", "Parsing error: " + e.getMessage());
                        }
                    }
                }
                @Override
                public void onFailure(Call<RegisterModal> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), " Sign up failed !", Toast.LENGTH_LONG).show();
                    progress.dismiss();
                }
            });
        } catch (Exception e) {
            System.out.println("fhsjdfhajfhash" +e.getMessage());
            throw new RuntimeException(e);
        }



//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
//
//                        task.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                            @Override
//                            public void onSuccess(AuthResult authResult) {
//                                userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//                                Query query = myRef.child("users").orderByChild("user_id").equalTo(userID);
//                                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        progress.dismiss();
//                                        User user = null;
//                                        if (!dataSnapshot.exists()) {
//                                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<String> task) {
//                                                    if (!task.isSuccessful()) {
//                                                        refreshedToken = task.getException().getMessage();
//                                                        Log.w("FCM TOKEN Failed", task.getException());
//                                                    } else {
//                                                        refreshedToken = task.getResult();
//                                                        Log.e("FCM TOKEN", refreshedToken);
//                                                    }
//                                                }
//                                            });
//                                            user = new User("0", userID, phonenumber, "", name, refreshedToken);
//                                            prefHelper.setuId(userID);
//                                            prefHelper.setPhone(phonenumber);
//                                            prefHelper.setuEmail("");
//                                            prefHelper.setuName(userNameEdit.getText().toString().trim());
//                                            myRef.child("users").child(userID).child("device_token").setValue(refreshedToken);
//                                            addUser(user);
//                                        } else {
//                                            progress.dismiss();
//                                            String name = userNameEdit.getText().toString() + ".";
//                                            name += Objects.requireNonNull(myRef.push().getKey()).substring(3, 10);
//                                            addUser(user);
////                    getUserDetails(userID);
//                                            Toast.makeText(getApplicationContext(), "Username already exists!!", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                progress.dismiss();
//                                Toast.makeText(getApplicationContext(), "Authentication Failed",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
    }

//    private void addUser(User user) {
//        myRef.child("users")
//                .child(userID)
//                .setValue(user).addOnCompleteListener(task -> {
//            if (!Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()) {
//                sendVerificationEmail(true);
//            }
////            getUserDetails(userID);
//            Log.e(TAG, "new user added");
//        });
//    }


//    private void getUserDetails(String userID) {
//        Query query = myRef.child("users").child(userID);
//        query.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                assert user != null;
//                block = user.getBlock();
//                if (block.equals("0")) {
//                    prefHelper.setuName(user.getUsername());
//                    prefHelper.setPhone(user.getPhone_number());
//                    prefHelper.setLogedIn("yes");
//                    prefHelper.setuId(userID);
//                    prefHelper.setuEmail(user.getEmail());
//                    Toast.makeText(getApplicationContext(), "Login Successfully", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finishAffinity();
//                } else {
//                    Toast.makeText(SignupActivity.this, "You are blocked by the admin shortly. If you want to access then contact with admin.", Toast.LENGTH_SHORT).show();
//                }
//                progress.dismiss();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }

//    private void sendVerificationEmail(final Boolean isRequired) {
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            user.sendEmailVerification().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Log.e("CodeStatus", "Verification code sent");
//                    Toast.makeText(getApplicationContext(), "हमने आपके भेजे गए पंजीकृत ईमेल पर सत्यापन लिंक भेजा है, कृपया अपना ईमेल इनबॉक्स देखें और सत्यापित करने के लिए लिंक पर क्लिक करें", Toast.LENGTH_LONG).show();
//                    progress.dismiss();
//                    if (isRequired) {
//                        mAuth.signOut();
//                        finish();
//                    }
//                } else {
//                    progress.dismiss();
//                    Toast.makeText(getApplicationContext(), "Couldn't send verification email!!", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }

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

        if (userNameEdit.getText().toString().isEmpty()) {
            nameLayout.setBackgroundResource((R.drawable.rejected_edit_back));
            isValid = false;
        }

        if (userPhoneEdit.getText().toString().isEmpty()) {
            phoneLayout.setBackgroundResource((R.drawable.rejected_edit_back));
            isValid = false;
        }

        if (confirmPasswordEdit.getText().toString()
                .isEmpty() || confirmPasswordEdit.getText().length() < 6 || confirmPasswordEdit.getText().length() > 20
        ) {
            passwordLayout.setBackgroundResource(R.drawable.rejected_edit_back);
            isValid = false;
        }


        if (userPasswordEdit.getText().toString()
                .isEmpty() || userPasswordEdit.getText().length() < 6 || userPasswordEdit.getText().length() > 20
        ) {
            passwordLayout.setBackgroundResource(R.drawable.rejected_edit_back);
            isValid = false;
        }

        return isValid;
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
