package com.raman.kumar.shrikrishan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

/**
 * Created by Amandeep on 09/04/2020.
 */

public class PrefHelper {

    public Context context;
    public static final String APPNAME = "krishna";
    public static final String AUTH_TOKEN = "authToken";
    public static final String U_ID = "userId";
    public static final String U_NAME = "userName";
    public static final String U_IMAGE = "userImage";
    public static final String U_EMAIL = "userEmail";
    public static final String LOGED_IN = "loggedIn";
    public static final String LOGED_IN_CLIENT = "loggedInClient";
    public static final String IS_CLIENT = "isClient";
    public static final String LANGUAGE = "language";
    public static final String COMPANY_NAME = "companyname";
    public static final String LAT = "weatherLat";
    public static final String LONG = "weatherLon";
    public static final String CITY = "city";
    public static final String CLIENT_ID = "clientId";
    public static final String ADDRESS = "address";
    public static final String PHONE = "phone";
    public static final String IS_ACTIVE = "isActive";
    public static final String CITY_ID = "cityId";
    public static final String CLIENT_LOGO = "logo";
    public static final String CLIENT_LOGIN = "clientLogin";
    public static final String CLIENT_COUPON = "clientCoupon";
    public static final String CLIENT_STATUS = "clientStatus";
    public static final String CAMERA_PERMISSION = "camera_p";
    public static final String LOCATION_PERMISSION = "location_p";
    public static final String PHONE_PERMISSION = "phone_p";
    public static final String STORAGE_PERMISSION = "storage_p";
    public static final String CURRENT_LATITUDE = "cLatitude";
    public static final String CURRENT_LONGITUDE = "cLongitude";
    public static final String FIREBASE_TOKEN = "firebaseToken";
    public static final String ADMIN_ID = "adminId";
    public static final String ROLE = "ROLE";


    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;

    public PrefHelper(Context context) {
        this.context = context;
    }

