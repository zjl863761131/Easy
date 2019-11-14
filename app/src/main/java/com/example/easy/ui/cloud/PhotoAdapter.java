package com.example.easy.ui.cloud;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.easy.R;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends ArrayAdapter<CloudPhoto> {
    private ArrayList<CloudPhoto> Object = new ArrayList<CloudPhoto>();
    private int resourceId;

    public PhotoAdapter(@NonNull Context context, int textViewResourceId, ArrayList<CloudPhoto> objects) {
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
        this.Object = objects;
    }

    @Override
    public int getCount(){
        return Object.size();
    }

    @Nullable
    @Override
    public CloudPhoto getItem(int position){
        return Object.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        String defultpath =getResourcesUri(R.drawable.defult);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        ImageView cloud_phto_show = (ImageView)view.findViewById(R.id.cloud_photo_show);
        TextView text = (TextView)view.findViewById(R.id.cloud_photo_text);
        CloudPhoto cloudPhoto = getItem(position);
        File f = new File(cloudPhoto.path);
        if(!f.exists()){
            cloudPhoto.setPath(defultpath);
        }
        Bitmap bitmap = BitmapFactory.decodeFile(cloudPhoto.path);
        Glide.with(getContext()).load(bitmap).fitCenter().into(cloud_phto_show);
        text.setText("颜值" + cloudPhoto.score + "\n" + "年龄" + cloudPhoto.age + "\n" + cloudPhoto.uploadtime);
        return view;
    }


    private String getResourcesUri(@DrawableRes int id) {
        Resources resources = getContext().getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
        return uriPath;
    }

}
