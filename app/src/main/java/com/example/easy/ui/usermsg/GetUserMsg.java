package com.example.easy.ui.usermsg;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GetUserMsg {

    public static String usermsg;

    public static String GetMsg(final String username,final String uri) throws InterruptedException {
        Thread thread = new Thread(){
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formbody = new FormBody.Builder()
                        .add("username", username)
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
                    usermsg = response.body().string();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        thread.join();
        return usermsg;
    }

}
