package com.example.easy;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

public class MyAplication extends Application {
    private List<Activity> activityList = new LinkedList<Activity>();
    private static MyAplication instance;
    private  MyAplication(){
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    public static MyAplication getInstance(){
        if(null == instance){
            instance = new MyAplication();
        }
        return instance;
    }

    public void addActivity(Activity activity){
        activityList.add(activity);
    }

    public void exit(){
        for(Activity activity:activityList){
            activity.finish();
        }
        activityList.clear();
    }
}
