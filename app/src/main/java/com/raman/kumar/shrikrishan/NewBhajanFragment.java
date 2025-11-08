package com.raman.kumar.shrikrishan;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.arges.sepan.argmusicplayer.Enums.ErrorType;
import com.arges.sepan.argmusicplayer.IndependentClasses.Arg;
import com.arges.sepan.argmusicplayer.IndependentClasses.ArgAudio;
import com.arges.sepan.argmusicplayer.IndependentClasses.ArgAudioList;
import com.arges.sepan.argmusicplayer.PlayerViews.ArgPlayerFullScreenView;
import com.raman.kumar.shrikrishan.Activity.FullScreenPlayerActivity;
import com.raman.kumar.shrikrishan.Pojo.AudioModel;
import com.raman.kumar.shrikrishan.networking.NetworkingCallbackInterface;
import com.raman.kumar.shrikrishan.networking.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dell- on 6/13/2019.
 */

public class NewBhajanFragment extends Fragment {
    ArgPlayerFullScreenView musicPlayer;
    ArgAudioList playlist;
    // ArrayList<AudioModel> audioList;
    private List<AudioModel> audioList = new ArrayList<>();
    public static boolean flag = false;

    int position = 0;
    String URL = "https://ramankumarynr.com/api/?json=get_post&id=1294";
    ProgressDialog progress;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.new_bhajan_lay, container, false);
        if (musicPlayer == null) {
            musicPlayer = (ArgPlayerFullScreenView) v.findViewById(R.id.argmusicplayer);
            //   musicPlayer.stopPlaylistWhenError();
        }

        musicPlayer.setOnErrorListener(new Arg.OnErrorListener() {
            @Override
            public void onError(ErrorType errorType, String description) {
                Toast.makeText(getActivity(), "Error:\nType: " + errorType + "\nDescription: " + description, Toast.LENGTH_LONG).show();
            }
        });
        // getAllAudios();
        return v;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        getAllAudios();
//    }

    public void showProgressDialog() {
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    public void getAllAudios() {
        JSONObject json = new JSONObject();
        showProgressDialog();
        RequestHandler.getAllAudios(URL, getActivity(), json, new NetworkingCallbackInterface() {
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

                    }
                    Collections.sort(audioList);

                    // musicPlayer.loadPlaylist(playlist);
                    if ((musicPlayer != null && !musicPlayer.isPlaying()) || flag) {
                        playlist = new ArgAudioList(true);

                        for (int k = 0; k < audioList.size(); k++) {
                            ArgAudio audioUrl = ArgAudio.createFromURL(audioList.get(k).getTitle(), "", audioList.get(k).getUrl());
                            playlist.add(audioUrl);
                        }
                        //  musicPlayer.loadPlaylist(playlist);
                        musicPlayer.setPlaylistRepeat(true);
                        musicPlayer.enableNotification(FullScreenPlayerActivity.class);
                        musicPlayer.disableErrorView();
                        musicPlayer.playPlaylist(playlist);
                    }
                    // musicPlayer.playPlaylistItem(position);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                progress.dismiss();
                Toast.makeText(getActivity(), "Connection not available", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNetworkFailure(String error) {
                progress.dismiss();
                Toast.makeText(getContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
//Intent i=new Intent(getActivity(),ArgMusicService.clas
        //ArgMusicPlayer.getInstance().stop();
        // ArgMusicService.killMediaPlayer();
        // musicPlayer.disableNotification();


    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void setUserVisibleHint(boolean isUserVisible) {
        super.setUserVisibleHint(isUserVisible);
        // when fragment visible to user and view is not null then enter here.
        if (isUserVisible && v != null) {
            onResume();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }
        // if(musicPlayer!=null&&!musicPlayer.isPlaying()) {
        getAllAudios();
        // }
        //do your stuff here
    }
}
