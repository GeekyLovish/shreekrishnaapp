package com.raman.kumar.shrikrishan;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class FullScreenTextActivity extends AppCompatActivity {
    AdView adView;
    Spanned spannedContent;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_text);
        setTitle("Arati");
        TextView txtview_content = (TextView) findViewById(R.id.content);
        adView = findViewById(R.id.adView);
        TextView title = (TextView) findViewById(R.id.title);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        String content = getIntent().getStringExtra("content");
        String titletext = getIntent().getStringExtra("title");

        title.setText(titletext);
        assert content != null;
        content = content.replace("\\u003C", "<").replace("\\u003E", ">");
        if (content.startsWith("\"") && content.endsWith("\"")) {
            content = content.substring(1, content.length() - 1);
        }
        Spanned spannedContent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spannedContent = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY);
        } else {
            spannedContent = Html.fromHtml(content);
        }

        txtview_content.setText(spannedContent);

    }

}
