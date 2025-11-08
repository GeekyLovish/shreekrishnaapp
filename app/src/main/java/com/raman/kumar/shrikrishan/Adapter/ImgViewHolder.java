package com.raman.kumar.shrikrishan.Adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.raman.kumar.shrikrishan.R;

/**
 * Created by Dell- on 3/21/2018.
 */

public class ImgViewHolder extends RecyclerView.ViewHolder {

    public TextView nameView;
    public ImageView imageView;

    public ImgViewHolder(View itemView) {
        super(itemView);

        nameView = (TextView) itemView.findViewById(R.id.tv_img_name);
        imageView = (ImageView) itemView.findViewById(R.id.img_view);
    }
}