package com.raman.kumar.shrikrishan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.raman.kumar.shrikrishan.networking.NetworkingCallbackInterface;
import com.raman.kumar.shrikrishan.networking.RequestHandler;
import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mann on 26/10/17.
 */

public class LoginActivity extends AppCompatActivity {
    Button email_sign_in_button,tryAsGuest;
    LinearLayout mainLay;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        email_sign_in_button = (Button) findViewById(R.id.sign_in);
        tryAsGuest=(Button)findViewById(R.id.tryAsGuest);
        mainLay=(LinearLayout)findViewById(R.id.mainLay);

        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
        tryAsGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);

            }
        });



        // To maintain FB Login session

    }

    // When Post Status Update button is clicked





    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @SuppressLint("NewApi")
    public Bitmap transform(Bitmap bitmap) {
        // Create another bitmap that will hold the results of the filter.
        Bitmap blurredBitmap = Bitmap.createBitmap(bitmap);
        android.renderscript.RenderScript rs = android.renderscript.RenderScript.create(getApplicationContext());
        try {
            // Allocate memory for Renderscript to work with
            android.renderscript.Allocation input = android.renderscript.Allocation.createFromBitmap(rs, bitmap, android.renderscript.Allocation.MipmapControl.MIPMAP_FULL,
                    android.renderscript.Allocation.USAGE_SHARED);
            android.renderscript.Allocation output = android.renderscript.Allocation.createTyped(rs, input.getType());
            // Load up an instance of the specific script that we want to use.
            android.renderscript.ScriptIntrinsicBlur script = android.renderscript.ScriptIntrinsicBlur.create(rs, android.renderscript.Element.U8_4(rs));
            script.setInput(input);
            // Set the blur radius
            script.setRadius(10);
            // Start the ScriptIntrinisicBlur
            script.forEach(output);
            // Copy the output to the blurred bitmap
            output.copyTo(blurredBitmap);
            // bitmap.recycle();
        } catch (Exception ex) {
            System.out.println("error in userprofilefragment class :" + ex);
        }
        return blurredBitmap;
    }
    public void getTopics() {

        JSONObject json =new JSONObject();
        try {

            json.put("svcName", "fieldagent");
            json.put("svcMethod", "getOfferList");
            json.put("requestType", "data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestHandler.getAllTopics(this,json, new NetworkingCallbackInterface() {
            @Override
            public void onSuccess(NetworkResponse response, boolean fromCache) {
              System.out.print("response........"+response);

            }

            @Override
            public void onSuccess(String response, boolean fromCache) {
                System.out.print("response........"+response);
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }

            @Override
            public void onFailure(VolleyError error) {

            }

            @Override
            public void onNetworkFailure(String error) {

            }
        });
    }
    public void callMethod() {

        JSONObject json =new JSONObject();
        try {

            json.put("svcName", "fieldagent");
            json.put("svcMethod", "getOfferList");
            json.put("requestType", "data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestHandler.login(this,json, new NetworkingCallbackInterface() {
            @Override
            public void onSuccess(NetworkResponse response, boolean fromCache) {

            }

            @Override
            public void onSuccess(String response, boolean fromCache) {

            }

            @Override
            public void onFailure(VolleyError error) {

            }

            @Override
            public void onNetworkFailure(String error) {

            }
        });
    }
}
