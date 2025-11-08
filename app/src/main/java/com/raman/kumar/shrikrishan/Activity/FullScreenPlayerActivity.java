package com.raman.kumar.shrikrishan.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.arges.sepan.argmusicplayer.Enums.ErrorType;
import com.arges.sepan.argmusicplayer.IndependentClasses.Arg;
import com.arges.sepan.argmusicplayer.IndependentClasses.ArgAudio;
import com.arges.sepan.argmusicplayer.IndependentClasses.ArgAudioList;
import com.arges.sepan.argmusicplayer.PlayerViews.ArgPlayerFullScreenView;
//import com.kumar.raman.shrikrishan.BhajanAdapter;
//import com.kumar.raman.shrikrishan.BhajanAdapter;
import com.raman.kumar.shrikrishan.Pojo.AudioModel;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.networking.NetworkingCallbackInterface;
import com.raman.kumar.shrikrishan.networking.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FullScreenPlayerActivity extends AppCompatActivity {

    ArgPlayerFullScreenView musicPlayer;
    ArgAudioList playlist = new ArgAudioList(true);
    // ArrayList<AudioModel> audioList;
    private List<AudioModel> audioList = new ArrayList<>();

    int position = 0;
    String URL = "https://ramankumarynr.com/api/?json=get_post&id=1294";
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_full_screen_player);
        Intent i = getIntent();
        //  audioList = (ArrayList<AudioModel>) i.getSerializableExtra("list");
        //  position=i.getIntExtra("position",1);

        musicPlayer = (ArgPlayerFullScreenView) findViewById(R.id.argmusicplayer);
        musicPlayer.stopPlaylistWhenError();

        musicPlayer.setOnErrorListener(new Arg.OnErrorListener() {
            @Override
            public void onError(ErrorType errorType, String description) {
                //     Toast.makeText(FullScreenPlayerActivity.this,"Error:\nType: "+errorType+"\nDescription: "+description,Toast.LENGTH_LONG).show();
            }
        });
        getAllAudios();
//        for(int k=0;k<audioList.size();k++){
//            ArgAudio audioUrl = ArgAudio.createFromURL(audioList.get(k).getTitle(),"",audioList.get(k).getUrl());
//            playlist.add(audioUrl);
//        }


//        musicPlayer.setPlaylistRepeat(true);
//        musicPlayer.enableNotification(FullScreenPlayerActivity.class);
//        musicPlayer.disableErrorView();
//        musicPlayer.loadPlaylist(playlist);
//        musicPlayer.playPlaylistItem(position);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (musicPlayer != null) {
            musicPlayer.pause();
            musicPlayer.disableNotification();
            musicPlayer.stop();
            // ArgMusicService.killMediaPlayer();
            finish();

            //musicPlayer=null;
            //musicPlayer.stop();
            //musicPlayer=null;
        }

    }

    public void showProgressDialog() {
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    public void getAllAudios() {
        JSONObject json = new JSONObject();
        showProgressDialog();
        RequestHandler.getAllAudios(URL, this, json, new NetworkingCallbackInterface() {
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
                    progress.dismiss();


                    JSONArray arrayAttachment = json.getJSONArray("attachments");
                    for (int j = 0; j < arrayAttachment.length(); j++) {
                        JSONObject attachJson = arrayAttachment.getJSONObject(j);

                        content = new AudioModel();
                        String url = attachJson.getString("url");
                        String title = attachJson.getString("title");
                        int id = attachJson.getInt("id");
                        content.setTitle(title);
                        content.setUrl(url);
                        content.setId(id);
                        audioList.add(content);
                        ArgAudio audioUrl = ArgAudio.createFromURL(title, title, url);
                        playlist.add(audioUrl);
                    }
                    Collections.sort(audioList);
                    musicPlayer.setPlaylistRepeat(true);
                    musicPlayer.enableNotification(FullScreenPlayerActivity.class);
                    musicPlayer.disableErrorView();
                    musicPlayer.loadPlaylist(playlist);
                    musicPlayer.playPlaylistItem(position);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Connection not available", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNetworkFailure(String error) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
