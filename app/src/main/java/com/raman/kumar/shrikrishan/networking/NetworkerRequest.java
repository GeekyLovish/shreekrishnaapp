package com.raman.kumar.shrikrishan.networking;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.raman.kumar.shrikrishan.CommonUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.content.ContentValues.TAG;


/**
 * Networker class used for api hit request to the server
 */
public class NetworkerRequest {

    private static RequestQueue mRequestQueue;
    private static Context context;
    private static String tag;

    public static void get(String url, final JSONObject header ,final NetworkingCallbackInterface callback) {


        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET, url
                ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response, false);
                        //MyFunctions.toastShort(LoginActivity.this, response);
                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
                callback.onFailure(error);
            }
        }) {

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    byte[] dataByte = response.data;
                    String dataResponse = new String(dataByte);
                    Log.e("Responce", "dataResponse: " + dataResponse);
//                    callback.onSuccess(response, false);
                } catch (OutOfMemoryError e) {
                    System.gc();
                }
                return super.parseNetworkResponse(response);
            }






            @Override
            public String getBodyContentType() {
                return "application/json";
            }


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    Iterator iterator= header.keys();
                    while(iterator.hasNext()) {
                        String key = (String) iterator.next();
                        headers.put(key,header.optString(key));
                    }
                    return headers;
                }
        };

        if (CommonUtil.isNetworkConnectionAvailable(context)) {
            addToRequestQueue(jsonObjRequest,
                    "hello");
        } else {

            callback.onNetworkFailure("Network is not available");
        }

    }


    public static void post(final JSONObject body,final JSONObject header, String url, final NetworkingCallbackInterface callback) {
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, url
                ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response, false);

                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
                callback.onFailure(error);
            }
        }) {


            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                byte[] dataByte = response.data;

                int statusCode = response.statusCode;
                String ss = response.toString();

                String dataResponse = new String(dataByte);
               // callback.onSuccess(response, false);
                return super.parseNetworkResponse(response);
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                Log.e("params", "" + body.toString());
                System.out.println("body"+body.toString());
                params.put("data", body.toString());
                return params;
            }

//            public byte[] getBody() throws AuthFailureError {
//                return body.toString().getBytes();
//            }



            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                if(header!=null) {
                    Iterator iterator = header.keys();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();

                        headers.put(key, header.optString(key));

                    }
                }
                return headers;


            }
        };



        if (CommonUtil.isNetworkConnectionAvailable(context)) {
            addToRequestQueue(jsonObjRequest,
                    "hello");
        } else {
            callback.onNetworkFailure("Network is not available");
        }


    }
    public static void poastReq(final JSONObject body, final String token, final String key, final JSONObject header, String url, final NetworkingCallbackInterface callback){
        StringRequest sr = new StringRequest(Request.Method.POST, url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                callback.onSuccess(response, false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, ""+error.getMessage()+","+error.toString());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", key);
                params.put("fcm_token", token);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type","application/x-www-form-urlencoded");
               // headers.put("abc", "value");
                return headers;
            }
        };
        if (CommonUtil.isNetworkConnectionAvailable(context)) {
            addToRequestQueue(sr,
                    "hello");
        } else {
            callback.onNetworkFailure("Network is not available");
        }

    }



    public static void postResponceHeader(final JSONObject body,final JSONObject header, String url, final NetworkingCallbackInterface callback) {
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, url
                ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
                callback.onFailure(error);
            }
        }) {


            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                byte[] dataByte = response.data;

                int statusCode = response.statusCode;
                String ss = response.toString();

                String dataResponse = new String(dataByte);
                callback.onSuccess(response, false);
                return super.parseNetworkResponse(response);
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                Log.e("params", "" + body.toString());
                params.put("data", body.toString());
                return params;
            }

            public byte[] getBody() throws AuthFailureError {
                return body.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                Iterator iterator = header.keys();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();

                    headers.put(key,header.optString(key));

                }
                return headers;


            }
        };
       /* addToRequestQueue(jsonObjRequest,
                "hello");*/
        addToRequestQueue(jsonObjRequest,
                    "hello");
        if (CommonUtil.isNetworkConnectionAvailable(context)) {
            addToRequestQueue(jsonObjRequest,
                    "hello");
        } else {
            callback.onNetworkFailure("Network is not available");
        }


    }





    public static void put(final JSONObject header, final JSONObject body, String url, final NetworkingCallbackInterface callback, final boolean isDataObject) {
        StringRequest jsonObjRequest = new StringRequest(Request.Method.PUT, url
                ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
                callback.onFailure(error);
            }
        }) {


            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                byte[] dataByte = response.data;
                String dataResponse = new String(dataByte);

                callback.onSuccess(response, false);

                return super.parseNetworkResponse(response);
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                Log.e("params", "" + body.toString());
                if (isDataObject)
                    params.put("data", body.toString());
                else
                    params.put("", body.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                Iterator iterator = header.keys();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    Log.e("params", "key:" + key + ", Value:" + header.optString(key));
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
                return headers;
            }
        };
        addToRequestQueue(jsonObjRequest,
                "hello");

        if (CommonUtil.isNetworkConnectionAvailable(context)) {
            addToRequestQueue(jsonObjRequest,
                    "hello");
        } else {
            callback.onNetworkFailure("Network is not available");
        }


    }






    private static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }

    private static void addToRequestQueue(Request req, String t) {
        // set the default tag if tag is empty
        getRequestQueue().getCache().clear();
        getRequestQueue().getCache().remove(t);
        req.setTag(TextUtils.isEmpty(t) ? tag : t);
        req.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);

    }

    public static void setup(Context c, String t) {
        context = c;
        tag = t;
    }

}

