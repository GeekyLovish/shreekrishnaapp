package com.raman.kumar.shrikrishan.apiNetworking;

import com.raman.kumar.AudiosModal.AudiosModal;
import com.raman.kumar.modals.audio.PostAudioModal;
import com.raman.kumar.modals.authentication.AuthModal.RegisterModal;
import com.raman.kumar.modals.authentication.loginMobile.LoginMobileModel;
import com.raman.kumar.modals.authentication.verifyOtp.VerifyOtpModel;
import com.raman.kumar.modals.comments.commnetLike.CommentLikeModel;
import com.raman.kumar.modals.comments.getAllComments.AllComentsModel;
import com.raman.kumar.modals.comments.postComment.PostComentsModel;
import com.raman.kumar.modals.comments.postLike.PostLikeModel;
import com.raman.kumar.modals.comments.updateComment.UploadCommentModel;
import com.raman.kumar.modals.comments.whoLikes.WhoLikeModel;
import com.raman.kumar.modals.gallary.getGallary.GalleryModal;
import com.raman.kumar.modals.gallary.postAmritGallary.PostAmirtGallaryModal;
import com.raman.kumar.modals.pictureByParts.getPictureByPart.GetPictureByPartModal;
import com.raman.kumar.modals.pictureByParts.pictureByPart.PictureByPartModal;
import com.raman.kumar.modals.pictureByParts.postPictureByPart.PostPartImageModel;
import com.raman.kumar.modals.video.EditVideo.EditVideoModal;
import com.raman.kumar.modals.video.GetVideoModal.GetVideoModal;
import com.raman.kumar.modals.getaModal.getGeetaModal.GetGeetaModal;
import com.raman.kumar.getWallPaper.GetWallpaperModal;
import com.raman.kumar.modals.getaModal.DeleteGetaModal;
import com.raman.kumar.modals.getaModal.createGeta.CreateGetaModal;
import com.raman.kumar.modals.authentication.login.LoginModal;
import com.raman.kumar.shrikrishan.model.CodeResponse;
import com.raman.kumar.shrikrishan.model.CommentImageResponse;
import com.raman.kumar.shrikrishan.model.CommentRepliesResponse;
import com.raman.kumar.shrikrishan.model.CommonResponse;
import com.raman.kumar.shrikrishan.model.ForgetPassResponse;
import com.raman.kumar.shrikrishan.model.UpdateProfileResponse;
import com.raman.kumar.shrikrishan.model.UploadByPartsResponse;
import com.raman.kumar.uploadWallpaper.UploadWallpaperModel;

import java.util.Map;

