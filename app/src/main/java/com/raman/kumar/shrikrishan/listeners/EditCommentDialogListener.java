package com.raman.kumar.shrikrishan.listeners;

import com.raman.kumar.modals.comments.getAllComments.Datum;
import com.raman.kumar.shrikrishan.util.CommonListeners;

public interface EditCommentDialogListener {
    void openEditCommentDialog(int  position, Datum commentData, CommonListeners listeners);
}
