package com.arges.sepan.argmusicplayer.Views;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;


public class SquareHeightImageView extends androidx.appcompat.widget.AppCompatImageView {
    public SquareHeightImageView(Context context) {
        super(context);
    }
    public SquareHeightImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareHeightImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
