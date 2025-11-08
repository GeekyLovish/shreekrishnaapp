package com.raman.kumar.shrikrishan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SupportActivity extends AppCompatActivity {

    RelativeLayout backRelativeLayout;
    LinearLayout callLinearlayout, emailLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        initView();
        listeneres();
    }

    private void listeneres() {

        backRelativeLayout.setOnClickListener(v -> onBackPressed());

        callLinearlayout.setOnClickListener(v -> {
            String appPhone = "9466660442";
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + appPhone));
            startActivity(intent);
        });

        emailLinearLayout.setOnClickListener(v -> {
            String appEmail = "ramankumar407@gmail.com";
            Intent intentMail = new Intent(Intent.ACTION_SEND);
            intentMail.setType("message/rfc822");
            intentMail.putExtra(Intent.EXTRA_EMAIL, new String[]{appEmail});
            try {
                startActivity(Intent.createChooser(intentMail, "Message to User to do what next"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(SupportActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        backRelativeLayout = findViewById(R.id.backRelativeLayout);
        callLinearlayout = findViewById(R.id.callLinearlayout);
        emailLinearLayout = findViewById(R.id.emailLinearLayout);
    }
}
