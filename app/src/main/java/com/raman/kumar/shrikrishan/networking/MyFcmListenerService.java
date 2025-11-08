package com.raman.kumar.shrikrishan.networking;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.raman.kumar.NotificationId;
import com.raman.kumar.shrikrishan.Activity.GalleryActivity;
import com.raman.kumar.shrikrishan.Activity.VideoActivity;
import com.raman.kumar.shrikrishan.Activity.WallpaperActivity;
import com.raman.kumar.shrikrishan.FullScrenActivity;
import com.raman.kumar.shrikrishan.MainActivity;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.tmrMusic.TmrMusicNewActivity;
import com.raman.kumar.shrikrishan.util.PrefHelper;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class MyFcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyFcmListenerService";
    private static final String CHANNEL_ID = "CHANNEL_5424N94LV2";
    private PrefHelper prefHelper;
    private boolean isRefresh = false;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.e(TAG, "From: " + message.getFrom());
        Log.e(TAG, "DATA: " + message.getData());


       /* String title = message.getData().get("title");
        String bodyMsg = message.getData().get("message");
        String imageUrl = message.getData().get("image");
        String likedByMeType = message.getData().get("like_type");
        String type = message.getData().get("type");
        String postId = message.getData().get("id");
        String commentId = message.getData().get("comment_id");
        String username = message.getData().get("username");
        String token = message.getData().get("token");
        String comment = message.getData().get("comment");
        String subType = message.getData().get("subType");
        String totalLikes = message.getData().get("total_likes");*/
        // Helper method for null-safe get


