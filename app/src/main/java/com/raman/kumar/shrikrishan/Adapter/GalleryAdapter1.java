package com.raman.kumar.shrikrishan.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.raman.kumar.modals.pictureByParts.pictureByPart.Datum;
import com.raman.kumar.shrikrishan.PhotoFullPopupWindow;
import com.raman.kumar.shrikrishan.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class GalleryAdapter1 extends RecyclerView.Adapter<GalleryAdapter1.MyViewHolder> {
    private List<Datum> imageList;
    private Context mContext;
    private FragmentTransaction ft;
    private ShareFacebook shareFacebook;
    private ProgressDialog progressDialog;
    private ProgressDialog newProgressDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textview1;
        ImageView imageView;
        LinearLayout countLayout;
        RelativeLayout learnMoreRelativeLayout;
        TextView likeTextView, title, shriKrishnaTextView;
        ImageView userAvatar;
        FrameLayout likeLayout;
        AdView adView;
        private ItemClickListener clickListener;
        private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;

        public MyViewHolder(View view) {
            super(view);
            textview1 = view.findViewById(R.id.title);
            imageView = view.findViewById(R.id.wallpaper_image);
            countLayout = view.findViewById(R.id.countLayout);
            likeTextView = view.findViewById(R.id.likeTextView);
            title = view.findViewById(R.id.title);
            learnMoreRelativeLayout = view.findViewById(R.id.learnMoreRelativeLayout);
            userAvatar = view.findViewById(R.id.userAvatar);
            shriKrishnaTextView = view.findViewById(R.id.shriKrishnaTextView);
            likeLayout = view.findViewById(R.id.likeLayout);
            adView = view.findViewById(R.id.adView);
            imageView.setOnClickListener(this);

            // Hiding unnecessary views
            countLayout.setVisibility(View.GONE);
            userAvatar.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            likeTextView.setVisibility(View.GONE);
            likeLayout.setVisibility(View.GONE);
            shriKrishnaTextView.setVisibility(View.GONE);
            learnMoreRelativeLayout.setVisibility(View.GONE);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

    public GalleryAdapter1(List<Datum> imageList, Context mContext, FragmentTransaction ft) {
        this.imageList = imageList;
        this.mContext = mContext;
        this.ft = ft;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String imageUrl = imageList.get(position).getUrl();

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imageView);

        holder.imageView.setBackgroundColor(mContext.getResources().getColor(R.color.black));
        holder.imageView.setOnClickListener(v -> new PhotoFullPopupWindow(mContext, R.layout.popup_photo_full, v, imageUrl, null));

        holder.imageView.setOnLongClickListener(v -> {
            long a = System.currentTimeMillis();
            showDialog(imageUrl, String.valueOf(a));
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public void setImageListener(ShareFacebook shareFacebook) {
        this.shareFacebook = shareFacebook;
    }

    public void showDialog(final String imageUrl, final String title) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_downloadshare);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView download = dialog.findViewById(R.id.download);
        ImageView shareButton = dialog.findViewById(R.id.fb_share_button);

        download.setOnClickListener(v -> {
            System.out.println("sfklsafjsklafj     Entered download");
            System.out.println("sfklsafjsklafj    Storage Permission Granted: " + isStoragePermissionGranted());
            if (isStoragePermissionGranted()) {
                System.out.println("Entered download");
                dialog.dismiss();  // Assuming `dialog` is a ProgressDialog or other UI element
                shareFacebook.downloadImageFromURL(imageUrl, title);
            } else {
                requestStoragePermission();  // Request permission if not granted
            }
        });

        shareButton.setOnClickListener(v -> {
            System.out.println("sfklsafjsklafj     Entered shareButton");
            newProgressDialog = shareFacebook.showProgress();
            shareFacebook.shareImage(imageUrl, title, newProgressDialog);
            dialog.dismiss();
        });

        dialog.show();
    }

    // Add this method to your GalleryActivity1 or Fragment where you're calling the adapter
    // Trigger permission request
    public void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(mContext, "Storage permission is needed to download images.", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MyViewHolder.STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    // Modify the isStoragePermissionGranted() method:
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            return mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true; // No need to check on pre-M (Marshmallow) devices
        }
    }

    public interface ShareFacebook {
        void shareImage(String image, String title, ProgressDialog dialog);
        ProgressDialog showProgress();
        void dismissProgressDialog(ProgressDialog progressDialog);
        void downloadImageFromURL(String image, String title);
    }
}
