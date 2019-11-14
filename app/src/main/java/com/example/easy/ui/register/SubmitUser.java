package com.example.easy.ui.register;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SubmitUser {
    private static String resultmsg = "";

    public static String Submit(final String username, final String password, final String uri) throws InterruptedException {
        Thread thread = new Thread(){
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formbody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build();
                Request request = new Request.Builder()
                        .url(uri)
                        .post(formbody)
                        .build();
                try{
                    Response response = client.newCall(request).execute();
                    if(!response.isSuccessful()){
                        throw new IOException("Unexpected code" + response);
                    }
                    resultmsg = response.body().string();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        thread.join();
        return resultmsg;
    }
}
