package com.raman.kumar.shrikrishan.tmrMusic.helpers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.tmrMusic.TmrMusicNewActivity;

import java.lang.reflect.Method;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.raman.kumar.shrikrishan.tmrMusic.helpers.UpdateReceiver.ACTION_CLOSE;
import static com.raman.kumar.shrikrishan.tmrMusic.helpers.UpdateReceiver.ACTION_NEXT;
import static com.raman.kumar.shrikrishan.tmrMusic.helpers.UpdateReceiver.ACTION_PLAY;
import static com.raman.kumar.shrikrishan.tmrMusic.helpers.UpdateReceiver.ACTION_PREV;


/**
 * Created by rahul on 6/12/2017.
 * <p>
 * UPDATE by @Sarangal on 01/01/2020:
 * NOTIFICATION Module Updated
 */

public class NotificationHandler extends Notification {

    /* Service Instance */
    private Service mContext;

    /* Notification Components */
    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManagerCompat mNotificationManager;
    private static final String CHANNEL_ID = "CHANNEL_5424N94L";
    private static final int NOTIFICATION_ID = 4950;

    /* Notification Layouts */
    private RemoteViews notificationView, bigNotificationView;

    private boolean isOldLayout = false;
    private PendingIntent pendingNotificationIntent;

    @SuppressLint("WrongConstant")
    private NotificationHandler(Service context) {
        super();

        /* Get Instance */
        mContext = context;

        UpdateReceiver.staticBool = true;

        /* Create Notification Channel */
        createNotificationChannel();

        /* Initialize NotificationManager */
        mNotificationManager = NotificationManagerCompat.from(mContext);

        /* Start this(MainActivity) on by Tapping mNotification */
        Intent mainIntent = new Intent(mContext, TmrMusicNewActivity.class);
        mainIntent.putExtra("intentValue", "bhajan");
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
     //   pendingNotificationIntent = PendingIntent.getActivity(mContext, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            pendingNotificationIntent = PendingIntent.getActivity(mContext, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            pendingNotificationIntent = PendingIntent.getActivity(mContext, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    /**
     * NOTIFICATION CHANNEL: NECESSARY FOR OREO+ DEVICES
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MUSIC_NOTIFICATION";
            String description = "BACKGROUND_MUSIC_NOTIFICATION";

            /* importance of your mNotification */
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.setSound(null, null);

            /* NotificationManager Component */
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static NotificationHandler notificationHandler = null;

    public static NotificationHandler getInstance() {
        return notificationHandler;
    }

    public static NotificationHandler getInstance(Service context) {
        if (notificationHandler == null) {
            notificationHandler = new NotificationHandler(context);
        }
        return notificationHandler;
    }

    /**
     * Initialize Notification Layout
     */
    private void initNotificationView() {
        /* Layout for Small Notification */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            notificationView = new RemoteViews(mContext.getPackageName(), R.layout.notif_old_layout);
            isOldLayout = true;
        } else
            notificationView = new RemoteViews(mContext.getPackageName(), R.layout.notif_new_layout);

        /* Layout for Expanded Notification */
        bigNotificationView = new RemoteViews(mContext.getPackageName(), R.layout.big_notif_layout);

        /* Set Listeners on Notification Layout */
        setListeners();

        /* Initialize NotificationBuilder */
        mNotificationBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                /* these are the three things a NotificationCompat.Builder object requires at a minimum */
                .setSmallIcon(R.drawable.player_play_button)

                /* TEXT Content For Notification */
                //.setContentTitle("NOTIFICATION_TITLE")
                //.setContentText("CONTENT_TEXT")

                /* mNotification will be dismissed when tapped */
                //.setAutoCancel(true)

                /* setting the custom collapsed and expanded views */
                .setCustomContentView(notificationView)
                .setCustomBigContentView(bigNotificationView)

        /* setting style to DecoratedCustomViewStyle() is necessary for custom views to display */
        //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
        ;
    }

