package com.raman.kumar.shrikrishan.util;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.raman.kumar.shrikrishan.R;

public class RingtoneUtils {

    private static final String LOG_TAG = "RingtoneUtils";

    public static boolean setRingtone(@NonNull Context context, @NonNull Uri ringtoneUri, int type) {
        Log.v(LOG_TAG, "Setting Ringtone to: " + ringtoneUri);

        if (!hasMarshmallow()) {
            Log.v(LOG_TAG, "On a Lollipop or below device, so go ahead and change device ringtone");
            setActualRingtone(context, ringtoneUri, type);
            return true;
        } else if (hasMarshmallow() && canEditSystemSettings(context)) {
            Log.v(LOG_TAG, "On a marshmallow or above device but app has the permission to edit system settings");
            setActualRingtone(context, ringtoneUri, type);
            return true;
        } else if (hasMarshmallow() && !canEditSystemSettings(context)) {
            Log.d(LOG_TAG, "On android Marshmallow and above but app does not have permission to" +
                    " edit system settings. Opening the manage write settings activity...");
            startManageWriteSettingsActivity(context);
            Toast.makeText(context, "Please allow app to edit settings so your ringtone/notification can be updated", Toast.LENGTH_LONG).show();
            return false;
        }

        return false;
    }

    private static void setActualRingtone(@NonNull Context context, @NonNull Uri ringtoneUri, int type) {
        RingtoneManager.setActualDefaultRingtoneUri(context, type, ringtoneUri);
        String message="";
        if(type == RingtoneManager.TYPE_RINGTONE) {
            message = context.getString(R.string.ringtone_set_success);
        } else if(type == RingtoneManager.TYPE_NOTIFICATION) {
            message = context.getString(R.string.notification_set_success);
        }
        if ((RingtoneManager.getActualDefaultRingtoneUri(context, type)).equals(ringtoneUri)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getString(R.string.operation_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void startManageWriteSettingsActivity(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        // Passing in the app package here allows the settings app to open the exact app
        intent.setData(Uri.parse("package:" + context.getApplicationContext().getPackageName()));
        // Optional. If you pass in a service context without setting this flag, you will get an exception
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static boolean hasMarshmallow() {
        // returns true if the device is Android Marshmallow or above, false otherwise
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean canEditSystemSettings(@NonNull Context context) {
        // returns true if the app can edit system settings, false otherwise
        return Settings.System.canWrite(context.getApplicationContext());
    }

}
