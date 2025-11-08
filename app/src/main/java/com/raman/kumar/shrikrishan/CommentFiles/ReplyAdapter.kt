package com.raman.kumar.shrikrishan.CommentFiles

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raman.kumar.customClasses.Extensions
import com.raman.kumar.modals.comments.commnetLike.CommentLikeModel
import com.raman.kumar.modals.comments.getAllComments.Reply
import com.raman.kumar.modals.comments.updateComment.UploadCommentModel
import com.raman.kumar.modals.getaModal.DeleteGetaModal
import com.raman.kumar.shrikrishan.PhotoFullPopupWindow
import com.raman.kumar.shrikrishan.R
import com.raman.kumar.shrikrishan.apiNetworking.APICalls
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient
import com.raman.kumar.shrikrishan.databinding.ItemTextBinding
import com.raman.kumar.shrikrishan.util.CommonListeners
import com.raman.kumar.shrikrishan.util.PrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReplyAdapter(
    private val context: Context,
    private val replies: MutableList<Reply>,
    private val prefHelper: PrefHelper,
    private val postId: String,
    private val listener: ReplyAdapterListener
) :
    RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>(), CommonListeners {

    inner class ReplyViewHolder(private val binding: ItemTextBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reply: Reply) {
            val createdAt = Extensions.formatDate(reply.createdAt)
            val id = reply.id.toString() ?: ""
            val profilePic = reply.commentedBy.profilePic.orEmpty()
            val image = reply.image.orEmpty()
            val userName = reply.commentedBy.name.orEmpty()
            val comment = reply.comment.orEmpty().trim()
            val isLikedByMe = reply.likedByMe ?: false
            val likedCount = reply.commentLikesCount


            if (prefHelper.getuRole() == "Administrator") {
                binding.likeButton.visibility = View.VISIBLE
                binding.likeCount.visibility = View.VISIBLE
            } else {
                binding.likeButton.visibility = View.GONE
                binding.likeCount.visibility = View.GONE
            }


            if (isLikedByMe) {
                binding.likeButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.like_solid
                    )
                )
            } else {
                binding.likeButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.like_fb
                    )
                )
            }

            if(reply.image!=null){
                binding.replyIv.visibility=View.VISIBLE
                Glide.with(context).load(image).placeholder(R.drawable.placeholder_image)
                    .into(binding.replyIv)
                binding.replyIv.setOnClickListener(View.OnClickListener { v: View? ->
                    PhotoFullPopupWindow(
                        context,
                        R.layout.popup_photo_full,
                        v,
                        image,
                        null
                    )
                })

            }else{
                binding.replyIv.visibility=View.GONE
            }


            Glide.with(context).load(profilePic).placeholder(R.drawable.ic_account)
                .into(binding.profilePicture)
            binding.userNameTv.text = userName
            binding.replyTv.text = comment
            binding.timeTextView.text = createdAt
            binding.likeCount.text = likedCount.toString()

            binding.likeButton.setOnClickListener(View.OnClickListener { v: View? ->
                println("sfjahjshf.   $postId , $id")
                if (isLikedByMe) {
                    removeCommentLike(postId, id)
                } else {
                    commentLike(postId, id)
                }
            })

            //        holder.lastcomment.setOnClickListener(v -> {
