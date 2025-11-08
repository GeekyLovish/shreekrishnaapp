package com.raman.kumar.shrikrishan;

import com.raman.kumar.modals.gallary.getGallary.GalleryData;

public interface OnGalleryAdapterListener {
    public void onRefresh(GalleryData data);
    public void openLikesPopup(String post_id);
}