    // getting & setting sharedPrefrences
    private void setPerfs(){
        prefs = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }
    private void getPrefs(){
        prefs = context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE);
    }


    public void setCurrentLatitude(String lati) {
        setPerfs();
        editor.putString(CURRENT_LATITUDE, lati);
        editor.commit();
    }

    public String getCurrentLatitude() {
        getPrefs();
        String lati = prefs.getString(CURRENT_LATITUDE, "0");
        return lati;
    }

 public void setAdminId(String id) {
        setPerfs();
        editor.putString(ADMIN_ID, id);
        editor.commit();
    }

    public String getAdminId() {
        getPrefs();
        String id = prefs.getString(ADMIN_ID, "0");
        return id;
    }

    public void setCurrentLongitude(String lngi) {
        setPerfs();
        editor.putString(CURRENT_LONGITUDE, lngi);
        editor.commit();
    }

    public String getCurrentLongitude() {
        getPrefs();
        String lngi = prefs.getString(CURRENT_LONGITUDE, "0");
        return lngi;
    }

    public void setFirebaseToken(String token) {
        setPerfs();
        editor.putString(FIREBASE_TOKEN, token);
        editor.commit();
    }

    public String getFirebaseToken() {
        getPrefs();
        String token = prefs.getString(FIREBASE_TOKEN, "0");
        return token;
    }

    public void clearAllPreferences() {
        setPerfs(); // Initialize prefs and editor
        editor.clear(); // Clear all the preferences
        editor.commit(); // Commit the changes
    }


    public void setStoragePermission(String per) {
        setPerfs();
        editor.putString(STORAGE_PERMISSION, per);
        editor.commit();
    }

    public String getStoragePermission() {
        getPrefs();
        String per = prefs.getString(STORAGE_PERMISSION, "0");
        return per;
    }

    public void setPhonePermission(String per) {
        setPerfs();
        editor.putString(PHONE_PERMISSION, per);
        editor.commit();
    }

    public String getPhonePermission() {
        getPrefs();
        String per = prefs.getString(PHONE_PERMISSION, "0");
        return per;
    }

    public void setLocationPermission(String per) {
        setPerfs();
        editor.putString(LOCATION_PERMISSION, per);
        editor.commit();
    }

    public String getLocationPermission() {
        getPrefs();
        String per = prefs.getString(LOCATION_PERMISSION, "0");
        return per;
    }

    public void setCameraPermission(String per) {
        setPerfs();
        editor.putString(CAMERA_PERMISSION, per);
        editor.commit();
    }

    public String getCameraPermission() {
        getPrefs();
        String per = prefs.getString(CAMERA_PERMISSION, "0");
        return per;
    }

    public void setClientStatus(String status) {
        setPerfs();
        editor.putString(CLIENT_STATUS, status);
        editor.commit();
    }

    public String getClientStatus() {
        getPrefs();
        String status = prefs.getString(CLIENT_STATUS, "0");
        return status;
    }

    public void setClientCoupon(String coupon) {
        setPerfs();
        editor.putString(CLIENT_COUPON, coupon);
        editor.commit();
    }

    public String getClientCoupon() {
        getPrefs();
        String coupon = prefs.getString(CLIENT_COUPON, "");
        return coupon;
    }

    public void setClientLogin(String param) {
        setPerfs();
        editor.putString(CLIENT_LOGIN, param);
        editor.commit();
    }

    public String getClientLogin() {
        getPrefs();
        String param = prefs.getString(CLIENT_LOGIN, "no");
        return param;
    }

    public void setClientLogo(String logo) {
        setPerfs();
        editor.putString(CLIENT_LOGO, logo);
        editor.commit();
    }

    public String getClientLogo() {
        getPrefs();
        String logo = prefs.getString(CLIENT_LOGO, "");
        return logo;
    }

    public void setCityId(String cityId) {
        setPerfs();
        editor.putString(CITY_ID, cityId);
        editor.commit();
    }

    public String getCityId() {
        getPrefs();
        String cityId = prefs.getString(CITY_ID, "");
        return cityId;
    }

    public void setIsActive(String isActive) {
        setPerfs();
        editor.putString(IS_ACTIVE, isActive);
        editor.commit();
    }

    public String getIsActive() {
        getPrefs();
        String isActive = prefs.getString(IS_ACTIVE, "");
        return isActive;
    }

    public void setPhone(String phone) {
        setPerfs();
        editor.putString(PHONE, phone);
        editor.commit();
    }

    public String getPhone() {
        getPrefs();
        String phone = prefs.getString(PHONE, "");
        return phone;
    }

    public void setAddress(String address) {
        setPerfs();
        editor.putString(ADDRESS, address);
        editor.commit();
    }

    public String getAddress() {
        getPrefs();
        String address = prefs.getString(ADDRESS, "");
        return address;
    }

    public void setClientId(String clientId) {
        setPerfs();
        editor.putString(CLIENT_ID, clientId);
        editor.commit();
    }

    public String getClientId() {
        getPrefs();
        String clientId = prefs.getString(CLIENT_ID, "");
        return clientId;
    }


    public void setCity(String city) {
        setPerfs();
        editor.putString(CITY, city);
        editor.commit();
    }

    public String getCity() {
        getPrefs();
        String city = prefs.getString(CITY, "");
        return city;
    }

    public Location getWeatherLocation() {
        Location location = new Location("");
        getPrefs();
        location.setLatitude(Double.parseDouble(prefs.getString(LAT, "")));
        location.setLatitude(Double.parseDouble(prefs.getString(LONG, "")));
        return location;
    }

    public void setWeatherLocation(Location weatherLocation) {
        setPerfs();
        editor.putString(LAT, ""+weatherLocation.getLatitude());
        editor.putString(LONG, ""+weatherLocation.getLongitude());
        editor.commit();
    }

    public void resetWeatherLocation() {
        setPerfs();
        editor.putString(LAT, ""+0);
        editor.putString(LONG, ""+0);
        editor.commit();
    }

    public void setCompanyName(String companyName) {
        setPerfs();
        editor.putString(COMPANY_NAME, companyName);
        editor.commit();
    }

    public String getCompanyName() {
        getPrefs();
        String companyName = prefs.getString(COMPANY_NAME, "");
        return companyName;
    }

    public void setLanguage(String lanuguag) {
        setPerfs();
        editor.putString(LANGUAGE, lanuguag);
        editor.commit();
    }

    public String getLanguage() {
        getPrefs();
        String lanuguag = prefs.getString(LANGUAGE, "");
        return lanuguag;
    }

    public void setIsClient(String isClient) {
        setPerfs();
        editor.putString(IS_CLIENT, isClient);
        editor.commit();
    }

    public String getIsClient() {
        getPrefs();
        String isClient = prefs.getString(IS_CLIENT, "");
        return isClient;
    }

    public void setAuthToken(String authToken) {
        setPerfs();
        editor.putString(AUTH_TOKEN, authToken);
        editor.commit();
    }

    public String getAuthToken() {
        getPrefs();
        String authToken = prefs.getString(AUTH_TOKEN, "");
        return authToken;
    }

    public void setLogedIn(String login) {
        setPerfs();
        editor.putString(LOGED_IN, login);
        editor.commit();
    }

    public String getLogedIn() {
        getPrefs();
        String login = prefs.getString(LOGED_IN, "no");
        return login;
    }

    public void setLogedInClient(String login) {
        setPerfs();
        editor.putString(LOGED_IN_CLIENT, login);
        editor.commit();
    }

    public String getLogedInClient() {
        getPrefs();
        String login = prefs.getString(LOGED_IN_CLIENT, "");
        return login;
    }

    public void setuName(String name) {
        setPerfs();
        editor.putString(U_NAME, name);
        editor.commit();
    }

    public String getuName() {
        getPrefs();
        return prefs.getString(U_NAME, "");
    }
    public void setuRole(String role) {
        setPerfs();
        editor.putString(ROLE, role);
        editor.commit();
    }

    public String getuRole() {
        getPrefs();
        return prefs.getString(ROLE, "");
    }

    public void setuImage(String imageURL){
        setPerfs();
        editor.putString(U_IMAGE, imageURL);
        editor.commit();
    }

    public String getuImage() {
        getPrefs();
        return prefs.getString(U_IMAGE, "");
    }

    public void setuEmail(String email) {
        setPerfs();
        editor.putString(U_EMAIL, email);
        editor.commit();
    }

    public String getuEmail() {
        getPrefs();
        String email = prefs.getString(U_EMAIL, "");
        return email;
    }

    public void setuId(String id) {
        setPerfs();
        editor.putString(U_ID, id);
        editor.commit();
    }

    public String getuId() {
        getPrefs();
        String id = prefs.getString(U_ID, "");
        return id;
    }

}