    /**
     * SET LISTENERS - NOTIFICATION LAYOUT
     */
    private void setListeners() {
        Intent playPauseIntent = new Intent(ACTION_PLAY);
        playPauseIntent.setClass(mContext, UpdateReceiver.class);
        @SuppressLint("WrongConstant") PendingIntent pendingPlayPauseIntent = PendingIntent.getBroadcast(mContext, 0, playPauseIntent, PendingIntent.FLAG_MUTABLE);
        notificationView.setOnClickPendingIntent(R.id.playNotifBtn, pendingPlayPauseIntent);
        notificationView.setOnClickPendingIntent(R.id.pauseNotifBtn, pendingPlayPauseIntent);
        bigNotificationView.setOnClickPendingIntent(R.id.playNotifBtn, pendingPlayPauseIntent);
        bigNotificationView.setOnClickPendingIntent(R.id.pauseNotifBtn, pendingPlayPauseIntent);

        Intent nextIntent = new Intent(ACTION_NEXT);
        nextIntent.setClass(mContext, UpdateReceiver.class);
        @SuppressLint("WrongConstant") PendingIntent pendingNextIntent = PendingIntent.getBroadcast(mContext, 1, nextIntent, PendingIntent.FLAG_MUTABLE );
        notificationView.setOnClickPendingIntent(R.id.nextNotifBtn, pendingNextIntent);
        bigNotificationView.setOnClickPendingIntent(R.id.nextNotifBtn, pendingNextIntent);

        Intent prevIntent = new Intent(ACTION_PREV);
        prevIntent.setClass(mContext, UpdateReceiver.class);
        @SuppressLint("WrongConstant") PendingIntent pendingPrevIntent = PendingIntent.getBroadcast(mContext, 1, prevIntent,  PendingIntent.FLAG_MUTABLE);
        notificationView.setOnClickPendingIntent(R.id.prevNotifBtn, pendingPrevIntent);
        bigNotificationView.setOnClickPendingIntent(R.id.prevNotifBtn, pendingPrevIntent);

        Intent closeIntent = new Intent(ACTION_CLOSE);
        closeIntent.setClass(mContext, UpdateReceiver.class);
        @SuppressLint("WrongConstant") PendingIntent pendingCloseIntent = PendingIntent.getBroadcast(mContext, 2, closeIntent, PendingIntent.FLAG_MUTABLE);
        notificationView.setOnClickPendingIntent(R.id.closeNotifBtn, pendingCloseIntent);
        bigNotificationView.setOnClickPendingIntent(R.id.closeNotifBtn, pendingCloseIntent);
    }

    /**
     * Show Notification
     */
    public void showNotification(final String title, final String artist, final String album, final boolean isPlay, final boolean isProgress) {

        /* Initialize Notification Views */
        initNotificationView();

        /* Set Data to View and Update View Status */
        setSongDetail(title, artist, album);
        notifPlayPauseEvent(isPlay, isProgress);

        /* tapping mNotification will open MainActivity */
        mNotificationBuilder.setContentIntent(pendingNotificationIntent);
        mNotificationBuilder.setCustomContentView(notificationView);
        mNotificationBuilder.setCustomBigContentView(bigNotificationView);
        mNotificationBuilder.setPriority(NotificationCompat.PRIORITY_LOW);
        mNotificationBuilder.setSound(null);
        Notification mNotification = mNotificationBuilder.build();
        mNotification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNotification.visibility = Notification.VISIBILITY_PUBLIC;
        }

