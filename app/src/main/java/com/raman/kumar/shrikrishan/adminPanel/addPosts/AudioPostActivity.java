package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.audio.PostAudioModal;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioPostActivity extends AppCompatActivity {

    Button ButtonAudio, ButtonChooseAudio;
    Uri audioPathUri;
    String ringtone = "", bhajan = "", audioDuration = "", audioType = "";
    int audio_Request_Code = 8;
    RadioButton ringtoneCheckBox, bhajanCheckBox;
    ProgressDialog progressDialog;
    EditText titleEditText, durationEditText;
    String song_id = "", song_path = "", created_at = "", display_name = "", song_duration = "", title = "", mPosition = "";
    DatabaseReference databaseReference;
    ArrayList<HashMap<String, String>> songsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_post);
        progressDialog = new ProgressDialog(AudioPostActivity.this);
        progressDialog.setCancelable(false);
        songsList = new ArrayList<>();
        initView();
        listeners();
    }

    private void listeners() {
        ButtonChooseAudio.setOnClickListener(v -> {
            // Create an intent to pick audio files
            Intent intent = new Intent();
            intent.setType("audio/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Please Select Audio"), audio_Request_Code);
        });

        ButtonAudio.setOnClickListener(v -> {
            String songTitle = titleEditText.getText().toString();
            if (song_id.isEmpty()) {
                uploadAudioFile(songTitle);
            } else {
                updateAudioFile(songTitle);
            }
        });

        ringtoneCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) audioType = "ringtone";
        });

        bhajanCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) audioType = "bhajan";
        });
    }

    private void updateAudioFile(String songTitle) {
        if (audioPathUri != null) {
            progressDialog.setTitle(audioType + " is Updating...");
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.show();

            MultipartBody.Part file = packFile(this, "audio", audioPathUri);
            String fileName = audioPathUri.getLastPathSegment();
            RequestBody songId = RequestBody.create(MediaType.parse("multipart/form-data"), song_id);
            RequestBody accept = RequestBody.create(MediaType.parse("multipart/form-data"), "application/json");
            RequestBody bearerToken = RequestBody.create(MediaType.parse("multipart/form-data"), Extensions.getBearerToken());
            RequestBody displayName = RequestBody.create(MediaType.parse("multipart/form-data"), fileName);
            RequestBody titleReq = RequestBody.create(MediaType.parse("multipart/form-data"), songTitle);
            RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), audioType);
            RequestBody pos = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(0)); // Default position
            RequestBody durationReq = RequestBody.create(MediaType.parse("multipart/form-data"), audioDuration);
            RequestBody methodReq = RequestBody.create(MediaType.parse("multipart/form-data"), "PUT");



            Call<PostAudioModal> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .updateAudio("application/json", Extensions.getBearerToken(),song_id, titleReq, displayName, type, pos, durationReq, file,methodReq);

            call.enqueue(new Callback<PostAudioModal>() {
                @Override
                public void onResponse(Call<PostAudioModal> call, Response<PostAudioModal> response) {
                    if (response.isSuccessful() && response.body().getStatus()) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Audio Updated Successfully", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(AudioPostActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PostAudioModal> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to update! " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(AudioPostActivity.this, "Please select an audio file to update", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadAudioFile(String songTitle) {
        if (audioPathUri != null) {
            progressDialog.setTitle(audioType + " is Uploading...");
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.show();

            MultipartBody.Part file = packFile(this, "audio", audioPathUri);
            String fileName = audioPathUri.getLastPathSegment();
            RequestBody accept = RequestBody.create(MediaType.parse("multipart/form-data"), "application/json");
            RequestBody bearerToken = RequestBody.create(MediaType.parse("multipart/form-data"), Extensions.getBearerToken());
            RequestBody displayName = RequestBody.create(MediaType.parse("multipart/form-data"), fileName);
            RequestBody titleReq = RequestBody.create(MediaType.parse("multipart/form-data"), songTitle);
            RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), audioType);
            RequestBody pos = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(0)); // Default position
            RequestBody durationReq = RequestBody.create(MediaType.parse("multipart/form-data"), audioDuration);

            Call<PostAudioModal> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .postAudio("application/json", Extensions.getBearerToken(), titleReq, displayName, type, pos, durationReq, file);

            call.enqueue(new Callback<PostAudioModal>() {
                @Override
                public void onResponse(Call<PostAudioModal> call, Response<PostAudioModal> response) {
                    if (response.isSuccessful() && response.body().getStatus()) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Audio Uploaded Successfully", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(AudioPostActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PostAudioModal> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to upload audio! " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } else {
            Toast.makeText(AudioPostActivity.this, "Please Select Audio", Toast.LENGTH_LONG).show();
        }
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            song_id = intent.getStringExtra("id");
            song_path = intent.getStringExtra("song_path");
            audioType = intent.getStringExtra("audio_type");
            created_at = intent.getStringExtra("created_at");
            display_name = intent.getStringExtra("display_name");
            song_duration = intent.getStringExtra("song_duration");
            title = intent.getStringExtra("title");
            mPosition = intent.getStringExtra("position");
        }

        ButtonAudio = findViewById(R.id.ButtonAudio);
        titleEditText = findViewById(R.id.titleEditText);
        ButtonChooseAudio = findViewById(R.id.ButtonChooseAudio);
        ringtoneCheckBox = findViewById(R.id.ringtoneCheckBox);
        bhajanCheckBox = findViewById(R.id.bhajanCheckBox);
        durationEditText = findViewById(R.id.durationEditText);

        if (!song_id.isEmpty()) {
            titleEditText.setText(title);
            durationEditText.setText(song_duration);
            if (audioType.equals("ringtone")) {
                ringtoneCheckBox.setChecked(true);
            } else {
                bhajanCheckBox.setChecked(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == audio_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {
            audioPathUri = data.getData();
            ButtonChooseAudio.setText("Audio Selected");

            // Get the audio file name using ContentResolver
            String audioFileName = getFileName(audioPathUri);

            // Extract the audio file duration
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(getApplicationContext(), audioPathUri);

            // Extract the duration in milliseconds and convert it to seconds
            String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long dur = Long.parseLong(duration);
            audioDuration = timeConversion((int) (dur / 1000)); // convert milliseconds to seconds

            // Set the audio file name and duration in the EditText fields
            titleEditText.setText(audioFileName); // Set the audio name
            durationEditText.setText(audioDuration); // Set the duration

            try {
                metaRetriever.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to extract the file name from the URI
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columnIndex != -1) {
                    result = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        }
        return result != null ? result : uri.getLastPathSegment(); // Fall back to last path segment if name is not found
    }


    @Nullable
    public static MultipartBody.Part packFile(@NonNull Context context, @NonNull String partName, @Nullable Uri fileUri) {
        if (fileUri == null) return null;
        ContentResolver cr = context.getContentResolver();
        String tp = cr.getType(fileUri);
        if (tp == null) {
            tp = "audio/*";
        }
        try {
            InputStream iStream = context.getContentResolver().openInputStream(fileUri);
            byte[] inputData = getBytes(iStream);
            RequestBody requestFile = RequestBody.create(MediaType.parse(tp), inputData);
            return MultipartBody.Part.createFormData(partName, fileUri.getLastPathSegment(), requestFile);
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] getBytes(InputStream iStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = iStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private static String timeConversion(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
