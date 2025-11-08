package com.raman.kumar.shrikrishan.util

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.raman.kumar.modals.comments.postComment.PostComentsModel
import com.raman.kumar.modals.comments.updateComment.UploadCommentModel
import com.raman.kumar.shrikrishan.model.CommentImageResponse
import retrofit2.Response
import kotlin.reflect.jvm.internal.impl.incremental.components.Position

interface CommonListeners {
    fun onCameraClick(bitmap: Bitmap, url: ByteArray) {}
    fun onGalleryClick(bitmap: Bitmap, url: ByteArray) {}
    fun onCommentAdded(response: Response<PostComentsModel>) {}
    fun onCommentFailed() {}
    fun onReplyAdded(response: Response<UploadCommentModel>) {}
    fun onReplyFailure() {}
    fun onCommentEdited(position: Int, response: Response<UploadCommentModel>) {}
    fun onActivityResults(requestCode: Int, resultCode: Int, data: Intent) {}
    fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    )
}