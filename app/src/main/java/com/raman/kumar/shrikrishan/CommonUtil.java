package com.raman.kumar.shrikrishan;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by mann on 3/11/17.
 */

public class CommonUtil {

    public static boolean isNetworkConnectionAvailable(Context contect) {
        ConnectivityManager cm = (ConnectivityManager) contect.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }
}
