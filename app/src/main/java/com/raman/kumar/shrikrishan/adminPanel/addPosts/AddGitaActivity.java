package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import static com.raman.kumar.customClasses.Extensions.removeUTFCharacters;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.customClasses.RichTextEditorView;
import com.raman.kumar.modals.getaModal.createGeta.CreateGetaModal;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;

import net.dankito.richtexteditor.android.RichTextEditor;
import net.dankito.richtexteditor.android.toolbar.AllCommandsEditorToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGitaActivity extends AppCompatActivity {

    //    private RichTextEditor contentEditText;
    private EditText titleEditText;
    //    private AllCommandsEditorToolbar editorToolbar;
//    RichTextEditorView richTextEditorView;
    private Button uploadButton;
    private ProgressDialog progressDialog;
    private WebView webView;

    private String title = "", content = "", position = "", id = "", from = "";
    Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gita);

        // Initialize views
        initViews();

        // Extract intent data
        handleIntent();

        // Set up editor
        setupEditor();

        // Set button click listener
        uploadButton.setOnClickListener(v -> handleUploadAction());
    }

    private void initViews() {
//        richTextEditorView = findViewById(R.id.richTextEditorView);
//        editorToolbar = findViewById(R.id.editorToolbar);
//        contentEditText = findViewById(R.id.contentEditText);
        uploadButton = findViewById(R.id.uploadButton);
        titleEditText = findViewById(R.id.titleEditText);
        progressDialog = new ProgressDialog(this);

        // Initialize WebView
        webView = findViewById(R.id.webViewEditor);

        // Configure WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript
        webView.setWebChromeClient(new WebChromeClient());

        // Load the Quill Editor HTML
        webView.loadUrl("file:///android_asset/quill_editor.html");

//        webView.evaluateJavascript("setEditorContent('" + content +"');",null);


    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra("title");
            id = intent.getStringExtra("id");
            content = intent.getStringExtra("content");
            position = intent.getStringExtra("position");
            from = intent.getStringExtra("from");
            System.out.println("sfajsfllv " +content);
        }

        if (title != null && !title.isEmpty()) {
            titleEditText.setText(title);
//            richTextEditorView.getHtmlContent();
//            contentEditText.setHtml(content);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Code to be executed after 1 second
                webView.evaluateJavascript("setEditorContent('" + content +"');",null);

            }, 1000); // Delay in milliseconds (1000ms = 1 second)

            System.out.println("sfajsfllvCON " +content);
            uploadButton.setText("Update");
        }
    }

    private void setupEditor() {
//        editorToolbar.setEditor(contentEditText);
//        contentEditText.setPadding(10, 10, 10, 10);
//        contentEditText.addHtmlChangedListener(html -> {
//            Log.d("RichTextEditor", "Updated HTML: " + html);
//            return null;
//        });
    }

    private void handleUploadAction() {
        showProgressDialog();

        if ("gita".equals(from)) {
            if (!title.isEmpty()) {
                updateGita();
            } else {
                uploadGita();
            }
        } else {
            if (!title.isEmpty()) {
                updateArti();
            } else {
                uploadArti();
            }
        }
    }

    private void uploadArti() {
        String title = titleEditText.getText().toString().trim();
        webView.evaluateJavascript("getEditorContent();", content -> {
            Log.d("EditorContent", "Content: " + content);
            String modifiedContent = replaceDoubleQuotes(content);
            if (validateInputs(title, content)) {
                String updatedContent = removeUTFCharacters(modifiedContent);
                Call<CreateGetaModal> call = RetrofitClient.getInstance()
                        .getApi()
                        .postAarti("application/json", Extensions.getBearerToken(), title, updatedContent);

                handleApiCall(call, "Uploaded Successfully");
            }
        });

    }

    private void updateArti() {
        String title = titleEditText.getText().toString().trim();
        webView.evaluateJavascript("getEditorContent();", content -> {
            Log.d("EditorContent", "Content: " + content);
            String modifiedContent = replaceDoubleQuotes(content);
            if (validateInputs(title, content)) {

                String updatedContent = removeUTFCharacters(modifiedContent);
                Call<CreateGetaModal> call = RetrofitClient.getInstance()
                        .getApi()
                        .updateAarti("application/json", Extensions.getBearerToken(), id, title, updatedContent);

                handleApiCall(call, "Updated Successfully");
            }
        });

    }

    private void uploadGita() {
        String title = titleEditText.getText().toString().trim();
        webView.evaluateJavascript("getEditorContent();", content -> {

            String modifiedContent = replaceDoubleQuotes(content);
            if (validateInputs(title, modifiedContent)) {

                String updatedContent = removeUTFCharacters(modifiedContent);

                Call<CreateGetaModal> call = RetrofitClient.getInstance()
                        .getApi()
                        .postGeeta("application/json", Extensions.getBearerToken(), title, updatedContent);

                handleApiCall(call, "Uploaded Successfully");
            }
        });

    }


    public static String replaceDoubleQuotes(String input) {
        // Replace all occurrences of " with space
        return input.replace("\"", " ");
    }
    private void updateGita() {
        String title = titleEditText.getText().toString().trim();
        webView.evaluateJavascript("getEditorContent();", content -> {
            String modifiedContent = replaceDoubleQuotes(content);
            if (validateInputs(title, modifiedContent)) {
                String updatedContent = removeUTFCharacters(modifiedContent);
                Call<CreateGetaModal> call = RetrofitClient.getInstance()
                        .getApi()
                        .updateGeeta("application/json", Extensions.getBearerToken(), id, title, updatedContent);
                handleApiCall(call, "Updated Successfully");
            }
        });

    }

    private void handleApiCall(Call<CreateGetaModal> call, String successMessage) {
        call.enqueue(new Callback<CreateGetaModal>() {
            @Override
            public void onResponse(Call<CreateGetaModal> call, Response<CreateGetaModal> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus()) {
                        Toast.makeText(AddGitaActivity.this, successMessage, Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(AddGitaActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddGitaActivity.this, "Unexpected server response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateGetaModal> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("API_ERROR", "Error: " + t.getMessage(), t);
                Toast.makeText(AddGitaActivity.this, "Failed to connect to the server!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInputs(String title, String content) {

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (content == null) {
            Toast.makeText(this, "Content is null", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter content", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        return true;

    }

    private void showProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}