package com.raman.kumar.shrikrishan.tmrMusic;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.raman.kumar.shrikrishan.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class TmrBhajanAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> sList;

    public TmrBhajanAdapter(ArrayList<HashMap<String, String>> sList) {
        this.sList = sList;
    }

    @Override
    public Object getItem(int position) {
        return sList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("MissingPermission")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_list_row, null);

            mViewHolder.textview1 = convertView.findViewById(R.id.textview1);
            mViewHolder.main_layout = convertView.findViewById(R.id.main_layout);
            mViewHolder.stop = convertView.findViewById(R.id.stop);
            mViewHolder.alarm_icon = convertView.findViewById(R.id.alarm_icon);
            mViewHolder.play_icon = convertView.findViewById(R.id.play_icon);
            mViewHolder.text = convertView.findViewById(R.id.text);
            mViewHolder.tvDuration = convertView.findViewById(R.id.tvDuration);
            mViewHolder.llDuration = convertView.findViewById(R.id.llDuration);
            mViewHolder.adView = convertView.findViewById(R.id.adView);
            mViewHolder.stop.setVisibility(View.GONE);
            mViewHolder.play_icon.setVisibility(View.GONE);
            mViewHolder.alarm_icon.setVisibility(View.GONE);
            mViewHolder.llDuration.setVisibility(View.VISIBLE);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        String title = "";
        String path = "";
        if (position < sList.size()) {
            title = sList.get(position).get("songTitle");
            path = sList.get(position).get("songPath");
        }
        String dur = Objects.requireNonNull(sList.get(position).get("songDuration")).replace("f", "");
        mViewHolder.tvDuration.setText(dur.replace(".", ":"));
        mViewHolder.text.setText(title);


        /*AdRequest adRequest = new AdRequest.Builder().build();
        mViewHolder.adView.loadAd(adRequest);
        if (position % 10 == 0){
            if (position == 0){
                mViewHolder.adView.setVisibility(View.GONE);
            }else {
                mViewHolder.adView.setVisibility(View.VISIBLE);
            }
        }else {
            mViewHolder.adView.setVisibility(View.GONE);
        }*/

        return convertView;
    }

    @Override
    public int getCount() {
        return (sList != null) ? sList.size() : 0;
    }

    class ViewHolder {
        LinearLayout textview1;
        LinearLayout stop, alarm_icon;
        RelativeLayout main_layout;
        LinearLayout play_icon;
        TextView text;
        LinearLayout llDuration;
        TextView tvDuration;
        AdView adView;
    }

}
