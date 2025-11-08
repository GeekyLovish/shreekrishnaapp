package com.raman.kumar.shrikrishan.apiNetworking;

import android.content.Context;
import android.content.SharedPreferences;

import com.raman.kumar.shrikrishan.util.User;

public class SharedPrefManager {

    private static  String SHARED_PREF_NAME = "ShriKrishna";
    private SharedPreferences sharedPreferences;
    Context context;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        this.context = context;
    }

    public void saveUser(User user)
    {
        sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        editor.putString("id",user.getUser_id());
        editor.putString("name",user.getUsername());
        editor.putString("email",user.getEmail());
        editor.putString("phone",user.getPhone_number());
        editor.putString("block",user.getBlock());
        editor.putString("userToken",user.getUserToken());
        editor.putBoolean("loggedIn", true);
        editor.putString("image", user.getUser_image());
        editor.putString("adminId", user.getAdmin_user_id());
        editor.apply();
    }

    public boolean isLoggedIn()
    {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("loggedIn", false);
    }

    public User getUser()
    {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(sharedPreferences.getString("block", null),
                sharedPreferences.getString("id", null),
                sharedPreferences.getString("phone", null),
                sharedPreferences.getString("email",null),
                sharedPreferences.getString("name",null),
                sharedPreferences.getString("userToken",null),
                sharedPreferences.getString("image",null),
                sharedPreferences.getString("adminId",null));
    }

    void logout(){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


}