// Usage
        Map<String, String> data = message.getData();

        String title        = getSafeData(data, "title");
        String bodyMsg      = getSafeData(data, "message");
        String imageUrl     = getSafeData(data, "image");
        String likedByMeType= getSafeData(data, "like_type");
        String type         = getSafeData(data, "type");
        String postId       = getSafeData(data, "id");
        String commentId    = getSafeData(data, "comment_id");
        String username     = getSafeData(data, "username");
        String token        = getSafeData(data, "token");
        String comment      = getSafeData(data, "comment");
        String subType      = getSafeData(data, "subType");
        String totalLikes   = getSafeData(data, "total_likes");
        String likesByTypes   = getSafeData(data, "likes_types");


        title = stripHtmlAndEntities(title);
        bodyMsg = stripHtmlAndEntities(bodyMsg);
        type = type != null ? type : "";

        if (type.equals("user_blocked")){
            Intent intent = new Intent("com.yourapp.REFRESH_SECRET");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            return;
        }



        Log.d(TAG, "Notification Data: imageUrl=" + imageUrl + ", title=" + title + ", message=" + bodyMsg);

        sendMyNotification(bodyMsg, title, imageUrl, type, postId, commentId, username, token, comment,likedByMeType,likesByTypes,subType,totalLikes);
    }
    private String getSafeData(Map<String, String> data, String key) {
        return data.get(key) != null ? data.get(key) : "";
    }

    public String stripHtmlAndEntities(String html) {
        if (html == null) return "";

        // Step 1: Remove all HTML tags
        String noTags = html.replaceAll("<[^>]+>", "");

        // Step 2: Decode HTML entities
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(noTags, Html.FROM_HTML_MODE_LEGACY).toString().trim();
        } else {
            return Html.fromHtml(noTags).toString().trim();
        }
    }

    private void sendMyNotification(String message, String title, String imageUrl, String type,
                                    String postId, String commentId, String username,
                                    String token, String comment, String likedByMeType, String likesByTypes, String subType,String totalLikes) {
        Bitmap largeImage = BitmapFactory.decodeResource(getResources(), R.drawable.logo_square);

        // Load image from URL if provided
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Bitmap bitmapFromUrl = loadBitmap(imageUrl);
            if (bitmapFromUrl != null) {
                largeImage = bitmapFromUrl; // Use the loaded image if successful
            }
        }

        Intent intent = getTargetIntent(type, imageUrl, postId,likedByMeType,likesByTypes,subType,totalLikes);

        // Add necessary flags to bring app to foreground
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Use appropriate PendingIntent flags
//        int requestCode = (int) System.currentTimeMillis();
        int requestCode = NotificationId.getID(); // use the same unique ID

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_square)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(largeImage) // Set large image
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        // Add big picture style only if a valid image URL is available
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Bitmap bitmap = loadBitmap(imageUrl);
            if (bitmap != null) {
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
            }
        }



        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Notification permission not granted.");
                return;
            }
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        try {
            notificationManager.notify(NotificationId.getID(), notificationBuilder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Failed to show notification due to missing permissions.", e);
        }
    }

    private Intent getTargetIntent(String type, String imageUrl, String postId, String likedByMeType, String likesByTypes, String subType,String totalLikes) {
        Intent intent;
        switch (type) {
            case "video":
                intent = new Intent(this, VideoActivity.class);
                intent.putExtra("url", imageUrl);
                break;
            case "amrit":
//                intent = new Intent(this, WallpaperActivity.class);
//                intent.putExtra("post_id", postId);
//                intent.putExtra("post_image", imageUrl);


                if(subType.isEmpty()){
                    intent = new Intent(this, WallpaperActivity.class);
                    intent.putExtra("post_id", postId);
                    intent.putExtra("post_image", imageUrl);
                }else{
                    intent = new Intent(this, WallpaperActivity.class);
                    intent.putExtra("FROM", "Notification");
                    intent.putExtra("post_id", postId);
                    intent.putExtra("image_url", imageUrl);
                    intent.putExtra("likedByMeType", likedByMeType);
                    intent.putExtra("likesByTypes", likesByTypes);
                    intent.putExtra("total_likes", totalLikes);
                }



                break;
            case "gallery":
                if(subType.isEmpty()){
                    intent = new Intent(this, GalleryActivity.class);
                    intent.putExtra("post_id", postId);
                    intent.putExtra("post_image", imageUrl);
                }else{
                    intent = new Intent(this, GalleryActivity.class);
                    intent.putExtra("FROM", "Notification");
                    intent.putExtra("post_id", postId);
                    intent.putExtra("image_url", imageUrl);
                    intent.putExtra("likedByMeType", likedByMeType);
                    intent.putExtra("likesByTypes", likesByTypes);
                    intent.putExtra("total_likes", totalLikes);
                }


//                intent = new Intent(this, GalleryActivity.class);
//                intent.putExtra("post_id", postId);
//                intent.putExtra("post_image", imageUrl);
                break;
            case "ringtone":
                intent = new Intent(this, TmrMusicNewActivity.class);
                intent.putExtra("intentValue", "");
                intent.putExtra("FROM", "");
                break;
            case "bhajan":
                intent = new Intent(this, TmrMusicNewActivity.class);
                intent.putExtra("intentValue", "");
                intent.putExtra("FROM", "Notification");
                break;
            case "comment":
                intent = new Intent(this, GalleryActivity.class);
                intent.putExtra("FROM", "Notification");
                intent.putExtra("post_id", postId);
                intent.putExtra("image_url", imageUrl);
                intent.putExtra("likedByMeType", likedByMeType);
                intent.putExtra("likesByTypes", likesByTypes);
                intent.putExtra("total_likes", totalLikes);
                break;
            default:
                intent = imageUrl != null && !imageUrl.isEmpty()
                        ? new Intent(this, FullScrenActivity.class).putExtra("url", imageUrl)
                        : new Intent(this, MainActivity.class);
                break;
        }
        return intent;


//        Intent intent = new Intent(mContext, CommentActivity.class);
//        intent.putExtra("post_id", post_id);
//        mContext.startActivity(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifications";
            String description = "Notification channel for general notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private Bitmap loadBitmap(String url) {
        if (url == null || url.isEmpty()) {
            Log.e(TAG, "Provided URL is null or empty.");
            return null;
        }

        try (InputStream is = new BufferedInputStream(new URL(url).openStream())) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e(TAG, "Error loading image from URL: " + url, e);
        }
        return null;
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        prefHelper = new PrefHelper(getApplicationContext());
        prefHelper.setFirebaseToken(token);
        MainActivity.saveToken(this, token, "");
    }
}
