package com.raman.kumar.shrikrishan.tmrMusic.helpers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;


import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.tmrMusic.PlayBackManagerForFragment;
import com.raman.kumar.shrikrishan.tmrMusic.SongService;
import com.raman.kumar.shrikrishan.util.ResourceHandler;
import com.raman.kumar.shrikrishan.util.ValidationsClass;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by absolutezero on 10/7/17.
 */

public class UpdateReceiver extends AppWidgetProvider {
    public static final String ACTION_PLAY = "com.rahulk11.randomplayer.ACTION_PLAY";
    public static final String ACTION_NEXT = "com.rahulk11.randomplayer.ACTION_NEXT";
    public static final String ACTION_PREV = "com.rahulk11.randomplayer.ACTION_PREV";
    public static final String ACTION_CLOSE = "com.rahulk11.randomplayer.ACTION_CLOSE";
    public static boolean staticBool;
    private static final String WIDGET_CLICK = "WIDGET_CLICK";


    @Override
    public void onUpdate(Context mContext, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int widgetId : appWidgetIds) {
            // create some random data
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.songTitle, String.valueOf("Hi...Don't think too much. \n This is just a test widget!"));

            // Register an onClickListener
            Intent intent = new Intent(mContext, UpdateReceiver.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
                    0, intent, PendingIntent.FLAG_IMMUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.songTitle, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }
    }

//    public void  update(Context mContext, AppWidgetManager appWidgetManager, ComponentName thisWidget, int[] appWidgetIds){
//
//
//    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        boolean isPlaying = SongService.isPlaying();
        switch (action) {
            case ACTION_PLAY:

                /* Manually Paused */
                PlayBackManagerForFragment.isManuallyPaused = isPlaying;

                Log.d("###NOTIF_PLAY_BTN: ", "CLICKED");
                PlayBackManagerForFragment.playPauseEvent(false, isPlaying);
                break;

            case ACTION_PREV:
            case ACTION_NEXT:
                if (new ValidationsClass().isNetworkConnected()) {
                    Log.d(action.equalsIgnoreCase(ACTION_NEXT) ? "###NOTIF_NEXT_BTN: " : "###NOTIF_PREV_BTN: ", "CLICKED");
                    PlayBackManagerForFragment.playPrevNext(action.equalsIgnoreCase(ACTION_NEXT));
                } else {
                    Toast.makeText(context, ResourceHandler.getString(R.string.internet_check), Toast.LENGTH_SHORT).show();
                }
                break;

            case ACTION_CLOSE:
                Log.d("###NOTIF_CLOSE_BTN: ", "CLICKED");
//                NotificationHandler.getInstance(context).onServiceDestroy();
                if (NotificationHandler.getInstance() != null)
                    NotificationHandler.getInstance().onServiceDestroy();
                PlayBackManagerForFragment.stopService();
                break;

            case AppWidgetManager.ACTION_APPWIDGET_UPDATE:
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
                ComponentName thisWidget = new ComponentName(context.getApplicationContext(), UpdateReceiver.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
                if (appWidgetIds != null && appWidgetIds.length > 0) {
//                    update(context, appWidgetManager, thisWidget, appWidgetIds);
                }
                break;

            case Intent.ACTION_HEADSET_PLUG:
                Log.d("###HEADSET_PLUG: ", "CLICKED");
                //noinspection deprecaRandom Playervition
                if (context != null && !staticBool && isPlaying)
                    if (!((AudioManager) context.getSystemService(AUDIO_SERVICE)).isWiredHeadsetOn()) {
                        PlayBackManagerForFragment.playPauseEvent(true, true);
                    }
                staticBool = false;
                break;
        }
    }

}
