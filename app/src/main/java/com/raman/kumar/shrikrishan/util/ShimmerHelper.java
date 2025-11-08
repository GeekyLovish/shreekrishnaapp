package com.raman.kumar.shrikrishan.util;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.raman.kumar.shrikrishan.R;

public class ShimmerHelper {
    public static void startShimmer(Activity activity, ShimmerLay type) {
        RelativeLayout shimmerView = activity.findViewById(R.id.shimmerView);
        ShimmerFrameLayout shimmerLay = activity.findViewById(R.id.shimmerLay);
        LinearLayout lay = null;
        switch (type) {
            case POSTLAY: {
                lay = activity.findViewById(R.id.postLay);
                break;
            }
            case WALLPAPERLAY: {
                lay = activity.findViewById(R.id.wallpaperLay);
                break;
            }
            case VIDEOSLAY: {
                lay = activity.findViewById(R.id.videosLay);
                break;
            }
            case AARTILAY: {
                lay = activity.findViewById(R.id.aartiLay);
                break;
            }
        }
        if (lay != null) {
            shimmerView.setVisibility(View.VISIBLE);
            lay.setVisibility(View.VISIBLE);
            shimmerLay.startShimmer();
        }
    }

    public static void stopShimmer(Activity activity, ShimmerLay type) {
        RelativeLayout shimmerView = activity.findViewById(R.id.shimmerView);
        ShimmerFrameLayout shimmerLay = activity.findViewById(R.id.shimmerLay);
        LinearLayout lay = null;
        switch (type) {
            case POSTLAY: {
                lay = activity.findViewById(R.id.postLay);
                break;
            }
            case WALLPAPERLAY: {
                lay = activity.findViewById(R.id.wallpaperLay);
                break;
            }
            case VIDEOSLAY: {
                lay = activity.findViewById(R.id.videosLay);
                break;
            }
            case AARTILAY: {
                lay = activity.findViewById(R.id.aartiLay);
                break;
            }
        }
        if (lay != null) {
            shimmerView.setVisibility(View.GONE);
            lay.setVisibility(View.GONE);
            shimmerLay.stopShimmer();
        }
    }
}

