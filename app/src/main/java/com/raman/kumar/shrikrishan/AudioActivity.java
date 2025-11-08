package com.raman.kumar.shrikrishan;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.raman.kumar.shrikrishan.Pojo.AudioModel;
import com.raman.kumar.shrikrishan.networking.NetworkingCallbackInterface;
import com.raman.kumar.shrikrishan.networking.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioActivity extends AppCompatActivity implements AudioAdapter.CallAudioField {
    RecyclerView recyclerView;
    private AudioAdapter mAdapter;
    private List<AudioModel> audioList = new ArrayList<>();
    Spinner selectOptions;
    LinearLayout uploadAudio;
    ImageView imagePreview;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    String uploadedAudioName = "";
    public ArrayList<String> audioURLList = new ArrayList<>();
    final private ArrayList<Integer> resID = new ArrayList<>();
    MediaPlayer mediaPlayer;
    String URL = "http://ramankumarynr.com/api/?json=get_post&id=598";
    ProgressDialog progress;
    ImageButton play_rec;
    Music music = new Music();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // uploadAudio = (LinearLayout) findViewById(R.id.uploadAudio);
        progress = new ProgressDialog(this);
        setTitle("Audios");

//        mAdapter = new AudioAdapter(new ArrayList<AudioModel>(), this, getSupportFragmentManager().beginTransaction(), resID, mediaPlayer, new ClickListener() {
//            @Override
//            public void onPositionClicked(int position) {
//            }
//
//            @Override
//            public void onLongClicked(int position) {
//            }
//        });
        //  mAdapter.setOnCallAudioFieldListener(this);
        if (isNetworkConnected()) {
            getAllAudios(this);
        } else {
//            Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
        }


        uploadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  showCustomDialog();

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        }
        );
        //  callMethod();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        progress.setCancelable(true);
        progress.setCanceledOnTouchOutside(true);
        finish();
    }

    public void showAudio(Uri videoUri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(videoUri, "audio/*");
        startActivity(intent);
    }

    private void startRecording() {
        File filepath = Environment.getExternalStorageDirectory();
        File dir = new File(filepath.getAbsolutePath() + "/srikrishna/audio/");
        dir.mkdirs();
        uploadedAudioName = "/srikrishna/audio/" + (audioURLList.size() + 1) + ".avi";
        uploadedAudioName = Environment.getExternalStorageDirectory().getAbsolutePath() + uploadedAudioName;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOutputFile(uploadedAudioName);


        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            // Log.e(LOG_TAG, "prepare() failed");
        }


    }

    private void stopRecording() {
        if (mRecorder != null) {

            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer = null;
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, Music.class));

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    @Override
    public void play(int position) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void showPlayDialog(final int position) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dialog.setContentView(R.layout.audio_dialog);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button okButton = (Button) dialog.findViewById(R.id.okButton);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        ImageButton stop_rec = (ImageButton) dialog.findViewById(R.id.stop_audio);
        play_rec = (ImageButton) dialog.findViewById(R.id.play_rec);
        final Integer audio = 0;
        play_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    try {
                        Thread t = new Thread() {
                            public void run() {
                                Intent serviceIntent = new Intent(getApplicationContext(), Music.class);
                                serviceIntent.putExtra("ServiceFileDescriptor", audioList.get(position).getUrl());
                                startService(serviceIntent);
                                // music.play();
                            }
                        };
                        t.start();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
//                    Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        stop_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                music.stop();
                stopService(new Intent(getApplicationContext(), Music.class));
                play_rec.setImageDrawable(getResources().getDrawable(R.drawable.play_icon));
//                if (mediaPlayer != null) {
//                    if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.stop();
//                        mediaPlayer=null;
//                    }
//
//                }
            }
        });
        dialog.show();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                music.stop();
                stopService(new Intent(getApplicationContext(), Music.class));

//                if (mediaPlayer != null) {
//                    if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.stop();
//                    }
//                }
//                mediaPlayer = null;
            }
        });
    }

    @Override
    public ProgressDialog showProgress() {
        ProgressDialog dialog = new ProgressDialog(this);
        //  progressDialog.setTitle("Loading");
        dialog.setMessage("Wait while setting ringtone/alarm");
        // progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        dialog.show();
        return dialog;
    }

    @Override
    public void dismissProgressDialog(ProgressDialog progressDialog) {
        progressDialog.dismiss();
    }

    public void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    public void getAllAudios(final AudioAdapter.CallAudioField a) {
        JSONObject json = new JSONObject();
        showProgressDialog();
        RequestHandler.getAllAudios("", this, json, new NetworkingCallbackInterface() {
            @Override
            public void onSuccess(NetworkResponse response, boolean fromCache) {
                System.out.print("response........" + response);
                progress.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    System.out.print("jsonArrayresponse........" + jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(String response, boolean fromCache) {
                System.out.print("response........" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject json = jsonObject.getJSONObject("post");
                    AudioModel content = new AudioModel();
                    JSONArray arrayAttachment = json.getJSONArray("attachments");
                    for (int j = 0; j < arrayAttachment.length(); j++) {
                        JSONObject attachJson = arrayAttachment.getJSONObject(j);
                        content = new AudioModel();
                        String url = attachJson.getString("url");
                        String title = attachJson.getString("title");
                        content.setTitle(title);
                        content.setUrl(url);
                        audioList.add(content);
                    }
                    mAdapter.setOnCallAudioFieldListener(a);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                    recyclerView.setAdapter(mAdapter);
                    progress.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                progress.dismiss();
//                Toast.makeText(getApplicationContext(), "Connection not available", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNetworkFailure(String error) {
                progress.dismiss();
//                Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();

            }
        });
    }

    class Player extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progress;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {

                mediaPlayer.setDataSource(params[0]);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        play_rec.setImageDrawable(getResources().getDrawable(R.drawable.play_icon));
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer = null;
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Log.d("IllegarArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progress.isShowing()) {
                progress.cancel();
            }
            play_rec.setImageDrawable(getResources().getDrawable(R.drawable.pause_icon));
            Log.d("Prepared", "//" + result);

        }

        public Player() {
            progress = new ProgressDialog(AudioActivity.this);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            this.progress.setMessage("Buffering...");
            this.progress.show();

        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