        mContext.startForeground(NOTIFICATION_ID, mNotification);
                /*if(mNotificationManager != null) {
                    mNotificationManager.notify(NOTIFICATION_ID, mNotification);
                }*/
    }

    /**
     * Update View Status
     */
    private void notifPlayPauseEvent(boolean isPlaying, boolean isProgress) {
        Method setDrawableParameters = null;
        if (notificationView != null && bigNotificationView != null) {
            try {
                Class RemoteViews = Class.forName("android.widget.RemoteViews");
                setDrawableParameters = RemoteViews.getMethod("setDrawableParameters",
                        int.class, boolean.class, int.class, int.class, PorterDuff.Mode.class, int.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isProgress) {
                notificationView.setViewVisibility(R.id.progressNotif, View.VISIBLE);
                notificationView.setViewVisibility(R.id.progressNotifNew, View.VISIBLE);
                notificationView.setViewVisibility(R.id.progressNotifBig, View.VISIBLE);
                bigNotificationView.setViewVisibility(R.id.progressNotifBig, View.VISIBLE);

                notificationView.setViewVisibility(R.id.pauseNotifBtn, View.GONE);
                notificationView.setViewVisibility(R.id.playNotifBtn, View.GONE);
                bigNotificationView.setViewVisibility(R.id.pauseNotifBtn, View.GONE);
                bigNotificationView.setViewVisibility(R.id.playNotifBtn, View.GONE);
            } else {
                notificationView.setViewVisibility(R.id.progressNotif, View.GONE);
                notificationView.setViewVisibility(R.id.progressNotifNew, View.GONE);
                notificationView.setViewVisibility(R.id.progressNotifBig, View.GONE);
                bigNotificationView.setViewVisibility(R.id.progressNotifBig, View.GONE);

                if (isPlaying) {
                    notificationView.setViewVisibility(R.id.pauseNotifBtn, View.VISIBLE);
                    notificationView.setViewVisibility(R.id.playNotifBtn, View.GONE);
                    bigNotificationView.setViewVisibility(R.id.pauseNotifBtn, View.VISIBLE);
                    bigNotificationView.setViewVisibility(R.id.playNotifBtn, View.GONE);
                } else {
                    notificationView.setViewVisibility(R.id.playNotifBtn, View.VISIBLE);
                    notificationView.setViewVisibility(R.id.pauseNotifBtn, View.GONE);
                    bigNotificationView.setViewVisibility(R.id.playNotifBtn, View.VISIBLE);
                    bigNotificationView.setViewVisibility(R.id.pauseNotifBtn, View.GONE);
                }
            }

            try {
                if (setDrawableParameters != null) {
                    setDrawableParameters.invoke(notificationView, R.id.playNotifBtn, false,
                            -1, BitmapPalette.darkVibrantTitleTextColor, PorterDuff.Mode.MULTIPLY, -1);
                    setDrawableParameters.invoke(notificationView, R.id.pauseNotifBtn, false,
                            -1, BitmapPalette.darkVibrantTitleTextColor, PorterDuff.Mode.MULTIPLY, -1);
                    setDrawableParameters.invoke(notificationView, R.id.nextNotifBtn, false,
                            -1, BitmapPalette.darkVibrantTitleTextColor, PorterDuff.Mode.MULTIPLY, -1);
                    setDrawableParameters.invoke(notificationView, R.id.closeNotifBtn, false,
                            -1, BitmapPalette.darkVibrantTitleTextColor, PorterDuff.Mode.MULTIPLY, -1);

                    setDrawableParameters.invoke(bigNotificationView, R.id.playNotifBtn, false,
                            -1, BitmapPalette.darkVibrantTitleTextColor, PorterDuff.Mode.MULTIPLY, -1);
                    setDrawableParameters.invoke(bigNotificationView, R.id.pauseNotifBtn, false,
                            -1, BitmapPalette.darkVibrantTitleTextColor, PorterDuff.Mode.MULTIPLY, -1);
                    setDrawableParameters.invoke(bigNotificationView, R.id.nextNotifBtn, false,
                            -1, BitmapPalette.darkVibrantTitleTextColor, PorterDuff.Mode.MULTIPLY, -1);
                    setDrawableParameters.invoke(bigNotificationView, R.id.closeNotifBtn, false,
                            -1, BitmapPalette.darkVibrantTitleTextColor, PorterDuff.Mode.MULTIPLY, -1);

                    setDrawableParameters.invoke(notificationView, R.id.prevNotifBtn, false,
                            -1, BitmapPalette.darkVibrantTitleTextColor, PorterDuff.Mode.MULTIPLY, -1);
                    setDrawableParameters.invoke(bigNotificationView, R.id.prevNotifBtn, false,
                            -1, BitmapPalette.darkVibrantTitleTextColor, PorterDuff.Mode.MULTIPLY, -1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set Data to View
     */
    private void setSongDetail(final String title1, final String artist, final String album) {
        if (BitmapPalette.smallBitmap != null) {
            notificationView.setImageViewBitmap(R.id.albumNotifArt, BitmapPalette.smallBitmap);
            bigNotificationView.setImageViewBitmap(R.id.albumNotifArt, BitmapPalette.smallBitmap);
        } else {
            notificationView.setImageViewResource(R.id.albumNotifArt, R.drawable.logo_square);
            bigNotificationView.setImageViewResource(R.id.albumNotifArt, R.drawable.logo_square);
        }

        int[] colors = new int[]{BitmapPalette.darkVibrantRGBColor}, colorsOverlay;

        if (isOldLayout) {
            colorsOverlay = new int[]{mContext.getResources().getColor(R.color.colorTransparent), BitmapPalette.darkVibrantRGBColor};
        } else {
            colorsOverlay = new int[]{BitmapPalette.darkVibrantRGBColor, mContext.getResources().getColor(R.color.colorTransparent)};
        }

        int width = BitmapPalette.calculatePixels(60, mContext);
        int height = BitmapPalette.calculatePixels(30, mContext);

        /* Set Overlay Color */
        setGradientBitmap(width, height, colors, false);
        setGradientBitmap(width, height, colorsOverlay, true);

        notificationView.setInt(R.id.songTitle, "setTextColor", BitmapPalette.darkVibrantTitleTextColor);
        notificationView.setInt(R.id.songArtist, "setTextColor", BitmapPalette.darkVibrantTitleTextColor);
        notificationView.setInt(R.id.songAlbum, "setTextColor", BitmapPalette.darkVibrantTitleTextColor);

        bigNotificationView.setInt(R.id.songTitle, "setTextColor", BitmapPalette.darkVibrantTitleTextColor);
        bigNotificationView.setInt(R.id.songArtist, "setTextColor", BitmapPalette.darkVibrantTitleTextColor);
        bigNotificationView.setInt(R.id.songAlbum, "setTextColor", BitmapPalette.darkVibrantTitleTextColor);

        notificationView.setTextViewText(R.id.songTitle, title1);
        notificationView.setTextViewText(R.id.songArtist, artist);
        notificationView.setTextViewText(R.id.songAlbum, album);

        bigNotificationView.setTextViewText(R.id.songTitle, title1);
        bigNotificationView.setTextViewText(R.id.songArtist, artist);
        bigNotificationView.setTextViewText(R.id.songAlbum, album);
    }

    /**
     * Change Overlay Colors
     */
    private void setGradientBitmap(int width, int height, int[] colors, boolean isOverlay) {
        Bitmap gradientBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(gradientBitmap);
        if (isOverlay) {
            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT, colors);

            gradientDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            gradientDrawable.draw(canvas);
            notificationView.setImageViewBitmap(R.id.fadeOverlay, gradientBitmap);
            if (isOldLayout) {
                int[] colorsRev = new int[]{BitmapPalette.darkVibrantRGBColor, colors[0]};
                Bitmap gradientBigBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas bigCanvas = new Canvas(gradientBigBitmap);
                GradientDrawable gradientBigDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT, colorsRev);

                gradientBigDrawable.setBounds(0, 0, bigCanvas.getWidth(), bigCanvas.getHeight());
                gradientBigDrawable.draw(bigCanvas);
                bigNotificationView.setImageViewBitmap(R.id.fadeOverlay, gradientBigBitmap);
            } else
                bigNotificationView.setImageViewBitmap(R.id.fadeOverlay, gradientBitmap);
        } else {
            canvas.drawColor(colors[0]);
            notificationView.setImageViewBitmap(R.id.ivBackground, gradientBitmap);
            bigNotificationView.setImageViewBitmap(R.id.ivBackground, gradientBitmap);
        }
    }

    /**
     * When Service DESTROY
     */
    public void onServiceDestroy() {
        /* Remove Notification From StatusBar */
        if (mNotificationManager != null)
            mNotificationManager.cancel(NOTIFICATION_ID);

        /* Release Variables */
        notificationHandler = null;
        mNotificationManager = null;
    }


}
