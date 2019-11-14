package com.example.easy.ui.cloud;

import org.json.JSONArray;

import java.io.InputStream;

import okhttp3.Response;

public class Download {
    private static String getphotouri = "http://114.55.64.152:3000/getphoto";
    private static String downloaduri = "http://114.55.64.152:3000/downloadphoto";
//    private static String getphotouri = "http://192.168.1.103:3000/getphoto";
//    private static String downloaduri = "http://192.168.1.103:3000/downloadphoto";
    private static JSONArray result;
    private static Response response;
    private static InputStream[] PhotoBitMap;


}
