package com.raman.kumar.shrikrishan;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.raman.kumar.AudiosModal.AudiosModal;
import com.raman.kumar.AudiosModal.Datum;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.media.MediaScannerConnection;


public class AudioFragment extends Fragment implements AudioAdapter.OnItemClickListener{

    private Context mContext;
    private RecyclerView recyclerView;
    private AudioAdapter mAdapter;
    private List<Datum> audioList1 = new ArrayList<>();
    private List<Datum> audioList = new ArrayList<>();
    private LinearLayout uploadAudio;
    private MediaPlayer mPlayer = null;
    final private ArrayList<Integer> resID = new ArrayList<>();
    private boolean isPlaying = false;
    private DownloadManager downloadManager;
    private long downloadId = -1;
    private ImageButton play_rec;
    private int currentPage = 1;
    private final int perPageLimit = 30; // Adjust the number of items per page as needed
    private boolean isLoading = false;

    private static final int REQUEST_CODE_WRITE_SETTINGS = 1001;
    private static final int REQUEST_CODE_DOCUMENT_TREE = 1002;

    // Assuming AudioAdapter is a class and CallAudioField is a nested class.
    AudioAdapter.CallAudioField callAudioField = new AudioAdapter.CallAudioField() {
        @Override
        public void play(int position) {

        }

        @Override
        public void pause() {
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            }

        }