import javax.annotation.Nullable;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    //Authentication APIs

    @FormUrlEncoded
    @POST("register")
    Call<RegisterModal> signUp(
            @Field("name") String full_name,
            @Field("email") String email,
            @Field("password") String phone_no,
            @Field("phone_number") String password,
            @Field("device_token") String device_token);

    @Multipart
    @POST("update-profile")
    Call<UpdateProfileResponse> updateProfile(
            @HeaderMap Map<String, String> token,
            @Part("user_id") RequestBody user_id,
            @Part("name") RequestBody username,
            @Part MultipartBody.Part image);

    @Multipart
    @POST("update-profile")
    Call<UpdateProfileResponse> updateProfilePic(
            @HeaderMap Map<String, String> token,
            @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("login")
    Call<LoginModal> login(
            @Field("email") String email,
            @Field("password") String password,
            @Field("device_token") String device_token);

//    @FormUrlEncoded
//    @POST("login_mobile")
//    Call<PhoneLoginResponse> phoneLogin(
//            @Field("phone_number") String phone_number);

    @FormUrlEncoded
    @POST("login/mobile")
    Call<LoginMobileModel> phoneLogin(
            @Field("phone_number") String phone_number);


    @FormUrlEncoded
    @POST("login/mobile/verify-otp")
    Call<VerifyOtpModel> verifyOTP(
            @Field("id") String id,
            @Field("device_token") String device_token,
            @Field("otp") String otp);

//    @FormUrlEncoded
//    @POST("login_mobile_otp")
//    Call<VerifyPhoneResponse> verifyPhone(
//            @Field("user_id") String phone_number,
//            @Field("otp") String otp,
//            @Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("forget-password")
    Call<ForgetPassResponse> forget_pass(
            @Field("email") String email);


    @GET("getcode")
    Call<CodeResponse> getCode();


//    @FormUrlEncoded
//    @POST("getUser")
//    Call<LoginResponse> getUser(@HeaderMap Map<String,String> token,
//            @Field("user_id") String user_id);

//    @GET("get_all_images")
//    Call<GetAllImages> getAllImages();

    //Admin Panel APIs

    //Amrit APIs (Add, update, get, delete)

    @Multipart
    @POST("posts")
    Call<PostAmirtGallaryModal> postAmritGalleryImage(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Part("title") RequestBody title,
            @Part MultipartBody.Part url,
            @Part("type") RequestBody type,
            @Part("content") RequestBody content,
            @Part("link_type") RequestBody linkType,
            @Part("link") RequestBody link);


    @DELETE("posts/{id}")
    Call<DeleteGetaModal> deleteAmritGalleryImage(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id);

    @Multipart
    @POST("posts/{id}")
    Call<PostAmirtGallaryModal> updateAmritGalleryImage(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id,
            @Part("title") RequestBody title,
            @Part MultipartBody.Part url,
            @Part("type") RequestBody type,
            @Part("content") RequestBody content,
            @Part("link_type") RequestBody linkType,
            @Part("link") RequestBody link,
            @Part("_method") RequestBody method);

    //ImagesByParts APIs (Add, update, get, delete)

    @Multipart
    @POST("parts/pictures")
    Call<PostPartImageModel> postImageByParts(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Part("part_id") RequestBody id,
            @Part MultipartBody.Part url);

    @Multipart
    @POST("parts/pictures/{id}")
    Call<UploadByPartsResponse> updateImageByParts(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id,
            @Part("part_id") RequestBody part_id,
            @Part("_method") RequestBody method,
            @Part MultipartBody.Part url);


    @DELETE("parts/pictures/{id}")
    Call<DeleteGetaModal> deleteImageByParts(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id);

    // Videos APIs (Add, update, get, delete)


    //"application/json", Extensions.getBearerToken(),
    @Multipart
    @POST("audios")
    Call<PostAudioModal> postAudio(
            @Header("Accept") String Accept,
            @Header("Authorization") String Authorization,
            @Part("title") RequestBody title,
            @Part("displayNAme") RequestBody content,
            @Part("type") RequestBody gallery,
            @Part("position") RequestBody amrit,
            @Part("duration") RequestBody byParts,
            @Part MultipartBody.Part file);

    @Multipart
    @POST("audios/{id}")
    Call<PostAudioModal> updateAudio(
            @Header("Accept") String Accept,
            @Header("Authorization") String Authorization,
            @Path("id") String id,
            @Part("title") RequestBody title,
            @Part("displayNAme") RequestBody content,
            @Part("type") RequestBody gallery,
            @Part("position") RequestBody amrit,
            @Part("duration") RequestBody byParts,
            @Part MultipartBody.Part file,
            @Part("_method") RequestBody method
    );


    @DELETE("audios/{id}")
    Call<DeleteGetaModal> deleteAudio(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id);

    // Videos APIs (Add, update, get, delete)


    @FormUrlEncoded
    @POST("videos")
    Call<EditVideoModal> postVideo(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Field("title") String title,
            @Field("url") String url);

    @FormUrlEncoded
    @PUT("videos/{id}")
    Call<EditVideoModal> updateVideo(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id,
            @Field("title") String title,
            @Field("url") String url);


    @DELETE("videos/{id}")
    Call<DeleteGetaModal> deleteVideo(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id);

    // Aarti APIs (Add, update, get, delete)

    @FormUrlEncoded
    @POST("aarti")
    Call<CreateGetaModal> postAarti(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Field("title") String title,
            @Field("content") String content);

    @FormUrlEncoded
    @PUT("aarti/{id}")
    Call<CreateGetaModal> updateAarti(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id,
            @Field("title") String title,
            @Field("content") String content);


    @DELETE("aarti/{id}")
    Call<DeleteGetaModal> deleteAarti(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id);

    // Geeta APIs (Add, update, get, delete)
    @FormUrlEncoded
    @POST("geeta-parts")
    Call<CreateGetaModal> postGeeta(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Field("title") String title,
            @Field("content") String content);

    @FormUrlEncoded
    @PUT("geeta-parts/{id}")
    Call<CreateGetaModal> updateGeeta(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id,
            @Field("title") String title,
            @Field("content") String content);


    @DELETE("geeta-parts/{id}")
    Call<DeleteGetaModal> deleteGeeta(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id);

    //Wallpaper APIs

    @Multipart
    @POST("wallpapers")
    Call<UploadWallpaperModel> postWallpaper(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Part("name") RequestBody name,
            @Part MultipartBody.Part image);

    // User Panel

    //Like APIs

    @FormUrlEncoded
    @POST("posts/{id}/likes")
    Call<PostLikeModel> likePost(@HeaderMap Map<String, String> token,
                                 @Path("id") String id,
                                 @Field("type") String type);

//    @FormUrlEncoded
//    @POST("is_like")
//    Call<LikePostResponse> isliked(@HeaderMap Map<String,String> token,
//                                 @Field("user_id") String user_id,
//                                 @Field("post_id") String id);


    @DELETE("posts/{id}/likes")
    Call<DeleteGetaModal> removeLike(@HeaderMap Map<String, String> token,
                                     @Path("id") String post_id);


    @GET("posts/{id}/likes")
    Call<WhoLikeModel> likes(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id);

    //Comment APIs

    @GET("posts/comments/{id}")
    Call<AllComentsModel> getComments(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id);


    @Multipart
    @POST("posts/comments")
    Call<PostComentsModel> postComment(@HeaderMap Map<String, String> token,
                                       @Part("post_id") RequestBody post_id,
                                       @Part("comment") RequestBody comment,
                                       @Nullable @Part MultipartBody.Part image,
                                       @Part("user_id") RequestBody userId,
                                       @Part("user_token") RequestBody user_token);

    @Multipart
    @POST("uploadimages")
    Call<CommentImageResponse> addCommentImage(
            @HeaderMap Map<String, String> token,
            @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("comment_like")
    Call<CommonResponse> commentlike(@HeaderMap Map<String, String> token,
                                     @Field("user_id") String user_id,
                                     @Field("post_id") String post_id,
                                     @Field("comment_id") String comment_id,
                                     @Field("type") String type);

    //    @FormUrlEncoded
//    @POST("delete_comment")
//    Call<CommonResponse> deleteComment(
//            @HeaderMap Map<String,String> token,
//            @Field("post_id") String post_id,
//            @Field("comment_id") String comment_id);
    @DELETE("posts/comments/{id}")
    Call<DeleteGetaModal> deleteComment(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id);

//    "application/json", Extensions.getBearerToken()

    @DELETE("comments/likes/{post_id}/{comment_id}")
    Call<DeleteGetaModal> deleteCommentLike(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("post_id") String post_id,
            @Path("comment_id") String comment_id);

    @FormUrlEncoded
    @POST("comments/likes")
    Call<CommentLikeModel> commentLike(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Field("post_id") String post_id,
            @Field("comment_id") String comment_id,
            @Field("type") String type);

    @FormUrlEncoded
    @PUT("posts/comments/{id}")
    Call<UploadCommentModel> editComment(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Path("id") String id,
            @Field("comment") String comment);



    @Multipart
    @POST("posts/replies")
    Call<UploadCommentModel> postReply(@HeaderMap Map<String, String> token,
                                       @Part("post_id") RequestBody post_id,
                                       @Part("comment") RequestBody comment,
                                       @Nullable @Part MultipartBody.Part image,
                                       @Part("comment_id") RequestBody userId);

    @FormUrlEncoded
    @POST("get_replycomment")
    Call<CommentRepliesResponse> getCommentReplies(@HeaderMap Map<String, String> token,
                                                   @Field("post_id") String post_id,
                                                   @Field("comment_id") String comment_id);

    @FormUrlEncoded
    @POST("delete_replycomment")
    Call<CommonResponse> deleteCommentReply(@HeaderMap Map<String, String> token,
                                            @Field("id") String reply_id,
                                            @Field("comment_id") String comment_id);

    @FormUrlEncoded
    @POST("block-user")
    Call<DeleteGetaModal> blockUnblockUser(
            @Header("Accept") String acceptHeader,
            @Header("Authorization") String Authorization,
            @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("edit_replycomment")
    Call<CommentRepliesResponse> editCommentReply(@HeaderMap Map<String, String> token,
                                                  @Field("post_id") String post_id,
                                                  @Field("comment_id") String comment_id,
                                                  @Field("reply_id") String reply_id,
                                                  @Field("comment") String comment,
                                                  @Field("user_token") String user_token,
                                                  @Field("user_id") String user_id);


    //get APIs

    @GET("posts")
    Call<GalleryModal> getAllAmritGalleryImages(
            @Query("page") int page,
            @Query("per_page") int perPage);


    @GET("posts/amrit")
    Call<GalleryModal> getAmrit(
            @Header("Authorization") String Authorization,
            @Query("page") int page,
            @Query("per_page") int perPage);


    @GET("posts/gallery")
    Call<GalleryModal> getGallery(
            @Header("Authorization") String Authorization,
            @Query("page") int page,
            @Query("per_page") int perPage);

    @GET("aarti")
    Call<GetGeetaModal> getAarti(
            @Query("page") int page,
            @Query("per_page") int perPage);

    @GET("geeta-parts")
    Call<GetGeetaModal> getGeeta(
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    @GET("videos")
    Call<GetVideoModal> getVideos(
            @Query("page") int page,
            @Query("per_page") int perPage);

    @GET("audios")
    Call<AudiosModal> getAllAudios(
            @Header("Accept") String acceptHeader,
            @Query("type") String type,
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    @GET("wallpapers")
    Call<GetWallpaperModal> getWallpapers(
            @Query("page") int page,
            @Query("per_page") int perPage);

    @GET("parts/pictures")
    Call<PictureByPartModal> getAllImagesByParts();

//    @GET("get_byPart")
//    Call<CommonResponse> getByParts();


    @GET("parts/{id}/pictures")
    Call<PictureByPartModal> getBySection(
            @Path("id") String id,
            @Query("page") int page,
            @Query("per_page") int perPage);

    @GET("parts")
    Call<GetPictureByPartModal> getPictureByPart(
            @Query("page") int page,
            @Query("per_page") int perPage);

}
