package com.raman.kumar.shrikrishan.apiNetworking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raman.kumar.shrikrishan.util.PrefHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.facebook.FacebookSdk.getApplicationContext;

public class RetrofitClient {

//    private static String BASE_URL = "https://jaishrikrishna.ramankumarynr.com/api/";//Dev
    private static String BASE_URL = "https://app.ramankumarynr.com/api/";//Prod
    private static RetrofitClient retrofitClient;
    private static Retrofit retrofit;
    private OkHttpClient.Builder builder = new OkHttpClient.Builder();
    private HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    private PrefHelper prefHelper;
    String authToken ="";

    private RetrofitClient()
    {
        prefHelper = new PrefHelper(getApplicationContext());
        authToken = prefHelper.getAuthToken();
        //Create a new Interceptor.
        Interceptor headerAuthorizationInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Request request = chain.request();
                Headers headers = request.headers().newBuilder().add("Authorization", authToken).build();
                request = request.newBuilder().headers(headers).build();
                return chain.proceed(request);
            }
        };

        Gson gson = new GsonBuilder().setLenient().create();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
//        builder.addInterceptor(interceptor);
//        builder.addInterceptor(headerAuthorizationInterceptor);

        // Configure OkHttpClient with timeout settings
        builder
                .addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS) // 30 seconds connection timeout
                .readTimeout(60, TimeUnit.SECONDS) // 30 seconds read timeout
                .writeTimeout(60, TimeUnit.SECONDS); // 30 seconds write timeout


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(builder.build())
                .build();
    }

    //Singleton class to check if the instance of the RetrofitClient class is created or not
    public static synchronized RetrofitClient getInstance(){
        if (retrofit == null)
        {
            retrofitClient = new RetrofitClient();
        }
        return retrofitClient;
    }

    public ApiInterface getApi()
    {
        return retrofit.create(ApiInterface.class);
    }

}
