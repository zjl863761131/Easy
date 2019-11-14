package com.example.easy.ui.login;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Logout {
    public static String result;

    public static String LogoutUser(final String uri) throws InterruptedException {
        Thread thread = new Thread(){
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(uri)
                        .get()
                        .build();
                try{
                    Response response = client.newCall(request).execute();
                    if(!response.isSuccessful()){
                        throw new IOException("Unexpected code" + response);
                    }
                    result = response.body().string();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        thread.join();
        return result;
    }
}