//            sendToReplyActivity(position);
//        });
            binding.root.setOnLongClickListener(View.OnLongClickListener { v: View? ->
                openDialogBox(position, binding)
                false
            })


        }
    }

    private fun openDialogBox(position: Int, binding: ItemTextBinding) {
        val myName = prefHelper.getuName()
        val myRole = prefHelper.getuRole()
        val myUserId = prefHelper.getuId()
        val otherUserId = replies[position].userId.toString()
//        val isBlocked = replies[position].commentedBy.isBlocked

        val colors: Array<CharSequence> = when {
            myRole == "Administrator" -> {
                arrayOf("Delete", "Edit", "Cancel")

            }

            myUserId == otherUserId -> arrayOf("Delete", "Edit", "Cancel")
            else -> return
        }

        println("sahfaisfhjh     myName $myName")
        println("sahfaisfhjh     myRole $myRole")
        println("sahfaisfhjh     myUserId $myUserId")
        println("sahfaisfhjh     otherUserId $otherUserId")
//        println("sahfaisfhjh     isBlocked $isBlocked")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select an Option")
        builder.setItems(colors) { dialog, pos ->
            when (colors[pos]) {
                "Delete" -> deleteComment(dialog, position, binding)
                "Edit" -> {
                    updateComment(position)
                    dialog.dismiss()
                }
//                "Unblock" -> unblockUser(position, dialog)
//                "Block" -> blockUser(position, dialog)
                else -> dialog.dismiss()
            }
        }
        builder.show()
    }


    private fun updateComment(position: Int) {
        val editDialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.edit_comment_dialog_layout)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val commentText = editDialog.findViewById<TextView>(R.id.commentText)
        val editComment = editDialog.findViewById<EditText>(R.id.editComment)
        val okButton = editDialog.findViewById<Button>(R.id.okButton)
        val cancelButton = editDialog.findViewById<Button>(R.id.cancelButton)

        commentText.text = replies[position].comment

        okButton.setOnClickListener {
            println("sahfaisfhjh   Clicked on Edit")

            APICalls.editComment(
                context,
                replies[position].id.toString(),
                editComment.text.toString().trim(),
                position,
                editDialog,
                this
            )
        }

        cancelButton.setOnClickListener {
            editDialog.dismiss()
        }

        editDialog.show()
    }

    override fun onCommentEdited(position: Int, response: Response<UploadCommentModel>) {
        replies.removeAt(position)
        notifyDataSetChanged()
        notifyItemRemoved(position)
//                        context.isCommentAdded = true
        if (listener != null) {
            listener.onRequestRefreshComments(postId)
        }
        notifyItemChanged(position)
        notifyDataSetChanged()
    }

    override fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

    }

    private fun deleteComment(dialog: DialogInterface?, position: Int, binding: ItemTextBinding) {
        if (dialog != null) {
            deleteComment(postId, replies[position].id.toString(), position, dialog)
        }
    }

    private fun deleteComment(
        postId: String,
        commentId: String,
        position: Int,
        dialog: DialogInterface
    ) {
        val call = RetrofitClient
            .getInstance()
            .api
            .deleteComment("application/json", Extensions.getBearerToken(), commentId)

        call.enqueue(object : Callback<DeleteGetaModal> {
            override fun onResponse(
                call: Call<DeleteGetaModal>,
                response: Response<DeleteGetaModal>
            ) {
                val likeResponse = response.body()
                if (response.isSuccessful) {
                    if (likeResponse?.status == true) {
                        replies.removeAt(position)
                        notifyDataSetChanged()
                        notifyItemRemoved(position)
                        dialog.dismiss()
                        Log.d("msg", likeResponse.message)
                    } else {
                        Toast.makeText(context, likeResponse?.message.orEmpty(), Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeleteGetaModal>, t: Throwable) {
                Toast.makeText(context, "Failed to load!", Toast.LENGTH_LONG).show()
                Log.d("error", "Message: ${t.message}")
            }
        })
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTextBinding.inflate(inflater, parent, false)
        return ReplyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        holder.bind(replies[position])
    }

    override fun getItemCount(): Int = replies.size


    fun removeCommentLike(postId: String, commentId: String) {
        val call = RetrofitClient
            .getInstance()
            .api
            .deleteCommentLike("application/json", Extensions.getBearerToken(), postId, commentId)

        call.enqueue(object : Callback<DeleteGetaModal> {
            override fun onResponse(
                call: Call<DeleteGetaModal>,
                response: Response<DeleteGetaModal>
            ) {
                val likeResponse = response.body()
                if (response.isSuccessful) {
                    if (likeResponse?.status == true) {
                        if (listener != null) {
                            listener.onRequestRefreshComments(postId)
                        }
                        notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            context,
                            likeResponse?.message ?: "Unknown error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeleteGetaModal>, t: Throwable) {
                Toast.makeText(context, "Failed to load!", Toast.LENGTH_LONG).show()
                Log.d("error", "Message: ${t.message}")
            }
        })
    }


    fun commentLike(postId: String, commentId: String) {
        val call = RetrofitClient
            .getInstance()
            .api
            .commentLike(
                "application/json",
                Extensions.getBearerToken(),
                postId,
                commentId,
                "like"
            )

        call.enqueue(object : Callback<CommentLikeModel> {
            override fun onResponse(
                call: Call<CommentLikeModel>,
                response: Response<CommentLikeModel>
            ) {
                val likeResponse = response.body()
                if (response.isSuccessful) {
                    likeResponse?.let {
                        if (it.status) {
                            if (listener != null) {
                                listener.onRequestRefreshComments(postId)
                            }
                            notifyDataSetChanged()
                        } else {
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CommentLikeModel>, t: Throwable) {
                Toast.makeText(context, "Failed to load!", Toast.LENGTH_LONG).show()
                Log.d("error", "Message: ${t.message}")
            }
        })
    }


    interface ReplyAdapterListener {
        fun onRequestRefreshComments(postId: String?)
    }
}
