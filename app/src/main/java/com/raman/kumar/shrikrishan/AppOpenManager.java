package com.raman.kumar.shrikrishan;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.appopen.AppOpenAd;

public class AppOpenManager implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "AppOpenManager";
    private static final String AD_UNIT_ID = "ca-app-pub-8877546392246070/5343449685";
    private final Application application;
    private AppOpenAd appOpenAd = null;
    private Activity currentActivity = null;
    private boolean isShowingAd = false;
    private boolean isAdLoading = false;

    public AppOpenManager(Application application) {
        this.application = application;

        // Register for app lifecycle
        application.registerActivityLifecycleCallbacks(this);

        // Listen for app foreground
        ProcessLifecycleOwner.get().getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_START) {
                Log.d(TAG, "App entered foreground");
                showAdIfAvailable();
            }
        });

        // Start loading the first ad
        loadAd(application.getApplicationContext());
    }

    private void loadAd(Context context) {
        if (isAdLoading || appOpenAd != null) {
            return;
        }

        isAdLoading = true;
        Log.d(TAG, "Loading App Open Ad...");

        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(context, AD_UNIT_ID, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        isAdLoading = false;
                        Log.d(TAG, "App Open Ad loaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull com.google.android.gms.ads.LoadAdError error) {
                        isAdLoading = false;
                        Log.e(TAG, "App Open Ad failed to load: " + error.getMessage());
                    }
                });
    }

    public void showAdIfAvailable() {
        if (isShowingAd) {
            Log.d(TAG, "Ad is already showing");
            return;
        }

        if (appOpenAd == null) {
            Log.d(TAG, "Ad not ready yet");
            loadAd(application.getApplicationContext());
            return;
        }

        if (currentActivity == null) {
            Log.d(TAG, "Current activity is null");
            return;
        }

        Log.d(TAG, "Showing App Open Ad");
        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                isShowingAd = true;
                Log.d(TAG, "Ad is showing");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                appOpenAd = null;
                isShowingAd = false;
                Log.d(TAG, "Ad dismissed");
                loadAd(application.getApplicationContext());
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                Log.e(TAG, "Ad failed to show: " + adError.getMessage());
                isShowingAd = false;
                appOpenAd = null;
                loadAd(application.getApplicationContext());
            }
        });

        // Add delay if needed
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (appOpenAd != null && currentActivity != null) {
                appOpenAd.show(currentActivity);
            }
        }, 200);
    }

    // ActivityLifecycleCallbacks
    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
        Log.d(TAG, "Current activity set: " + activity.getLocalClassName());
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {}
    @Override
    public void onActivityStarted(@NonNull Activity activity) {}
    @Override
    public void onActivityPaused(@NonNull Activity activity) {}
    @Override
    public void onActivityStopped(@NonNull Activity activity) {}
    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}
    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}
}
