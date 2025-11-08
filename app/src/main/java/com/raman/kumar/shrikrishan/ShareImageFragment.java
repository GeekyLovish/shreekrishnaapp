package com.raman.kumar.shrikrishan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

public class ShareImageFragment extends Fragment {
    private static final String TAG = ShareImageFragment.class.getName();
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Facebook SDK
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        // Create a callbackManager to handle the login responses.
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_share, container, false);
        ShareButton shareButton = v.findViewById(R.id.fb_share_button);
        progress = new ProgressDialog(getActivity());
        showProgressDialog();

        if (getArguments() != null) {
            String image = getArguments().getString("image");
            setImageShare(image);
        }

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setImageShare(String imageUrl) {
        if (isValidUrl(imageUrl)) {
            new ImageDownloadTask().execute(imageUrl);
        } else {
            Log.e(TAG, "Invalid URL: " + imageUrl);
            progress.dismiss();
        }
    }

    // AsyncTask to handle image downloading
    private class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap imageBitmap = null;
            try {
                String imageUrl = params[0];
                // Try to fetch the image from the URL
                URL url = new URL(imageUrl);
                imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                // Handle malformed URL exception
                Log.e(TAG, "MalformedURLException: Invalid URL format", e);
            } catch (IOException e) {
                // Handle IO exception
                Log.e(TAG, "IOException: Error downloading image", e);
            } catch (Exception e) {
                // Catch other exceptions
                Log.e(TAG, "Exception: Unexpected error", e);
            }
            return imageBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap imageBitmap) {
            super.onPostExecute(imageBitmap);
            if (imageBitmap != null) {
                shareImageOnFacebook(imageBitmap);  // Share the image if downloaded successfully
            } else {
                Log.e(TAG, "Failed to download image");
                progress.dismiss();  // Dismiss progress dialog if image download fails
            }
        }
    }

    private void shareImageOnFacebook(Bitmap imageBitmap) {
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            shareDialog = new ShareDialog(this);
            shareDialog.registerCallback(callbackManager, callback);

            // Create a SharePhoto with the downloaded image
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(imageBitmap)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            // Show the share dialog with the image content
            shareDialog.show(content);
        } else {
            // If Facebook sharing is not available, fall back to ShareLinkContent
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("hi")
                    .setContentDescription("hi")
                    .build();
            shareDialog.show(linkContent);
        }
        progress.dismiss();
    }

    private boolean isValidUrl(String urlString) {
        try {
            new URL(urlString); // Try to create a new URL object
            return true;
        } catch (MalformedURLException e) {
            Log.e(TAG, "Invalid URL: " + urlString, e);
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass login result to the callback manager
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            Log.v(TAG, "Successfully posted");
            // Handle success
        }

        @Override
        public void onCancel() {
            Log.v(TAG, "Sharing cancelled");
            // Handle cancel
        }

        @Override
        public void onError(FacebookException error) {
            Log.v(TAG, "Error: " + error.getMessage());
            // Handle error
        }
    };

    private void showProgressDialog() {
        progress.setTitle("Loading");
        progress.setMessage("Please wait while loading...");
        progress.setCancelable(false); // Disable dismiss by tapping outside
        progress.show();
    }
}
