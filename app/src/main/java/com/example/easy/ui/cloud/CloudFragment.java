package com.example.easy.ui.cloud;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.easy.tool.Globe;
import com.example.easy.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CloudFragment extends Fragment {


    private ArrayList<CloudPhoto> photoList = new ArrayList<>();
    private ListView listView;
    PhotoAdapter adapter;
    //private static String downloaduri = "http://192.168.1.103:3000/downloadphoto";
    private static String downloaduri = "http://114.55.64.152:3000/downloadphoto";
    private static InputStream PhotoBitMap;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GetPicMsg.downloadPhoto();
        String path = Globe.getPath();
        String[][] PhotoMsg =  Globe.getPhotoMsg();
        //InputStream[] PhotoBitmap = Globe.getPhotoBitMap();

        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_cloud, container, false);

        for(int i=0; i<PhotoMsg.length; i++){
            CloudPhoto cloudPhoto = new CloudPhoto();
            cloudPhoto.setUsername(PhotoMsg[i][0]);
            cloudPhoto.setAge(PhotoMsg[i][5]);
            cloudPhoto.setScore(PhotoMsg[i][6]);
            cloudPhoto.setUploadtime(PhotoMsg[i][4]);
            cloudPhoto.setFilename(PhotoMsg[i][1]);
            cloudPhoto.setPath(path+"/"+PhotoMsg[i][1]);
            File file = new File(cloudPhoto.getPath());
            if(!file.exists()){
                new Thread(){
                    public void run(){
                        OkHttpClient client = new OkHttpClient();
                        RequestBody formbody = new FormBody.Builder()
                                .add("username", Globe.getLoginUser())
                                .add("filename", cloudPhoto.getFilename())
                                .build();
                        Request request = new Request.Builder()
                                .url(downloaduri)
                                .post(formbody)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code" + response);
                            }
                            PhotoBitMap = response.body().byteStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(PhotoBitMap);
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                            bos.flush();
                            bos.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }.start();
        }

//            byte [] img = new byte[0];
//            try {
//                img = tools.convertToBytes(PhotoBitmap[i]);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            System.out.println(img.length);
//            cloudPhoto.setBytes(img);
            //cloudPhoto.setBytes(Base64.decode(PhotoMsg[i][5],0));
            photoList.add(cloudPhoto);
    }

        adapter = new PhotoAdapter(getActivity(),R.layout.cloud_photo, photoList);
        listView = root.findViewById(R.id.cloudlist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CloudPhoto thisCloud = photoList.get(position);
                Intent intent = new Intent(getActivity(), PhotoShowActivity.class);
                intent.putExtra("filename",thisCloud.filename);
                intent.putExtra("username",thisCloud.username);
                intent.putExtra("age",thisCloud.age);
                intent.putExtra("score",thisCloud.score);
                intent.putExtra("uploadtime",thisCloud.uploadtime);
                intent.putExtra("path",thisCloud.path);
                startActivity(intent);
            }
        });
        return root;
    }
}
