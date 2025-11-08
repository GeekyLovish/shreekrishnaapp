package com.raman.kumar.shrikrishan.networking;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

/**
 *     Callback interface to return the api response to the associate method.
 */
public interface NetworkingCallbackInterface {

    public void onSuccess(NetworkResponse response, boolean fromCache);

    public void onSuccess(String response, boolean fromCache);

    public void onFailure(VolleyError error);

	public void onNetworkFailure(String error);
}


