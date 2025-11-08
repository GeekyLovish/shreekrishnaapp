package com.raman.kumar.shrikrishan.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Patterns;

import com.raman.kumar.shrikrishan.application.MyApp;

/**
 *
 * Written By Sarangal
 *
 * */

public class ValidationsClass {
  private static final String EMAIL_PATTERN =
    "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
      "(?:[\\x01-\\x07\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01" +
      "-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x07\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";


  public boolean checkStringNull(String string) {
    return !(string == null || string.equals("null") || string.isEmpty());
  }

  /**
   * Check Phone Validation Format
   */
  public boolean isValidPhoneNumber(String mPhone) {
    return isValidPhone(mPhone, 10, 15);
  }

  /**
   * Check PHONE is VALID or not.
   */
  boolean isValidPhone(String mPhone, int minLenght, int maxLenght) {
    return mPhone != null && !mPhone.isEmpty() && !(mPhone.length() < minLenght || mPhone.length() > maxLenght) && Patterns.PHONE.matcher(mPhone).matches();
  }

  /**
   * CHECK INTERNET CONNECTION STATUS
   */
  public boolean isNetworkConnected() {
    ConnectivityManager connectivityManager = (ConnectivityManager) MyApp.getApplication().getApplicationContext()
      .getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(network);
        return
          (actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)));
      } else {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && connectivityManager.getActiveNetworkInfo().isConnected();
      }
    }
    return false;
  }
}
