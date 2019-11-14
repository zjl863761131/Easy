package com.example.easy.tool;

import android.app.Application;

import java.io.InputStream;


public class Globe extends Application {

    public static String LoginUser = "";
    public static InputStream[] PhotoBitMap = null;
    public static String[][] PhotoMsg = null;
    public static String path = "";
    public static String[][] UserSearch = null;

    public static void setUserSearch(String[][] userSearch) {
        UserSearch = userSearch;
    }

    public static String[][] getUserSearch() {
        return UserSearch;
    }

    public static void setPath(String path) {
        Globe.path = path;
    }

    public static String getPath() {
        return path;
    }

    public static void setPhotoMsg(String[][] photoMsg) {
        PhotoMsg = photoMsg;
    }

    public static String[][] getPhotoMsg() {
        return PhotoMsg;
    }

    public static void setPhotoBitMap(InputStream[] photoBitMap) {
        PhotoBitMap = photoBitMap;
    }


    public static InputStream[] getPhotoBitMap() {
        return PhotoBitMap;
    }

    public static String getLoginUser(){
        return LoginUser;
    }

    public static void setLoginUser(String user){
        LoginUser = user;
    }

}