        @Override
        public void showPlayDialog(int position) {

            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.audio_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            Button okButton = dialog.findViewById(R.id.okButton);
            Button cancelButton = dialog.findViewById(R.id.cancelButton);
            final TextView bufferingText = dialog.findViewById(R.id.bufferingText);
            ImageButton stop_rec = dialog.findViewById(R.id.stop_audio);
            play_rec = dialog.findViewById(R.id.play_rec);

            play_rec.setOnClickListener(v -> {
                if (isNetworkConnected()) {
                    try {
                        if (isPlaying) {
                            stopAudio();
                        } else {
                            startAudio(position);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
//                Toast.makeText(getActivity(), "Internet connection not available", Toast.LENGTH_SHORT).show();
                }
            });

            stop_rec.setOnClickListener(v -> stopAudio());

            dialog.show();

            okButton.setOnClickListener(v -> dialog.dismiss());
            cancelButton.setOnClickListener(v -> {
                dialog.dismiss();
                stopAudio();
            });
        }

        @Override
        public ProgressDialog showProgress() {
            return null;
        }

        @Override
        public void dismissProgressDialog(ProgressDialog progressDialog) {

        }
    }; // Example instantiation


    private ActivityResultLauncher<String> notificationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
//                    Toast.makeText(requireContext(), "Notification permission granted.", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(requireContext(), "Notification permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
    );
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.activity_audio2, container, false);
//
//        recyclerView = v.findViewById(R.id.recycler_view);
//        uploadAudio = v.findViewById(R.id.uploadAudio);
//        downloadManager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
//
//        checkAndRequestNotificationPermission();
//        requestPermissionsIfNeeded();
//        if(!checkWriteSettingsPermission()){
//            requestWriteSettingsPermission();
//        }
//
//        // Initialize Adapter with ClickListener
//        mAdapter = new AudioAdapter(audioList, getActivity(), getActivity().getSupportFragmentManager().beginTransaction(), resID, mPlayer, new AudioAdapter.ClickListener() {
//            @Override
//            public void onPositionClicked(int position) {
//                // Handle item click (play audio)
//                Toast.makeText(getActivity(), "Playing " + position, Toast.LENGTH_SHORT).show();
////                showPlayDialog(position);
//
//                callAudioField.showPlayDialog(position);
//            }
//
//            @Override
//            public void onLongClicked(int position) {}
//        }, this);
//
//        if (isNetworkConnected()) {
//            getAllRingtones();
//        }else {
////            Toast.makeText(getActivity(), "Internet connection not available", Toast.LENGTH_SHORT).show();
//        }
//
//        uploadAudio.setOnClickListener(v1 -> {
//            // Show custom dialog logic (if needed)
//        });
//
//
//
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                if (layoutManager != null && !isLoading) {
//                    int visibleItemCount = layoutManager.getChildCount();
//                    int totalItemCount = layoutManager.getItemCount();
//                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//
//                    // Check if the user has reached the bottom
//                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                            && firstVisibleItemPosition >= 0) {
//                        currentPage++; // Increment the page number
//                        getAllRingtones(); // Load more ringtones
//                    }
//                }
//            }
//        });
//
//
//
//
//        return v;
//
//
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_audio2, container, false);

        recyclerView = v.findViewById(R.id.recycler_view);
        uploadAudio = v.findViewById(R.id.uploadAudio);
        downloadManager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);

        checkAndRequestNotificationPermission();
        requestPermissionsIfNeeded();
        if (!checkWriteSettingsPermission()) {
            requestWriteSettingsPermission();
        }

        // Initialize Adapter with ClickListener
        mAdapter = new AudioAdapter(audioList, getActivity(), getActivity().getSupportFragmentManager().beginTransaction(), resID, mPlayer, new AudioAdapter.ClickListener() {
            @Override
            public void onPositionClicked(int position) {
                Toast.makeText(getActivity(), "Playing " + position, Toast.LENGTH_SHORT).show();
                callAudioField.showPlayDialog(position);
            }

            @Override
            public void onLongClicked(int position) {}
        }, this);

        recyclerView.setAdapter(mAdapter); // Ensure the adapter is set

        // ✅ If permission is already granted, call allowedPermission here
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            mAdapter.allowedPermission();
        }

        if (isNetworkConnected()) {
            getAllRingtones();
        }

        uploadAudio.setOnClickListener(v1 -> {
            // Show custom dialog logic (if needed)
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        currentPage++;
                        getAllRingtones();
                    }
                }
            }
        });

        return v;
    }
    private void getAllRingtones() {
        if (isLoading) return; // Prevent multiple simultaneous requests

        isLoading = true;



        // API call with page and limit parameters
        Call<AudiosModal> call = RetrofitClient
                .getInstance()
                .getApi()
                .getAllAudios("application/json", "ringtone", currentPage, perPageLimit); // Pass page and limit

        call.enqueue(new Callback<AudiosModal>() {
            @Override
            public void onResponse(Call<AudiosModal> call, Response<AudiosModal> response) {
                isLoading = false;


                if (response.isSuccessful() && response.body() != null) {
                    AudiosModal getAllAudioResponse = response.body();

                    if (getAllAudioResponse.getData() != null) {
                        if (currentPage == 1) {
                            audioList.clear(); // Clear the list for the first page
                        }

                        audioList1.addAll(getAllAudioResponse.getData());
                        for (Datum datum : audioList1) {
                            if ("ringtone".equals(datum.getType())) {
                                audioList.add(datum);
                            }
                        }

                        if (!audioList.isEmpty()) {
                            mAdapter.setOnCallAudioFieldListener(callAudioField);
                            recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL));
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            Log.e("Audio Filter", "No ringtones found in audio list.");
                        }
                    } else {
                        Log.e("API Response", "getData() returned null.");
                    }
                } else {
                    Log.e("API Response", "Response unsuccessful or body is null.");
                }
            }

            @Override
            public void onFailure(Call<AudiosModal> call, Throwable t) {
                isLoading = false;

                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                Log.e("API Error", "Failed to fetch audios", t);
            }
        });
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void startAudio(int position) {
        // Start playing the audio
        String audioPath = audioList.get(position).getPath();  // The URL or path of the audio
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(audioPath);
            mPlayer.prepare();
            mPlayer.start();
            isPlaying = true;
            play_rec.setImageDrawable(getResources().getDrawable(R.drawable.pause_icon));  // Change to pause icon
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopAudio() {
        // Stop playing the audio
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
            isPlaying = false;
            play_rec.setImageDrawable(getResources().getDrawable(R.drawable.play_icon));  // Change to play icon
        }
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void requestPermissionsIfNeeded() {
        if (checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }



    private void startDownload(String url) {
        // Generate a unique filename using the song name or any identifier from the URL
        String fileName = "custom_ringtone_" + url.hashCode() + ".mp3";
        File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), fileName);

        if (filePath.exists()) {
            // File already exists, set it as the ringtone directly
            Uri uri = Uri.fromFile(filePath);
            setRingtone(uri);
            showToast("File already available. Setting as ringtone.");
        } else {
            // Proceed with download if file doesn't exist
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                    .setTitle("Downloading song")
                    .setDescription("Downloading your selected song")
                    .setDestinationUri(Uri.fromFile(filePath));

            downloadId = downloadManager.enqueue(request);
            showToast("Setting ringtone.");

            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            requireActivity().registerReceiver(downloadReceiver, filter, requireActivity().RECEIVER_EXPORTED);
        }
    }

    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadCompleteId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Log.e("Ringtone"+downloadCompleteId, "Download Complete");
            if (downloadId == downloadCompleteId) {
                showToast("Setting ringtone in progress.");
                Uri uri = getDownloadedFileUri();
                Log.e("Ringtone", String.valueOf(uri));
                if (uri != null) {
                    setRingtone(uri);
                } else {
                    Toast.makeText(requireContext(), "Failed to get set ringtone file URI.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    private Uri getDownloadedFileUri() {
        File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES), "custom_ringtone_" + downloadId + ".mp3");
        Log.e("Ringtone", "File Path: " + filePath.getAbsolutePath());
        if (filePath.exists()) {
            return Uri.fromFile(filePath);
        }

        android.database.Cursor cursor = null;
        try {
            cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
            Log.e("Ringtone", "Cursor: " + cursor.toString());
            if (cursor != null && cursor.moveToFirst()) {
                int fileUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                String uriString = cursor.getString(fileUriIndex);
                Log.e("Ringtone", "URI String: " + uriString);
                if (uriString != null) {
                    return Uri.parse(uriString);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }



    // Set ringtone function
    private void setRingtone(Uri uri) {
        // Check if WRITE_SETTINGS permission is granted
        if (checkWriteSettingsPermission()) {
            // Permission granted, proceed with setting ringtone
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                applyRingtoneModern(uri);
            } else {
                applyRingtoneLegacy(uri);
            }
        } else {
            // Request permission if not granted
            requestWriteSettingsPermission();
        }
    }


    private void requestWriteSettingsPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
        startActivity(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void applyRingtoneModern(Uri sourceUri) {
        try {
            ContentResolver resolver = requireContext().getContentResolver();
            String fileName = getFileName(sourceUri);
            if (fileName == null) {
                fileName = "custom_ringtone.mp3";
            }

            // Create the file in Ringtones directory
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_RINGTONES);

            Uri newUri = resolver.insert(MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY), values);

            if (newUri == null) {
                Toast.makeText(requireContext(), "Failed to create ringtone file",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Copy the audio file
            try (InputStream in = resolver.openInputStream(sourceUri);
                 OutputStream out = resolver.openOutputStream(newUri)) {
                if (in != null && out != null) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                }
            }

            // Set as default ringtone
            RingtoneManager.setActualDefaultRingtoneUri(
                    requireContext(),
                    RingtoneManager.TYPE_RINGTONE,
                    newUri
            );

            Toast.makeText(requireContext(), "Ringtone set successfully!",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to set ringtone: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            Log.e("Ringtone", "Error", e);
        }
    }

    private void applyRingtoneLegacy(Uri sourceUri) {
        try {
            ContentResolver resolver = requireContext().getContentResolver();
            String fileName = getFileName(sourceUri);
            if (fileName == null) {
                fileName = "custom_ringtone.mp3";
            }

            File ringtoneFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES), fileName);
            try (InputStream in = resolver.openInputStream(sourceUri);
                 OutputStream out = Files.newOutputStream(ringtoneFile.toPath())) {
                if (in != null && out != null) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                }
            }

            // Scan the file so it appears in the system media store
            MediaScannerConnection.scanFile(requireContext(),
                    new String[]{ringtoneFile.getAbsolutePath()},
                    new String[]{"audio/mp3"},
                    null);

            // Set as default ringtone
            Uri ringtoneUri = Uri.fromFile(ringtoneFile);
            RingtoneManager.setActualDefaultRingtoneUri(
                    requireContext(),
                    RingtoneManager.TYPE_RINGTONE,
                    ringtoneUri
            );

            Toast.makeText(requireContext(), "Ringtone set successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Ringtone", "Error setting ringtone", e);
        }
    }

    // Ask user to select a directory to store the ringtone (For Android 10+)
    private void requestDirectoryAccess(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CODE_DOCUMENT_TREE);
    }

    // Copy file and apply ringtone
    private void applyRingtone(Uri uri) {
        try {
            ContentResolver resolver = requireContext().getContentResolver();

            // 1️⃣ Get the File Name
            String fileName = getFileName(uri);
            if (fileName == null) {
                fileName = "custom_ringtone.mp3"; // Default name
            }

            // 2️⃣ Define the Target Location in MediaStore (DO NOT USE RELATIVE_PATH)
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);

            // Insert into MediaStore and Get New URI
            Uri newUri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(requireContext(), "Failed to create file", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3️⃣ Copy File from Original URI to New URI
            try (InputStream inputStream = resolver.openInputStream(uri);
                 OutputStream outputStream = resolver.openOutputStream(newUri)) {

                if (inputStream != null && outputStream != null) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.flush();

                    // 4️⃣ Set the Ringtone
                    RingtoneManager.setActualDefaultRingtoneUri(requireContext(),
                            RingtoneManager.TYPE_RINGTONE, newUri);

                    Toast.makeText(requireContext(), "Ringtone set successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("RingtoneError", "Error setting ringtone", e);
        }
    }

    // Utility function to get the file name
    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    Log.e("Ringtone", "File Name: " + result);
                }
            } catch (Exception e) {
                Log.e("Ringtone", "Error getting file name", e);
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }



    private boolean checkWriteSettingsPermission() {
        return Settings.System.canWrite(requireContext());
    }

    // Handle permission results
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(requireContext())) {
                    // User granted permission, retry setting ringtone
                    Uri uri = getDownloadedFileUri();
                    if (uri != null) {
                        setRingtone(uri);
                    }
                } else {
                    Toast.makeText(requireContext(),
                            "Permission denied. Cannot set ringtone.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    // ✅ Null-safe permission callback method
    public void setPermission() {
        if (mAdapter != null) {
            mAdapter.allowedPermission();
        } else {
            Log.e("AudioFragment", "setPermission() called but mAdapter is null.");
        }
    }

    @Override
    public void onItemClick(String data) {
        if (data != null){
            startDownload(data);
        }

    }
}
