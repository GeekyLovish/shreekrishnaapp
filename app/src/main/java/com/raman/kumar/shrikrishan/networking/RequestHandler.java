package com.raman.kumar.shrikrishan.networking;

import android.app.Activity;
import android.content.Context;

import org.json.JSONObject;

/**
 * Created by mann on 3/11/17.
 */

public class RequestHandler {

public static void login(Activity context, JSONObject body, NetworkingCallbackInterface networkingCallbackInterface){
    String url="http://192.168.1.130:8080/MT-Collect_Gateway/ServiceController";
    NetworkerRequest.setup(context,"offer");
    NetworkerRequest.post(body,null,url,networkingCallbackInterface);
}


    public static void getAllTopics(Activity context, JSONObject body, NetworkingCallbackInterface networkingCallbackInterface){
        String url="https://ramankumarynr.com/api/get_recent_posts/";
        NetworkerRequest.setup(context,"topic");
        NetworkerRequest.get(url,body,networkingCallbackInterface);
    }

    public static void getAllText(Activity context, JSONObject body, NetworkingCallbackInterface networkingCallbackInterface){
        NetworkerRequest.setup(context,"aarti");
        String url="https://ramankumarynr.com/api/core/get_category_posts/?id=4&count=50";
        NetworkerRequest.get(url,body,networkingCallbackInterface);
    }

    public static void getGeetaDetails(Activity context, JSONObject body, NetworkingCallbackInterface networkingCallbackInterface){
        String url="https://ramankumarynr.com/api/core/get_category_posts/?id=5&count=50";
        NetworkerRequest.setup(context,"Geeta");
        NetworkerRequest.get(url,body,networkingCallbackInterface);
    }

    public static void getAllImages(Activity context, JSONObject body, NetworkingCallbackInterface networkingCallbackInterface){
        String url="https://ramankumarynr.com/api/get_category_posts/?id=7&count=500";
        NetworkerRequest.setup(context,"Images");
        NetworkerRequest.get(url,body,networkingCallbackInterface);
    }

    public static void getAllAudios(String url,Activity context, JSONObject body, NetworkingCallbackInterface networkingCallbackInterface){
       // String url="http://ramankumarynr.com/api/?json=get_post&id=598";
        NetworkerRequest.setup(context,"Audios");
        NetworkerRequest.get(url,body,networkingCallbackInterface);
    }
    public static void getGalleryImages(Activity context, JSONObject body, NetworkingCallbackInterface networkingCallbackInterface){
        String url="https://ramankumarynr.com/api/get_category_posts/?id=8&count=500";
        NetworkerRequest.setup(context,"Gallery");
        NetworkerRequest.get(url,body,networkingCallbackInterface);
    }
    public static void getNewGalleryImages(String url,Activity context, JSONObject body, NetworkingCallbackInterface networkingCallbackInterface){
       // String url="http://ramankumarynr.com/api/core/get_category_posts/?id=9&count=1000";
        NetworkerRequest.setup(context,"NewGallery");
        NetworkerRequest.get(url,body,networkingCallbackInterface);
    }
    public static void getVideo(String url,Activity context, JSONObject body, NetworkingCallbackInterface networkingCallbackInterface){
      //  String url="http://ramankumarynr.com/api/get_category_posts/?id=8";
        NetworkerRequest.setup(context,"Gallery");
        NetworkerRequest.get(url,body,networkingCallbackInterface);
    }
    public static void saveToken(Context context, String token, String key, JSONObject body, NetworkingCallbackInterface networkingCallbackInterface){
        String url="https://ramankumarynr.com/?requestType=savetoken";
        NetworkerRequest.setup(context,"fcm_token");
        NetworkerRequest.poastReq(body,token,key,null,url,networkingCallbackInterface);
    }
}
