package com.raman.kumar.shrikrishan;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.ads.AdView;
import com.raman.kumar.AudiosModal.Datum;
import org.apache.commons.io.FilenameUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static android.content.ContentValues.TAG;

class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.MyViewHolder> {

    private List<Datum> audioList;
    private Context mContext;
    private final ClickListener listener;
    private MediaPlayer mediaPlayer;
    private String fNmae = "ring1.mp3";
    private String fPAth = "android.resource://com.example.mann.myapplication/raw/ring1";
    private CallAudioField callAudioField;

    private String downloadAudioPath;
    private String urlDownloadLink = "";
    private int ringtoneStatus = 0;
    private ProgressDialog newProgressDialog;
    private OnItemClickListener clickListener;

    public void allowedPermission() {
        isStoragePermissionGranted();
    }
    public interface OnItemClickListener {
        void onItemClick(String data);
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        LinearLayout textview1;
        LinearLayout stop, alarm_icon;
        RelativeLayout main_layout;
        LinearLayout play_icon;
        TextView text;
        AdView adView;
        private WeakReference<ClickListener> listenerRef;



        public MyViewHolder(View view, ClickListener listener) {
            super(view);
            listenerRef = new WeakReference<>(listener);
            textview1 = view.findViewById(R.id.textview1);
            main_layout = view.findViewById(R.id.main_layout);
            stop = view.findViewById(R.id.stop);
            alarm_icon = view.findViewById(R.id.alarm_icon);
            play_icon = view.findViewById(R.id.play_icon);
            text = view.findViewById(R.id.text);
            adView = view.findViewById(R.id.adView);
            stop.setOnClickListener(this);
            alarm_icon.setOnClickListener(this);
            play_icon.setOnClickListener(this); // Add play_icon click listener
        }

        @Override
        public void onClick(View v) {
            Log.d("AudioAdapter", "Item clicked at position: " + getAdapterPosition());
            if (listenerRef.get() != null) {
                listenerRef.get().onPositionClicked(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    public void setOnCallAudioFieldListener(CallAudioField callAudioField) {
        this.callAudioField = callAudioField;
    }

    public AudioAdapter(List<Datum> audioList, Context context, FragmentTransaction ft, ArrayList<Integer> resID, MediaPlayer mediaPlayer, ClickListener listener, OnItemClickListener clickListener) {
        this.audioList = audioList;
        this.mContext = context;
        this.listener = listener;
        this.mediaPlayer = mediaPlayer;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_list_row, parent, false);
        return new MyViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final String txt = audioList.get(position).getTitle();
        holder.text.setText(txt);

        // Handle item click through the listener
        holder.textview1.setOnClickListener(v -> {
            if (callAudioField != null) {
                callAudioField.showPlayDialog(position);
            }
        });

        holder.play_icon.setOnClickListener(view -> {
            Log.d("AudioAdapter", "Play icon clicked, position: " + position);
            if (callAudioField != null) {
                callAudioField.showPlayDialog(position);
            }
        });

        holder.alarm_icon.setOnClickListener(v -> {
            ringtoneStatus = 1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(mContext)) {
                    if (isStoragePermissionGranted()) {
                        downloadFile(position, "alarm");
                    }
                } else {
                    requestWriteSettingsPermission();
                }
            } else {
                if (isStoragePermissionGranted()) {
                    downloadFile(position, "alarm");
                }
            }
        });

        holder.stop.setOnClickListener(v -> {
            ringtoneStatus = 2;
            clickListener.onItemClick(audioList.get(position).getPath());
        });
    }

    private void requestWriteSettingsPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    File newFile;

    public void downloadFile(int position, String type) {
        urlDownloadLink = audioList.get(position).getPath();
        File audioDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "audio");
        if (!audioDir.exists()) {
            audioDir.mkdir();
        }

        String originalFilename = FilenameUtils.getName(urlDownloadLink);
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String extension = FilenameUtils.getExtension(originalFilename);
        File fileToSave = new File(audioDir, baseName + "_custom_ringtone." + extension);

        System.out.println("wejfklsajfkj  "+ fileToSave);
        // If file exists, avoid download
        if (fileToSave.exists()) {
            Log.d(TAG, "File already exists: " + fileToSave.getAbsolutePath());
            SetAsRingtoneOrNotification(fileToSave, originalFilename);
            return;
        }

        Log.d(TAG, "Downloading file: " + urlDownloadLink);
        downloadAudioPath = fileToSave.getAbsolutePath();
        newProgressDialog = callAudioField.showProgress();
        DownloadFile downloadAudioFile = new DownloadFile();
        downloadAudioFile.execute(urlDownloadLink, downloadAudioPath, originalFilename);
    }





    public interface CallAudioField {
        void play(int position);
        void pause();
        void showPlayDialog(int position);
        ProgressDialog showProgress();
        void dismissProgressDialog(ProgressDialog progressDialog);
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    private class DownloadFile extends AsyncTask<String, Integer, String> {
        String filename = "";

        @Override
        protected String doInBackground(String... params) {
            int count;
            filename = params[2];
            try {
                URL url = new URL(params[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = Files.newOutputStream(Paths.get(params[1]));
                byte[] data = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                return params[1];
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            newProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                // Set as ringtone/notification after download completes
                SetAsRingtoneOrNotification(new File(result), filename);
            } else {
                newProgressDialog.dismiss();
                Toast.makeText(mContext, "Download failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String getMIMEType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getMimeTypeFromExtension(extension);
    }

    private void SetAsRingtoneOrNotification(File file, String filename) {
        if (newProgressDialog != null && newProgressDialog.isShowing()) {
            newProgressDialog.dismiss();
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, filename);
        values.put(MediaStore.MediaColumns.SIZE, file.length());
        values.put(MediaStore.MediaColumns.MIME_TYPE, getMIMEType(filename));
        values.put(MediaStore.Audio.Media.IS_RINGTONE, ringtoneStatus == 1);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, ringtoneStatus == 2);
        values.put(MediaStore.Audio.Media.IS_ALARM, ringtoneStatus == 1);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = mContext.getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath()), values);
        if (uri != null) {
            RingtoneManager.setActualDefaultRingtoneUri(
                    mContext,
                    ringtoneStatus == 1 ? RingtoneManager.TYPE_RINGTONE : RingtoneManager.TYPE_NOTIFICATION,
                    uri
            );
            Toast.makeText(mContext, "Ringtone set successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Failed to set ringtone", Toast.LENGTH_SHORT).show();
        }
    }



    public interface ClickListener {
        void onPositionClicked(int position);

        void onLongClicked(int position);
    }
}
